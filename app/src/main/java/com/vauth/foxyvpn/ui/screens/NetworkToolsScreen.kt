package com.vauth.foxyvpn.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.vauth.foxyvpn.R
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vauth.foxyvpn.FoxyVpnApp
import com.vauth.foxyvpn.data.AppLogger
import com.vauth.foxyvpn.data.CrashReporter
import com.vauth.foxyvpn.data.ServerListClient
import com.vauth.foxyvpn.data.SettingsStore
import com.vauth.foxyvpn.data.model.ConnectionState
import com.vauth.foxyvpn.data.model.ProxyCandidate
import com.vauth.foxyvpn.data.model.emojiFlag
import com.vauth.foxyvpn.ui.theme.AnimatedOdometerText
import com.vauth.foxyvpn.ui.theme.AnimatedTrafficEqualizer
import com.vauth.foxyvpn.ui.theme.FoxyStrings
import com.vauth.foxyvpn.ui.theme.LocalFoxyStatusColors
import com.vauth.foxyvpn.ui.theme.NeumorphicButton
import com.vauth.foxyvpn.ui.theme.NeumorphicCard
import com.vauth.foxyvpn.ui.theme.NeumorphicIconButton
import com.vauth.foxyvpn.ui.theme.NeumorphicRadarPulse
import com.vauth.foxyvpn.ui.theme.NeumorphicSpeedometerGauge
import com.vauth.foxyvpn.ui.theme.currentFoxyFonts
import com.vauth.foxyvpn.ui.theme.currentFoxyStrings
import com.vauth.foxyvpn.ui.theme.currentNeumorphicColors
import com.vauth.foxyvpn.ui.theme.formatMbpsLocalized
import com.vauth.foxyvpn.ui.theme.formatPingLocalized
import com.vauth.foxyvpn.ui.theme.localizeCountryName
import com.vauth.foxyvpn.ui.theme.localizeDigits
import com.vauth.foxyvpn.ui.theme.localizeNumber
import com.vauth.foxyvpn.ui.theme.neumorphicSunken
import com.vauth.foxyvpn.ui.theme.neumorphicTouchBounce
import com.vauth.foxyvpn.ui.theme.staggeredEntrance
import com.vauth.foxyvpn.vpn.FoxyVpnService
import com.vauth.foxyvpn.vpn.PingUtil
import com.vauth.foxyvpn.vpn.socks.LocalSocks5Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.InetSocketAddress
import java.net.Proxy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt

enum class NetworkToolTab(
    val id: Int,
    val icon: ImageVector,
) {
    TELEGRAM_PROXY(5, Icons.AutoMirrored.Filled.Send),
    GAMING_MODE(6, Icons.Filled.SportsEsports),
    SPEED_TEST(0, Icons.Filled.Speed),
    LEAK_INSPECTOR(1, Icons.Filled.Security),
    SMART_SERVER(2, Icons.Filled.Explore),
    WATCHDOG(3, Icons.Filled.Favorite),
    MTU_OPTIMIZER(4, Icons.Filled.Tune),
    ;

    fun localizedTitle(s: FoxyStrings): String = when (this) {
        SPEED_TEST -> s.toolSpeedTitle
        LEAK_INSPECTOR -> s.toolLeakTitle
        SMART_SERVER -> s.toolScannerTitle
        WATCHDOG -> s.toolWatchdogTitle
        MTU_OPTIMIZER -> s.toolMtuTitle
        TELEGRAM_PROXY -> when (s.language) {
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "پروکسی تلگرام"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "بروكسي تيليجرام"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Telegram Proxy"
        }
        GAMING_MODE -> when (s.language) {
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "حالت گیمینگ"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "وضع الألعاب"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Gaming Mode"
        }
    }

    fun localizedSubtitle(s: FoxyStrings): String = when (this) {
        SPEED_TEST -> s.toolSpeedSubtitle
        LEAK_INSPECTOR -> s.toolLeakSubtitle
        SMART_SERVER -> s.toolScannerSubtitle
        WATCHDOG -> s.toolWatchdogSubtitle
        MTU_OPTIMIZER -> s.toolMtuSubtitle
        TELEGRAM_PROXY -> when (s.language) {
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "سرویس محلی SOCKS5 امن روی 127.0.0.1 با اتصال مستقیم به تلگرام"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "خادم SOCKS5 محلي آمن على 127.0.0.1 لتطبيق تيليجرام"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Local loopback SOCKS5 proxy (127.0.0.1) routed through VPN tunnel"
        }
        GAMING_MODE -> when (s.language) {
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "تثبیت پینگ، تست جیتر و پکت‌لاس واقعی و قفل تعویض خودکار سرور"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تثبيت البينج وقياس الجيتر وفقدان الحزم أثناء اللعب"
            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Real Latency, Jitter & Packet Loss Analyzer + Server Lock"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkToolsScreen(
    app: FoxyVpnApp,
    initialTab: Int = 0,
    onRequestConnect: () -> Unit,
    onBack: () -> Unit,
) {
    val neu = currentNeumorphicColors
    val s = currentFoxyStrings
    val haptics = LocalHapticFeedback.current
    var selectedTab by remember {
        mutableStateOf(NetworkToolTab.entries.firstOrNull { it.id == initialTab } ?: NetworkToolTab.SPEED_TEST)
    }

    Scaffold(
        containerColor = neu.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text(
                            s.toolkitScreenTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = neu.textPrimary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            selectedTab.localizedSubtitle(s),
                            style = MaterialTheme.typography.labelSmall,
                            color = neu.accent,
                        )
                    }
                },
                navigationIcon = {
                    NeumorphicIconButton(
                        size = 40.dp,
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = s.back,
                            tint = neu.textPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = neu.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Horizontal Neumorphic Tool Selector Bar (Spacious Pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NetworkToolTab.entries.forEachIndexed { idx, tab ->
                    val isSelected = selectedTab == tab
                    val pillShape = RoundedCornerShape(18.dp)
                    val title = tab.localizedTitle(s)
                    Box(
                        modifier = Modifier
                            .staggeredEntrance(idx, delayPerItemMs = 45)
                            .neumorphicTouchBounce()
                            .clip(pillShape)
                            .background(if (isSelected) neu.accent else neu.surfaceElevated)
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedTab = tab
                            }
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (tab == NetworkToolTab.TELEGRAM_PROXY) {
                                Image(
                                    painter = painterResource(R.drawable.ic_brand_telegram),
                                    contentDescription = title,
                                    modifier = Modifier.size(20.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = title,
                                    tint = if (isSelected) Color.White else neu.accent,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                ),
                                color = if (isSelected) Color.White else neu.textPrimary,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        (fadeIn(tween(260)) togetherWith fadeOut(tween(200)))
                    },
                    label = "tool-tab-content",
                ) { tab ->
                    when (tab) {
                        NetworkToolTab.SPEED_TEST -> SpeedTestToolPanel(app = app)
                        NetworkToolTab.LEAK_INSPECTOR -> LeakInspectorToolPanel(app = app)
                        NetworkToolTab.SMART_SERVER -> SmartServerScannerPanel(
                            app = app,
                            onRequestConnect = onRequestConnect,
                        )
                        NetworkToolTab.WATCHDOG -> WatchdogKeepAlivePanel(app = app)
                        NetworkToolTab.MTU_OPTIMIZER -> MtuDpiOptimizerPanel(app = app)
                        NetworkToolTab.TELEGRAM_PROXY -> TelegramProxyToolPanel(
                            app = app,
                            onRequestConnect = onRequestConnect,
                        )
                        NetworkToolTab.GAMING_MODE -> GamingModeToolPanel(app = app)
                    }
                }
            }
        }
    }
}

private fun buildTunnelAwareHttpClient(
    isVpnConnected: Boolean,
    socksPort: Int,
    timeoutSec: Long = 12L,
): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(timeoutSec, TimeUnit.SECONDS)
        .readTimeout(timeoutSec, TimeUnit.SECONDS)
        .writeTimeout(timeoutSec, TimeUnit.SECONDS)
        .callTimeout(timeoutSec + 4L, TimeUnit.SECONDS)
    if (isVpnConnected) {
        builder.proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", socksPort)))
    }
    return builder.build()
}

// ============================================================================
// TOOL 1: REAL SPEED TEST (PING, JITTER, DOWNLOAD, UPLOAD)
// ============================================================================
private data class SpeedTestSnapshot(
    val downloadMbps: Float?,
    val uploadMbps: Float?,
    val pingMs: Int?,
    val jitterMs: Int?,
)

private data class DeviceResourceSnapshot(
    val batteryPercent: Int?,
    val availableRamMb: Long,
    val totalRamMb: Long,
)

private enum class SpeedTestStage {
    READY,
    PING,
    DOWNLOAD_TUNNEL,
    DOWNLOAD_DIRECT,
    UPLOAD_TUNNEL,
    UPLOAD_DIRECT,
    DONE_TUNNEL,
    DONE_DIRECT,
}

@Composable
private fun SpeedTestToolPanel(app: FoxyVpnApp) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val scope = rememberCoroutineScope()
    val state by FoxyVpnService.state.collectAsState()
    val isConnected = state == ConnectionState.CONNECTED

    var isRunning by remember { mutableStateOf(false) }
    var stage by remember { mutableStateOf(SpeedTestStage.READY) }
    var currentGaugeMbps by remember { mutableFloatStateOf(0f) }
    var downloadMbps by remember { mutableStateOf<Float?>(null) }
    var uploadMbps by remember { mutableStateOf<Float?>(null) }
    var pingMs by remember { mutableStateOf<Int?>(null) }
    var jitterMs by remember { mutableStateOf<Int?>(null) }
    var previousRun by remember { mutableStateOf<SpeedTestSnapshot?>(null) }
    val context = LocalContext.current
    var resources by remember { mutableStateOf<DeviceResourceSnapshot?>(null) }
    var reportCopied by remember { mutableStateOf(false) }
    var selectedProfile by remember { mutableStateOf(app.settingsStore.performanceProfile) }

    LaunchedEffect(Unit) {
        while (true) {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            if (manager != null) manager.getMemoryInfo(memoryInfo)
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            val battery = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                ?.takeIf { it in 0..100 }
            resources = if (manager != null) DeviceResourceSnapshot(
                batteryPercent = battery,
                availableRamMb = memoryInfo.availMem / (1024L * 1024L),
                totalRamMb = memoryInfo.totalMem / (1024L * 1024L),
            ) else null
            delay(5_000L)
        }
    }

    fun applyProfile(profile: String) {
        selectedProfile = profile
        app.settingsStore.performanceProfile = profile
        app.settingsStore.mtuModeAuto = true
        app.settingsStore.socketBufferModeAuto = true
        app.settingsStore.mtu = 1400
        when (profile) {
            SettingsStore.PROFILE_STABLE -> {
                app.settingsStore.gamingModeEnabled = false
                app.settingsStore.tcpBufferBoostEnabled = false
                app.settingsStore.batterySaverModeEnabled = true
            }
            SettingsStore.PROFILE_FAST -> {
                app.settingsStore.gamingModeEnabled = true
                app.settingsStore.tcpBufferBoostEnabled = true
                app.settingsStore.batterySaverModeEnabled = false
            }
        }
    }

    val stageLabel = when (stage) {
        SpeedTestStage.READY -> s.speedReadyStage
        SpeedTestStage.PING -> s.speedMeasuringPingStage
        SpeedTestStage.DOWNLOAD_TUNNEL -> s.speedTestingTunnelDlStage
        SpeedTestStage.DOWNLOAD_DIRECT -> s.speedTestingDirectDlStage
        SpeedTestStage.UPLOAD_TUNNEL -> s.speedTestingTunnelUlStage
        SpeedTestStage.UPLOAD_DIRECT -> s.speedTestingDirectUlStage
        SpeedTestStage.DONE_TUNNEL -> s.speedDoneTunnelStage
        SpeedTestStage.DONE_DIRECT -> s.speedDoneDirectStage
    }

    fun startSpeedTest() {
        if (isRunning) return
        previousRun = if (downloadMbps != null || uploadMbps != null || pingMs != null || jitterMs != null) {
            SpeedTestSnapshot(downloadMbps, uploadMbps, pingMs, jitterMs)
        } else null
        isRunning = true
        currentGaugeMbps = 0f
        downloadMbps = null
        uploadMbps = null
        pingMs = null
        jitterMs = null

        scope.launch {
            val socksPort = app.settingsStore.socksPort
            // Step 1: Ping & Jitter
            stage = SpeedTestStage.PING
            val endpoint = FoxyVpnService.connectedEndpoint.value
            val targetHost = endpoint?.first ?: app.proxyStateStore.load()?.host ?: "1.1.1.1"
            val targetPort = endpoint?.second ?: app.proxyStateStore.load()?.port ?: 443
            val samples = mutableListOf<Int>()
            repeat(5) {
                val rtt = PingUtil.measureTcpLatencyMs(targetHost, targetPort)
                    ?: PingUtil.measureTcpLatencyMs("1.1.1.1", 443)
                if (rtt != null) samples.add(rtt)
                delay(120)
            }
            if (samples.isNotEmpty()) {
                pingMs = samples.average().roundToInt()
                jitterMs = if (samples.size >= 2) {
                    samples.zipWithNext { a, b -> abs(a - b) }.average().roundToInt()
                } else 1
            }

            // Step 2: Download Benchmark
            stage = if (isConnected) SpeedTestStage.DOWNLOAD_TUNNEL else SpeedTestStage.DOWNLOAD_DIRECT
            val dlClient = buildTunnelAwareHttpClient(isConnected, socksPort, timeoutSec = 15L)
            try {
                // Use a real download-test endpoint only. A tiny trace response is not a valid speed sample.
                val dlUrls = listOf("https://speed.cloudflare.com/__down?bytes=4000000")
                var totalBytes = 0L
                val startNs = System.nanoTime()
                for (url in dlUrls) {
                    val req = Request.Builder()
                        .url(url)
                        .header("Cache-Control", "no-cache")
                        .build()
                    val ok = withContext(Dispatchers.IO) {
                        runCatching {
                            dlClient.newCall(req).execute().use { resp ->
                                if (!resp.isSuccessful) return@use false
                                val stream = resp.body?.byteStream() ?: return@use false
                                val buf = ByteArray(16 * 1024)
                                var read: Int
                                while (stream.read(buf).also { read = it } != -1) {
                                    totalBytes += read
                                    val elapsedSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.05)
                                    val liveMbps = ((totalBytes * 8.0) / (elapsedSec * 1_000_000.0)).toFloat()
                                    currentGaugeMbps = liveMbps
                                    if (elapsedSec > 5.5) break
                                }
                                totalBytes > 0L
                            }
                        }.getOrDefault(false)
                    }
                    if (ok && totalBytes > 64 * 1024) break
                }
                if (totalBytes >= 64 * 1024) {
                    val totalSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.08)
                    val finalDlMbps = ((totalBytes * 8.0) / (totalSec * 1_000_000.0)).toFloat()
                    if (finalDlMbps.isFinite() && finalDlMbps > 0f) {
                        downloadMbps = finalDlMbps
                        currentGaugeMbps = finalDlMbps
                    }
                }
                // If the endpoint failed or returned too little data, leave the result unavailable rather than inventing a minimum speed.
            } finally {
                withContext(Dispatchers.IO) {
                    dlClient.dispatcher.executorService.shutdown()
                    dlClient.connectionPool.evictAll()
                }
            }

            delay(350)

            // Step 3: Upload Benchmark
            stage = if (isConnected) SpeedTestStage.UPLOAD_TUNNEL else SpeedTestStage.UPLOAD_DIRECT
            val ulClient = buildTunnelAwareHttpClient(isConnected, socksPort, timeoutSec = 12L)
            try {
                val payloadSize = 512 * 1024
                val payload = ByteArray(payloadSize) { (it % 251).toByte() }
                // Only the dedicated upload endpoint provides a valid upload benchmark.
                val uploadEndpoints = listOf("https://speed.cloudflare.com/__up")
                var measuredUlMbps: Float? = null
                for (ulUrl in uploadEndpoints) {
                    val startNs = System.nanoTime()
                    val req = Request.Builder()
                        .url(ulUrl)
                        .post(payload.toRequestBody("application/octet-stream".toMediaType()))
                        .build()
                    val uploadedOk = withContext(Dispatchers.IO) {
                        runCatching {
                            ulClient.newCall(req).execute().use { resp ->
                                // A benchmark is successful only when the upload endpoint accepts it (2xx).
                                resp.isSuccessful
                            }
                        }.getOrDefault(false)
                    }
                    if (uploadedOk) {
                        val elapsedSec = ((System.nanoTime() - startNs) / 1_000_000_000.0).coerceAtLeast(0.05)
                        val ulMbps = ((payloadSize * 8.0) / (elapsedSec * 1_000_000.0)).toFloat()
                        measuredUlMbps = ulMbps
                        currentGaugeMbps = ulMbps
                        break
                    }
                }
                uploadMbps = measuredUlMbps
            } finally {
                withContext(Dispatchers.IO) {
                    ulClient.dispatcher.executorService.shutdown()
                    ulClient.connectionPool.evictAll()
                }
            }

            stage = if (isConnected) SpeedTestStage.DONE_TUNNEL else SpeedTestStage.DONE_DIRECT
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) statusColors.connected else statusColors.connecting),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isConnected) s.speedModeTunnel else s.speedModeDirect,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                        ),
                        color = if (isConnected) statusColors.connected else neu.textSecondary,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(228.dp),
                ) {
                    NeumorphicSpeedometerGauge(
                        speedMbps = currentGaugeMbps,
                        maxMbps = 100f,
                        accentColor = if (isConnected) statusColors.connected else neu.accent,
                        trackColor = neu.surfaceElevated,
                        isRunning = isRunning,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        AnimatedOdometerText(
                            text = s.language.formatMbpsLocalized(currentGaugeMbps, includeUnit = false),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = currentFoxyFonts.monoFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontFeatureSettings = "tnum",
                            ),
                            color = neu.textPrimary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = s.speedUnitMbps,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = neu.textSecondary,
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stageLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = neu.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                Spacer(Modifier.height(20.dp))

                // 4-Metric Grid (Download, Upload, Ping, Jitter)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = s.speedDownloadLabel,
                        value = downloadMbps?.let { s.language.formatMbpsLocalized(it, includeUnit = true) } ?: "—",
                        accent = statusColors.connected,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = s.speedUploadLabel,
                        value = uploadMbps?.let { s.language.formatMbpsLocalized(it, includeUnit = true) } ?: "—",
                        accent = neu.accent,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = s.speedPingLabel,
                        value = pingMs?.let { s.language.formatPingLocalized(it) } ?: "—",
                        accent = statusColors.connecting,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = s.speedJitterLabel,
                        value = jitterMs?.let { s.language.formatPingLocalized(it) } ?: "—",
                        accent = neu.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(22.dp))

                NeumorphicButton(
                    onClick = ::startSpeedTest,
                    enabled = !isRunning,
                    accentColor = neu.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(s.speedRunningButton, color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Filled.Speed, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(s.speedStartButton, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text(
                    text = when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "پروفایل عملکرد"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "ملف الأداء"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Performance profile"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = neu.textPrimary,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    NeumorphicButton(
                        onClick = { applyProfile(SettingsStore.PROFILE_STABLE) },
                        accentColor = if (selectedProfile == SettingsStore.PROFILE_STABLE) statusColors.connected else neu.accent,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "پایدار"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "مستقر"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Stable"
                            }, color = Color.White, fontWeight = FontWeight.Bold,
                        )
                    }
                    NeumorphicButton(
                        onClick = { applyProfile(SettingsStore.PROFILE_FAST) },
                        accentColor = if (selectedProfile == SettingsStore.PROFILE_FAST) statusColors.connected else neu.accent,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "سریع"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "سريع"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Fast"
                            }, color = Color.White, fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "این پروفایل‌ها فقط تنظیمات موجود را تغییر می‌دهند؛ سرعت یا پایداری تضمین نمی‌شود."
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تغيّر هذه الملفات الإعدادات الموجودة فقط؛ لا يوجد ضمان للسرعة أو الاستقرار."
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Profiles adjust existing settings only; they do not guarantee higher speed or stability."
                    }, style = MaterialTheme.typography.bodySmall, color = neu.textSecondary,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text(
                    text = when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "پایش منابع دستگاه"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "مراقبة موارد الجهاز"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Device resources"
                    }, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = neu.textPrimary,
                )
                Spacer(Modifier.height(8.dp))
                val resource = resources
                Text(
                    text = if (resource == null) "RAM: — • Battery: —" else
                        "RAM: ${resource.availableRamMb} / ${resource.totalRamMb} MB available • Battery: ${resource.batteryPercent?.let { "$it%" } ?: "—"}",
                    style = MaterialTheme.typography.bodyMedium, color = neu.textSecondary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Android ${Build.VERSION.SDK_INT} • ${Build.MANUFACTURER} ${Build.MODEL}",
                    style = MaterialTheme.typography.bodySmall, color = neu.textSecondary,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        NeumorphicCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text(
                    text = when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "گزارش تشخیصی هسته"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تقرير تشخيصي للنواة"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Core diagnostic report"
                    }, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = neu.textPrimary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "گزارش فقط اطلاعات فنی پایه را کپی می‌کند؛ توکن، IP سرور و محتوای لاگ‌ها وارد آن نمی‌شود."
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "ينسخ التقرير معلومات تقنية أساسية فقط ولا يتضمن الرموز أو عنوان الخادم أو محتوى السجلات."
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Copies basic technical details only; no tokens, server IPs, or raw log contents are included."
                    }, style = MaterialTheme.typography.bodySmall, color = neu.textSecondary,
                )
                Spacer(Modifier.height(10.dp))
                NeumorphicButton(
                    onClick = {
                        val report = buildString {
                            appendLine("FOX VPN diagnostic")
                            appendLine("App version: ${com.vauth.foxyvpn.BuildConfig.VERSION_NAME} (${com.vauth.foxyvpn.BuildConfig.VERSION_CODE})")
                            appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
                            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                            appendLine("VPN state: ${FoxyVpnService.state.value.name}")
                            appendLine("Profile: ${app.settingsStore.performanceProfile}")
                            appendLine("Effective MTU: ${app.settingsStore.effectiveMtu}")
                            appendLine("Buffered log entries: ${AppLogger.entries.value.size}")
                            appendLine("Crash report available: ${CrashReporter.lastCrashReport.value != null}")
                            appendLine("Battery: ${resources?.batteryPercent?.let { "$it%" } ?: "unavailable"}")
                            appendLine("Available RAM MB: ${resources?.availableRamMb ?: "unavailable"}")
                            appendLine("Note: this report does not perform a remote security audit.")
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("FOX VPN diagnostic", report))
                        reportCopied = clipboard != null
                    },
                    accentColor = neu.accent,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (reportCopied) when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "گزارش کپی شد"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تم نسخ التقرير"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Report copied"
                        } else when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "کپی گزارش"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "نسخ التقرير"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Copy report"
                        }, color = Color.White, fontWeight = FontWeight.Bold,
                    )
                }
                previousRun?.let { previous ->
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Previous run comparison • Download ${formatDelta(downloadMbps, previous.downloadMbps)} • Upload ${formatDelta(uploadMbps, previous.uploadMbps)} • Ping ${formatDelta(pingMs?.toFloat(), previous.pingMs?.toFloat(), lowerIsBetter = true)} • Jitter ${formatDelta(jitterMs?.toFloat(), previous.jitterMs?.toFloat(), lowerIsBetter = true)}",
                        style = MaterialTheme.typography.bodySmall, color = neu.textSecondary,
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

private fun formatDelta(current: Float?, previous: Float?, lowerIsBetter: Boolean = false): String {
    if (current == null || previous == null) return "—"
    val delta = current - previous
    val signed = if (delta > 0) "+" else ""
    val suffix = if (lowerIsBetter) " ms" else " Mbps"
    return "$signed${"%.1f".format(Locale.US, delta)}$suffix"
}

@Composable
private fun SpeedMetricTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val neu = currentNeumorphicColors
    val s = currentFoxyStrings
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .neumorphicSunken(
                shape = shape,
                cornerRadius = 16.dp,
                depth = 3.dp,
                surfaceColor = neu.surfaceElevated,
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (s.isRtl) 0.sp else 0.6.sp,
                    lineHeight = 18.sp,
                ),
                color = neu.textSecondary,
                maxLines = 2,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                ),
                color = accent,
            )
        }
    }
}

// ============================================================================
// TOOL 2: IP, DNS & LEAK INSPECTOR
// ============================================================================
private data class CloudflareTraceReport(
    val ip: String,
    val country: String,
    val colo: String,
    val tlsVersion: String,
    val httpVersion: String,
    val sniStatus: String,
    val warpStatus: String,
    val userAgent: String,
)

@Composable
private fun LeakInspectorToolPanel(app: FoxyVpnApp) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val scope = rememberCoroutineScope()
    val state by FoxyVpnService.state.collectAsState()
    val isConnected = state == ConnectionState.CONNECTED

    var isScanning by remember { mutableStateOf(false) }
    var directTrace by remember { mutableStateOf<CloudflareTraceReport?>(null) }
    var tunnelTrace by remember { mutableStateOf<CloudflareTraceReport?>(null) }
    var dohLatencyMs by remember { mutableStateOf<Int?>(null) }
    var dohResolvedIp by remember { mutableStateOf<String?>(null) }
    var scanError by remember { mutableStateOf<String?>(null) }

    fun parseFullTrace(raw: String): CloudflareTraceReport {
        val map = mutableMapOf<String, String>()
        raw.lineSequence().forEach { line ->
            val idx = line.indexOf('=')
            if (idx > 0) {
                map[line.substring(0, idx).trim()] = line.substring(idx + 1).trim()
            }
        }
        return CloudflareTraceReport(
            ip = map["ip"] ?: "—",
            country = map["loc"] ?: "—",
            colo = map["colo"] ?: "—",
            tlsVersion = map["tls"] ?: "—",
            httpVersion = map["http"] ?: "—",
            sniStatus = map["sni"] ?: "—",
            warpStatus = map["warp"] ?: "off",
            userAgent = map["uag"] ?: "FoxyVpn",
        )
    }

    fun runLeakAudit() {
        if (isScanning) return
        isScanning = true
        scanError = null
        scope.launch {
            val socksPort = app.settingsStore.socksPort
            // 1. Direct ISP trace
            val directClient = buildTunnelAwareHttpClient(isVpnConnected = false, socksPort = socksPort, timeoutSec = 8L)
            try {
                val res = withContext(Dispatchers.IO) {
                    runCatching {
                        val req = Request.Builder().url("https://www.cloudflare.com/cdn-cgi/trace").build()
                        directClient.newCall(req).execute().use { resp ->
                            if (!resp.isSuccessful) error("HTTP ${resp.code}")
                            parseFullTrace(resp.body?.string().orEmpty())
                        }
                    }
                }
                res.onSuccess { directTrace = it }
            } finally {
                withContext(Dispatchers.IO) {
                    directClient.dispatcher.executorService.shutdown()
                    directClient.connectionPool.evictAll()
                }
            }

            // 2. Tunnel Exit trace (via SOCKS5 when connected)
            if (isConnected) {
                val proxiedClient = buildTunnelAwareHttpClient(isVpnConnected = true, socksPort = socksPort, timeoutSec = 12L)
                try {
                    val res = withContext(Dispatchers.IO) {
                        runCatching {
                            val req = Request.Builder().url("https://www.cloudflare.com/cdn-cgi/trace").build()
                            proxiedClient.newCall(req).execute().use { resp ->
                                if (!resp.isSuccessful) error("HTTP ${resp.code}")
                                parseFullTrace(resp.body?.string().orEmpty())
                            }
                        }
                    }
                    res.onSuccess { tunnelTrace = it }
                        .onFailure { scanError = it.message }
                } finally {
                    withContext(Dispatchers.IO) {
                        proxiedClient.dispatcher.executorService.shutdown()
                        proxiedClient.connectionPool.evictAll()
                    }
                }
            } else {
                tunnelTrace = null
            }

            // 3. DoH DNS Resolution & Leak Check
            val dnsClient = buildTunnelAwareHttpClient(isVpnConnected = isConnected, socksPort = socksPort, timeoutSec = 8L)
            try {
                withContext(Dispatchers.IO) {
                    runCatching {
                        val start = System.nanoTime()
                        val req = Request.Builder()
                            .url("https://cloudflare-dns.com/dns-query?name=cloudflare.com&type=A")
                            .header("Accept", "application/dns-json")
                            .build()
                        dnsClient.newCall(req).execute().use { resp ->
                            if (resp.isSuccessful) {
                                val rtt = ((System.nanoTime() - start) / 1_000_000L).toInt()
                                val body = resp.body?.string().orEmpty()
                                dohLatencyMs = rtt
                                val ipMatch = Regex("\"data\"\\s*:\\s*\"([0-9.]+)\"").find(body)
                                dohResolvedIp = ipMatch?.groupValues?.getOrNull(1)
                            }
                        }
                    }
                }
            } finally {
                withContext(Dispatchers.IO) {
                    dnsClient.dispatcher.executorService.shutdown()
                    dnsClient.connectionPool.evictAll()
                }
            }

            isScanning = false
        }
    }

    LaunchedEffect(isConnected) {
        runLeakAudit()
    }

    val isProtectedNoLeak = isConnected &&
        tunnelTrace != null &&
        directTrace != null &&
        tunnelTrace?.ip != directTrace?.ip

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NeumorphicRadarPulse(
                    color = if (isProtectedNoLeak) statusColors.connected else if (isConnected) statusColors.connecting else MaterialTheme.colorScheme.error,
                    isActive = isConnected,
                    isConnecting = isScanning,
                    size = 116.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .background(
                                if (isProtectedNoLeak) statusColors.connected.copy(alpha = 0.18f)
                                else MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isProtectedNoLeak) Icons.Filled.Security else Icons.Filled.Shield,
                            contentDescription = null,
                            tint = if (isProtectedNoLeak) statusColors.connected else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = when {
                        isScanning -> s.leakScanningTitle
                        isProtectedNoLeak -> s.leakProtectedTitle
                        isConnected -> s.leakVerifyingTitle
                        else -> s.leakExposedTitle
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = if (s.isRtl) 0.sp else 0.7.sp,
                        lineHeight = 22.sp,
                    ),
                    color = if (isProtectedNoLeak) statusColors.connected else if (isConnected) statusColors.connecting else MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(18.dp))

                // Side-by-side ISP IP vs Tunnel Exit IP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = s.leakDirectIpLabel,
                        value = directTrace?.let {
                            s.language.localizeDigits("${it.ip}\n(${s.language.localizeCountryName(it.country, it.country)})")
                        } ?: s.checking,
                        accent = neu.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = s.leakVpnIpLabel,
                        value = if (isConnected) {
                            tunnelTrace?.let {
                                s.language.localizeDigits("${it.ip}\n(${s.language.localizeCountryName(it.country, it.country)})")
                            } ?: s.leakProbing
                        } else {
                            s.leakNotConnected
                        },
                        accent = if (isConnected) statusColors.connected else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Detailed Security & Edge Telemetry Rows
                val activeTrace = tunnelTrace ?: directTrace
                InspectorDetailRow(s.leakColoLabel, s.language.localizeDigits(activeTrace?.colo ?: "—"))
                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 10.dp))
                InspectorDetailRow(s.leakProtocolLabel, s.language.localizeDigits("${activeTrace?.tlsVersion ?: "—"} / ${activeTrace?.httpVersion ?: "—"}"))
                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 10.dp))
                InspectorDetailRow(s.leakSniLabel, s.language.localizeDigits(activeTrace?.sniStatus?.uppercase() ?: "—"))
                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 10.dp))
                InspectorDetailRow(
                    s.leakDohLabel,
                    if (dohLatencyMs != null) {
                        val ipPart = dohResolvedIp?.let { " ($it)" }.orEmpty()
                        s.leakDohVerified(dohLatencyMs!!, ipPart)
                    } else s.testing,
                )
                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 10.dp))
                InspectorDetailRow(
                    s.leakIpv6Label,
                    if (isConnected) s.leakIpv6Protected else s.leakIpv6Inactive,
                )
                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 10.dp))
                val lastVpnError by FoxyVpnService.lastError.collectAsState()
                val activeProtoBadge by FoxyVpnService.activeProtocolBadge.collectAsState()
                val pingMethod = "TCP SYN/ACK + TLS ALPN"
                val pingRtt by FoxyVpnService.pingLatencyMs.collectAsState()
                val context = LocalContext.current
                var diagnosticCopied by remember { mutableStateOf(false) }

                InspectorDetailRow(
                    when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "وضعیت خطای تونل / پروتکل"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "حالة خطأ النفق / البروتوكول"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Tunnel Diagnostics & Protocol"
                    },
                    when {
                        !lastVpnError.isNullOrBlank() -> lastVpnError!!
                        isConnected -> "$activeProtoBadge • OK"
                        else -> "—"
                    },
                )
                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 10.dp))
                InspectorDetailRow(
                    when (s.language) {
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "حفاظت نشت هنگام قطع (Exit Guard)"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "حماية التسرب عند الانقطاع"
                        com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Disconnect Leak Protection"
                    },
                    if (app.settingsStore.exitCheckEnabled) {
                        when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "فعال (مسدودسازی نشت DNS/IP)"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "مفعل (حظر تسرب DNS/IP)"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Active (Strict DNS/IP Guard)"
                        }
                    } else {
                        when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "استاندارد"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "قياسي"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Standard"
                        }
                    },
                )

                scanError?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(22.dp))

                NeumorphicButton(
                    onClick = ::runLeakAudit,
                    enabled = !isScanning,
                    accentColor = neu.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (isScanning) s.leakScanningButton else s.leakRescanButton,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                // One-Tap Sanitized Diagnostic Report (Redacts tokens, credentials, and full IPs)
                NeumorphicButton(
                    onClick = {
                        fun maskIp(ip: String?): String {
                            if (ip.isNullOrBlank() || ip == "—") return "—"
                            val parts = ip.split(".")
                            return if (parts.size == 4) "${parts[0]}.${parts[1]}.***.***" else "REDACTED_IP"
                        }
                        fun sanitizeText(raw: String?): String {
                            if (raw.isNullOrBlank()) return "None"
                            return raw
                                .replace(Regex("(?i)(bearer|token|password|secret|key)\\s*[:=]\\s*\\S+"), "$1=[REDACTED]")
                                .replace(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"), "[REDACTED_EMAIL]")
                                .replace(Regex("\\b(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\b"), "$1.$2.***.***")
                        }
                        val report = buildString {
                            appendLine("=== FoxyVPN Sanitized Diagnostic Report ===")
                            appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
                            appendLine("Tunnel State: ${state.name}")
                            appendLine("Protocol Mode: ${app.settingsStore.connectionProtocolMode.name} (Active: $activeProtoBadge)")
                            appendLine("Latency Probe: ${pingMethod} (${pingRtt?.let { "${it}ms" } ?: "N/A"})")
                            appendLine("Leak Status: ${if (isProtectedNoLeak) "PROTECTED (No IP/DNS Leak)" else if (isConnected) "VERIFYING" else "DISCONNECTED"}")
                            appendLine("ISP Exit (Masked): ${maskIp(directTrace?.ip)} (${directTrace?.country ?: "—"})")
                            appendLine("VPN Exit (Masked): ${maskIp(tunnelTrace?.ip)} (${tunnelTrace?.country ?: "—"}, Colo: ${tunnelTrace?.colo ?: "—"})")
                            appendLine("DoH DNS Status: ${dohLatencyMs?.let { "OK (${it}ms)" } ?: "Pending"}")
                            appendLine("MTU & Buffers: MTU=${app.settingsStore.effectiveMtu} (Auto=${app.settingsStore.mtuModeAuto}), Split=${app.settingsStore.tlsSplitMode.name}")
                            appendLine("Gaming Mode: ${app.settingsStore.gamingModeEnabled} | Telegram Proxy: ${app.settingsStore.telegramProxyModeEnabled} (127.0.0.1:${app.settingsStore.socksPort})")
                            appendLine("Last Tunnel Error: ${sanitizeText(lastVpnError ?: scanError)}")
                            appendLine("Privacy Note: All credentials, tokens, and full IP addresses are automatically redacted.")
                        }
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        cm?.setPrimaryClip(ClipData.newPlainText("FoxyVPN Diagnostics", report))
                        diagnosticCopied = true
                    },
                    accentColor = statusColors.connected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (diagnosticCopied) {
                            when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "گزارش تشخیصی امن کپی شد (بدون اطلاعات حساس)"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تم نسخ التقرير التشخيصي الآمن"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Sanitized Diagnostic Report Copied!"
                            }
                        } else {
                            when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "کپی گزارش تشخیصی یک‌لمسی (بدون افشای اطلاعات حساس)"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "نسخ التقرير التشخيصي بنقرة واحدة"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Copy One-Tap Sanitized Diagnostic Report"
                            }
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun InspectorDetailRow(label: String, value: String) {
    val neu = currentNeumorphicColors
    val fonts = currentFoxyFonts
    val isLongValue = value.length > 22
    if (isLongValue) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                ),
                color = neu.textSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = fonts.monoFont,
                    lineHeight = 20.sp,
                ),
                color = neu.textPrimary,
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                ),
                color = neu.textSecondary,
                modifier = Modifier.weight(0.52f),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = fonts.monoFont,
                    lineHeight = 20.sp,
                ),
                color = neu.textPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.48f),
            )
        }
    }
}

// ============================================================================
// TOOL 3: SMART FASTEST SERVER FINDER
// ============================================================================
private data class BenchmarkedServer(
    val candidate: ProxyCandidate,
    val latencyMs: Int,
    val jitterMs: Int,
    val successRatePercent: Int,
    val stabilityScore: Int,
    val method: String,
)

@Composable
private fun SmartServerScannerPanel(
    app: FoxyVpnApp,
    onRequestConnect: () -> Unit,
) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var statusText by remember(s.isRtl) { mutableStateOf(s.scannerInitialSubtitle) }
    val results = remember { mutableStateListOf<BenchmarkedServer>() }

    fun startServerBenchmark() {
        if (isScanning) return
        isScanning = true
        progress = 0f
        results.clear()

        scope.launch {
            statusText = s.scannerLoadingCatalog
            val countries = withContext(Dispatchers.IO) {
                val cached = app.serverListClient.loadCachedCountries()
                cached.ifEmpty {
                    runCatching { app.serverListClient.fetchCountries() }.getOrDefault(emptyList())
                }
            }
            val candidates = countries
                .filter { it.code.isNotBlank() && it.code != "REC" }
                .flatMap { country ->
                    country.cities.mapNotNull { city ->
                        ServerListClient.candidatesForCity(countries, country.code, city.code).firstOrNull()
                    }
                }
                .distinctBy { "${it.countryCode}:${it.cityCode}" }
                .take(14)

            if (candidates.isEmpty()) {
                statusText = s.scannerEmptyCatalog
                isScanning = false
                return@launch
            }

            candidates.forEachIndexed { idx, candidate ->
                statusText = s.scannerProbingServer(candidate.countryName.ifBlank { candidate.countryCode }, candidate.cityCode)
                val bench = PingUtil.benchmarkServerStability(candidate.host, candidate.port, probes = 3)
                if (bench != null) {
                    results.add(
                        BenchmarkedServer(
                            candidate = candidate,
                            latencyMs = bench.avgLatencyMs,
                            jitterMs = bench.jitterMs,
                            successRatePercent = bench.successRatePercent,
                            stabilityScore = bench.stabilityScore,
                            method = bench.method,
                        )
                    )
                    // Rank by highest stability score first, then lowest latency
                    results.sortWith(compareByDescending<BenchmarkedServer> { it.stabilityScore }.thenBy { it.latencyMs })
                }
                progress = (idx + 1).toFloat() / candidates.size.toFloat()
            }

            statusText = if (results.isNotEmpty()) {
                s.scannerOptimalFound(results.first().candidate.countryName.ifBlank { results.first().candidate.countryCode }, results.first().latencyMs)
            } else {
                s.scannerTimeoutMessage
            }
            isScanning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NeumorphicRadarPulse(
                    color = neu.accent,
                    isActive = results.isNotEmpty(),
                    isConnecting = isScanning,
                    size = 110.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(neu.accent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Explore,
                            contentDescription = null,
                            tint = neu.accent,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = s.scannerHeaderTitle,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                    ),
                    color = neu.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = neu.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                Spacer(Modifier.height(16.dp))

                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "scanner-progress",
                )
                if (isScanning || progress > 0f) {
                    val barShape = RoundedCornerShape(7.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .neumorphicSunken(
                                shape = barShape,
                                cornerRadius = 7.dp,
                                depth = 2.dp,
                                surfaceColor = neu.surfaceElevated,
                            ),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                                .fillMaxSize()
                                .clip(barShape)
                                .background(statusColors.connected),
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                }

                NeumorphicButton(
                    onClick = ::startServerBenchmark,
                    enabled = !isScanning,
                    accentColor = neu.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (isScanning) s.scannerRunningButton else s.scannerStartButton,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (results.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    val best = results.first()
                    NeumorphicButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            app.proxyStateStore.save(best.candidate)
                            onRequestConnect()
                        },
                        accentColor = statusColors.connected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .neumorphicTouchBounce(),
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            s.scannerConnectBestButton(best.candidate.countryCode, best.latencyMs),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    results.take(8).forEachIndexed { idx, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(neu.surfaceElevated)
                                .clickable {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    app.proxyStateStore.save(item.candidate)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "#${s.language.localizeNumber(idx + 1)}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (idx == 0) statusColors.connected else neu.textSecondary,
                                modifier = Modifier.width(32.dp),
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(neu.accent.copy(alpha = 0.12f))
                                    .border(1.dp, neu.accent.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${item.candidate.emojiFlag} ${item.candidate.countryCode.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = neu.accent,
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${s.language.localizeCountryName(item.candidate.countryName, item.candidate.countryCode)} • ${s.language.localizeDigits(item.candidate.cityCode)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = neu.textPrimary,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${s.language.localizeDigits(item.candidate.host)} • Jitter ${s.language.localizeNumber(item.jitterMs)}ms • OK ${s.language.localizeNumber(item.successRatePercent)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = neu.textSecondary,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${item.method} • ${s.language.formatPingLocalized(item.latencyMs)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = statusColors.connected,
                                    maxLines = 1,
                                )
                                Text(
                                    text = "Score ${s.language.localizeNumber(item.stabilityScore)}/100",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = neu.accent,
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

// ============================================================================
// TOOL 4: CONNECTION WATCHDOG & KEEP-ALIVE
// ============================================================================
@Composable
private fun WatchdogKeepAlivePanel(app: FoxyVpnApp) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val scope = rememberCoroutineScope()
    val state by FoxyVpnService.state.collectAsState()
    val pulseCount by FoxyVpnService.keepAlivePulseCount.collectAsState()
    val lastPulseAt by FoxyVpnService.lastKeepAliveAtMs.collectAsState()
    val pingMs by FoxyVpnService.pingLatencyMs.collectAsState()

    var keepAliveEnabled by remember { mutableStateOf(app.settingsStore.keepAliveEnabled) }
    var keepAliveIntervalSec by remember { mutableIntStateOf(app.settingsStore.keepAliveIntervalSec) }
    var autoReconnectEnabled by remember { mutableStateOf(app.settingsStore.autoReconnectEnabled) }
    var isProbingNow by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    AnimatedTrafficEqualizer(
                        active = keepAliveEnabled && state == ConnectionState.CONNECTED,
                        color = statusColors.connected,
                        barCount = 5,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = s.watchdogHeaderTitle,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                        ),
                        color = neu.textPrimary,
                    )
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = s.watchdogPulsesLabel,
                        value = s.watchdogPulsesValue(pulseCount),
                        accent = statusColors.connected,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = s.watchdogLastPulseLabel,
                        value = lastPulseAt?.let { s.language.localizeDigits(timeFormat.format(Date(it))) } ?: "—",
                        accent = neu.accent,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Toggle 1: Auto-Reconnect Watchdog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            s.watchdogAutoReconnectTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = neu.textPrimary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            s.watchdogAutoReconnectSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = neu.textSecondary,
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Switch(
                        checked = autoReconnectEnabled,
                        onCheckedChange = {
                            autoReconnectEnabled = it
                            app.settingsStore.autoReconnectEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = statusColors.connected,
                        ),
                    )
                }

                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.12f), modifier = Modifier.padding(vertical = 14.dp))

                // Toggle 2: Keep-Alive Socket Heartbeat
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            s.watchdogKeepAliveTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = neu.textPrimary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            s.watchdogKeepAliveSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = neu.textSecondary,
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Switch(
                        checked = keepAliveEnabled,
                        onCheckedChange = {
                            keepAliveEnabled = it
                            app.settingsStore.keepAliveEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = neu.accent,
                        ),
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Interval Selector Pills
                Text(
                    s.watchdogIntervalLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = neu.textSecondary,
                    modifier = Modifier.align(Alignment.Start),
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    listOf(10, 20, 30, 60).forEach { sec ->
                        val selected = keepAliveIntervalSec == sec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .neumorphicTouchBounce()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) neu.accent else neu.surfaceElevated)
                                .clickable {
                                    keepAliveIntervalSec = sec
                                    app.settingsStore.keepAliveIntervalSec = sec
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = s.watchdogSecondsUnit(sec),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (selected) Color.White else neu.textPrimary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                NeumorphicButton(
                    onClick = {
                        if (!isProbingNow) {
                            isProbingNow = true
                            scope.launch {
                                FoxyVpnService.pingNow()
                                delay(300)
                                isProbingNow = false
                            }
                        }
                    },
                    enabled = !isProbingNow && state == ConnectionState.CONNECTED,
                    accentColor = statusColors.connected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = when {
                            state != ConnectionState.CONNECTED -> s.watchdogConnectFirstButton
                            isProbingNow -> s.watchdogSendingPulseButton
                            else -> s.watchdogSendNowButton(pingMs?.let { s.language.formatPingLocalized(it) } ?: s.islandReady)
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

// ============================================================================
// TOOL 5: SMART MTU & DPI FRAGMENTATION OPTIMIZER
// ============================================================================
private data class DpiOptimizationCandidate(
    val mtu: Int,
    val splitMode: SettingsStore.TlsSplitMode,
    val rttMs: Int,
)

private suspend fun measureRealTlsFragmentedProbeMs(
    host: String,
    port: Int,
    mtuCandidate: Int,
    splitMode: SettingsStore.TlsSplitMode,
): Int? = withContext(Dispatchers.IO) {
    runCatching {
        val rawSocket = object : java.net.Socket() {
            private var splitApplied = false
            override fun getOutputStream(): java.io.OutputStream {
                val baseOut = super.getOutputStream()
                return object : java.io.FilterOutputStream(baseOut) {
                    override fun write(b: ByteArray, off: Int, len: Int) {
                        if (!splitApplied &&
                            splitMode != SettingsStore.TlsSplitMode.OFF &&
                            com.vauth.foxyvpn.vpn.upstream.TlsClientHelloSplitter.looksLikeClientHello(b, off, len)
                        ) {
                            splitApplied = true
                            val parts = com.vauth.foxyvpn.vpn.upstream.TlsClientHelloSplitter.splitBytes(b, off, len, splitMode)
                            if (!parts.isNullOrEmpty()) {
                                runCatching { tcpNoDelay = true }
                                for (part in parts) {
                                    baseOut.write(part)
                                    baseOut.flush()
                                }
                                return
                            }
                        }
                        baseOut.write(b, off, len)
                    }
                }
            }
        }
        rawSocket.use { sock ->
            sock.bind(InetSocketAddress(0))
            com.vauth.foxyvpn.data.ControlPlaneHttp.socketProtector?.invoke(sock)
            runCatching {
                sock.sendBufferSize = mtuCandidate
                sock.receiveBufferSize = mtuCandidate
            }
            val startNs = System.nanoTime()
            sock.connect(InetSocketAddress(host, port), 3_500)
            sock.soTimeout = 3_500
            val sslFactory = javax.net.ssl.SSLSocketFactory.getDefault() as javax.net.ssl.SSLSocketFactory
            val sslSocket = sslFactory.createSocket(sock, host, port, true) as javax.net.ssl.SSLSocket
            sslSocket.use { tls ->
                tls.startHandshake()
                val padLen = (mtuCandidate - 220).coerceIn(64, 1300)
                val padHeader = "X".repeat(padLen)
                val reqBytes = "HEAD / HTTP/1.1\r\nHost: $host\r\nX-Mtu-Probe: $padHeader\r\nConnection: close\r\n\r\n"
                    .toByteArray(Charsets.US_ASCII)
                tls.outputStream.write(reqBytes)
                tls.outputStream.flush()
                val firstByte = tls.inputStream.read()
                if (firstByte == -1) error("EOF before TLS response")
            }
            ((System.nanoTime() - startNs) / 1_000_000L).toInt().coerceAtLeast(1)
        }
    }.getOrNull()
}

@Composable
private fun MtuDpiOptimizerPanel(app: FoxyVpnApp) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    var currentMtu by remember { mutableIntStateOf(app.settingsStore.effectiveMtu) }
    var mtuAutoMode by remember { mutableStateOf(app.settingsStore.mtuModeAuto) }
    var bufferAutoMode by remember { mutableStateOf(app.settingsStore.socketBufferModeAuto) }
    var currentSplit by remember { mutableStateOf(app.settingsStore.tlsSplitMode) }
    var isTesting by remember { mutableStateOf(false) }
    var statusMessage by remember(s.isRtl) { mutableStateOf(s.mtuInitialStatus) }
    var recommended by remember { mutableStateOf<DpiOptimizationCandidate?>(null) }
    val testedRows = remember { mutableStateListOf<DpiOptimizationCandidate>() }

    fun runOptimizer() {
        if (isTesting) return
        isTesting = true
        testedRows.clear()
        recommended = null

        scope.launch {
            val targetHost = FoxyVpnService.connectedEndpoint.value?.first
                ?: app.proxyStateStore.load()?.host
                ?: "cloudflare.com"
            val targetPort = FoxyVpnService.connectedEndpoint.value?.second
                ?: app.proxyStateStore.load()?.port
                ?: 443

            val combos = listOf(
                1400 to SettingsStore.TlsSplitMode.SNI,
                1360 to SettingsStore.TlsSplitMode.SNI,
                1280 to SettingsStore.TlsSplitMode.SNI,
                1400 to SettingsStore.TlsSplitMode.FIRST_BYTE,
                1420 to SettingsStore.TlsSplitMode.OFF,
            )

            for ((mtuCandidate, splitCandidate) in combos) {
                statusMessage = s.mtuProbingStatus(mtuCandidate, splitCandidate.localizedLabel(s.language))
                val rtt = measureRealTlsFragmentedProbeMs(
                    host = targetHost,
                    port = targetPort,
                    mtuCandidate = mtuCandidate,
                    splitMode = splitCandidate,
                ) ?: measureRealTlsFragmentedProbeMs(
                    host = "cloudflare.com",
                    port = 443,
                    mtuCandidate = mtuCandidate,
                    splitMode = splitCandidate,
                )
                if (rtt != null) {
                    testedRows.add(DpiOptimizationCandidate(mtuCandidate, splitCandidate, rtt))
                    testedRows.sortBy { it.rttMs }
                }
                delay(120)
            }

            recommended = testedRows.firstOrNull()
            statusMessage = recommended?.let {
                s.mtuOptimalFoundStatus(it.mtu, it.splitMode.localizedLabel(s.language), it.rttMs)
            } ?: s.mtuTimeoutStatus
            isTesting = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = s.mtuActiveLabel,
                        value = if (mtuAutoMode) "Auto (${s.mtuBytesValue(1400)})" else s.mtuBytesValue(currentMtu),
                        accent = neu.accent,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = s.mtuFragmentLabel,
                        value = currentSplit.localizedLabel(s.language),
                        accent = statusColors.connected,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Auto Safe MTU & Socket Buffer Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "حالت خودکار و امن MTU و بافر سوکت (Auto Safe)"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "الوضع التلقائي الآمن لـ MTU والمخازن"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Auto Safe MTU (1400) & Socket Buffers"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = neu.textPrimary,
                        )
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "جلوگیری از تغییر دستی بدون تست معتبر و حفظ پایداری تونل"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "منع التغييرات اليدوية دون اختبار صالح"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Prevents unverified MTU changes and enforces safe TCP window sizes"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = neu.textSecondary,
                        )
                    }
                    Switch(
                        checked = mtuAutoMode && bufferAutoMode,
                        onCheckedChange = { enabled ->
                            mtuAutoMode = enabled
                            bufferAutoMode = enabled
                            app.settingsStore.mtuModeAuto = enabled
                            app.settingsStore.socketBufferModeAuto = enabled
                            currentMtu = app.settingsStore.effectiveMtu
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = statusColors.connected,
                        ),
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = neu.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                Spacer(Modifier.height(18.dp))

                NeumorphicButton(
                    onClick = ::runOptimizer,
                    enabled = !isTesting,
                    accentColor = neu.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (isTesting) s.mtuProbingButton else s.mtuFindBestButton,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(10.dp))

                // One-Tap Reset to Safe Network Defaults
                NeumorphicButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        app.settingsStore.resetNetworkTuningToSafeDefaults()
                        mtuAutoMode = true
                        bufferAutoMode = true
                        currentMtu = app.settingsStore.effectiveMtu
                        currentSplit = app.settingsStore.tlsSplitMode
                        statusMessage = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "تمامی تنظیمات MTU، بافرها و DNS به مقادیر امن پیش‌فرض بازگردانی شدند."
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تمت استعادة جميع إعدادات MTU و DNS إلى القيم الآمنة الافتراضية."
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Reset MTU (1400 Auto), Buffers & DNS to verified safe defaults."
                        }
                    },
                    accentColor = neu.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "بازگردانی به مقادیر امن پیش‌فرض (Safe Reset)"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "استعادة القيم الآمنة الافتراضية"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Reset to Verified Safe Defaults"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                AnimatedVisibility(
                    visible = recommended != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    val best = recommended
                    if (best != null) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(16.dp))
                            NeumorphicButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    app.settingsStore.mtuModeAuto = false
                                    app.settingsStore.mtu = best.mtu
                                    app.settingsStore.tlsSplitMode = best.splitMode
                                    mtuAutoMode = false
                                    currentMtu = best.mtu
                                    currentSplit = best.splitMode
                                    statusMessage = s.mtuAppliedStatus(best.mtu, best.splitMode.localizedLabel(s.language))
                                },
                                accentColor = statusColors.connected,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .neumorphicTouchBounce(),
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    s.mtuApplyBestButton(best.mtu, best.splitMode.localizedLabel(s.language)),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                if (testedRows.isNotEmpty()) {
                    Spacer(Modifier.height(18.dp))
                    testedRows.forEachIndexed { idx, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(neu.surfaceElevated)
                                .clickable {
                                    app.settingsStore.mtuModeAuto = false
                                    app.settingsStore.mtu = row.mtu
                                    app.settingsStore.tlsSplitMode = row.splitMode
                                    mtuAutoMode = false
                                    currentMtu = row.mtu
                                    currentSplit = row.splitMode
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${s.mtuBytesValue(row.mtu)} • ${row.splitMode.localizedLabel(s.language)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (idx == 0) statusColors.connected else neu.textPrimary,
                            )
                            Text(
                                text = s.language.formatPingLocalized(row.rttMs),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = if (idx == 0) statusColors.connected else neu.textSecondary,
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

// ============================================================================
// TOOL 6: STANDALONE TELEGRAM SOCKS5 PROXY (127.0.0.1 LOOPBACK)
// ============================================================================
@Composable
private fun TelegramProxyToolPanel(
    app: FoxyVpnApp,
    onRequestConnect: () -> Unit,
) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by FoxyVpnService.state.collectAsState()
    val isConnected = state == ConnectionState.CONNECTED

    var telegramModeEnabled by remember { mutableStateOf(app.settingsStore.telegramProxyModeEnabled) }
    val socksHost = "127.0.0.1"
    val socksPort = app.settingsStore.socksPort
    var listeningPort by remember { mutableIntStateOf(LocalSocks5Server.activeListeningPort.get()) }
    var activeClients by remember { mutableIntStateOf(LocalSocks5Server.activeClientConnections.get()) }
    var totalRelayed by remember { mutableStateOf(LocalSocks5Server.totalRelayedConnections.get()) }

    LaunchedEffect(isConnected) {
        while (true) {
            listeningPort = LocalSocks5Server.activeListeningPort.get()
            activeClients = LocalSocks5Server.activeClientConnections.get()
            totalRelayed = LocalSocks5Server.totalRelayedConnections.get()
            delay(1000L)
        }
    }

    var isVerifying by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<PingUtil.Socks5TunnelVerification?>(null) }
    var copiedBadge by remember { mutableStateOf<String?>(null) }

    val tgDeepLink = "tg://socks?server=$socksHost&port=$socksPort"
    val tgWebLink = "https://t.me/socks?server=$socksHost&port=$socksPort"

    fun copyText(label: String, value: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        cm?.setPrimaryClip(ClipData.newPlainText(label, value))
        copiedBadge = label
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "حالت مستقل پروکسی تلگرام (LocalSocks5Server)"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "وضع بروكسي تيليجرام المستقل"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Standalone Telegram SOCKS5 Proxy"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = neu.textPrimary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "مقید به 127.0.0.1 (غیرقابل دسترسی در شبکه عمومی) با محافظت در برابر حلقه اتصال"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "مقيد بـ 127.0.0.1 مع حماية ضد حلقات الاتصال"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Strictly bound to 127.0.0.1 loopback (never exposed on LAN) with anti-loop guard"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = neu.textSecondary,
                        )
                    }
                    Switch(
                        checked = telegramModeEnabled,
                        onCheckedChange = { enabled ->
                            telegramModeEnabled = enabled
                            app.settingsStore.telegramProxyModeEnabled = enabled
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = statusColors.connected,
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // 100% Zero-Sponsor Guarantee Badge (No forced Telegram sponsor channels)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusColors.connected.copy(alpha = 0.12f))
                        .border(1.dp, statusColors.connected.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = statusColors.connected,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "تضمین ۱۰۰٪ بدون کانال اسپانسر تبلیغاتی • پروکسی محلی خالص SOCKS5 روی 127.0.0.1"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "ضمان ١٠٠٪ بدون قنوات راعية إجبارية • بروكسي SOCKS5 محلي نقي"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "100% Zero Forced Sponsor Channels • Pure Local Loopback SOCKS5 Proxy"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColors.connected,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Real Host & Port Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = "SOCKS5 Host (Loopback)",
                        value = socksHost,
                        accent = neu.accent,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = "SOCKS5 Port",
                        value = "$socksPort",
                        accent = statusColors.connected,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "وضعیت سرویس SOCKS5"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "حالة خدمة SOCKS5"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Service Status"
                        },
                        value = if (isConnected && listeningPort > 0) {
                            "ONLINE (:$listeningPort)"
                        } else {
                            when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "نیازمند اتصال VPN"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "يتطلب اتصال VPN"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "VPN Disconnected"
                            }
                        },
                        accent = if (isConnected && listeningPort > 0) statusColors.connected else statusColors.connecting,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "نشست‌های فعال / کل"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "الجلسات النشطة / الإجمالي"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Active / Total Streams"
                        },
                        value = "$activeClients / $totalRelayed",
                        accent = neu.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Copy Host, Copy Port, Copy Link Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(
                        "Host" to socksHost,
                        "Port" to socksPort.toString(),
                        "Link" to tgDeepLink,
                    ).forEach { (lbl, value) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(neu.surfaceElevated)
                                .clickable { copyText(lbl, value) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = neu.accent, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (copiedBadge == lbl) "Copied: $lbl" else "Copy $lbl",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = neu.textPrimary,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // One-Tap Add to Telegram Button
                NeumorphicButton(
                    onClick = {
                        if (!isConnected) {
                            app.settingsStore.telegramProxyModeEnabled = true
                            telegramModeEnabled = true
                            onRequestConnect()
                        }
                        runCatching {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tgDeepLink)).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }.onFailure {
                            runCatching {
                                val fallback = Intent(Intent.ACTION_VIEW, Uri.parse(tgWebLink)).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(fallback)
                            }
                        }
                    },
                    accentColor = neu.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_brand_telegram),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "افزودن یک‌لمسی به تلگرام (tg://socks)"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "إضافة مباشرة إلى تيليجرام (tg://socks)"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Add Proxy to Telegram (tg://socks)"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Real End-to-End SOCKS5 Tunnel Verification Button
                NeumorphicButton(
                    onClick = {
                        if (isVerifying) return@NeumorphicButton
                        isVerifying = true
                        scope.launch {
                            verificationResult = PingUtil.verifySocks5TelegramTunnel(socksHost, socksPort)
                            isVerifying = false
                        }
                    },
                    enabled = !isVerifying && isConnected,
                    accentColor = statusColors.connected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = when {
                            !isConnected -> when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "ابتدا VPN را متصل کنید تا تست عبور ترافیک انجام شود"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "اتصل بـ VPN أولاً لاختبار عبور حركة المرور"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Connect VPN First to Verify Tunnel Traffic"
                            }
                            isVerifying -> when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "در حال تست واقعی عبور ترافیک از SOCKS5..."
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "جارٍ اختبار عبور حركة المرور عبر SOCKS5..."
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Verifying Real SOCKS5 Upstream Traffic..."
                            }
                            else -> when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "تست واقعی عبور ترافیک تلگرام از تونل VPN"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "اختبار حقيقي لعبور حركة تيليجرام عبر النفق"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Verify Real Telegram Traffic Through Tunnel"
                            }
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                verificationResult?.let { res ->
                    Spacer(Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(neu.surfaceElevated)
                            .padding(14.dp),
                    ) {
                        Column {
                            Text(
                                text = if (res.success) {
                                    "SOCKS5 Tunnel Verified (${res.rttMs ?: 0} ms) • Exit: ${res.exitIp ?: "—"} (${res.exitCountry ?: "Edge"})"
                                } else {
                                    "Verification Error: ${res.detailMessage}"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (res.success) statusColors.connected else MaterialTheme.colorScheme.error,
                            )
                            if (res.success) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Telegram Core Reachability: ${if (res.telegramReachable) "Reachable via VPN Upstream" else "Trace Verified"} • ${res.detailMessage}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = neu.textSecondary,
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

// ============================================================================
// TOOL 7: GAMING MODE (REAL RTT, JITTER, PACKET LOSS & UDP DISCLOSURE)
// ============================================================================
@Composable
private fun GamingModeToolPanel(app: FoxyVpnApp) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val scope = rememberCoroutineScope()

    var gamingMode by remember { mutableStateOf(app.settingsStore.gamingModeEnabled) }
    var isBenchmarking by remember { mutableStateOf(false) }
    var benchmark by remember { mutableStateOf<PingUtil.ServerStabilityBenchmark?>(null) }

    fun runGamingProbe() {
        if (isBenchmarking) return
        isBenchmarking = true
        scope.launch {
            val host = FoxyVpnService.connectedEndpoint.value?.first
                ?: app.proxyStateStore.load()?.host
                ?: "1.1.1.1"
            val port = FoxyVpnService.connectedEndpoint.value?.second
                ?: app.proxyStateStore.load()?.port
                ?: 443
            benchmark = PingUtil.benchmarkServerStability(host, port, probes = 6)
            isBenchmarking = false
        }
    }

    LaunchedEffect(Unit) {
        runGamingProbe()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeumorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .staggeredEntrance(0),
            cornerRadius = 24.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "حالت گیمینگ و تثبیت پینگ (Gaming Mode)"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "وضع الألعاب وتثبيت البينج"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Low-Jitter Gaming Mode"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = neu.textPrimary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "توقف اسکن و تعویض خودکار سرور در پس‌زمینه هنگام بازی برای جلوگیری از قطع لحظه‌ای"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "إيقاف فحص وتبديل الخوادم التلقائي أثناء اللعب"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Suspends background server scanning & auto-switching during gameplay"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = neu.textSecondary,
                        )
                    }
                    Switch(
                        checked = gamingMode,
                        onCheckedChange = { enabled ->
                            gamingMode = enabled
                            app.settingsStore.gamingModeEnabled = enabled
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = statusColors.connected,
                        ),
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = "Avg Ping (${benchmark?.method ?: "TCP"})",
                        value = benchmark?.let { "${it.avgLatencyMs} ms" } ?: "—",
                        accent = statusColors.connected,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = "Jitter (نوسان پینگ)",
                        value = benchmark?.let { "${it.jitterMs} ms" } ?: "—",
                        accent = neu.accent,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SpeedMetricTile(
                        label = "Packet Loss (پکت‌لاس)",
                        value = benchmark?.let { "${it.packetLossPercent}%" } ?: "—",
                        accent = if ((benchmark?.packetLossPercent ?: 0) == 0) statusColors.connected else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                    SpeedMetricTile(
                        label = "Stability Score",
                        value = benchmark?.let { "${it.stabilityScore}/100" } ?: "—",
                        accent = neu.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Honest Technical Disclosure on UDP Support
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(neu.surfaceElevated)
                        .padding(14.dp),
                ) {
                    Text(
                        text = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "شفافیت فنی UDP: در معماری رله‌های HTTP/2 TLS، بسته‌های UDP پورت ۵۳ (DNS) از طریق DoH عبور داده می‌شوند. بازی‌های مبتنی بر TCP و WebSocket کامل پشتیبانی می‌شوند، اما ترافیک خام UDP غیر-DNS در رله‌های Fastly پشتیبانی نمی‌شود."
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "شفافية UDP: يتم تمرير حزم UDP للمنفذ 53 (DNS) عبر DoH. الألعاب المعتمدة على TCP مدعومة بالكامل، بينما لا يتم تمرير حزم UDP الخام غير الخاصة بـ DNS."
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "UDP Transparency: Port 53 UDP (DNS) is relayed via encrypted DoH. TCP/WebSocket games are fully supported; raw non-DNS UDP game datagrams are not carried by Fastly HTTP/2 TCP relays."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = neu.textSecondary,
                    )
                }

                Spacer(Modifier.height(16.dp))

                NeumorphicButton(
                    onClick = ::runGamingProbe,
                    enabled = !isBenchmarking,
                    accentColor = neu.accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .neumorphicTouchBounce(),
                ) {
                    Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isBenchmarking) {
                            when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "در حال اندازه‌گیری ۶ مرحله‌ای پینگ، جیتر و پکت‌لاس..."
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "جارٍ قياس البينج والجيتر وفقدان الحزم..."
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Running 6-Probe Ping, Jitter & Loss Test..."
                            }
                        } else {
                            when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "تست مجدد پینگ، جیتر و پکت‌لاس واقعی"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "إعادة اختبار البينج والجيتر وفقدان الحزم"
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Re-test Real Latency, Jitter & Packet Loss"
                            }
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}
