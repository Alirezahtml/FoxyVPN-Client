package com.vauth.foxyvpn.vpn

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.math.abs
import kotlin.math.roundToInt

object PingUtil {
    data class PingMeasurement(
        val latencyMs: Int,
        val method: String, // "ICMP", "TCP", or "TLS"
    )

    data class ServerStabilityBenchmark(
        val avgLatencyMs: Int,
        val minLatencyMs: Int,
        val maxLatencyMs: Int,
        val jitterMs: Int,
        val packetLossPercent: Int,
        val successRatePercent: Int,
        val stabilityScore: Int, // 0..100 composite score
        val method: String,
    )

    data class Socks5TunnelVerification(
        val success: Boolean,
        val rttMs: Int?,
        val exitIp: String?,
        val exitCountry: String?,
        val telegramReachable: Boolean,
        val detailMessage: String,
    )

    private const val TIMEOUT_MS = 3_000

    /**
     * Measures real server latency with automatic method selection & fallback:
     * 1. When VPN is not intercepting raw sockets, tries native ICMP / Echo via [InetAddress.isReachable].
     * 2. Falls back to TCP SYN/ACK Handshake on [host]:[port].
     * 3. Falls back to TLS Handshake on port 443 if TCP raw socket fails.
     * Always returns the exact measurement method ("ICMP", "TCP", or "TLS") alongside the real RTT in ms.
     */
    suspend fun measureServerLatency(host: String, port: Int): PingMeasurement? = withContext(Dispatchers.IO) {
        val vpnActive = FoxyVpnService.state.value != com.vauth.foxyvpn.data.model.ConnectionState.DISCONNECTED
        if (!vpnActive) {
            val icmpResult = runCatching {
                val addr = InetAddress.getByName(host)
                val startNs = System.nanoTime()
                val reachable = addr.isReachable(1_200)
                val elapsedMs = ((System.nanoTime() - startNs) / 1_000_000.0).roundToInt().coerceAtLeast(1)
                if (reachable && elapsedMs in 2..1_150) {
                    PingMeasurement(latencyMs = elapsedMs, method = "ICMP")
                } else {
                    null
                }
            }.getOrNull()
            if (icmpResult != null) return@withContext icmpResult
        }

        val tcpRtt = measureTcpLatencyMs(host, port)
        if (tcpRtt != null) {
            return@withContext PingMeasurement(latencyMs = tcpRtt, method = "TCP")
        }

        val tlsRtt = measureTlsLatencyMs(host, port)
        if (tlsRtt != null) {
            return@withContext PingMeasurement(latencyMs = tlsRtt, method = "TLS")
        }

        null
    }

    /**
     * Multi-probe stability benchmark for smart server selection and Gaming Mode diagnostics.
     * Measures average RTT, min/max RTT, Jitter, Packet Loss %, Success Rate %, and Stability Score (0..100).
     */
    suspend fun benchmarkServerStability(
        host: String,
        port: Int,
        probes: Int = 3,
        delayBetweenMs: Long = 60L,
    ): ServerStabilityBenchmark? = withContext(Dispatchers.IO) {
        val totalProbes = probes.coerceIn(2, 12)
        val samples = mutableListOf<Int>()
        var detectedMethod = "TCP"
        for (i in 0 until totalProbes) {
            val m = measureServerLatency(host, port)
            if (m != null) {
                samples.add(m.latencyMs)
                detectedMethod = m.method
            }
            if (i < totalProbes - 1) delay(delayBetweenMs)
        }
        if (samples.isEmpty()) return@withContext null

        val avgRtt = samples.average().roundToInt().coerceAtLeast(1)
        val minRtt = samples.minOrNull() ?: avgRtt
        val maxRtt = samples.maxOrNull() ?: avgRtt
        val jitter = if (samples.size >= 2) {
            samples.zipWithNext { a, b -> abs(a - b) }.average().roundToInt()
        } else {
            0
        }
        val successRate = ((samples.size * 100.0) / totalProbes.toDouble()).roundToInt().coerceIn(0, 100)
        val packetLoss = (100 - successRate).coerceIn(0, 100)

        // Composite score: rewards high success rate, low RTT, and low jitter
        val latencyPenalty = (avgRtt / 8).coerceIn(0, 45)
        val jitterPenalty = (jitter / 3).coerceIn(0, 25)
        val lossPenalty = packetLoss * 0.7
        val stabilityScore = (100.0 - latencyPenalty - jitterPenalty - lossPenalty).roundToInt().coerceIn(0, 100)

        ServerStabilityBenchmark(
            avgLatencyMs = avgRtt,
            minLatencyMs = minRtt,
            maxLatencyMs = maxRtt,
            jitterMs = jitter,
            packetLossPercent = packetLoss,
            successRatePercent = successRate,
            stabilityScore = stabilityScore,
            method = detectedMethod,
        )
    }

    /**
     * Verifies end-to-end traffic traversal through LocalSocks5Server (127.0.0.1:socksPort)
     * over the active VPN upstream session without any routing loop, and checks Telegram Web/API reachability.
     */
    suspend fun verifySocks5TelegramTunnel(socksHost: String, socksPort: Int): Socks5TunnelVerification = withContext(Dispatchers.IO) {
        val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress(socksHost, socksPort))
        val client = OkHttpClient.Builder()
            .proxy(proxy)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(12, TimeUnit.SECONDS)
            .build()
        try {
            val startNs = System.nanoTime()
            val traceRes = runCatching {
                val req = Request.Builder()
                    .url("https://www.cloudflare.com/cdn-cgi/trace")
                    .header("Cache-Control", "no-cache")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) error("HTTP ${resp.code}")
                    resp.body?.string().orEmpty()
                }
            }
            val rttMs = ((System.nanoTime() - startNs) / 1_000_000.0).roundToInt().coerceAtLeast(1)
            val body = traceRes.getOrElse { err ->
                return@withContext Socks5TunnelVerification(
                    success = false,
                    rttMs = null,
                    exitIp = null,
                    exitCountry = null,
                    telegramReachable = false,
                    detailMessage = err.message ?: "SOCKS5 upstream connection failed",
                )
            }
            var ip = ""
            var loc = ""
            for (line in body.lineSequence()) {
                if (line.startsWith("ip=")) ip = line.removePrefix("ip=").trim()
                if (line.startsWith("loc=")) loc = line.removePrefix("loc=").trim()
            }

            // Also verify Telegram core endpoint over the same SOCKS5 proxy
            val tgOk = runCatching {
                val tgReq = Request.Builder()
                    .url("https://core.telegram.org")
                    .head()
                    .build()
                client.newCall(tgReq).execute().use { resp ->
                    resp.code in 200..499
                }
            }.getOrDefault(false)

            Socks5TunnelVerification(
                success = ip.isNotBlank(),
                rttMs = rttMs,
                exitIp = ip.ifBlank { null },
                exitCountry = loc.ifBlank { null },
                telegramReachable = tgOk,
                detailMessage = if (tgOk) "Verified via SOCKS5 ($loc • ${rttMs}ms • Telegram OK)" else "Verified via SOCKS5 ($loc • ${rttMs}ms)",
            )
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }

    suspend fun measureTcpLatencyMs(host: String, port: Int): Int? = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                com.vauth.foxyvpn.data.ControlPlaneHttp.socketProtector?.invoke(socket)
                val addr = InetSocketAddress(host, port)
                val start = System.nanoTime()
                socket.connect(addr, TIMEOUT_MS)
                ((System.nanoTime() - start) / 1_000_000.0).roundToInt().coerceAtLeast(1)
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun measureTlsLatencyMs(host: String, port: Int): Int? {
        return try {
            Socket().use { rawSocket ->
                com.vauth.foxyvpn.data.ControlPlaneHttp.socketProtector?.invoke(rawSocket)
                rawSocket.connect(InetSocketAddress(host, port), TIMEOUT_MS)
                rawSocket.soTimeout = TIMEOUT_MS
                val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
                val start = System.nanoTime()
                (factory.createSocket(rawSocket, host, port, true) as SSLSocket).use { ssl ->
                    ssl.startHandshake()
                }
                ((System.nanoTime() - start) / 1_000_000.0).roundToInt().coerceAtLeast(1)
            }
        } catch (_: Throwable) {
            null
        }
    }
}
