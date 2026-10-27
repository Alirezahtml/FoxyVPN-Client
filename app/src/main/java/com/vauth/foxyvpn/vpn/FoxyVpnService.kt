package com.vauth.foxyvpn.vpn

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.VpnService
import android.net.wifi.WifiManager
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.vauth.foxyvpn.FoxyVpnApp
import com.vauth.foxyvpn.MainActivity
import com.vauth.foxyvpn.R
import com.vauth.foxyvpn.data.AppLogger
import com.vauth.foxyvpn.data.ControlPlaneHttp
import com.vauth.foxyvpn.data.FxaAuthRepository
import com.vauth.foxyvpn.data.GUARDIAN_ENDPOINT_DEFAULT
import com.vauth.foxyvpn.data.GuardianClient
import com.vauth.foxyvpn.data.ProxyPass
import com.vauth.foxyvpn.data.ProxyStateStore
import com.vauth.foxyvpn.data.QuotaExceededError
import com.vauth.foxyvpn.data.RECOMMENDED_COUNTRY_CODE
import com.vauth.foxyvpn.data.ServerListClient
import com.vauth.foxyvpn.data.SettingsStore
import com.vauth.foxyvpn.data.TokenInvalidError
import com.vauth.foxyvpn.data.TokenStore
import com.vauth.foxyvpn.data.formatBytesPerSecond
import com.vauth.foxyvpn.data.model.ConnectionState
import com.vauth.foxyvpn.data.model.ProxyCandidate
import com.vauth.foxyvpn.data.model.RuntimeAuth
import com.vauth.foxyvpn.vpn.socks.LocalHttpProxyServer
import com.vauth.foxyvpn.vpn.socks.LocalSocks5Server
import com.vauth.foxyvpn.vpn.tun.HevSocks5Tunnel
import com.vauth.foxyvpn.vpn.tun.HevSocks5TunnelConfig
import com.vauth.foxyvpn.vpn.upstream.EdgeAddressResolver
import com.vauth.foxyvpn.vpn.upstream.H2UpstreamSession
import com.vauth.foxyvpn.vpn.upstream.UpstreamProxyConfig
import com.vauth.foxyvpn.vpn.upstream.UpstreamSession
import com.vauth.foxyvpn.widget.QuickConnectWidgetProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

data class VpnLiveStats(
    val txRateBytesPerSec: Long = 0L,
    val rxRateBytesPerSec: Long = 0L,
    val sessionBytes: Long = 0L,
    val totalTxBytes: Long = 0L,
    val totalRxBytes: Long = 0L,
)

private const val TAG = "FoxyVpnService"
private const val CONNECT_TIMEOUT_MS = 20_000L
private const val SERVER_LIST_FETCH_TIMEOUT_MS = 15_000L

private const val UPSTREAM_WATCHDOG_INTERVAL_MS = 2_000L

private const val RECONNECT_FAILURES_BEFORE_WARNING = 5
private const val RECONNECT_BACKOFF_BASE_MS = 3_000L
private const val RECONNECT_BACKOFF_CAP_MS = 60_000L

private const val RECONNECT_FAILURES_BEFORE_EDGE_ROTATION = 2

private const val MAX_ALTERNATE_EDGES = 3

private const val UNHEALTHY_REDIAL_COOLDOWN_MS = 30_000L

private const val PROXY_PASS_RENEWAL_FRACTION = 0.5
private const val PROXY_PASS_RENEWAL_SAFETY_MARGIN_MS = 30_000L

private const val PROXY_PASS_RENEWAL_FLOOR_MS = 15_000L

private const val PROXY_PASS_RENEWAL_CEILING_MS = 30 * 60_000L

private const val PROXY_PASS_RENEWAL_FALLBACK_MS = 4 * 60_000L

private const val PROXY_PASS_RENEWAL_RETRY_MS = 30_000L

private const val POST_TUN_SETTLE_MS = 250L

private const val INITIAL_DIAL_SETTLE_MS = 600L

private const val INITIAL_DIAL_MAX_ATTEMPTS = MAX_ALTERNATE_EDGES + 1
private const val INITIAL_DIAL_BACKOFF_BASE_MS = 2_000L
private const val INITIAL_DIAL_BACKOFF_CAP_MS = 8_000L

private const val SPEED_UPDATE_INTERVAL_MS = 1_000L
private const val IDLE_TRAFFIC_THRESHOLD_BYTES_PER_SEC = 2_560L

private const val TUN_ADDRESS_IPV6 = "fd00:1:fd00:1:fd00:1:fd00:1"
private const val TUN_PREFIX_LENGTH_IPV6 = 128

private const val WAKE_LOCK_TAG = "com.vauth.foxyvpn.neu:vpn"
private const val WIFI_LOCK_TAG = "com.vauth.foxyvpn.neu:vpn-wifi"

private fun fullJitterBackoffMs(attempt: Int, baseMs: Long, capMs: Long): Long {
    val exponential = baseMs * (1L shl attempt.coerceIn(0, 10))
    val upperBound = exponential.coerceAtMost(capMs)
    return (upperBound.toDouble() * Math.random()).toLong().coerceAtLeast(baseMs / 4)
}

private fun proxyPassRenewalDelayMs(expiresAtEpochSeconds: Long?): Long {
    if (expiresAtEpochSeconds == null) return PROXY_PASS_RENEWAL_FALLBACK_MS
    val remainingMs = expiresAtEpochSeconds * 1_000L - System.currentTimeMillis()
    if (remainingMs <= 0L) return PROXY_PASS_RENEWAL_FLOOR_MS
    val atHalfLife = (remainingMs * PROXY_PASS_RENEWAL_FRACTION).toLong()
    val beforeExpiry = remainingMs - PROXY_PASS_RENEWAL_SAFETY_MARGIN_MS
    return minOf(atHalfLife, beforeExpiry)
        .coerceAtMost(PROXY_PASS_RENEWAL_CEILING_MS)
        .coerceAtLeast(PROXY_PASS_RENEWAL_FLOOR_MS)
}

class FoxyVpnService : VpnService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val opMutex = Mutex()

    private var tunFd: ParcelFileDescriptor? = null
    private var socksServer: LocalSocks5Server? = null
    private var httpProxyServer: LocalHttpProxyServer? = null
    private var upstreamSession: UpstreamSession? = null
    private var connectJob: Job? = null
    private var watchdogJob: Job? = null
    private var speedJob: Job? = null

    private var tokenRenewalJob: Job? = null

    private var wakeLock: PowerManager.WakeLock? = null
    private var wifiLock: WifiManager.WifiLock? = null

    @Volatile private var tunnelInterfaceActive = false

    @Volatile private var boundNetwork: Network? = null

    @Volatile private var reportedUnderlying: Network? = null

    @Volatile private var lastUnhealthyRedialAt = 0L

    @Volatile private var statusLabel: String = "Connecting\u2026"

    @Volatile private var foregroundActive = false

    @Volatile private var lastNotificationText: String? = null

    private class SessionResources(
        val tunFd: ParcelFileDescriptor?,
        val socksServer: LocalSocks5Server?,
        val httpProxyServer: LocalHttpProxyServer?,
        val upstreamSession: UpstreamSession?,
    ) {
        val isEmpty: Boolean get() = tunFd == null && socksServer == null && httpProxyServer == null && upstreamSession == null
    }

    @Volatile
    private var connectionGeneration = 0

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            reportUnderlyingNetwork(network)
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            reportUnderlyingNetwork(network, capabilities)
        }

        override fun onLost(network: Network) {
            if (boundNetwork != network) return
            boundNetwork = null
            reportedUnderlying = null

            if (tunnelInterfaceActive) {
                runCatching { setUnderlyingNetworks(null) }
            }
        }
    }

    private fun reportUnderlyingNetwork(
        network: Network,
        capabilities: NetworkCapabilities? = null,
    ) {
        val caps = capabilities
            ?: getSystemService(ConnectivityManager::class.java)?.getNetworkCapabilities(network)

        if (caps == null) return

        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) return

        if (tunnelInterfaceActive && reportedUnderlying != network) {
            reportedUnderlying = network
            runCatching { setUnderlyingNetworks(arrayOf(network)) }
                .onFailure {

                    reportedUnderlying = null
                    AppLogger.w(TAG, "failed to report the underlying network to the platform", it)
                }
        }

        val previous = boundNetwork
        if (previous == network) return

        if (!caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) && previous != null) return
        boundNetwork = network
        runCatching { ControlPlaneHttp.client.connectionPool.evictAll() }
        if (previous == null) return

        if (_state.value != ConnectionState.CONNECTED) return
        val session = upstreamSession ?: return

        AppLogger.i(
            TAG,
            "underlying network changed; closing the upstream session bound to the old one so it is redialed now " +
                "(its socket would otherwise stay ESTABLISHED and look healthy for minutes)",
        )
        runCatching { session.close() }
    }

    private fun onUpstreamSessionUnhealthy() {
        if (_state.value != ConnectionState.CONNECTED) return
        val session = upstreamSession ?: return
        val now = System.currentTimeMillis()
        val sinceLast = now - lastUnhealthyRedialAt
        if (lastUnhealthyRedialAt != 0L && sinceLast < UNHEALTHY_REDIAL_COOLDOWN_MS) {
            AppLogger.d(
                TAG,
                "ignoring an unhealthy-session verdict ${sinceLast}ms after the last rebuild " +
                    "(cooldown ${UNHEALTHY_REDIAL_COOLDOWN_MS}ms)",
            )
            return
        }
        lastUnhealthyRedialAt = now
        AppLogger.w(
            TAG,
            "the local proxy reports the upstream session is failing as a whole rather than for one destination; " +
                "closing it so the watchdog redials",
        )
        runCatching { session.close() }
    }

    override fun onCreate() {
        super.onCreate()
        runCatching {

            val underlyingNetworks = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                .build()
            getSystemService(ConnectivityManager::class.java)
                ?.registerNetworkCallback(underlyingNetworks, networkCallback)
        }.onFailure { AppLogger.w(TAG, "could not register a network callback; handovers will be slower to detect", it) }

        scope.launch {
            _state.collect { state ->
                QuickConnectWidgetProvider.updateAll(this@FoxyVpnService, state)
            }
        }
    }

    override fun onDestroy() {
        ControlPlaneHttp.socketProtector = null
        runCatching {
            getSystemService(ConnectivityManager::class.java)?.unregisterNetworkCallback(networkCallback)
        }
        connectJob?.cancel()
        connectJob = null
        watchdogJob?.cancel()
        watchdogJob = null
        connectionGeneration++
        releaseResources(detachResources(), stopNativeTunnel = true)
        releaseWakeLocks()
        foregroundActive = false
        lastNotificationText = null
        boundNetwork = null
        reportedUnderlying = null
        scope.cancel()
        _state.value = ConnectionState.DISCONNECTED
        QuickConnectWidgetProvider.updateAll(this, ConnectionState.DISCONNECTED)
        super.onDestroy()
    }

    private fun acquireWakeLocks() {
        if (SettingsStore(applicationContext).batterySaverModeEnabled) {
            releaseWakeLocks()
            return
        }
        if (wifiLock == null) {
            runCatching {
                val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                    ?: error("WifiManager unavailable")
                @Suppress("DEPRECATION")
                wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, WIFI_LOCK_TAG).apply {
                    setReferenceCounted(false)
                    acquire()
                }
            }.onFailure { AppLogger.d(TAG, "Wi-Fi high-performance lock unavailable: ${it.message}") }
        }
    }

    private fun releaseWakeLocks() {
        runCatching { wifiLock?.takeIf { it.isHeld }?.release() }
            .onFailure { AppLogger.d(TAG, "error releasing the Wi-Fi lock: ${it.message}") }
        wifiLock = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.action ?: ACTION_CONNECT
        if (action == ACTION_CONNECT || action == VpnService.SERVICE_INTERFACE) {

            if (_state.value != ConnectionState.DISCONNECTED) {
                AppLogger.d(TAG, "ignoring duplicate connect request while ${_state.value}")

                enterForeground(statusLabel)
                return START_STICKY
            }
            if (intent == null) {
                AppLogger.i(TAG, "the service was restarted by the system; bringing the tunnel back up")
            }
            _state.value = ConnectionState.CONNECTING
            _lastError.value = null
            statusLabel = "Connecting\u2026"
            enterForeground(statusLabel)

            acquireWakeLocks()
            connectJob?.cancel()
            connectJob = scope.launch { opMutex.withLock { connect() } }

            return START_STICKY
        } else if (action == ACTION_DISCONNECT) {
            requestDisconnect("requested by the user")
        } else if (action == ACTION_SWITCH_PROTOCOL) {
            val newMode = SettingsStore(applicationContext).connectionProtocolMode
            _activeProtocolBadge.value = newMode.shortBadge
            if (_state.value == ConnectionState.CONNECTED || _state.value == ConnectionState.CONNECTING) {
                AppLogger.i(TAG, "live protocol switch requested -> ${newMode.shortBadge}; redialing upstream tunnel")
                statusLabel = "Switching to ${newMode.shortBadge}\u2026"
                updateNotification(statusLabel)
                lastUnhealthyRedialAt = 0L
                runCatching { upstreamSession?.close() }
            }
            return START_STICKY
        }
        return START_NOT_STICKY
    }

    private fun requestDisconnect(reason: String) {
        val idle = _state.value == ConnectionState.DISCONNECTED &&
            connectJob == null &&
            watchdogJob == null
        if (idle) {
            releaseWakeLocks()
            stopSelf()
            return
        }
        AppLogger.i(TAG, "disconnect: $reason")

        val endedGeneration = ++connectionGeneration
        _state.value = ConnectionState.DISCONNECTED
        _vpnLiveStats.value = VpnLiveStats()
        _connectedAtEpochMs.value = null
        _pingLatencyMs.value = null
        _vpnEdgeIp.value = null
        _connectedEndpoint.value = null
        connectJob?.cancel()
        connectJob = null
        watchdogJob?.cancel()
        watchdogJob = null
        exitForeground()

        releaseWakeLocks()

        val doomed = detachResources()

        scope.launch {
            opMutex.withLock {

                val stillOurs = connectionGeneration == endedGeneration
                releaseResources(doomed, stopNativeTunnel = stillOurs)

                if (stillOurs && _state.value == ConnectionState.DISCONNECTED) {
                    stopSelf()
                }
            }
        }
    }

    private fun ensureGenerationCurrent(myGeneration: Int) {
        if (myGeneration != connectionGeneration) {
            throw CancellationException("connect was superseded by a newer request")
        }
    }

    private fun firstUsableNetwork(): Network? {
        val manager = getSystemService(ConnectivityManager::class.java) ?: return null
        @Suppress("DEPRECATION")
        val networks = runCatching { manager.allNetworks }.getOrDefault(emptyArray())
        var unvalidated: Network? = null
        for (network in networks) {
            val capabilities = manager.getNetworkCapabilities(network) ?: continue

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) continue
            if (!capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) continue
            if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) return network
            if (unvalidated == null) unvalidated = network
        }
        return unvalidated
    }

    private fun hasUsableNetwork(): Boolean {

        getSystemService(ConnectivityManager::class.java) ?: return true
        return firstUsableNetwork() != null
    }

    private fun isFatalUpstreamError(error: Throwable): Boolean =
        error is TokenInvalidError || error is QuotaExceededError

    private fun startProxyPassRenewal(
        myGeneration: Int,
        initialExpiry: Long?,
        mintProxyPass: suspend () -> ProxyPass,
    ) {
        tokenRenewalJob?.cancel()
        tokenRenewalJob = scope.launch {
            var expiry = initialExpiry
            AppLogger.i(
                TAG,
                "proxy pass renewal scheduled in ${proxyPassRenewalDelayMs(expiry) / 1_000}s " +
                    (if (expiry == null) "(pass lifetime unknown; using a fixed interval)" else "(at half of its remaining life)"),
            )
            while (isActive) {
                delay(proxyPassRenewalDelayMs(expiry))
                if (myGeneration != connectionGeneration) return@launch

                val session = upstreamSession
                if (session == null || !session.isConnected) {
                    AppLogger.d(TAG, "skipping proxy pass renewal: no live session (the watchdog's redial mints its own)")
                    continue
                }

                val attempt = runCatching { mintProxyPass() }
                val pass = attempt.getOrNull()
                if (pass == null) {
                    val error = attempt.exceptionOrNull()
                    if (error is CancellationException) throw error
                    if (error != null && isFatalUpstreamError(error)) {
                        AppLogger.e(
                            TAG,
                            "proxy pass renewal failed for a reason retrying cannot fix; leaving the session to the watchdog",
                            error,
                        )
                        return@launch
                    }
                    AppLogger.w(TAG, "proxy pass renewal failed; retrying shortly: ${error?.message}")

                    expiry = null
                    delay(PROXY_PASS_RENEWAL_RETRY_MS)
                    continue
                }

                if (myGeneration != connectionGeneration) return@launch

                val live = upstreamSession
                if (live == null || !live.isConnected) {

                    AppLogger.d(
                        TAG,
                        "minted a fresh proxy pass but the session went down meanwhile; the watchdog's redial " +
                            "will mint its own",
                    )
                    expiry = pass.expiresAtEpochSeconds
                    continue
                }

                val lifetimeNote = pass.expiresAtEpochSeconds?.let { expiresAt ->
                    val seconds = (expiresAt * 1_000L - System.currentTimeMillis()) / 1_000L
                    " (valid for ${seconds}s)"
                } ?: " (lifetime not stated)"
                live.updateBearerToken(pass.token)
                AppLogger.i(TAG, "proxy pass renewed in place$lifetimeNote; the tunnel was not rebuilt")

                expiry = pass.expiresAtEpochSeconds
            }
        }
    }

    private suspend fun connect() {

        val myGeneration = ++connectionGeneration

        lastUnhealthyRedialAt = 0L

        boundNetwork = null

        EdgeAddressResolver.invalidate()
        runCatching {
            AppLogger.i(TAG, "connect: starting")
            val tokenStore = TokenStore(applicationContext)
            if (tokenStore.loadAuth() == null) error("Not signed in")
            val proxyStateStore = ProxyStateStore(applicationContext)
            val settingsStore = SettingsStore(applicationContext)

            val primaryCandidate = resolveConnectCandidate(proxyStateStore)
            ensureGenerationCurrent(myGeneration)

            var candidates = listOf(primaryCandidate)
            var candidateIndex = 0
            var alternatesDiscovered = false

            if (settingsStore.smartPingAutoSwitch &&
                !settingsStore.gamingModeEnabled &&
                settingsStore.effectiveCustomEdgeAddress == null
            ) {
                runCatching {
                    val primaryBench = PingUtil.benchmarkServerStability(primaryCandidate.host, primaryCandidate.port, probes = 2)
                    // Only probe alternates if primary is unreachable or has high latency (> 220ms)
                    if (primaryBench == null || primaryBench.avgLatencyMs > 220) {
                        val alt = discoverAlternateCandidates(primaryCandidate)
                            .filter { it.authority != primaryCandidate.authority }
                            .take(MAX_ALTERNATE_EDGES)
                        if (alt.isNotEmpty()) {
                            alternatesDiscovered = true
                            val pool = listOf(primaryCandidate) + alt
                            val scored = pool.map { cand ->
                                val bench = if (cand.authority == primaryCandidate.authority && primaryBench != null) {
                                    primaryBench
                                } else {
                                    PingUtil.benchmarkServerStability(cand.host, cand.port, probes = 2)
                                }
                                Triple(cand, bench?.avgLatencyMs ?: Int.MAX_VALUE, bench?.stabilityScore ?: 0)
                            }.sortedWith(compareByDescending<Triple<ProxyCandidate, Int, Int>> { it.third }.thenBy { it.second })
                            candidates = scored.map { it.first }
                            val best = scored.first()
                            val primaryRtt = primaryBench?.avgLatencyMs ?: Int.MAX_VALUE
                            if (best.second < Int.MAX_VALUE &&
                                best.first.authority != primaryCandidate.authority &&
                                best.second + 35 < primaryRtt
                            ) {
                                proxyStateStore.save(best.first)
                                AppLogger.i(
                                    TAG,
                                    "Smart Ping Auto-Optimizer selected healthier edge ${best.first.authority} (${best.second}ms, score=${best.third})",
                                )
                            }
                        }
                    }
                }
            }

            fun activeCandidate(): ProxyCandidate = candidates[candidateIndex.coerceIn(0, candidates.lastIndex)]

            suspend fun rotateCandidate(): Boolean {
                if (candidateIndex + 1 >= candidates.size && !alternatesDiscovered) {
                    alternatesDiscovered = true
                    val fresh = discoverAlternateCandidates(primaryCandidate)
                        .filter { discovered -> candidates.none { known -> known.authority == discovered.authority } }
                        .take(MAX_ALTERNATE_EDGES)
                    if (fresh.isNotEmpty()) {
                        candidates = candidates + fresh
                        AppLogger.i(
                            TAG,
                            "found ${fresh.size} alternate edge(s) in ${primaryCandidate.countryCode} to fail over to",
                        )
                    }
                }
                if (candidateIndex + 1 >= candidates.size) return false
                candidateIndex++
                AppLogger.i(
                    TAG,
                    "failing over to edge ${activeCandidate().authority} " +
                        "(${candidateIndex + 1} of ${candidates.size})",
                )
                return true
            }

            val upstreamProxyConfig = if (settingsStore.upstreamProxyEnabled && settingsStore.upstreamProxyHost.isNotBlank()) {
                UpstreamProxyConfig(
                    type = when (settingsStore.upstreamProxyType) {
                        SettingsStore.UpstreamProxyType.SOCKS5 -> UpstreamProxyConfig.Type.SOCKS5
                        SettingsStore.UpstreamProxyType.HTTP -> UpstreamProxyConfig.Type.HTTP
                    },
                    host = settingsStore.upstreamProxyHost,
                    port = settingsStore.upstreamProxyPort,
                    username = settingsStore.upstreamProxyUsername.ifBlank { null },
                    password = settingsStore.upstreamProxyPassword.ifBlank { null },
                )
            } else {
                null
            }
            val chainSuffix = if (upstreamProxyConfig != null) {
                " via ${upstreamProxyConfig.type} proxy ${upstreamProxyConfig.host}:${upstreamProxyConfig.port}"
            } else {
                ""
            }

            val customEdgeAddress = settingsStore.effectiveCustomEdgeAddress
            val edgeSuffix = if (customEdgeAddress != null) {
                " via pinned edge $customEdgeAddress:${primaryCandidate.port}"
            } else {
                ""
            }

            val dohEndpointAddresses = settingsStore.effectiveBootstrapAddresses
            val bootstrapDesc = if (dohEndpointAddresses.isEmpty()) {
                "system DNS"
            } else {
                "${settingsStore.bootstrapDnsProvider.label} (${dohEndpointAddresses.joinToString(", ")})"
            }

            AppLogger.i(
                TAG,
                "connect: target=${activeCandidate().authority} country=${activeCandidate().countryCode} bootstrap=$bootstrapDesc$chainSuffix$edgeSuffix",
            )

            val socksPort = settingsStore.socksPort
            val telegramStandalone = settingsStore.telegramProxyModeEnabled
            val socksBindAddress = if (telegramStandalone) "127.0.0.1" else settingsStore.socksBindAddress
            val httpProxyPort = settingsStore.httpProxyPort
            val httpProxyEnabled = settingsStore.httpProxyEnabled
            val proxyOnlyMode = settingsStore.proxyOnlyMode || telegramStandalone
            val excludedApps = settingsStore.excludedApps
            val turboBuffers = if (settingsStore.socketBufferModeAuto) true else settingsStore.tcpBufferBoostEnabled
            val activeMtu = settingsStore.effectiveMtu

            val dohUrl = settingsStore.effectiveDohUrl

            fun connectedLabel(): String = if (telegramStandalone) {
                "Telegram Proxy \u2022 127.0.0.1:$socksPort"
            } else if (proxyOnlyMode) {
                if (httpProxyEnabled) {
                    "Proxy active \u2022 SOCKS $socksPort, HTTP $httpProxyPort"
                } else {
                    "Proxy active \u2022 $socksBindAddress:$socksPort"
                }
            } else {
                val target = activeCandidate()
                val protoBadge = _activeProtocolBadge.value
                "Connected \u2022 ${target.countryName.ifBlank { target.countryCode }} [$protoBadge]"
            }

            if (proxyOnlyMode) {
                ControlPlaneHttp.socketProtector = null
            } else {
                ControlPlaneHttp.socketProtector = { socket -> protect(socket) }
            }

            val mapdnsActive = !proxyOnlyMode && dohUrl == null
            val socks = LocalSocks5Server(
                bindAddress = socksBindAddress,
                port = socksPort,
                rejectFakeDnsAddresses = mapdnsActive,
                dohUrl = dohUrl,
                tcpBufferBoost = turboBuffers,
                sessionProvider = { upstreamSession },
            ) {
                onUpstreamSessionUnhealthy()
            }
            socks.start()
            socksServer = socks

            if (httpProxyEnabled) {
                val http = LocalHttpProxyServer(
                    socksBindAddress,
                    httpProxyPort,
                    { upstreamSession },
                ) {
                    onUpstreamSessionUnhealthy()
                }
                http.start()
                httpProxyServer = http
            }
            ensureGenerationCurrent(myGeneration)

            if (proxyOnlyMode) {

                tunnelInterfaceActive = false
                AppLogger.i(
                    TAG,
                    "connect: proxy-only / Telegram proxy mode enabled, skipping TUN interface; " +
                        "local SOCKS5 at $socksBindAddress:$socksPort",
                )
            } else {
                val fd = establishTun(
                    excludedApps = excludedApps,
                    dohUrl = dohUrl,
                    mtu = activeMtu,
                    unmetered = settingsStore.unmeteredVpnModeEnabled,
                ) ?: error("Failed to establish TUN interface")
                tunFd = fd
                tunnelInterfaceActive = true

                reportedUnderlying = null
                runCatching {

                    firstUsableNetwork()?.let { underlying -> reportUnderlyingNetwork(underlying) }
                }

                val tunnelConfigPath = HevSocks5TunnelConfig.write(
                    applicationContext,
                    socksPort,
                    useMapDns = mapdnsActive,
                    mtu = activeMtu,
                    tcpBufferBoost = turboBuffers,
                )
                val tunnelStarted = HevSocks5Tunnel.start(tunnelConfigPath, fd.fd)
                if (!tunnelStarted) error("Failed to start the tun2socks tunnel (hev-socks5-tunnel)")
                AppLogger.i(TAG, "connect: TUN interface up (mtu=$activeMtu), hev-socks5-tunnel started")
                delay(POST_TUN_SETTLE_MS)
            }
            ensureGenerationCurrent(myGeneration)

            suspend fun mintProxyPass(): ProxyPass {
                var auth = tokenStore.loadAuth() ?: throw TokenInvalidError("Not signed in")

                if (!tokenStore.hasValidAccessToken()) {
                    val renewed = renewAccessToken(tokenStore)
                    if (renewed != null) {
                        auth = renewed
                        AppLogger.i(TAG, "the account's access token had expired and was renewed")
                    } else {
                        AppLogger.w(
                            TAG,
                            "the account's access token is past its expiry and could not be renewed automatically; " +
                                "you may need to sign in again",
                        )
                    }
                }
                val guardian = GuardianClient()

                suspend fun fetchOrActivate(token: String): ProxyPass = try {
                    guardian.fetchProxyPass(GUARDIAN_ENDPOINT_DEFAULT, token)
                } catch (invalid: TokenInvalidError) {
                    AppLogger.w(TAG, "proxy pass rejected, activating Guardian entitlement and retrying", invalid)
                    guardian.activateGuardian(GUARDIAN_ENDPOINT_DEFAULT, token)
                    guardian.fetchProxyPass(GUARDIAN_ENDPOINT_DEFAULT, token)
                }

                var pass = try {
                    fetchOrActivate(auth.accessToken)
                } catch (quotaErr: QuotaExceededError) {
                    tokenStore.updateActiveAccountQuota(remaining = 0L, max = settingsStore.cachedQuotaMax, unlimited = false)
                    if (settingsStore.autoSwitchAccountByQuotaEnabled) {
                        val backup = tokenStore.findHealthyFailoverAccount(settingsStore.autoSwitchQuotaThresholdPercent)
                        if (backup != null && tokenStore.switchToAccount(backup.email)) {
                            AppLogger.i(
                                TAG,
                                "Auto-Quota Failover: active account exhausted; switched automatically to backup account ${backup.email}",
                            )
                            val backupAuth = tokenStore.loadAuth() ?: throw quotaErr
                            fetchOrActivate(backupAuth.accessToken)
                        } else {
                            throw quotaErr
                        }
                    } else {
                        throw quotaErr
                    }
                }

                val rem = pass.quotaRemaining
                val max = pass.quotaMax
                if (rem != null && max != null && max > 0L && settingsStore.autoSwitchAccountByQuotaEnabled) {
                    val usedPct = (((max - rem).toDouble() / max.toDouble()) * 100.0).toInt().coerceIn(0, 100)
                    tokenStore.updateActiveAccountQuota(remaining = rem, max = max, unlimited = false)
                    if (usedPct >= settingsStore.autoSwitchQuotaThresholdPercent) {
                        val backup = tokenStore.findHealthyFailoverAccount(settingsStore.autoSwitchQuotaThresholdPercent)
                        if (backup != null && tokenStore.switchToAccount(backup.email)) {
                            AppLogger.i(
                                TAG,
                                "Auto-Quota Failover: account usage reached $usedPct% (threshold ${settingsStore.autoSwitchQuotaThresholdPercent}%); " +
                                    "switching to ${backup.email}",
                            )
                            val backupAuth = tokenStore.loadAuth()
                            if (backupAuth != null) {
                                runCatching { fetchOrActivate(backupAuth.accessToken) }
                                    .onSuccess { switchedPass -> pass = switchedPass }
                            }
                        }
                    }
                } else {
                    tokenStore.updateActiveAccountQuota(
                        remaining = rem,
                        max = max,
                        unlimited = (rem == null && max == null && settingsStore.cachedIsUnlimited),
                    )
                }

                if (pass.quotaRemaining != null) {
                    settingsStore.cachedQuotaRemaining = pass.quotaRemaining
                }
                if (pass.quotaMax != null) {
                    settingsStore.cachedQuotaMax = pass.quotaMax
                }
                return pass
            }

            var currentPassExpiry: Long? = null

            suspend fun dialUpstream(): Pair<H2UpstreamSession, Long?> {
                val target = activeCandidate()
                val liveProtocolMode = settingsStore.connectionProtocolMode
                _activeProtocolBadge.value = liveProtocolMode.shortBadge
                val pass = mintProxyPass()
                currentPassExpiry = pass.expiresAtEpochSeconds
                val lifetimeNote = pass.expiresAtEpochSeconds?.let { expiresAt ->
                    val seconds = (expiresAt * 1_000L - System.currentTimeMillis()) / 1_000L
                    " (valid for ${seconds}s)"
                } ?: " (lifetime not stated)"
                AppLogger.i(TAG, "connect: acquired Guardian proxy pass$lifetimeNote (protocol=${liveProtocolMode.shortBadge})")

                val edgeAddress = customEdgeAddress
                    ?: resolveEdgeAddress(target.host, upstreamProxyConfig, dohEndpointAddresses)

                val session = H2UpstreamSession(
                    target.host,
                    target.port,
                    pass.token,
                    upstreamProxyConfig,
                    edgeAddress,
                    tlsSplitMode = settingsStore.tlsSplitMode,
                    tcpBufferBoost = turboBuffers,
                    protocolMode = liveProtocolMode,
                )
                try {
                    session.connect()
                } catch (failure: Throwable) {

                    runCatching { session.close() }
                    throw failure
                }
                _activeProtocolBadge.value = session.negotiatedProtocolBadge
                AppLogger.i(
                    TAG,
                    "connect: upstream tunnel [${session.negotiatedProtocolBadge}] established to ${target.authority}",
                )
                return session to pass.expiresAtEpochSeconds
            }

            var dialAttempt = 0
            var lastDialFailure: Throwable? = null
            while (true) {
                dialAttempt++
                ensureGenerationCurrent(myGeneration)
                val target = activeCandidate()

                val dialResult = try {
                    withTimeout(CONNECT_TIMEOUT_MS) { dialUpstream() }
                } catch (cancellation: CancellationException) {

                    if (cancellation !is TimeoutCancellationException) throw cancellation
                    lastDialFailure = cancellation
                    AppLogger.w(TAG, "dial to ${target.authority} timed out (attempt $dialAttempt)")
                    null
                } catch (error: Throwable) {

                    if (isFatalUpstreamError(error)) throw error
                    lastDialFailure = error
                    AppLogger.w(TAG, "dial to ${target.authority} failed (attempt $dialAttempt): ${error.message}")
                    null
                }

                val session = dialResult?.first

                if (session != null) {
                    if (myGeneration != connectionGeneration) {

                        runCatching { session.close() }
                        ensureGenerationCurrent(myGeneration)
                    }
                    upstreamSession = session
                    delay(INITIAL_DIAL_SETTLE_MS)
                    if (upstreamSession?.isConnected == true) break
                    AppLogger.w(
                        TAG,
                        "upstream tunnel to ${target.authority} died immediately after connecting " +
                            "(attempt $dialAttempt/$INITIAL_DIAL_MAX_ATTEMPTS)",
                    )
                    runCatching { upstreamSession?.close() }
                    upstreamSession = null

                    lastDialFailure = null
                }

                if (dialAttempt >= INITIAL_DIAL_MAX_ATTEMPTS) {

                    val (failures, cleared) = proxyStateStore.recordFailure()
                    if (cleared) {
                        AppLogger.w(
                            TAG,
                            "the saved location failed $failures connects in a row; clearing it so the next " +
                                "connect auto-selects a location",
                        )
                    }
                    val failure = lastDialFailure
                    if (failure != null) {
                        error(
                            "Could not reach a VPN server after $dialAttempt attempts across " +
                                "${candidates.size} server(s). Last error: ${friendlyErrorMessage(failure)}",
                        )
                    }
                    error(
                        "The VPN server closed the connection immediately, $dialAttempt times in a row. " +
                            "Try again shortly or pick a different location.",
                    )
                }

                rotateCandidate()
                delay(fullJitterBackoffMs(dialAttempt - 1, INITIAL_DIAL_BACKOFF_BASE_MS, INITIAL_DIAL_BACKOFF_CAP_MS))
            }

            ensureGenerationCurrent(myGeneration)

            val establishedCandidate = activeCandidate()

            if (establishedCandidate.authority == primaryCandidate.authority) {
                proxyStateStore.save(primaryCandidate)
            }

            val connectedText = connectedLabel()
            _connectedAtEpochMs.value = System.currentTimeMillis()
            _state.value = ConnectionState.CONNECTED
            statusLabel = connectedText
            updateNotification(connectedText)
            AppLogger.i(TAG, "connect: CONNECTED via ${establishedCandidate.authority}")

            _connectedEndpoint.value = establishedCandidate.host to establishedCandidate.port
            scope.launch {
                val ping = PingUtil.measureTcpLatencyMs(establishedCandidate.host, establishedCandidate.port)
                if (myGeneration == connectionGeneration) {
                    _pingLatencyMs.value = ping
                }
            }

            startProxyPassRenewal(myGeneration, currentPassExpiry) { mintProxyPass() }

            if (settingsStore.exitCheckEnabled) {
                scope.launch {
                    val exitCheck = ExitCheck()
                    exitCheck.fetchProxiedIpInfo(socksPort)
                        .onSuccess { info ->
                            if (myGeneration == connectionGeneration) {
                                _vpnEdgeIp.value = info
                                AppLogger.i(TAG, "exit check: observed ip=${info.ip}, country=${info.countryCode}")
                            }
                        }
                        .onFailure { AppLogger.w(TAG, "exit check failed (non-fatal)", it) }
                }
            }

            startSpeedUpdates()

            watchdogJob = scope.launch {
                var consecutiveFailures = 0
                var waitingForNetwork = false
                var lastKeepAliveProbeMs = System.currentTimeMillis()

                suspend fun stopWithFatalError(message: String) {
                    AppLogger.e(TAG, "unrecoverable upstream failure; disconnecting: $message")
                    _lastError.value = message
                    connectionGeneration++
                    watchdogJob = null
                    _state.value = ConnectionState.DISCONNECTED
                    exitForeground()
                    releaseWakeLocks()
                    val doomed = detachResources()
                    opMutex.withLock { releaseResources(doomed, stopNativeTunnel = true) }
                    stopSelf()
                }

                while (isActive) {
                    delay(UPSTREAM_WATCHDOG_INTERVAL_MS)
                    val current = upstreamSession
                    if (current != null && current.isConnected) {
                        if (consecutiveFailures > 0 || waitingForNetwork) {
                            AppLogger.i(TAG, "upstream tunnel is healthy again")
                            _lastError.value = null
                            statusLabel = connectedLabel()
                            updateNotification(statusLabel)
                        }
                        consecutiveFailures = 0
                        waitingForNetwork = false

                        if (settingsStore.keepAliveEnabled) {
                            val now = System.currentTimeMillis()
                            val intervalMs = settingsStore.keepAliveIntervalSec * 1_000L
                            if (now - lastKeepAliveProbeMs >= intervalMs) {
                                lastKeepAliveProbeMs = now
                                val endpoint = _connectedEndpoint.value
                                if (endpoint != null) {
                                    val probeLatency = PingUtil.measureTcpLatencyMs(endpoint.first, endpoint.second)
                                    if (probeLatency != null && myGeneration == connectionGeneration) {
                                        _pingLatencyMs.value = probeLatency
                                        _keepAlivePulseCount.value += 1
                                        _lastKeepAliveAtMs.value = System.currentTimeMillis()
                                    }
                                }
                            }
                        }
                        continue
                    }

                    if (!settingsStore.autoReconnectEnabled) {
                        stopWithFatalError("Connection dropped (Auto-Reconnect Watchdog is disabled).")
                        return@launch
                    }

                    if (!hasUsableNetwork()) {
                        if (!waitingForNetwork) {
                            AppLogger.i(TAG, "no usable network; holding the session and waiting for connectivity")
                            waitingForNetwork = true
                            statusLabel = "Waiting for network\u2026"
                            updateNotification(statusLabel)
                        }
                        consecutiveFailures = 0
                        continue
                    }
                    if (waitingForNetwork) {
                        AppLogger.i(TAG, "network is back; redialing the upstream tunnel")
                        waitingForNetwork = false
                    }

                    AppLogger.w(TAG, "upstream tunnel is down; attempting to reconnect")
                    runCatching { current?.close() }
                    statusLabel = "Reconnecting\u2026"
                    updateNotification(statusLabel)

                    val dialResult = try {
                        withTimeout(CONNECT_TIMEOUT_MS) { dialUpstream() }
                    } catch (cancellation: CancellationException) {
                        if (cancellation !is TimeoutCancellationException) throw cancellation
                        consecutiveFailures++
                        AppLogger.w(TAG, "upstream reconnect attempt $consecutiveFailures timed out")
                        null
                    } catch (error: Throwable) {
                        if (isFatalUpstreamError(error)) {
                            stopWithFatalError(friendlyErrorMessage(error))
                            return@launch
                        }
                        consecutiveFailures++
                        AppLogger.w(TAG, "upstream reconnect attempt $consecutiveFailures failed: ${error.message}")
                        null
                    }
                    val fresh = dialResult?.first

                    if (fresh == null) {

                        if (consecutiveFailures % RECONNECT_FAILURES_BEFORE_EDGE_ROTATION == 0) {
                            rotateCandidate()
                        }

                        if (consecutiveFailures == RECONNECT_FAILURES_BEFORE_WARNING) {
                            _lastError.value =
                                "Still trying to reconnect to the VPN server. Tap Disconnect to stop."
                            AppLogger.e(
                                TAG,
                                "$consecutiveFailures consecutive reconnect failures; continuing to retry with backoff " +
                                    "(capped at ${RECONNECT_BACKOFF_CAP_MS / 1_000}s between attempts)",
                            )
                        }
                        delay(fullJitterBackoffMs(consecutiveFailures - 1, RECONNECT_BACKOFF_BASE_MS, RECONNECT_BACKOFF_CAP_MS))
                        continue
                    }

                    if (myGeneration != connectionGeneration) {

                        AppLogger.i(TAG, "discarding stale reconnect from a torn-down session generation")
                        runCatching { fresh.close() }
                        return@launch
                    }
                    upstreamSession = fresh
                    consecutiveFailures = 0
                    _lastError.value = null

                    statusLabel = connectedLabel()
                    updateNotification(statusLabel)
                    AppLogger.i(TAG, "upstream tunnel reconnected via ${activeCandidate().authority}")

                    startProxyPassRenewal(myGeneration, currentPassExpiry) { mintProxyPass() }
                }
            }
        }.onFailure { failure ->

            if (failure is CancellationException) {
                AppLogger.i(TAG, "connect: aborted because the connection was cancelled")
                releaseResources(detachResources(), stopNativeTunnel = true)
                return@onFailure
            }
            val message = if (failure is TimeoutCancellationException) {
                "Connection timed out. Check your network and try again."
            } else {
                friendlyErrorMessage(failure)
            }
            AppLogger.e(TAG, "connect failed", failure)
            _lastError.value = message
            teardown()
            exitForeground()
            releaseWakeLocks()
            stopSelf()
        }
        connectJob = null
    }

    private suspend fun resolveConnectCandidate(proxyStateStore: ProxyStateStore): ProxyCandidate {
        proxyStateStore.load()?.let { return it }
        AppLogger.i(TAG, "connect: no server previously selected; auto-selecting recommended location")
        val protoMode = SettingsStore(applicationContext).connectionProtocolMode
        val client = (applicationContext as? FoxyVpnApp)?.serverListClient ?: ServerListClient(applicationContext)
        val countries = runCatching {
            withTimeout(SERVER_LIST_FETCH_TIMEOUT_MS) { client.fetchCountries() }
        }.getOrElse {
            client.loadCachedCountries()
        }
        val recommended = ServerListClient.candidatesForCountry(countries, RECOMMENDED_COUNTRY_CODE, protoMode).randomOrNull()
        val chosen = recommended ?: countries.firstOrNull { it.code.isNotBlank() }?.let { country ->
            ServerListClient.candidatesForCountry(countries, country.code, protoMode).randomOrNull()
        } ?: error("No server selected and no servers are available. Choose a location first.")
        proxyStateStore.save(chosen)
        AppLogger.i(TAG, "connect: auto-selected ${chosen.authority} country=${chosen.countryCode} proto=${protoMode.shortBadge}")
        return chosen
    }

    private suspend fun discoverAlternateCandidates(primary: ProxyCandidate): List<ProxyCandidate> {
        val attempt = runCatching {
            val protoMode = SettingsStore(applicationContext).connectionProtocolMode
            val client = (applicationContext as? FoxyVpnApp)?.serverListClient ?: ServerListClient(applicationContext)
            val countries = runCatching {
                withTimeout(SERVER_LIST_FETCH_TIMEOUT_MS) { client.fetchCountries() }
            }.getOrElse {
                client.loadCachedCountries()
            }
            val sameCity = ServerListClient.candidatesForCity(countries, primary.countryCode, primary.cityCode, protoMode)
            val sameCountry = ServerListClient.candidatesForCountry(countries, primary.countryCode, protoMode)
            (sameCity + sameCountry)
                .distinctBy { it.authority }
                .filter { it.authority != primary.authority }
        }
        val failure = attempt.exceptionOrNull()
        if (failure is CancellationException && failure !is TimeoutCancellationException) throw failure
        if (failure != null) {
            AppLogger.w(TAG, "could not fetch alternate edges; staying with ${primary.authority}", failure)
            return emptyList()
        }
        return attempt.getOrDefault(emptyList())
    }

    private suspend fun resolveEdgeAddress(
        host: String,
        upstreamProxy: UpstreamProxyConfig?,
        dohEndpointAddresses: List<String>,
    ): String? {
        if (upstreamProxy != null) return null
        if (dohEndpointAddresses.isEmpty()) {
            AppLogger.d(TAG, "resolving $host via system DNS resolver (direct network)")
            return null
        }
        return EdgeAddressResolver.resolve(host, dohEndpointAddresses)
    }

    private suspend fun renewAccessToken(tokenStore: TokenStore): RuntimeAuth? {
        val attempt = runCatching { FxaAuthRepository(tokenStore).refreshAccessToken() }
        val failure = attempt.exceptionOrNull()
        if (failure is CancellationException) throw failure
        if (failure != null) {
            AppLogger.w(TAG, "renewing the account's access token failed", failure)
            return null
        }
        return attempt.getOrNull()
    }

    private fun friendlyErrorMessage(error: Throwable): String = when (error) {
        is QuotaExceededError -> "Your VPN quota is exhausted. Try again later."
        is TokenInvalidError -> "Your session was rejected. Please sign in again."
        is TimeoutCancellationException -> "the connection timed out"
        else -> {
            val untrusted = generateSequence(error) { it.cause }.take(8).any {
                it is java.security.cert.CertPathValidatorException || it is java.security.cert.CertificateException
            }
            val base = error.message ?: (error::class.simpleName ?: "Connection failed")
            if (untrusted) {
                "$base (this device does not trust the VPN server's certificate; updating Android or the device's date/time may help)"
            } else {
                base
            }
        }
    }

    private fun establishTun(
        excludedApps: Set<String>,
        dohUrl: String?,
        mtu: Int = HevSocks5TunnelConfig.TUN_MTU,
        unmetered: Boolean = true,
    ): ParcelFileDescriptor? {
        val dnsServer = HevSocks5TunnelConfig.MAPDNS_ADDRESS
        if (dohUrl != null) {
            AppLogger.i(TAG, "establishTun: dns=DoH ($dohUrl)")
        } else {
            AppLogger.i(TAG, "establishTun: dns=mapdns ($dnsServer)")
        }
        return Builder()
            .setSession("FoxyVpn")
            .addAddress(HevSocks5TunnelConfig.TUN_ADDRESS, 32)
            .addRoute("0.0.0.0", 0)
            .apply {

                val ipv6Address = runCatching {
                    addAddress(TUN_ADDRESS_IPV6, TUN_PREFIX_LENGTH_IPV6)
                }.onFailure {
                    AppLogger.w(
                        TAG,
                        "could not add an IPv6 address to the TUN interface; IPv6 will be captured by the " +
                            "tunnel's default route and dropped rather than sent in the clear",
                        it,
                    )
                }

                runCatching { addRoute("::", 0) }.onFailure {
                    AppLogger.e(
                        TAG,
                        "could not claim the IPv6 default route; refusing to establish a tunnel that may leak IPv6",
                        it,
                    )
                    throw IllegalStateException(
                        "IPv6 default route could not be secured; VPN tunnel setup aborted to prevent a possible leak",
                        it,
                    )
                }
                if (ipv6Address.isFailure) {
                    AppLogger.i(TAG, "establishTun: IPv6 is routed into the tunnel and blackholed (no IPv6 address)")
                }
            }
            .addDnsServer(dnsServer)

            .setMtu(mtu)
            .setBlocking(true)
            .apply {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    runCatching { setMetered(!unmetered) }
                        .onFailure { AppLogger.d(TAG, "could not set tunnel metered=${!unmetered}: ${it.message}") }
                }
            }
            .apply {
                runCatching { addDisallowedApplication(packageName) }
                    .onFailure { AppLogger.w(TAG, "failed to exclude own app from the VPN tunnel", it) }
                for (excludedPackage in excludedApps) {
                    runCatching { addDisallowedApplication(excludedPackage) }
                        .onFailure { AppLogger.w(TAG, "failed to exclude $excludedPackage from the VPN tunnel (app may be uninstalled)", it) }
                }
            }
            .establish()
    }

    private fun teardown() {
        connectionGeneration++
        watchdogJob?.cancel()
        watchdogJob = null
        releaseResources(detachResources(), stopNativeTunnel = true)
        _state.value = ConnectionState.DISCONNECTED
        _vpnLiveStats.value = VpnLiveStats()
        _connectedAtEpochMs.value = null
        _pingLatencyMs.value = null
        _vpnEdgeIp.value = null
        _connectedEndpoint.value = null
    }

    private fun detachResources(): SessionResources {
        speedJob?.cancel()
        speedJob = null

        tokenRenewalJob?.cancel()
        tokenRenewalJob = null
        val detached = SessionResources(tunFd, socksServer, httpProxyServer, upstreamSession)
        tunFd = null
        tunnelInterfaceActive = false
        socksServer = null
        httpProxyServer = null
        upstreamSession = null
        return detached
    }

    private fun releaseResources(resources: SessionResources, stopNativeTunnel: Boolean) {
        if (resources.isEmpty && !stopNativeTunnel) return
        if (stopNativeTunnel) {
            runCatching { HevSocks5Tunnel.stop() }.onFailure { AppLogger.w(TAG, "error stopping tun2socks tunnel", it) }
        }
        runCatching { resources.socksServer?.stop() }.onFailure { AppLogger.w(TAG, "error stopping local SOCKS5 server", it) }
        runCatching { resources.httpProxyServer?.stop() }.onFailure { AppLogger.w(TAG, "error stopping local HTTP proxy server", it) }
        runCatching { resources.upstreamSession?.close() }.onFailure { AppLogger.w(TAG, "error closing upstream session", it) }
        runCatching { resources.tunFd?.close() }.onFailure { AppLogger.w(TAG, "error closing TUN interface", it) }
    }

    override fun onRevoke() {
        requestDisconnect("VPN permission was revoked by the system")
        super.onRevoke()
    }

    private fun startSpeedUpdates() {
        speedJob?.cancel()
        speedJob = scope.launch {
            val myUid = android.os.Process.myUid()
            var lastStats = runCatching { HevSocks5Tunnel.stats() }.getOrElse { LongArray(4) }
            val initialUidRx = android.net.TrafficStats.getUidRxBytes(myUid).coerceAtLeast(0L)
            val initialUidTx = android.net.TrafficStats.getUidTxBytes(myUid).coerceAtLeast(0L)
            var lastUidRx = initialUidRx
            var lastUidTx = initialUidTx
            var accumulatedTx = 0L
            var accumulatedRx = 0L
            var lastSampleAt = System.currentTimeMillis()
            var tickCount = 0
            var idleSeconds = 0L
            var consecutiveHighPingChecks = 0
            _idleTimerSeconds.value = 0L

            while (isActive) {
                delay(SPEED_UPDATE_INTERVAL_MS)
                if (_state.value != ConnectionState.CONNECTED) continue

                val stats = runCatching { HevSocks5Tunnel.stats() }.getOrElse { LongArray(4) }
                val curUidRx = android.net.TrafficStats.getUidRxBytes(myUid).coerceAtLeast(lastUidRx)
                val curUidTx = android.net.TrafficStats.getUidTxBytes(myUid).coerceAtLeast(lastUidTx)

                val now = System.currentTimeMillis()
                val elapsedSeconds = ((now - lastSampleAt).coerceAtLeast(1)).toDouble() / 1_000.0

                val hevTxDelta = (stats[1] - lastStats[1]).coerceAtLeast(0L)
                val hevRxDelta = (stats[3] - lastStats[3]).coerceAtLeast(0L)
                val uidTxDelta = (curUidTx - lastUidTx).coerceAtLeast(0L)
                val uidRxDelta = (curUidRx - lastUidRx).coerceAtLeast(0L)

                val deltaTx = maxOf(hevTxDelta, uidTxDelta)
                val deltaRx = maxOf(hevRxDelta, uidRxDelta)

                val txRate = (deltaTx / elapsedSeconds).toLong()
                val rxRate = (deltaRx / elapsedSeconds).toLong()

                accumulatedTx += deltaTx
                accumulatedRx += deltaRx

                val totalTx = maxOf(stats[1].coerceAtLeast(0L), accumulatedTx)
                val totalRx = maxOf(stats[3].coerceAtLeast(0L), accumulatedRx)
                val totalSessionBytes = (totalTx + totalRx).coerceAtLeast(0L)

                lastStats = stats
                lastUidRx = curUidRx
                lastUidTx = curUidTx
                lastSampleAt = now
                tickCount++

                _vpnLiveStats.value = VpnLiveStats(
                    txRateBytesPerSec = txRate,
                    rxRateBytesPerSec = rxRate,
                    sessionBytes = totalSessionBytes,
                    totalTxBytes = totalTx,
                    totalRxBytes = totalRx,
                )

                val settingsStore = SettingsStore(applicationContext)
                val ecoMode = settingsStore.batterySaverModeEnabled
                if (ecoMode) {
                    releaseWakeLocks()
                }

                // Adaptive ping cadence: every 3s normally, every 9s in Smart Battery Saver mode
                val pingMod = if (ecoMode) 9 else 3
                if (tickCount % pingMod == 0) {
                    val endpoint = _connectedEndpoint.value
                    if (endpoint != null) {
                        scope.launch {
                            val livePing = PingUtil.measureTcpLatencyMs(endpoint.first, endpoint.second)
                            if (livePing != null && _state.value == ConnectionState.CONNECTED) {
                                _pingLatencyMs.value = livePing
                                // Never auto-switch servers during Gaming Mode, and require 6 consecutive severe >600ms spikes otherwise
                                if (settingsStore.smartPingAutoSwitch &&
                                    !settingsStore.gamingModeEnabled &&
                                    livePing > 600
                                ) {
                                    consecutiveHighPingChecks++
                                    if (consecutiveHighPingChecks >= 6) {
                                        consecutiveHighPingChecks = 0
                                        AppLogger.i(
                                            TAG,
                                            "Smart Ping Auto-Optimizer detected sustained severe latency (${livePing}ms x6); redialing",
                                        )
                                        onUpstreamSessionUnhealthy()
                                    }
                                } else {
                                    consecutiveHighPingChecks = 0
                                }
                            }
                        }
                    }
                }

                // Smart Idle Auto-Disconnect (Power & Data Saver)
                if (txRate + rxRate < IDLE_TRAFFIC_THRESHOLD_BYTES_PER_SEC) {
                    idleSeconds += 1L
                } else {
                    idleSeconds = 0L
                }
                _idleTimerSeconds.value = idleSeconds

                if (settingsStore.idleDisconnectEnabled) {
                    val timeoutSec = (settingsStore.idleTimeoutMinutes.coerceAtLeast(1)) * 60L
                    if (idleSeconds >= timeoutSec) {
                        AppLogger.i(TAG, "Smart Idle Auto-Disconnect triggered after ${idleSeconds}s of inactivity")
                        requestDisconnect("Smart Idle Saver: auto-disconnected after ${settingsStore.idleTimeoutMinutes} min of inactivity")
                        return@launch
                    }
                }

                // Session Data Limit Auto-Cutoff
                if (settingsStore.sessionDataLimitEnabled) {
                    val capBytes = settingsStore.sessionDataLimitMb.toLong() * 1024L * 1024L
                    if (totalSessionBytes >= capBytes) {
                        AppLogger.i(TAG, "Session Data Limit (${settingsStore.sessionDataLimitMb} MB) reached; auto-disconnecting")
                        requestDisconnect("Session data limit (${settingsStore.sessionDataLimitMb} MB) reached")
                        return@launch
                    }
                }

                updateNotification(
                    statusLabel,
                    "\u2193 ${formatBytesPerSecond(rxRate)}  \u2191 ${formatBytesPerSecond(txRate)}",
                )
            }
        }
    }

    private fun buildNotification(text: String, subText: String? = null): Notification {
        val openIntent = PendingIntent.getActivity(
            this, REQUEST_CODE_OPEN, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val disconnectIntent = PendingIntent.getService(
            this, REQUEST_CODE_DISCONNECT, Intent(this, FoxyVpnService::class.java).setAction(ACTION_DISCONNECT),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val stealth = runCatching { SettingsStore(applicationContext).stealthNotificationEnabled }.getOrDefault(false)
        val displayTitle = "FoxyVpn"
        val displayBody = if (stealth) "Protected Tunnel Active" else text

        val builder = NotificationCompat.Builder(this, FoxyVpnApp.VPN_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(displayTitle)
            .setContentText(displayBody)
            .setSmallIcon(R.drawable.ic_notification_foxy)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(if (stealth) NotificationCompat.VISIBILITY_SECRET else NotificationCompat.VISIBILITY_PUBLIC)
            .setLocalOnly(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", disconnectIntent)
        val largeIcon = runCatching {
            BitmapFactory.decodeResource(resources, R.drawable.ic_foxy_logo_round)
        }.getOrNull()
        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon)
        }
        if (subText != null && !stealth) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText("$text\n$subText"))
        }
        return builder.build()
    }

    private fun enterForeground(text: String) {
        foregroundActive = true
        lastNotificationText = text
        val notification = buildNotification(text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            runCatching {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
                )
            }.onFailure {
                runCatching { startForeground(NOTIFICATION_ID, notification) }
            }
        } else {
            runCatching { startForeground(NOTIFICATION_ID, notification) }
        }
    }

    private fun exitForeground() {
        foregroundActive = false
        lastNotificationText = null
        runCatching { stopForeground(STOP_FOREGROUND_REMOVE) }
    }

    private fun updateNotification(text: String, subText: String? = null) {
        if (!foregroundActive) return
        val key = if (subText == null) text else "$text\n$subText"
        if (key == lastNotificationText) return
        lastNotificationText = key
        val manager = getSystemService(NotificationManager::class.java) ?: return
        runCatching { manager.notify(NOTIFICATION_ID, buildNotification(text, subText)) }
    }

    companion object {
        const val ACTION_CONNECT = "com.vauth.foxyvpn.neu.action.CONNECT"
        const val ACTION_DISCONNECT = "com.vauth.foxyvpn.neu.action.DISCONNECT"
        const val ACTION_SWITCH_PROTOCOL = "com.vauth.foxyvpn.neu.action.SWITCH_PROTOCOL"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE_OPEN = 0
        private const val REQUEST_CODE_DISCONNECT = 1

        private val _state = MutableStateFlow(ConnectionState.DISCONNECTED)
        val state: StateFlow<ConnectionState> = _state

        private val _lastError = MutableStateFlow<String?>(null)
        val lastError: StateFlow<String?> = _lastError

        private val _vpnLiveStats = MutableStateFlow(VpnLiveStats())
        val vpnLiveStats: StateFlow<VpnLiveStats> = _vpnLiveStats

        private val _connectedAtEpochMs = MutableStateFlow<Long?>(null)
        val connectedAtEpochMs: StateFlow<Long?> = _connectedAtEpochMs

        private val _pingLatencyMs = MutableStateFlow<Int?>(null)
        val pingLatencyMs: StateFlow<Int?> = _pingLatencyMs

        private val _vpnEdgeIp = MutableStateFlow<IpLocationInfo?>(null)
        val vpnEdgeIp: StateFlow<IpLocationInfo?> = _vpnEdgeIp

        private val _connectedEndpoint = MutableStateFlow<Pair<String, Int>?>(null)
        val connectedEndpoint: StateFlow<Pair<String, Int>?> = _connectedEndpoint

        private val _keepAlivePulseCount = MutableStateFlow(0)
        val keepAlivePulseCount: StateFlow<Int> = _keepAlivePulseCount

        private val _lastKeepAliveAtMs = MutableStateFlow<Long?>(null)
        val lastKeepAliveAtMs: StateFlow<Long?> = _lastKeepAliveAtMs

        private val _idleTimerSeconds = MutableStateFlow(0L)
        val idleTimerSeconds: StateFlow<Long> = _idleTimerSeconds

        private val _activeProtocolBadge = MutableStateFlow("AUTO")
        val activeProtocolBadge: StateFlow<String> = _activeProtocolBadge

        suspend fun pingNow(): Int? {
            val endpoint = _connectedEndpoint.value ?: return null
            val latency = PingUtil.measureTcpLatencyMs(endpoint.first, endpoint.second)
            if (_state.value == ConnectionState.CONNECTED) {
                _pingLatencyMs.value = latency
                if (latency != null) {
                    _keepAlivePulseCount.value += 1
                    _lastKeepAliveAtMs.value = System.currentTimeMillis()
                }
            }
            return latency
        }

        fun switchProtocol(context: Context, mode: com.vauth.foxyvpn.data.ConnectionProtocolMode) {
            SettingsStore(context).connectionProtocolMode = mode
            _activeProtocolBadge.value = mode.shortBadge
            if (_state.value == ConnectionState.CONNECTED || _state.value == ConnectionState.CONNECTING) {
                val intent = Intent(context, FoxyVpnService::class.java).setAction(ACTION_SWITCH_PROTOCOL)
                runCatching { context.startService(intent) }
                    .onFailure { AppLogger.w(TAG, "could not send live protocol switch intent", it) }
            }
        }

        fun start(context: Context) {
            _activeProtocolBadge.value = SettingsStore(context).connectionProtocolMode.shortBadge
            val intent = Intent(context, FoxyVpnService::class.java).setAction(ACTION_CONNECT)
            runCatching {
                context.startService(intent)
            }.onFailure {
                runCatching { ContextCompat.startForegroundService(context, intent) }
                    .onFailure { err -> AppLogger.e(TAG, "could not start FoxyVpnService", err) }
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FoxyVpnService::class.java).setAction(ACTION_DISCONNECT)
            runCatching { context.startService(intent) }
                .onFailure {
                    AppLogger.w(TAG, "could not deliver the disconnect intent; the service is not running", it)
                }
        }
    }
}
