package com.vauth.foxyvpn.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

enum class LogLevel { INFO, WARN, ERROR }

data class LogEntry(
    val id: Long,
    val timestampMillis: Long,
    val level: LogLevel,
    val tag: String,
    val message: String,
)

object AppLogger {

    private const val MAX_ENTRIES = 2_000
    private const val MIN_PUBLISH_INTERVAL_MS = 200L

    private val buffer = ConcurrentLinkedDeque<LogEntry>()
    private val _entries = MutableStateFlow<List<LogEntry>>(emptyList())
    val entries: StateFlow<List<LogEntry>> = _entries

    private val lastPublishAt = AtomicLong(0L)
    private val flushScheduled = AtomicBoolean(false)

    private val scheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "AppLogger-flush").apply { isDaemon = true }
    }

    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun d(tag: String, message: String) {
        Log.d(tag, sanitizeSensitiveData(message))
    }

    fun i(tag: String, message: String) {
        val safe = sanitizeSensitiveData(message)
        Log.i(tag, safe)
        log(LogLevel.INFO, tag, safe)
    }

    fun w(tag: String, message: String, error: Throwable? = null) {
        val safe = sanitizeSensitiveData(if (error != null) "$message: ${error.message}" else message)
        // Avoid passing raw Throwable objects to logcat because their messages may contain credentials.
        Log.w(tag, safe)
        log(LogLevel.WARN, tag, safe)
    }

    fun e(tag: String, message: String, error: Throwable? = null) {
        val safe = sanitizeSensitiveData(if (error != null) "$message: ${error.message}" else message)
        Log.e(tag, safe)
        log(LogLevel.ERROR, tag, safe)
    }

    fun sanitizeSensitiveData(value: String): String {
        var safe = value
        safe = safe.replace(Regex("(?i)(authorization\\s*[:=]\\s*(?:bearer\\s+)?)[^\\s,;]+"), "$1[REDACTED]")
        safe = safe.replace(Regex("(?i)(access[_-]?token|refresh[_-]?token|id[_-]?token|client[_-]?secret|password|passwd|api[_-]?key)(\\s*[=:]\\s*)[^\\s,;&]+"), "$1$2[REDACTED]")
        safe = safe.replace(Regex("(?i)(https?://[^\\s?]+)\\?[^\\s]+"), "$1?[REDACTED_QUERY]")
        return safe.take(8_000)
    }

    private val currentSize = java.util.concurrent.atomic.AtomicInteger(0)

    private val entryCounter = java.util.concurrent.atomic.AtomicLong(0)

    private fun log(level: LogLevel, tag: String, message: String) {
        buffer.addLast(LogEntry(entryCounter.incrementAndGet(), System.currentTimeMillis(), level, tag, message))
        var current = currentSize.incrementAndGet()

        while (current > MAX_ENTRIES) {
            if (buffer.pollFirst() != null) {
                current = currentSize.decrementAndGet()
            } else {
                currentSize.set(0)
                break
            }
        }
        schedulePublish()
    }

    private fun schedulePublish() {
        val now = System.currentTimeMillis()
        val last = lastPublishAt.get()
        if (now - last >= MIN_PUBLISH_INTERVAL_MS) {
            if (lastPublishAt.compareAndSet(last, now)) {
                _entries.value = buffer.toList()
            }
            return
        }
        if (flushScheduled.compareAndSet(false, true)) {
            val delayMs = (MIN_PUBLISH_INTERVAL_MS - (now - last)).coerceAtLeast(0)
            val scheduledOk = runCatching {
                scheduler.schedule({
                    flushScheduled.set(false)
                    lastPublishAt.set(System.currentTimeMillis())
                    _entries.value = buffer.toList()
                }, delayMs, TimeUnit.MILLISECONDS)
            }.isSuccess
            if (!scheduledOk) flushScheduled.set(false)
        }
    }

    fun clear() {
        buffer.clear()
        currentSize.set(0)
        _entries.value = emptyList()
    }

    fun exportAsText(): String = buffer.toList().joinToString("\n") { entry ->
        val ts = timeFormat.format(Date(entry.timestampMillis))
        "$ts ${entry.level.name.padEnd(5)} [${entry.tag}] ${entry.message}"
    }
}
