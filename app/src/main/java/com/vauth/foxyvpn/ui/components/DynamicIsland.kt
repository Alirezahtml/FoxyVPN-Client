package com.vauth.foxyvpn.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import com.vauth.foxyvpn.vpn.PingUtil
import com.vauth.foxyvpn.data.ConnectionProtocolMode
import com.vauth.foxyvpn.data.DynamicIslandIndicatorStyle
import com.vauth.foxyvpn.data.DynamicIslandMotionStyle
import com.vauth.foxyvpn.data.DynamicIslandWaveStyle
import com.vauth.foxyvpn.data.SettingsStore
import com.vauth.foxyvpn.data.model.ConnectionState
import com.vauth.foxyvpn.data.model.ProxyCandidate
import com.vauth.foxyvpn.data.model.emojiFlag
import com.vauth.foxyvpn.ui.theme.AnimatedSignalTowerWave
import com.vauth.foxyvpn.ui.theme.AppLanguage
import com.vauth.foxyvpn.ui.theme.FoxyLanguage
import com.vauth.foxyvpn.ui.theme.LocalFoxyStatusColors
import com.vauth.foxyvpn.ui.theme.currentFoxyFonts
import com.vauth.foxyvpn.ui.theme.currentFoxyStrings
import com.vauth.foxyvpn.ui.theme.currentNeumorphicColors
import com.vauth.foxyvpn.ui.theme.dynamicIslandMotionEffect
import com.vauth.foxyvpn.ui.theme.formatBytesLocalized
import com.vauth.foxyvpn.ui.theme.formatDurationLocalized
import com.vauth.foxyvpn.ui.theme.formatPercentLocalized
import com.vauth.foxyvpn.ui.theme.formatPingLocalized
import com.vauth.foxyvpn.ui.theme.formatSpeedLocalized
import com.vauth.foxyvpn.ui.theme.localizeCountryName
import com.vauth.foxyvpn.ui.theme.localizeNumber
import com.vauth.foxyvpn.ui.theme.neumorphicTouchBounce
import com.vauth.foxyvpn.vpn.FoxyVpnService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class IslandPinnedAppInfo(
    val packageName: String,
    val label: String,
    val iconBitmap: ImageBitmap? = null,
)

@Composable
fun DynamicIslandStatusIndicator(
    indicatorStyle: DynamicIslandIndicatorStyle,
    active: Boolean,
    color: Color,
    settingsStore: SettingsStore,
    modifier: Modifier = Modifier,
) {
    when (indicatorStyle) {
        DynamicIslandIndicatorStyle.TOWER_WAVE -> {
            if (settingsStore.dynamicIslandShowTowerWave) {
                AnimatedSignalTowerWave(
                    active = active && settingsStore.dynamicIslandAnimation,
                    color = color,
                    waveStyle = settingsStore.dynamicIslandWaveStyle,
                    speedMultiplier = settingsStore.waveSpeedMultiplier,
                    amplitudeScale = settingsStore.waveAmplitudeScale,
                    glowEnabled = settingsStore.waveGlowEnabled,
                    colorMode = settingsStore.waveColorMode,
                    modifier = modifier,
                )
            } else {
                Box(
                    modifier = modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(color),
                )
            }
        }
        DynamicIslandIndicatorStyle.PULSE_ORB -> {
            val infinite = rememberInfiniteTransition(label = "island-pulse-orb")
            val pulse by infinite.animateFloat(
                initialValue = 0.65f,
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "orb-scale",
            )
            Canvas(modifier = modifier.size(16.dp)) {
                val r = size.minDimension / 2f
                if (active) {
                    drawCircle(
                        color = color.copy(alpha = 0.25f),
                        radius = r * pulse.coerceIn(0.6f, 1.25f),
                    )
                    drawCircle(
                        color = color.copy(alpha = 0.45f),
                        radius = r * 0.72f,
                    )
                }
                drawCircle(
                    color = color,
                    radius = r * 0.44f,
                )
            }
        }
        DynamicIslandIndicatorStyle.RADAR_SWEEP -> {
            val infinite = rememberInfiniteTransition(label = "island-radar-sweep")
            val angleDeg by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1600, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "radar-angle",
            )
            Canvas(modifier = modifier.size(16.dp)) {
                val r = size.minDimension * 0.45f
                drawCircle(
                    color = color.copy(alpha = 0.32f),
                    radius = r,
                    style = Stroke(width = 1.4.dp.toPx()),
                )
                drawCircle(
                    color = color.copy(alpha = 0.18f),
                    radius = r * 0.55f,
                    style = Stroke(width = 1.dp.toPx()),
                )
                val rad = Math.toRadians((if (active) angleDeg else 45f).toDouble())
                val end = Offset(
                    x = center.x + (r * cos(rad)).toFloat(),
                    y = center.y + (r * sin(rad)).toFloat(),
                )
                drawLine(
                    color = color,
                    start = center,
                    end = end,
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawCircle(color = color, radius = 2.dp.toPx(), center = center)
            }
        }
        DynamicIslandIndicatorStyle.EQUALIZER_BARS -> {
            val infinite = rememberInfiniteTransition(label = "island-eq-bars")
            val phase by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 6.283f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 850, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "eq-phase",
            )
            Canvas(modifier = modifier.size(width = 18.dp, height = 14.dp)) {
                val barCount = 4
                val barW = size.width / (barCount * 1.8f)
                val gap = (size.width - barW * barCount) / (barCount - 1).coerceAtLeast(1)
                for (i in 0 until barCount) {
                    val ratio = if (active) {
                        (0.32f + 0.68f * abs(sin(phase + i * 0.9f))).coerceIn(0.25f, 1f)
                    } else {
                        0.35f
                    }
                    val bh = size.height * ratio
                    val x = i * (barW + gap)
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, (size.height - bh) / 2f),
                        size = Size(barW, bh),
                        cornerRadius = CornerRadius(barW / 2f, barW / 2f),
                    )
                }
            }
        }
        DynamicIslandIndicatorStyle.SHIELD_GUARD -> {
            Canvas(modifier = modifier.size(16.dp)) {
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.08f)
                    lineTo(w * 0.88f, h * 0.24f)
                    lineTo(w * 0.82f, h * 0.62f)
                    lineTo(w * 0.5f, h * 0.92f)
                    lineTo(w * 0.18f, h * 0.62f)
                    lineTo(w * 0.12f, h * 0.24f)
                    close()
                }
                drawPath(
                    path = path,
                    color = color.copy(alpha = if (active) 0.28f else 0.12f),
                )
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 1.6.dp.toPx()),
                )
            }
        }
        DynamicIslandIndicatorStyle.SIGNAL_BARS -> {
            Canvas(modifier = modifier.size(width = 17.dp, height = 13.dp)) {
                val bars = 4
                val bw = size.width / 6.2f
                val step = (size.width - bw * bars) / 3f
                for (i in 0 until bars) {
                    val frac = (i + 1) / bars.toFloat()
                    val bh = size.height * (0.28f + 0.72f * frac)
                    val x = i * (bw + step)
                    drawRoundRect(
                        color = if (active || i < 2) color else color.copy(alpha = 0.35f),
                        topLeft = Offset(x, size.height - bh),
                        size = Size(bw, bh),
                        cornerRadius = CornerRadius(bw / 2f, bw / 2f),
                    )
                }
            }
        }
        DynamicIslandIndicatorStyle.MINIMAL_RING -> {
            Canvas(modifier = modifier.size(14.dp)) {
                val r = size.minDimension * 0.42f
                drawCircle(
                    color = color,
                    radius = r,
                    style = Stroke(width = 2.dp.toPx()),
                )
                if (active) {
                    drawCircle(
                        color = color,
                        radius = r * 0.38f,
                    )
                }
            }
        }
        DynamicIslandIndicatorStyle.QUANTUM_ORBIT -> {
            val infinite = rememberInfiniteTransition(label = "island-quantum")
            val angle by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "quantum-rot",
            )
            Canvas(modifier = modifier.size(18.dp)) {
                val r = size.minDimension * 0.44f
                drawCircle(color = color, radius = 3.dp.toPx())
                if (active) {
                    rotate(45f + angle) {
                        drawOval(
                            color = color.copy(alpha = 0.35f),
                            style = Stroke(width = 1.2.dp.toPx()),
                        )
                        val rad1 = Math.toRadians(angle.toDouble())
                        val eX = center.x + (r * cos(rad1)).toFloat()
                        val eY = center.y + (r * 0.45f * sin(rad1)).toFloat()
                        drawCircle(color = Color.White, radius = 1.8.dp.toPx(), center = Offset(eX, eY))
                    }
                    rotate(-45f - angle) {
                        drawOval(
                            color = color.copy(alpha = 0.35f),
                            style = Stroke(width = 1.2.dp.toPx()),
                        )
                        val rad2 = Math.toRadians((angle + 180f).toDouble())
                        val eX = center.x + (r * cos(rad2)).toFloat()
                        val eY = center.y + (r * 0.45f * sin(rad2)).toFloat()
                        drawCircle(color = Color(0xFF00E5FF), radius = 1.8.dp.toPx(), center = Offset(eX, eY))
                    }
                }
            }
        }
        DynamicIslandIndicatorStyle.HEX_MATRIX -> {
            val infinite = rememberInfiniteTransition(label = "island-hex")
            val pulse by infinite.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "hex-pulse",
            )
            Canvas(modifier = modifier.size(17.dp)) {
                val r = size.minDimension * 0.44f * if (active) pulse else 1f
                val hexPath = Path()
                for (i in 0 until 6) {
                    val angleRad = (i * 60.0 - 30.0) * Math.PI / 180.0
                    val x = center.x + (r * cos(angleRad)).toFloat()
                    val y = center.y + (r * sin(angleRad)).toFloat()
                    if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
                }
                hexPath.close()
                drawPath(path = hexPath, color = color.copy(alpha = if (active) 0.35f else 0.15f))
                drawPath(path = hexPath, color = color, style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(color = color, radius = 2.dp.toPx(), center = center)
            }
        }
        DynamicIslandIndicatorStyle.SONIC_RIPPLE -> {
            val infinite = rememberInfiniteTransition(label = "island-sonic")
            val progress by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1400, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "sonic-progress",
            )
            Canvas(modifier = modifier.size(18.dp)) {
                val maxR = size.minDimension * 0.46f
                drawCircle(color = color, radius = 2.5.dp.toPx(), center = center)
                if (active) {
                    for (i in 0 until 3) {
                        val frac = (progress + i / 3f) % 1f
                        val r = maxR * frac
                        val alpha = (1f - frac).coerceIn(0f, 1f) * 0.75f
                        drawCircle(
                            color = color.copy(alpha = alpha),
                            radius = r,
                            style = Stroke(width = 1.3.dp.toPx()),
                        )
                    }
                }
            }
        }
        DynamicIslandIndicatorStyle.NEON_COMPASS -> {
            val infinite = rememberInfiniteTransition(label = "island-compass")
            val rot by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "compass-rot",
            )
            Canvas(modifier = modifier.size(17.dp)) {
                val r = size.minDimension * 0.44f
                drawCircle(color = color.copy(alpha = 0.28f), radius = r, style = Stroke(width = 1.dp.toPx()))
                val curRot = if (active) rot else 45f
                rotate(curRot) {
                    drawLine(
                        color = Color(0xFF00E5FF),
                        start = center,
                        end = Offset(center.x, center.y - r),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = color.copy(alpha = 0.45f),
                        start = center,
                        end = Offset(center.x, center.y + r),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = center)
            }
        }
    }
}

private fun drawableToImageBitmap(drawable: Drawable, sizePx: Int = 72): ImageBitmap? {
    return runCatching {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return@runCatching drawable.bitmap.asImageBitmap()
        }
        val w = drawable.intrinsicWidth.takeIf { it > 0 }?.coerceAtMost(sizePx) ?: sizePx
        val h = drawable.intrinsicHeight.takeIf { it > 0 }?.coerceAtMost(sizePx) ?: sizePx
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bmp.asImageBitmap()
    }.getOrNull()
}

@Composable
fun QuickAppIconBadge(
    packageName: String,
    iconBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(6.dp)),
        )
    } else {
        Canvas(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(6.dp))
        ) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            when (packageName) {
                "com.google.android.youtube" -> {
                    drawRoundRect(
                        color = Color(0xFFFF0033),
                        cornerRadius = CornerRadius(w * 0.26f, w * 0.26f),
                    )
                    val playPath = Path().apply {
                        moveTo(w * 0.38f, h * 0.28f)
                        lineTo(w * 0.70f, h * 0.50f)
                        lineTo(w * 0.38f, h * 0.72f)
                        close()
                    }
                    drawPath(playPath, Color.White)
                }
                "org.telegram.messenger" -> {
                    drawCircle(
                        brush = Brush.verticalGradient(listOf(Color(0xFF2AABEE), Color(0xFF229ED9))),
                        radius = w * 0.48f,
                        center = Offset(cx, cy),
                    )
                    // Authentic Telegram tail fold shadow
                    val foldPath = Path().apply {
                        moveTo(w * 0.43f, h * 0.70f)
                        lineTo(w * 0.40f, h * 0.59f)
                        lineTo(w * 0.49f, h * 0.64f)
                        close()
                    }
                    drawPath(foldPath, Color(0xFFB5E2F7))
                    // Authentic Telegram paper plane body
                    val planePath = Path().apply {
                        moveTo(w * 0.22f, h * 0.49f)
                        lineTo(w * 0.74f, h * 0.28f)
                        lineTo(w * 0.68f, h * 0.73f)
                        lineTo(w * 0.49f, h * 0.65f)
                        lineTo(w * 0.43f, h * 0.71f)
                        lineTo(w * 0.40f, h * 0.60f)
                        lineTo(w * 0.64f, h * 0.38f)
                        lineTo(w * 0.33f, h * 0.56f)
                        close()
                    }
                    drawPath(planePath, Color.White)
                }
                "com.android.chrome" -> {
                    drawCircle(color = Color(0xFFEA4335), radius = w * 0.46f, center = Offset(cx, cy))
                    drawArc(
                        color = Color(0xFFFBBC05),
                        startAngle = -30f,
                        sweepAngle = 120f,
                        useCenter = true,
                        size = Size(w * 0.92f, h * 0.92f),
                        topLeft = Offset(w * 0.04f, h * 0.04f),
                    )
                    drawArc(
                        color = Color(0xFF34A853),
                        startAngle = 90f,
                        sweepAngle = 120f,
                        useCenter = true,
                        size = Size(w * 0.92f, h * 0.92f),
                        topLeft = Offset(w * 0.04f, h * 0.04f),
                    )
                    drawCircle(color = Color.White, radius = w * 0.24f, center = Offset(cx, cy))
                    drawCircle(color = Color(0xFF4285F4), radius = w * 0.19f, center = Offset(cx, cy))
                }
                "com.instagram.android" -> {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCAF45)),
                            start = Offset(0f, h),
                            end = Offset(w, 0f),
                        ),
                        cornerRadius = CornerRadius(w * 0.26f, w * 0.26f),
                    )
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.20f, h * 0.20f),
                        size = Size(w * 0.60f, h * 0.60f),
                        cornerRadius = CornerRadius(w * 0.17f, w * 0.17f),
                        style = Stroke(width = w * 0.075f),
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.14f,
                        center = Offset(cx, cy),
                        style = Stroke(width = w * 0.075f),
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.042f,
                        center = Offset(w * 0.68f, h * 0.31f),
                    )
                }
                "com.whatsapp" -> {
                    drawCircle(color = Color(0xFF25D366), radius = w * 0.48f, center = Offset(cx, cy))
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.27f,
                        center = Offset(cx, cy),
                        style = Stroke(width = w * 0.09f),
                    )
                }
                "com.twitter.android" -> {
                    drawRoundRect(
                        color = Color(0xFF0F1419),
                        cornerRadius = CornerRadius(w * 0.26f, w * 0.26f),
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.28f, h * 0.26f),
                        end = Offset(w * 0.72f, h * 0.74f),
                        strokeWidth = w * 0.11f,
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(w * 0.72f, h * 0.26f),
                        end = Offset(w * 0.28f, h * 0.74f),
                        strokeWidth = w * 0.11f,
                    )
                }
                "com.spotify.music" -> {
                    drawCircle(color = Color(0xFF1DB954), radius = w * 0.48f, center = Offset(cx, cy))
                    for (i in -1..1) {
                        val yOff = cy + i * (h * 0.14f)
                        drawLine(
                            color = Color.Black,
                            start = Offset(w * 0.26f, yOff),
                            end = Offset(w * 0.74f, yOff),
                            strokeWidth = w * 0.08f,
                        )
                    }
                }
                "com.discord" -> {
                    drawRoundRect(
                        color = Color(0xFF5865F2),
                        cornerRadius = CornerRadius(w * 0.28f, w * 0.28f),
                    )
                    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(w * 0.37f, cy))
                    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(w * 0.63f, cy))
                }
                else -> {
                    drawRoundRect(
                        brush = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF2563EB))),
                        cornerRadius = CornerRadius(w * 0.28f, w * 0.28f),
                    )
                    drawCircle(color = Color.White, radius = w * 0.18f, center = Offset(cx, cy))
                }
            }
        }
    }
}

private data class IslandTelemetryCardItem(
    val label: String,
    val value: String,
    val valueColor: Color,
)

private val KNOWN_APP_FALLBACK_LABELS = mapOf(
    "com.google.android.youtube" to "YouTube",
    "org.telegram.messenger" to "Telegram",
    "com.android.chrome" to "Chrome",
    "com.instagram.android" to "Instagram",
    "com.whatsapp" to "WhatsApp",
    "com.twitter.android" to "X (Twitter)",
    "com.spotify.music" to "Spotify",
    "com.discord" to "Discord",
    "com.zhiliaoapp.musically" to "TikTok",
)

private val KNOWN_APP_WEB_FALLBACKS = mapOf(
    "com.google.android.youtube" to "https://m.youtube.com",
    "org.telegram.messenger" to "https://web.telegram.org",
    "com.android.chrome" to "https://www.google.com",
    "com.instagram.android" to "https://www.instagram.com",
    "com.whatsapp" to "https://web.whatsapp.com",
    "com.twitter.android" to "https://x.com",
    "com.spotify.music" to "https://open.spotify.com",
    "com.discord" to "https://discord.com/app",
)

private fun formatCompactIslandSpeed(bytesPerSec: Long, lang: FoxyLanguage): String {
    val safe = bytesPerSec.coerceAtLeast(0L)
    val valueText: String
    val unit: String
    when {
        safe < 1024L -> {
            valueText = safe.toString()
            unit = when (lang) {
                FoxyLanguage.FA, FoxyLanguage.AR -> "ب/ث"
                FoxyLanguage.EN -> "B/s"
            }
        }
        safe < 1024L * 1024L -> {
            valueText = String.format(java.util.Locale.US, "%.1f", safe / 1024.0)
            unit = when (lang) {
                FoxyLanguage.FA -> "ک‌ب/ث"
                FoxyLanguage.AR -> "ك.ب/ث"
                FoxyLanguage.EN -> "KB/s"
            }
        }
        else -> {
            valueText = String.format(java.util.Locale.US, "%.2f", safe / (1024.0 * 1024.0))
            unit = when (lang) {
                FoxyLanguage.FA -> "م‌ب/ث"
                FoxyLanguage.AR -> "م.ب/ث"
                FoxyLanguage.EN -> "MB/s"
            }
        }
    }
    val localizedVal = when (lang) {
        FoxyLanguage.FA -> valueText.map { ch -> if (ch in '0'..'9') ('\u06F0'.code + (ch - '0')).toChar() else if (ch == '.') '٫' else ch }.joinToString("")
        FoxyLanguage.AR -> valueText.map { ch -> if (ch in '0'..'9') ('\u0660'.code + (ch - '0')).toChar() else if (ch == '.') '٫' else ch }.joinToString("")
        FoxyLanguage.EN -> valueText
    }
    return "$localizedVal $unit"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DynamicIsland(
    settingsStore: SettingsStore,
    selectedProxy: ProxyCandidate?,
    onDisconnect: () -> Unit,
    onRequestConnect: () -> Unit,
    onOpenServers: () -> Unit,
    isSystemOverlay: Boolean = false,
    onOverlayDragDelta: ((Float, Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val configRevision by settingsStore.islandConfigRevision.collectAsState()
    @Suppress("UNUSED_EXPRESSION")
    configRevision

    if (!settingsStore.dynamicIslandEnabled) return

    val context = LocalContext.current
    val state by FoxyVpnService.state.collectAsState()
    val liveStats by FoxyVpnService.vpnLiveStats.collectAsState()
    val connectedAtEpochMs by FoxyVpnService.connectedAtEpochMs.collectAsState()
    val pingLatency by FoxyVpnService.pingLatencyMs.collectAsState()
    val activeProtocolBadge by FoxyVpnService.activeProtocolBadge.collectAsState()

    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val fonts = currentFoxyFonts
    val haptics = LocalHapticFeedback.current

    var isExpanded by remember { mutableStateOf(false) }
    // Keep external overlay window width stable throughout the entire open/close spring animation
    // so WindowManager never reallocates SurfaceFlinger buffers on intermediate frames!
    var overlayAnimationCanvasActive by remember { mutableStateOf(false) }
    var showCustomizeModal by remember { mutableStateOf(false) }
    var showDirectAppPicker by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    // Live measured ping & jitter state for real on-demand ping refresh inside the Island
    val scope = rememberCoroutineScope()
    var manualMeasuredPingMs by remember { mutableStateOf<Int?>(null) }
    var measuredJitterMs by remember { mutableStateOf<Int?>(null) }
    var isTestingLivePing by remember { mutableStateOf(false) }
    var lastPingSampleMs by remember { mutableStateOf<Int?>(null) }
    var networkActionFeedback by remember { mutableStateOf<String?>(null) }

    val effectivePingMs: Int? = manualMeasuredPingMs ?: pingLatency
    LaunchedEffect(effectivePingMs) {
        if (effectivePingMs != null && effectivePingMs > 0) {
            val prev = lastPingSampleMs
            measuredJitterMs = if (prev != null && prev > 0) {
                kotlin.math.abs(effectivePingMs - prev).coerceIn(1, 99)
            } else {
                (effectivePingMs / 14).coerceIn(1, 12)
            }
            lastPingSampleMs = effectivePingMs
        }
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            overlayAnimationCanvasActive = true
            val autoCollapseSec = settingsStore.dynamicIslandAutoCollapseSec
            if (autoCollapseSec > 0) {
                delay(autoCollapseSec * 1_000L)
                isExpanded = false
            }
        } else {
            delay(220L)
            overlayAnimationCanvasActive = false
        }
    }

    // Touch-drag position state for in-app dragging (synced with SettingsStore)
    var inAppOffsetX by remember(configRevision) { mutableFloatStateOf(settingsStore.dynamicIslandOffsetX) }
    var inAppOffsetY by remember(configRevision) { mutableFloatStateOf(settingsStore.dynamicIslandOffsetY) }

    LaunchedEffect(state, connectedAtEpochMs) {
        if (state == ConnectionState.CONNECTED && connectedAtEpochMs != null) {
            while (isActive) {
                val now = System.currentTimeMillis()
                elapsedSeconds = ((now - connectedAtEpochMs!!) / 1000L).coerceAtLeast(0L)
                delay(1000L)
            }
        } else {
            elapsedSeconds = 0L
        }
    }

    // Load labels and real icons for pinned apps (with clean fallback labels + icons for YouTube, Telegram, Chrome, etc.)
    val pinnedPacNames = settingsStore.dynamicIslandPinnedApps.ifEmpty {
        setOf(
            "com.google.android.youtube",
            "org.telegram.messenger",
            "com.android.chrome",
            "com.instagram.android",
        )
    }
    val pinnedApps by produceState<List<IslandPinnedAppInfo>>(initialValue = emptyList(), key1 = pinnedPacNames) {
        value = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            pinnedPacNames.mapNotNull { pkg ->
                runCatching {
                    val info = pm.getApplicationInfo(pkg, 0)
                    val label = pm.getApplicationLabel(info).toString()
                    val iconBmp = runCatching { drawableToImageBitmap(pm.getApplicationIcon(info)) }.getOrNull()
                    IslandPinnedAppInfo(pkg, label, iconBmp)
                }.getOrElse {
                    KNOWN_APP_FALLBACK_LABELS[pkg]?.let { fallbackLabel ->
                        val iconBmp = runCatching { drawableToImageBitmap(pm.getApplicationIcon(pkg)) }.getOrNull()
                        IslandPinnedAppInfo(pkg, fallbackLabel, iconBmp)
                    }
                }
            }
        }
    }

    val islandTheme = settingsStore.dynamicIslandTheme
    val bgAlpha = (settingsStore.dynamicIslandBgAlphaPercent.coerceIn(55, 100) / 100f)
    val pillBrush = when (islandTheme) {
        SettingsStore.DynamicIslandTheme.DARK -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1F232B).copy(alpha = bgAlpha),
                Color(0xFF15181E).copy(alpha = bgAlpha),
            ),
        )
        SettingsStore.DynamicIslandTheme.ACCENT -> Brush.linearGradient(
            colors = listOf(
                neu.accent.copy(alpha = bgAlpha),
                neu.accent.copy(alpha = (bgAlpha * 0.92f).coerceIn(0.5f, 1f)),
            ),
        )
        SettingsStore.DynamicIslandTheme.NEUMORPHIC -> Brush.verticalGradient(
            colors = if (neu.isDark) {
                listOf(Color(0xFF23262D).copy(alpha = bgAlpha), Color(0xFF1A1C22).copy(alpha = bgAlpha))
            } else {
                listOf(Color(0xFFECEFF4).copy(alpha = bgAlpha), Color(0xFFDDE2EB).copy(alpha = bgAlpha))
            },
        )
    }

    val contentColor = when (islandTheme) {
        SettingsStore.DynamicIslandTheme.DARK -> Color.White
        SettingsStore.DynamicIslandTheme.ACCENT -> Color.White
        SettingsStore.DynamicIslandTheme.NEUMORPHIC -> neu.textPrimary
    }

    // Manual 0%..100% linear size scale factor
    val scalePercent = settingsStore.dynamicIslandScalePercent.coerceIn(0, 100)
    val rawManualScaleFactor by animateFloatAsState(
        targetValue = 0.74f + (scalePercent / 100f) * 0.52f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "island-manual-scale",
    )
    val manualScaleFactor = rawManualScaleFactor.takeIf { it.isFinite() }?.coerceIn(0.65f, 1.35f) ?: 1f

    val sizeConfig = settingsStore.dynamicIslandSize
    val customCollapsedHeightDp = settingsStore.dynamicIslandCollapsedHeightDp.coerceIn(32, 56)
    val expandedWidthFraction = (settingsStore.dynamicIslandExpandedWidthPercent.coerceIn(82, 100) / 100f)
    val fontScaleMultiplier = (settingsStore.dynamicIslandFontScalePercent.coerceIn(80, 125) / 100f)
    val borderStrokeWidth = settingsStore.dynamicIslandBorderWidthDp.coerceIn(0.5f, 3.0f).dp
    val shadowDepth = settingsStore.dynamicIslandShadowDepthDp.coerceIn(0, 20).dp
    val springDamping = (settingsStore.dynamicIslandSpringDampingPercent.coerceIn(55, 95) / 100f)
    val springStiffness = settingsStore.dynamicIslandSpringStiffness.coerceIn(180, 600).toFloat()
    val quickAppIconSize = settingsStore.dynamicIslandQuickAppIconSizeDp.coerceIn(28, 48).dp
    val showQuickAppNames = settingsStore.dynamicIslandShowAppNames
    val doubleTapAction = settingsStore.dynamicIslandDoubleTapAction
    val indicatorStyle = settingsStore.dynamicIslandIndicatorStyle
    val showJitterBadge = settingsStore.dynamicIslandShowJitterBadge
    val showSecurityBadge = settingsStore.dynamicIslandShowSecurityBadge
    val showNetworkActions = settingsStore.dynamicIslandShowNetworkActions

    val minWidthDpVal = (sizeConfig.widthDp * manualScaleFactor).coerceIn(115f, 380f)
    val maxCollapsedWidthDpVal = ((sizeConfig.widthDp + 115) * manualScaleFactor)
        .coerceIn(minWidthDpVal + 16f, 430f)
    val scaledMinWidth = minWidthDpVal.dp
    val scaledMaxCollapsedWidth = maxCollapsedWidthDpVal.dp
    val scaledMinHeight = (customCollapsedHeightDp * manualScaleFactor).coerceIn(28f, 110f).dp

    val baseCornerRadius = settingsStore.dynamicIslandCornerRadius.coerceIn(18, 42).dp
    val rawPillCornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 28.dp else (baseCornerRadius * manualScaleFactor).coerceIn(18.dp, 42.dp),
        animationSpec = spring(
            dampingRatio = springDamping,
            stiffness = springStiffness,
        ),
        label = "island-corner-radius",
    )
    val pillCornerRadius = rawPillCornerRadius.coerceIn(14.dp, 44.dp)

    // Single-pass GPU spring progress so opening/closing happens in one fluid motion without step-by-step relayouts
    val expandMorphProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = springDamping,
            stiffness = springStiffness,
        ),
        label = "island-liquid-morph",
    )

    val activeStatusColor by animateColorAsState(
        targetValue = when (state) {
            ConnectionState.CONNECTED -> statusColors.connected
            ConnectionState.CONNECTING -> statusColors.connecting
            ConnectionState.DISCONNECTED -> neu.accent
        },
        animationSpec = tween(300),
        label = "island-status-color",
    )

    val alignment = when (settingsStore.dynamicIslandPosition) {
        SettingsStore.DynamicIslandPosition.TOP_START -> Alignment.TopStart
        SettingsStore.DynamicIslandPosition.TOP_END -> Alignment.TopEnd
        SettingsStore.DynamicIslandPosition.TOP_CENTER -> Alignment.TopCenter
    }

    if (showCustomizeModal && !isSystemOverlay) {
        DynamicIslandCustomizationModal(
            settingsStore = settingsStore,
            onDismiss = { showCustomizeModal = false },
        )
    }

    if (showDirectAppPicker && !isSystemOverlay) {
        DynamicIslandAppPickerDialog(
            current = settingsStore.dynamicIslandPinnedApps,
            onDismiss = { showDirectAppPicker = false },
            onConfirm = { updatedSet ->
                settingsStore.dynamicIslandPinnedApps = updatedSet
                showDirectAppPicker = false
            },
        )
    }

    val isDraggable = settingsStore.dynamicIslandDraggable
    val dragGestureModifier = if (isDraggable) {
        Modifier.pointerInput(isSystemOverlay, isDraggable) {
            detectDragGestures(
                onDragEnd = {
                    if (!isSystemOverlay) {
                        settingsStore.dynamicIslandOffsetX = inAppOffsetX
                        settingsStore.dynamicIslandOffsetY = inAppOffsetY
                    }
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    if (isSystemOverlay && onOverlayDragDelta != null) {
                        onOverlayDragDelta(dragAmount.x, dragAmount.y)
                    } else {
                        inAppOffsetX = (inAppOffsetX + dragAmount.x).coerceIn(-360f, 360f)
                        inAppOffsetY = (inAppOffsetY + dragAmount.y).coerceIn(-60f, 900f)
                    }
                },
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(
                if (isSystemOverlay) {
                    if (isExpanded || overlayAnimationCanvasActive) {
                        Modifier.width(368.dp)
                    } else {
                        Modifier
                    }
                } else {
                    Modifier.fillMaxWidth()
                },
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = if (isSystemOverlay) Alignment.TopCenter else alignment,
    ) {
        val shape = RoundedCornerShape(pillCornerRadius)

        val smoothOffsetX by animateFloatAsState(
            targetValue = inAppOffsetX.takeIf { it.isFinite() }?.coerceIn(-360f, 360f) ?: 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
            label = "island-smooth-drag-x",
        )
        val smoothOffsetY by animateFloatAsState(
            targetValue = inAppOffsetY.takeIf { it.isFinite() }?.coerceIn(-60f, 900f) ?: 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
            label = "island-smooth-drag-y",
        )

        val collapsedMaxWidth = maxOf(scaledMaxCollapsedWidth, scaledMinWidth + 8.dp)
        val collapsedHorizontalPadding = (16.dp * manualScaleFactor).coerceIn(12.dp, 22.dp)
        val collapsedVerticalPadding = (9.dp * manualScaleFactor).coerceIn(7.dp, 14.dp)

        // Clean hardware-accelerated Soft Neumorphic Dynamic Island container with single-shot GPU spring morphing
        Box(
            modifier = Modifier
                .graphicsLayer {
                    if (!isSystemOverlay) {
                        translationX = smoothOffsetX
                        translationY = smoothOffsetY
                    }
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    val morphScale = if (isExpanded) {
                        0.94f + 0.06f * expandMorphProgress.coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                    scaleX = morphScale
                    scaleY = morphScale
                }
                .then(dragGestureModifier)
                .dynamicIslandMotionEffect(
                    enabled = settingsStore.dynamicIslandAnimation && !settingsStore.batterySaverModeEnabled,
                    style = settingsStore.dynamicIslandMotionStyle,
                    isConnected = state == ConnectionState.CONNECTED,
                    isExpanded = isExpanded,
                    trafficRateBytesPerSec = liveStats.rxRateBytesPerSec + liveStats.txRateBytesPerSec,
                    accentColor = activeStatusColor,
                    cornerRadius = pillCornerRadius,
                )
                .then(
                    if ((shadowDepth > 0.dp && !isSystemOverlay) || settingsStore.dynamicIslandGlowEnabled) {
                        Modifier.shadow(
                            elevation = if (settingsStore.dynamicIslandGlowEnabled) maxOf(shadowDepth, 10.dp) else shadowDepth,
                            shape = shape,
                            ambientColor = activeStatusColor.copy(alpha = if (settingsStore.dynamicIslandGlowEnabled) 0.78f else 0.45f),
                            spotColor = activeStatusColor.copy(alpha = if (settingsStore.dynamicIslandGlowEnabled) 0.92f else 0.65f),
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(shape)
                .background(pillBrush)
                .border(
                    width = borderStrokeWidth,
                    brush = Brush.verticalGradient(
                        colors = when (state) {
                            ConnectionState.CONNECTED -> listOf(
                                statusColors.connected.copy(alpha = 0.58f),
                                statusColors.connected.copy(alpha = 0.16f),
                            )
                            ConnectionState.CONNECTING -> listOf(
                                statusColors.connecting.copy(alpha = 0.58f),
                                statusColors.connecting.copy(alpha = 0.16f),
                            )
                            ConnectionState.DISCONNECTED -> listOf(
                                Color.White.copy(alpha = if (islandTheme == SettingsStore.DynamicIslandTheme.NEUMORPHIC && !neu.isDark) 0.90f else 0.18f),
                                Color.Black.copy(alpha = 0.25f),
                            )
                        },
                    ),
                    shape = shape,
                )
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onDoubleClick = {
                        if (settingsStore.dynamicIslandHapticEnabled) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        when (doubleTapAction) {
                            com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.EXPAND -> {
                                val nextExpanded = !isExpanded
                                if (nextExpanded) overlayAnimationCanvasActive = true
                                isExpanded = nextExpanded
                            }
                            com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.TOGGLE_VPN -> {
                                if (state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING) {
                                    onDisconnect()
                                } else {
                                    onRequestConnect()
                                }
                            }
                            com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.OPEN_SERVERS -> {
                                onOpenServers()
                            }
                            com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.TOGGLE_KILLSWITCH -> {
                                settingsStore.killSwitchEnabled = !settingsStore.killSwitchEnabled
                            }
                            com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.SPEED_TEST -> {
                                val host = selectedProxy?.host
                                if (!host.isNullOrBlank()) {
                                    scope.launch {
                                        val measurement = PingUtil.measureServerLatency(host, 443)
                                        val rtt = measurement?.latencyMs
                                        networkActionFeedback = if (rtt != null) "Ping: ${rtt}ms" else "Timeout"
                                        delay(2600)
                                        networkActionFeedback = null
                                    }
                                }
                            }
                            com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.COPY_EXIT_IP -> {
                                val ip = selectedProxy?.host
                                if (!ip.isNullOrBlank()) {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("VPN IP", ip))
                                    networkActionFeedback = "IP Copied"
                                    scope.launch {
                                        delay(2200)
                                        networkActionFeedback = null
                                    }
                                }
                            }
                        }
                    },
                    onClick = {
                        if (settingsStore.dynamicIslandHapticEnabled) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        val nextExpanded = !isExpanded
                        if (nextExpanded) overlayAnimationCanvasActive = true
                        isExpanded = nextExpanded
                    },
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    val enterTransition = fadeIn(
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.94f,
                        transformOrigin = TransformOrigin(0.5f, 0f),
                        animationSpec = spring(
                            dampingRatio = springDamping,
                            stiffness = springStiffness,
                        ),
                    )
                    val exitTransition = fadeOut(
                        animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.94f,
                        transformOrigin = TransformOrigin(0.5f, 0f),
                        animationSpec = spring(
                            dampingRatio = (springDamping + 0.04f).coerceAtMost(0.98f),
                            stiffness = (springStiffness + 60f).coerceAtMost(650f),
                        ),
                    )
                    // Use null SizeTransform so the window/container resizes in one clean pass
                    // without multi-step SurfaceFlinger buffer reallocation stutter!
                    enterTransition
                        .togetherWith(exitTransition)
                        .using(null)
                },
                contentAlignment = Alignment.TopCenter,
                label = "island-expand-collapse",
            ) { expanded ->
                if (!expanded) {
                    // Collapsed Dynamic Island Pill
                    Row(
                        modifier = Modifier
                            .widthIn(min = scaledMinWidth, max = collapsedMaxWidth)
                            .heightIn(min = scaledMinHeight)
                            .padding(
                                horizontal = collapsedHorizontalPadding,
                                vertical = collapsedVerticalPadding,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        when (state) {
                            ConnectionState.CONNECTED -> {
                                if (settingsStore.dynamicIslandShowTowerWave) {
                                    DynamicIslandStatusIndicator(
                                        indicatorStyle = indicatorStyle,
                                        active = settingsStore.dynamicIslandAnimation,
                                        color = statusColors.connected,
                                        settingsStore = settingsStore,
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(statusColors.connected),
                                    )
                                }
                            }
                            ConnectionState.CONNECTING -> {
                                if (settingsStore.dynamicIslandShowTowerWave) {
                                    DynamicIslandStatusIndicator(
                                        indicatorStyle = indicatorStyle,
                                        active = true,
                                        color = statusColors.connecting,
                                        settingsStore = settingsStore,
                                    )
                                } else {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(12.dp),
                                        color = statusColors.connecting,
                                    )
                                }
                            }
                            ConnectionState.DISCONNECTED -> {
                                if (settingsStore.dynamicIslandShowTowerWave) {
                                    DynamicIslandStatusIndicator(
                                        indicatorStyle = indicatorStyle,
                                        active = false,
                                        color = contentColor.copy(alpha = 0.65f),
                                        settingsStore = settingsStore,
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(contentColor.copy(alpha = 0.45f)),
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        when (state) {
                            ConnectionState.CONNECTED -> {
                                val flag = selectedProxy?.emojiFlag
                                if (settingsStore.dynamicIslandShowFlag && !flag.isNullOrBlank()) {
                                    Text(flag, fontSize = (13f * manualScaleFactor.coerceIn(0.85f, 1.15f) * fontScaleMultiplier).sp)
                                    Spacer(Modifier.width(6.dp))
                                }
                                if (settingsStore.dynamicIslandShowServerName && selectedProxy != null && !settingsStore.dynamicIslandCompactTelemetry) {
                                    Text(
                                        text = s.language.localizeCountryName(selectedProxy.countryName, selectedProxy.countryCode),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = fonts.headingFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (12f * fontScaleMultiplier).sp,
                                        ),
                                        color = contentColor,
                                        maxLines = 1,
                                    )
                                    Spacer(Modifier.width(6.dp))
                                }
                                if (settingsStore.dynamicIslandShowTimer) {
                                    Text(
                                        text = s.language.formatDurationLocalized(elapsedSeconds, alwaysShowHours = false),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (12f * fontScaleMultiplier).sp,
                                            fontFeatureSettings = "tnum",
                                        ),
                                        color = contentColor,
                                        maxLines = 1,
                                    )
                                } else if (!settingsStore.dynamicIslandShowServerName || settingsStore.dynamicIslandCompactTelemetry) {
                                    Text(
                                        text = s.appName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = fonts.headingFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (12f * fontScaleMultiplier).sp,
                                        ),
                                        color = contentColor,
                                        maxLines = 1,
                                    )
                                }

                                if (settingsStore.dynamicIslandShowSpeed) {
                                    Spacer(Modifier.width(8.dp))
                                    AnimatedContent(
                                        targetState = formatCompactIslandSpeed(liveStats.rxRateBytesPerSec, s.language),
                                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(120)) },
                                        label = "island-live-speed",
                                    ) { rxFormatted ->
                                        Text(
                                            text = "\u200E↓$rxFormatted",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = fonts.monoFont,
                                                fontSize = (11f * fontScaleMultiplier).sp,
                                            ),
                                            color = statusColors.connected,
                                            maxLines = 1,
                                        )
                                    }
                                }
                                if (settingsStore.dynamicIslandShowPing && effectivePingMs != null && !settingsStore.dynamicIslandCompactTelemetry) {
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = s.language.formatPingLocalized(effectivePingMs),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = (11f * fontScaleMultiplier).sp,
                                        ),
                                        color = contentColor.copy(alpha = 0.85f),
                                        maxLines = 1,
                                    )
                                }
                                if (showJitterBadge && measuredJitterMs != null && !settingsStore.dynamicIslandCompactTelemetry) {
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "\u200E±${measuredJitterMs}ms",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (10f * fontScaleMultiplier).sp,
                                        ),
                                        color = statusColors.connected.copy(alpha = 0.90f),
                                        maxLines = 1,
                                    )
                                }
                                if (settingsStore.dynamicIslandShowProtocolBadge && !settingsStore.dynamicIslandCompactTelemetry) {
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = activeProtocolBadge,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (10f * fontScaleMultiplier).sp,
                                        ),
                                        color = neu.accent,
                                        maxLines = 1,
                                    )
                                }
                            }
                            ConnectionState.CONNECTING -> {
                                Text(
                                    text = s.islandConnecting,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = fonts.bodyFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = (12f * fontScaleMultiplier).sp,
                                    ),
                                    color = statusColors.connecting,
                                    maxLines = 1,
                                )
                            }
                            ConnectionState.DISCONNECTED -> {
                                Text(
                                    text = s.appName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (12f * fontScaleMultiplier).sp,
                                    ),
                                    color = contentColor.copy(alpha = 0.92f),
                                    maxLines = 1,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = s.islandReady,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.bodyFont,
                                        fontSize = (11f * fontScaleMultiplier).sp,
                                    ),
                                    color = contentColor.copy(alpha = 0.65f),
                                    maxLines = 1,
                                )
                            }
                        }

                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = contentColor.copy(alpha = 0.55f),
                            modifier = Modifier.size(14.dp),
                        )
                    }
                } else {
                    // Expanded Native Dynamic Island Sheet
                    Column(
                        modifier = Modifier
                            .then(
                                if (isSystemOverlay) {
                                    Modifier.width((348f * expandedWidthFraction).dp)
                                } else {
                                    Modifier
                                        .widthIn(max = maxOf(460.dp, scaledMinWidth + 24.dp))
                                        .fillMaxWidth(expandedWidthFraction)
                                },
                            )
                            .padding(horizontal = 18.dp, vertical = 15.dp),
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                if (settingsStore.dynamicIslandShowTowerWave) {
                                    DynamicIslandStatusIndicator(
                                        indicatorStyle = indicatorStyle,
                                        active = state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING,
                                        color = activeStatusColor,
                                        settingsStore = settingsStore,
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                val flag = selectedProxy?.emojiFlag
                                if (settingsStore.dynamicIslandShowFlag && !flag.isNullOrBlank()) {
                                    Text(flag, fontSize = (20f * fontScaleMultiplier).sp)
                                    Spacer(Modifier.width(8.dp))
                                } else if (!settingsStore.dynamicIslandShowTowerWave) {
                                    Icon(
                                        imageVector = Icons.Filled.Public,
                                        contentDescription = null,
                                        tint = contentColor,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                Column {
                                    Text(
                                        text = selectedProxy?.let {
                                            s.language.localizeCountryName(it.countryName, it.countryCode)
                                        } ?: s.islandServerDefault,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = fonts.headingFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (14f * fontScaleMultiplier).sp,
                                            lineHeight = 20.sp,
                                        ),
                                        color = contentColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when (state) {
                                                ConnectionState.CONNECTED -> s.islandActiveEncrypted
                                                ConnectionState.CONNECTING -> s.islandSecuringConnection
                                                ConnectionState.DISCONNECTED -> s.islandProtectedReady
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = fonts.bodyFont,
                                                fontSize = (11.5f * fontScaleMultiplier).sp,
                                                lineHeight = 17.sp,
                                            ),
                                            color = when (state) {
                                                ConnectionState.CONNECTED -> statusColors.connected
                                                ConnectionState.CONNECTING -> statusColors.connecting
                                                ConnectionState.DISCONNECTED -> contentColor.copy(alpha = 0.65f)
                                            },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        if ((settingsStore.dynamicIslandShowIpBadge || settingsStore.dynamicIslandShowProtocolBadge) && selectedProxy != null) {
                                            Spacer(Modifier.width(6.dp))
                                            val protoText = if (settingsStore.dynamicIslandShowProtocolBadge) {
                                                activeProtocolBadge
                                            } else {
                                                "H2/TLS"
                                            }
                                            Text(
                                                text = "\u200E• $protoText :${s.language.localizeNumber(selectedProxy.port)}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = fonts.monoFont,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (10.5f * fontScaleMultiplier).sp,
                                                ),
                                                color = neu.accent,
                                                maxLines = 1,
                                            )
                                        }
                                        if (showSecurityBadge) {
                                            Spacer(Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(statusColors.connected.copy(alpha = 0.16f))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                                            ) {
                                                Text(
                                                    text = "TLS 1.3 • Guard",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = fonts.monoFont,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (9.5f * fontScaleMultiplier).sp,
                                                    ),
                                                    color = statusColors.connected,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(contentColor.copy(alpha = 0.10f))
                                        .clickable { isExpanded = false },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = contentColor.copy(alpha = 0.85f),
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }

                        // Spacious 2x2 Telemetry Cards Grid (No text cramping, wrapping, or bottom-line clipping!)
                        val telemetryCards = buildList {
                            if (settingsStore.dynamicIslandShowPing) {
                                val pingText = effectivePingMs?.let { s.language.formatPingLocalized(it) } ?: "\u2014"
                                val jitterSuffix = if (showJitterBadge && measuredJitterMs != null) " (\u200E±${measuredJitterMs}ms)" else ""
                                add(
                                    IslandTelemetryCardItem(
                                        label = s.islandLatency,
                                        value = "$pingText$jitterSuffix",
                                        valueColor = if (effectivePingMs != null) statusColors.connected else contentColor,
                                    )
                                )
                            }
                            if (settingsStore.dynamicIslandShowTimer) {
                                add(
                                    IslandTelemetryCardItem(
                                        label = s.islandDuration,
                                        value = if (state == ConnectionState.CONNECTED) {
                                            s.language.formatDurationLocalized(elapsedSeconds, alwaysShowHours = true)
                                        } else {
                                            s.language.formatDurationLocalized(0L, alwaysShowHours = true)
                                        },
                                        valueColor = contentColor,
                                    )
                                )
                            }
                            if (settingsStore.dynamicIslandShowSessionData) {
                                add(
                                    IslandTelemetryCardItem(
                                        label = s.dataUsedLabel,
                                        value = s.language.formatBytesLocalized(liveStats.totalRxBytes + liveStats.totalTxBytes),
                                        valueColor = contentColor,
                                    )
                                )
                            }
                            if (settingsStore.dynamicIslandShowSpeed) {
                                val tx = formatCompactIslandSpeed(liveStats.txRateBytesPerSec, s.language)
                                val rx = formatCompactIslandSpeed(liveStats.rxRateBytesPerSec, s.language)
                                add(
                                    IslandTelemetryCardItem(
                                        label = s.islandLiveSpeed,
                                        value = "\u200E↓$rx  ↑$tx",
                                        valueColor = if (state == ConnectionState.CONNECTED) statusColors.connected else contentColor,
                                    )
                                )
                            }
                        }

                        if (telemetryCards.isNotEmpty()) {
                            Spacer(Modifier.height(11.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                telemetryCards.chunked(2).forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        rowItems.forEach { item ->
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(contentColor.copy(alpha = 0.08f))
                                                    .border(
                                                        width = 0.8.dp,
                                                        color = contentColor.copy(alpha = 0.10f),
                                                        shape = RoundedCornerShape(14.dp),
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                            ) {
                                                Text(
                                                    text = item.label,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = fonts.bodyFont,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = (10.5f * fontScaleMultiplier).sp,
                                                        lineHeight = 15.sp,
                                                    ),
                                                    color = contentColor.copy(alpha = 0.68f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(bottom = 2.dp),
                                                )
                                                Text(
                                                    text = item.value,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = fonts.monoFont,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (11.5f * fontScaleMultiplier).sp,
                                                        lineHeight = 17.sp,
                                                        fontFeatureSettings = "tnum",
                                                    ),
                                                    color = item.valueColor,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(vertical = 1.dp),
                                                )
                                            }
                                        }
                                        if (rowItems.size == 1) {
                                            Spacer(Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        // Real Interactive Network Quick-Action Strip inside Expanded Dynamic Island
                        if (showNetworkActions) {
                            Spacer(Modifier.height(9.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // 1. Live TCP Ping Refresh Action
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(contentColor.copy(alpha = 0.11f))
                                        .border(0.7.dp, contentColor.copy(alpha = 0.13f), RoundedCornerShape(11.dp))
                                        .clickable(enabled = !isTestingLivePing) {
                                            val proxy = selectedProxy
                                            if (proxy != null) {
                                                isTestingLivePing = true
                                                scope.launch {
                                                    val startNs = System.nanoTime()
                                                    val measured = withContext(Dispatchers.IO) {
                                                        runCatching {
                                                            java.net.Socket().use { sock ->
                                                                sock.connect(java.net.InetSocketAddress(proxy.host, proxy.port), 2200)
                                                            }
                                                            ((System.nanoTime() - startNs) / 1_000_000L).toInt().coerceAtLeast(1)
                                                        }.getOrNull()
                                                    }
                                                    if (measured != null) {
                                                        manualMeasuredPingMs = measured
                                                    }
                                                    isTestingLivePing = false
                                                }
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = when {
                                            isTestingLivePing -> when (s.language) {
                                                FoxyLanguage.FA -> "تست پینگ..."
                                                FoxyLanguage.AR -> "فحص البينج..."
                                                FoxyLanguage.EN -> "Pinging..."
                                            }
                                            else -> when (s.language) {
                                                FoxyLanguage.FA -> "تست پینگ زنده"
                                                FoxyLanguage.AR -> "فحص البينج الحي"
                                                FoxyLanguage.EN -> "Live Ping Test"
                                            }
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.bodyFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (10f * fontScaleMultiplier).sp,
                                        ),
                                        color = statusColors.connected,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                                // 2. Live Tunnel Transport Mode Switcher (AUTO / H2 / H1.1)
                                var currentTransport by remember(configRevision) {
                                    mutableStateOf(settingsStore.connectionProtocolMode)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(neu.accent.copy(alpha = 0.16f))
                                        .border(0.7.dp, neu.accent.copy(alpha = 0.25f), RoundedCornerShape(11.dp))
                                        .clickable {
                                            val modes = listOf(
                                                ConnectionProtocolMode.AUTO,
                                                ConnectionProtocolMode.HTTP2_TCP,
                                                ConnectionProtocolMode.HTTP11_TLS,
                                            )
                                            val nextIdx = (modes.indexOf(currentTransport) + 1) % modes.size
                                            val nextMode = modes[nextIdx]
                                            currentTransport = nextMode
                                            settingsStore.connectionProtocolMode = nextMode
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    val shortProto = when (currentTransport) {
                                        ConnectionProtocolMode.AUTO -> "Auto TLS"
                                        ConnectionProtocolMode.HTTP2_TCP -> "HTTP/2"
                                        ConnectionProtocolMode.HTTP11_TLS -> "HTTP/1.1"
                                        ConnectionProtocolMode.HTTP3_QUIC -> "Auto TLS"
                                    }
                                    Text(
                                        text = "\u200E⇄ $shortProto",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (10f * fontScaleMultiplier).sp,
                                        ),
                                        color = contentColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                                // 3. Zero-Sponsor Telegram SOCKS5 Proxy Quick Copy/Open
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(contentColor.copy(alpha = 0.11f))
                                        .border(0.7.dp, contentColor.copy(alpha = 0.13f), RoundedCornerShape(11.dp))
                                        .clickable {
                                            settingsStore.telegramProxyModeEnabled = true
                                            val tgUri = "tg://socks?server=127.0.0.1&port=${settingsStore.socksPort}"
                                            runCatching {
                                                val clip = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                clip?.setPrimaryClip(android.content.ClipData.newPlainText("Telegram SOCKS5", tgUri))
                                            }
                                            networkActionFeedback = when (s.language) {
                                                FoxyLanguage.FA -> "پروکسی بدون اسپانسر کپی شد"
                                                FoxyLanguage.AR -> "تم نسخ البروكسي"
                                                FoxyLanguage.EN -> "Zero-Sponsor Proxy Copied"
                                            }
                                            scope.launch {
                                                delay(2000L)
                                                networkActionFeedback = null
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = networkActionFeedback ?: when (s.language) {
                                            FoxyLanguage.FA -> "پروکسی تلگرام"
                                            FoxyLanguage.AR -> "بروكسي تيليجرام"
                                            FoxyLanguage.EN -> "TG SOCKS5"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.bodyFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (10f * fontScaleMultiplier).sp,
                                        ),
                                        color = contentColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }

                        // Pinned Quick-Launch Apps Row (Shown both in-app and in external system overlay with real App Icon + App Name)
                        if (settingsStore.dynamicIslandShowQuickApps) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    pinnedApps.forEach { appItem ->
                                        val badgeVisualSize = (quickAppIconSize.value * 0.50f).coerceIn(14f, 26f).dp
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(contentColor.copy(alpha = 0.13f))
                                                .border(
                                                    width = 0.8.dp,
                                                    color = contentColor.copy(alpha = 0.14f),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .clickable {
                                                    runCatching {
                                                        val launchIntent = context.packageManager.getLaunchIntentForPackage(appItem.packageName)
                                                        if (launchIntent != null) {
                                                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            context.startActivity(launchIntent)
                                                            isExpanded = false
                                                        } else {
                                                            val fallbackUrl = KNOWN_APP_WEB_FALLBACKS[appItem.packageName]
                                                            if (fallbackUrl != null) {
                                                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
                                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                }
                                                                context.startActivity(webIntent)
                                                                isExpanded = false
                                                            }
                                                        }
                                                    }
                                                }
                                                .padding(horizontal = if (showQuickAppNames) 9.dp else 7.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                QuickAppIconBadge(
                                                    packageName = appItem.packageName,
                                                    iconBitmap = appItem.iconBitmap,
                                                    size = badgeVisualSize,
                                                )
                                                if (showQuickAppNames) {
                                                    Spacer(Modifier.width(5.dp))
                                                    Text(
                                                        text = appItem.label,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontFamily = fonts.bodyFont,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = (11f * fontScaleMultiplier).sp,
                                                            lineHeight = 15.sp,
                                                        ),
                                                        color = contentColor,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (!isSystemOverlay) {
                                        // Direct "+ Add App" button right inside the quick-apps bar
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(neu.accent.copy(alpha = 0.22f))
                                                .clickable { showDirectAppPicker = true }
                                                .padding(horizontal = 11.dp, vertical = 7.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    contentDescription = s.islandAddAppsButton,
                                                    tint = contentColor,
                                                    modifier = Modifier.size(14.dp),
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = s.islandAddAppsButton,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = fonts.bodyFont,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (11f * fontScaleMultiplier).sp,
                                                        lineHeight = 16.sp,
                                                    ),
                                                    color = contentColor,
                                                    maxLines = 1,
                                                )
                                            }
                                        }

                                        // Customize Island Studio button
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(contentColor.copy(alpha = 0.12f))
                                                .clickable { showCustomizeModal = true }
                                                .padding(horizontal = 11.dp, vertical = 7.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.Tune,
                                                    contentDescription = s.islandCustomizeButton,
                                                    tint = contentColor,
                                                    modifier = Modifier.size(14.dp),
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = s.islandCustomizeButton,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = fonts.bodyFont,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = (11f * fontScaleMultiplier).sp,
                                                        lineHeight = 16.sp,
                                                    ),
                                                    color = contentColor,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.width(2.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(13.dp))

                        // Primary Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .neumorphicTouchBounce()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(contentColor.copy(alpha = 0.13f))
                                    .clickable {
                                        isExpanded = false
                                        onOpenServers()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = s.islandChangeServer,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = fonts.bodyFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = (12f * fontScaleMultiplier).sp,
                                    ),
                                    color = contentColor,
                                    maxLines = 1,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .neumorphicTouchBounce()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (state == ConnectionState.CONNECTED) statusColors.connected.copy(alpha = 0.24f)
                                        else neu.accent.copy(alpha = 0.30f),
                                    )
                                    .clickable {
                                        isExpanded = false
                                        if (state == ConnectionState.DISCONNECTED) onRequestConnect() else onDisconnect()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = when (state) {
                                        ConnectionState.CONNECTED -> s.islandDisconnect
                                        ConnectionState.CONNECTING -> s.islandCancel
                                        ConnectionState.DISCONNECTED -> s.islandConnect
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (12f * fontScaleMultiplier).sp,
                                    ),
                                    color = if (state == ConnectionState.CONNECTED) statusColors.connected else Color.White,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Live In-App Preview Stage for Dynamic Island & Wave Studio customization.
 * Responds immediately to EVERY customization parameter (including Deep Studio geometry,
 * border width, 3D shadow depth, spring physics, font scale, app icon size, indicator style,
 * and network badges/actions).
 */
@Composable
fun DynamicIslandLivePreviewCard(
    settingsStore: SettingsStore,
    modifier: Modifier = Modifier,
) {
    val configRevision by settingsStore.islandConfigRevision.collectAsState()
    @Suppress("UNUSED_EXPRESSION")
    configRevision

    val s = currentFoxyStrings
    val neu = currentNeumorphicColors
    val fonts = currentFoxyFonts
    val statusColors = LocalFoxyStatusColors.current
    val activeProtocolBadge by FoxyVpnService.activeProtocolBadge.collectAsState()

    var previewExpanded by remember { mutableStateOf(false) }

    val islandTheme = settingsStore.dynamicIslandTheme
    val bgAlpha = (settingsStore.dynamicIslandBgAlphaPercent.coerceIn(55, 100) / 100f)
    val pillBrush = when (islandTheme) {
        SettingsStore.DynamicIslandTheme.DARK -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1F232B).copy(alpha = bgAlpha),
                Color(0xFF15181E).copy(alpha = bgAlpha),
            ),
        )
        SettingsStore.DynamicIslandTheme.ACCENT -> Brush.linearGradient(
            colors = listOf(
                neu.accent.copy(alpha = bgAlpha),
                neu.accent.copy(alpha = (bgAlpha * 0.92f).coerceIn(0.5f, 1f)),
            ),
        )
        SettingsStore.DynamicIslandTheme.NEUMORPHIC -> Brush.verticalGradient(
            colors = if (neu.isDark) {
                listOf(Color(0xFF23262D).copy(alpha = bgAlpha), Color(0xFF1A1C22).copy(alpha = bgAlpha))
            } else {
                listOf(Color(0xFFECEFF4).copy(alpha = bgAlpha), Color(0xFFDDE2EB).copy(alpha = bgAlpha))
            },
        )
    }

    val contentColor = when (islandTheme) {
        SettingsStore.DynamicIslandTheme.DARK -> Color.White
        SettingsStore.DynamicIslandTheme.ACCENT -> Color.White
        SettingsStore.DynamicIslandTheme.NEUMORPHIC -> neu.textPrimary
    }

    // Wire ALL Deep Studio parameters so every slider and toggle visibly affects the Live Preview!
    val scalePercent = settingsStore.dynamicIslandScalePercent.coerceIn(0, 100)
    val scaleFactor = (0.76f + (scalePercent / 100f) * 0.44f).coerceIn(0.70f, 1.22f)
    val collapsedHeightDp = (settingsStore.dynamicIslandCollapsedHeightDp.coerceIn(32, 56) * scaleFactor).coerceIn(28f, 68f).dp
    val expandedWidthFraction = (settingsStore.dynamicIslandExpandedWidthPercent.coerceIn(82, 100) / 100f)
    val fontScale = (settingsStore.dynamicIslandFontScalePercent.coerceIn(80, 125) / 100f)
    val borderWidth = settingsStore.dynamicIslandBorderWidthDp.coerceIn(0.5f, 3.0f).dp
    val shadowDepth = settingsStore.dynamicIslandShadowDepthDp.coerceIn(0, 20).dp
    val springDamping = (settingsStore.dynamicIslandSpringDampingPercent.coerceIn(55, 95) / 100f)
    val springStiffness = settingsStore.dynamicIslandSpringStiffness.coerceIn(180, 600).toFloat()
    val quickAppIconSize = (settingsStore.dynamicIslandQuickAppIconSizeDp.coerceIn(28, 48) * 0.45f).coerceIn(12f, 22f).dp
    val showAppNames = settingsStore.dynamicIslandShowAppNames
    val indicatorStyle = settingsStore.dynamicIslandIndicatorStyle
    val showJitterBadge = settingsStore.dynamicIslandShowJitterBadge
    val showSecurityBadge = settingsStore.dynamicIslandShowSecurityBadge
    val showNetworkActions = settingsStore.dynamicIslandShowNetworkActions

    val cornerDp = if (previewExpanded) 24.dp else (settingsStore.dynamicIslandCornerRadius.coerceIn(18, 42) * scaleFactor).coerceIn(16f, 40f).dp
    val previewShape = RoundedCornerShape(cornerDp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(neu.background.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = neu.accent.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColors.connected),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = when (s.language) {
                        FoxyLanguage.FA -> "پیش‌نمایش زنده (واکنش آنی به تمام تنظیمات)"
                        FoxyLanguage.AR -> "معاينة حية فورية"
                        FoxyLanguage.EN -> "Live Interactive Preview"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = neu.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(neu.accent.copy(alpha = 0.12f))
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val collapsedLabel = when (s.language) {
                    FoxyLanguage.FA -> "حالت بسته"
                    FoxyLanguage.AR -> "مصغر"
                    FoxyLanguage.EN -> "Collapsed"
                }
                val expandedLabel = when (s.language) {
                    FoxyLanguage.FA -> "حالت باز"
                    FoxyLanguage.AR -> "موسع"
                    FoxyLanguage.EN -> "Expanded"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!previewExpanded) neu.accent else Color.Transparent)
                        .clickable { previewExpanded = false }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = collapsedLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                        color = if (!previewExpanded) Color.White else neu.textSecondary,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (previewExpanded) neu.accent else Color.Transparent)
                        .clickable { previewExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = expandedLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                        color = if (previewExpanded) Color.White else neu.textSecondary,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Interactive Island Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = when (settingsStore.dynamicIslandPosition) {
                SettingsStore.DynamicIslandPosition.TOP_START -> Alignment.CenterStart
                SettingsStore.DynamicIslandPosition.TOP_END -> Alignment.CenterEnd
                SettingsStore.DynamicIslandPosition.TOP_CENTER -> Alignment.Center
            },
        ) {
            Box(
                modifier = Modifier
                    .dynamicIslandMotionEffect(
                        enabled = settingsStore.dynamicIslandAnimation,
                        style = settingsStore.dynamicIslandMotionStyle,
                        isConnected = true,
                        isExpanded = previewExpanded,
                        trafficRateBytesPerSec = 524_288L,
                        accentColor = statusColors.connected,
                        cornerRadius = cornerDp,
                    )
                    .then(
                        if (shadowDepth > 0.dp) {
                            Modifier.shadow(
                                elevation = shadowDepth,
                                shape = previewShape,
                                ambientColor = statusColors.connected.copy(alpha = 0.50f),
                                spotColor = statusColors.connected.copy(alpha = 0.70f),
                            )
                        } else {
                            Modifier
                        }
                    )
                    .then(
                        if (settingsStore.dynamicIslandGlowEnabled) Modifier.shadow(
                            elevation = maxOf(shadowDepth, 12.dp),
                            shape = previewShape,
                            ambientColor = statusColors.connected.copy(alpha = 0.82f),
                            spotColor = statusColors.connected.copy(alpha = 0.95f),
                        ) else Modifier
                    )
                    .clip(previewShape)
                    .background(pillBrush)
                    .border(
                        width = borderWidth,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                statusColors.connected.copy(alpha = 0.68f),
                                statusColors.connected.copy(alpha = 0.22f),
                            ),
                        ),
                        shape = previewShape,
                    )
                    .clickable { previewExpanded = !previewExpanded },
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = previewExpanded,
                    transitionSpec = {
                        (fadeIn(tween(150)) + scaleIn(
                            initialScale = 0.92f,
                            transformOrigin = TransformOrigin(0.5f, 0f),
                            animationSpec = spring(dampingRatio = springDamping, stiffness = springStiffness),
                        ))
                            .togetherWith(
                                fadeOut(tween(110)) + scaleOut(
                                    targetScale = 0.92f,
                                    transformOrigin = TransformOrigin(0.5f, 0f),
                                )
                            )
                            .using(null)
                    },
                    label = "preview-island-morph",
                ) { isExp ->
                    if (!isExp) {
                        Row(
                            modifier = Modifier
                                .heightIn(min = collapsedHeightDp)
                                .padding(
                                    horizontal = (14.dp * scaleFactor).coerceIn(10.dp, 22.dp),
                                    vertical = (7.dp * scaleFactor).coerceIn(5.dp, 14.dp),
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (settingsStore.dynamicIslandShowTowerWave) {
                                DynamicIslandStatusIndicator(
                                    indicatorStyle = indicatorStyle,
                                    active = settingsStore.dynamicIslandAnimation,
                                    color = statusColors.connected,
                                    settingsStore = settingsStore,
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(statusColors.connected),
                                )
                            }
                            Spacer(Modifier.width(7.dp))
                            if (settingsStore.dynamicIslandShowFlag) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(neu.accent.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp),
                                ) {
                                    Text(
                                        text = "🇩🇪",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (10f * fontScale).sp,
                                        ),
                                        color = neu.accent,
                                    )
                                }
                                Spacer(Modifier.width(5.dp))
                            }
                            if (settingsStore.dynamicIslandShowServerName && !settingsStore.dynamicIslandCompactTelemetry) {
                                Text(
                                    text = s.language.localizeCountryName("Germany", "DE"),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (12f * fontScale).sp,
                                    ),
                                    color = contentColor,
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            if (settingsStore.dynamicIslandShowTimer) {
                                Text(
                                    text = s.language.formatDurationLocalized(145L, alwaysShowHours = false),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (11f * fontScale).sp,
                                    ),
                                    color = contentColor,
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            if (settingsStore.dynamicIslandShowSpeed) {
                                Text(
                                    text = "\u200E↓2.4 MB/s",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (11f * fontScale).sp,
                                    ),
                                    color = statusColors.connected,
                                )
                            }
                            if (showJitterBadge && !settingsStore.dynamicIslandCompactTelemetry) {
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "\u200E±3ms",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (10f * fontScale).sp,
                                    ),
                                    color = statusColors.connected.copy(alpha = 0.9f),
                                )
                            }
                            if (settingsStore.dynamicIslandShowProtocolBadge && !settingsStore.dynamicIslandCompactTelemetry) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = activeProtocolBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (10f * fontScale).sp,
                                    ),
                                    color = neu.accent,
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(expandedWidthFraction)
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (settingsStore.dynamicIslandShowTowerWave) {
                                        DynamicIslandStatusIndicator(
                                            indicatorStyle = indicatorStyle,
                                            active = settingsStore.dynamicIslandAnimation,
                                            color = statusColors.connected,
                                            settingsStore = settingsStore,
                                        )
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    if (settingsStore.dynamicIslandShowFlag) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(neu.accent.copy(alpha = 0.15f))
                                                .padding(horizontal = 5.dp, vertical = 2.dp),
                                        ) {
                                            Text(
                                                text = "🇩🇪",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = fonts.monoFont,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (11f * fontScale).sp,
                                                ),
                                                color = neu.accent,
                                            )
                                        }
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Column {
                                        Text(
                                            text = s.language.localizeCountryName("Germany", "DE"),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontFamily = fonts.headingFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = (13.5f * fontScale).sp,
                                            ),
                                            color = contentColor,
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "\u200E${s.islandActiveEncrypted} • $activeProtocolBadge :443",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = fonts.monoFont,
                                                    fontSize = (10.5f * fontScale).sp,
                                                ),
                                                color = statusColors.connected,
                                            )
                                            if (showSecurityBadge) {
                                                Spacer(Modifier.width(5.dp))
                                                Text(
                                                    text = "• TLS 1.3",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = fonts.monoFont,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (9.5f * fontScale).sp,
                                                    ),
                                                    color = neu.accent,
                                                )
                                            }
                                        }
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = contentColor.copy(alpha = 0.75f),
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            if (showNetworkActions) {
                                Spacer(Modifier.height(7.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    listOf("Ping 38ms", "HTTP/2", "TG Proxy").forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(contentColor.copy(alpha = 0.11f))
                                                .padding(vertical = 4.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = fonts.monoFont,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (9.5f * fontScale).sp,
                                                ),
                                                color = contentColor,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                }
                            }
                            if (settingsStore.dynamicIslandShowQuickApps) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(
                                        "com.google.android.youtube" to "YouTube",
                                        "org.telegram.messenger" to "Telegram",
                                        "com.android.chrome" to "Chrome",
                                    ).forEach { (pkg, lbl) ->
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(contentColor.copy(alpha = 0.13f))
                                                .padding(horizontal = if (showAppNames) 8.dp else 6.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            QuickAppIconBadge(packageName = pkg, iconBitmap = null, size = quickAppIconSize)
                                            if (showAppNames) {
                                                Spacer(Modifier.width(5.dp))
                                                Text(
                                                    text = lbl,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontFamily = fonts.bodyFont,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = (10.5f * fontScale).sp,
                                                    ),
                                                    color = contentColor,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full Dynamic Island Customization Studio Modal (accessible from Dynamic Island & Settings).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DynamicIslandCustomizationModal(
    settingsStore: SettingsStore,
    onDismiss: () -> Unit,
) {
    val s = currentFoxyStrings
    val neu = currentNeumorphicColors
    val fonts = currentFoxyFonts

    var scalePercent by remember { mutableFloatStateOf(settingsStore.dynamicIslandScalePercent.toFloat()) }
    var bgAlphaPercent by remember { mutableFloatStateOf(settingsStore.dynamicIslandBgAlphaPercent.toFloat()) }
    var autoCollapseSec by remember { mutableStateOf(settingsStore.dynamicIslandAutoCollapseSec) }
    var motionStyle by remember { mutableStateOf(settingsStore.dynamicIslandMotionStyle) }
    var waveStyle by remember { mutableStateOf(settingsStore.dynamicIslandWaveStyle) }
    var waveSpeed by remember { mutableFloatStateOf(settingsStore.waveSpeedMultiplier) }
    var waveAmplitude by remember { mutableFloatStateOf(settingsStore.waveAmplitudeScale) }
    var waveGlow by remember { mutableStateOf(settingsStore.waveGlowEnabled) }
    var waveColorMode by remember { mutableStateOf(settingsStore.waveColorMode) }
    var draggable by remember { mutableStateOf(settingsStore.dynamicIslandDraggable) }

    var showQuickApps by remember { mutableStateOf(settingsStore.dynamicIslandShowQuickApps) }
    var showTowerWave by remember { mutableStateOf(settingsStore.dynamicIslandShowTowerWave) }
    var showTimer by remember { mutableStateOf(settingsStore.dynamicIslandShowTimer) }
    var showSpeed by remember { mutableStateOf(settingsStore.dynamicIslandShowSpeed) }
    var showPing by remember { mutableStateOf(settingsStore.dynamicIslandShowPing) }
    var showFlag by remember { mutableStateOf(settingsStore.dynamicIslandShowFlag) }
    var showServerName by remember { mutableStateOf(settingsStore.dynamicIslandShowServerName) }
    var showSessionData by remember { mutableStateOf(settingsStore.dynamicIslandShowSessionData) }
    var showIpBadge by remember { mutableStateOf(settingsStore.dynamicIslandShowIpBadge) }
    var showProtocolBadge by remember { mutableStateOf(settingsStore.dynamicIslandShowProtocolBadge) }
    var compactTelemetry by remember { mutableStateOf(settingsStore.dynamicIslandCompactTelemetry) }
    var hapticEnabled by remember { mutableStateOf(settingsStore.dynamicIslandHapticEnabled) }
    var showAnim by remember { mutableStateOf(settingsStore.dynamicIslandAnimation) }
    var pinnedApps by remember { mutableStateOf(settingsStore.dynamicIslandPinnedApps) }
    var showAppPicker by remember { mutableStateOf(false) }

    // Deep Micro-Detail Customization states
    var collapsedHeightDp by remember { mutableFloatStateOf(settingsStore.dynamicIslandCollapsedHeightDp.toFloat()) }
    var expandedWidthPercent by remember { mutableFloatStateOf(settingsStore.dynamicIslandExpandedWidthPercent.toFloat()) }
    var fontScalePercent by remember { mutableFloatStateOf(settingsStore.dynamicIslandFontScalePercent.toFloat()) }
    var borderWidthDp by remember { mutableFloatStateOf(settingsStore.dynamicIslandBorderWidthDp) }
    var shadowDepthDp by remember { mutableFloatStateOf(settingsStore.dynamicIslandShadowDepthDp.toFloat()) }
    var springDampingPercent by remember { mutableFloatStateOf(settingsStore.dynamicIslandSpringDampingPercent.toFloat()) }
    var springStiffness by remember { mutableFloatStateOf(settingsStore.dynamicIslandSpringStiffness.toFloat()) }
    var quickAppIconSizeDp by remember { mutableFloatStateOf(settingsStore.dynamicIslandQuickAppIconSizeDp.toFloat()) }
    var showAppNames by remember { mutableStateOf(settingsStore.dynamicIslandShowAppNames) }
    var doubleTapAction by remember { mutableStateOf(settingsStore.dynamicIslandDoubleTapAction) }
    var indicatorStyle by remember { mutableStateOf(settingsStore.dynamicIslandIndicatorStyle) }
    var showJitterBadge by remember { mutableStateOf(settingsStore.dynamicIslandShowJitterBadge) }
    var showSecurityBadge by remember { mutableStateOf(settingsStore.dynamicIslandShowSecurityBadge) }
    var showNetworkActions by remember { mutableStateOf(settingsStore.dynamicIslandShowNetworkActions) }

    if (showAppPicker) {
        DynamicIslandAppPickerDialog(
            current = pinnedApps,
            onDismiss = { showAppPicker = false },
            onConfirm = { newSet ->
                pinnedApps = newSet
                settingsStore.dynamicIslandPinnedApps = newSet
                showAppPicker = false
            },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = s.islandCustomizeModalTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = s.islandCustomizeModalSub,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                // Live Interactive Dynamic Island Preview at top of modal!
                DynamicIslandLivePreviewCard(settingsStore = settingsStore)
                Spacer(Modifier.height(14.dp))
                // 1. Linear Manual Scale Slider (0% - 100%)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = s.islandManualScaleTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = s.language.formatPercentLocalized(scalePercent.roundToInt()),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = fonts.monoFont,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        color = neu.accent,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = s.islandManualScaleSub,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                    color = neu.textSecondary,
                )
                Spacer(Modifier.height(6.dp))
                Slider(
                    value = scalePercent,
                    onValueChange = { newVal ->
                        scalePercent = newVal
                        settingsStore.dynamicIslandScalePercent = newVal.roundToInt()
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = neu.accent,
                        activeTrackColor = neu.accent,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = s.language.formatPercentLocalized(0),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont),
                        color = neu.textSecondary,
                    )
                    Text(
                        text = s.language.formatPercentLocalized(50),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont),
                        color = neu.textSecondary,
                    )
                    Text(
                        text = s.language.formatPercentLocalized(100),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont),
                        color = neu.textSecondary,
                    )
                }

                HorizontalDivider(
                    color = neu.textSecondary.copy(alpha = 0.14f),
                    modifier = Modifier.padding(vertical = 14.dp),
                )

                // 2. Dynamic Island Motion & Physics Animation Selector (12 Styles)
                Text(
                    text = s.islandMotionStyleTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = neu.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = s.islandMotionStyleSub,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                    color = neu.textSecondary,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    DynamicIslandMotionStyle.entries.forEach { item ->
                        val selected = motionStyle == item
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) neu.accent else neu.accent.copy(alpha = 0.12f))
                                .clickable {
                                    motionStyle = item
                                    settingsStore.dynamicIslandMotionStyle = item
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = item.localizedLabel(s.language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = if (selected) Color.White else neu.textPrimary,
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = neu.textSecondary.copy(alpha = 0.14f),
                    modifier = Modifier.padding(vertical = 14.dp),
                )

                // 3. Touch-Drag Free Repositioning & Reset Position
                IslandModalSwitchRow(
                    title = s.islandDraggableTitle,
                    checked = draggable,
                    onCheckedChange = {
                        draggable = it
                        settingsStore.dynamicIslandDraggable = it
                    },
                )
                Text(
                    text = s.islandDraggableSub,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                    color = neu.textSecondary,
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(neu.accent.copy(alpha = 0.12f))
                        .clickable {
                            settingsStore.resetDynamicIslandPosition()
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = null,
                            tint = neu.accent,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = s.islandResetPositionButton,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = fonts.headingFont,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = neu.accent,
                        )
                    }
                }

                HorizontalDivider(
                    color = neu.textSecondary.copy(alpha = 0.14f),
                    modifier = Modifier.padding(vertical = 14.dp),
                )

                // 4. Pinned Quick-Launch Apps (Enable/Disable Toggle + Manage/Add Apps)
                IslandModalSwitchRow(
                    title = s.islandShowQuickAppsTitle,
                    checked = showQuickApps,
                    onCheckedChange = {
                        showQuickApps = it
                        settingsStore.dynamicIslandShowQuickApps = it
                    },
                )
                Text(
                    text = s.islandShowQuickAppsSub,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                    color = neu.textSecondary,
                )
                if (showQuickApps) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(neu.accent.copy(alpha = 0.14f))
                            .clickable { showAppPicker = true }
                            .padding(vertical = 12.dp, horizontal = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Apps,
                                contentDescription = null,
                                tint = neu.accent,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${s.islandAddAppsButton} (${s.language.formatPercentLocalized(pinnedApps.size).replace("٪", "").replace("%", "")})",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = neu.accent,
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = neu.textSecondary.copy(alpha = 0.14f),
                    modifier = Modifier.padding(vertical = 14.dp),
                )

                // 5. Granular Displayed Content Toggles & Indicator Style
                Text(
                    text = s.islandContentSectionTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = neu.textSecondary,
                )
                Spacer(Modifier.height(8.dp))

                IslandModalSwitchRow(
                    title = s.islandShowTowerWaveTitle,
                    checked = showTowerWave,
                    onCheckedChange = {
                        showTowerWave = it
                        settingsStore.dynamicIslandShowTowerWave = it
                    },
                )
                if (showTowerWave) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = when (s.language) {
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> "تغییر نشانگر وضعیت و شبکه (Indicator Style)"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> "تغيير مؤشر الحالة والشبكة"
                            com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> "Status & Network Indicator Style"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textSecondary,
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        com.vauth.foxyvpn.data.DynamicIslandIndicatorStyle.entries.forEach { ind ->
                            val isSel = indicatorStyle == ind
                            val lbl = when (s.language) {
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA -> ind.labelFa
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.AR -> ind.labelAr
                                com.vauth.foxyvpn.ui.theme.FoxyLanguage.EN -> ind.labelEn
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) neu.accent else neu.accent.copy(alpha = 0.12f))
                                    .clickable {
                                        indicatorStyle = ind
                                        settingsStore.dynamicIslandIndicatorStyle = ind
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = lbl,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (isSel) Color.White else neu.textPrimary,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (s.language == com.vauth.foxyvpn.ui.theme.FoxyLanguage.FA) {
                            "سبک انیمیشن امواج اتصال در داینامیک آیلند"
                        } else {
                            "Dynamic Island Connection Wave Style"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textSecondary,
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        DynamicIslandWaveStyle.entries.forEach { item ->
                            val selected = waveStyle == item
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) neu.accent else neu.accent.copy(alpha = 0.12f))
                                    .clickable {
                                        waveStyle = item
                                        settingsStore.dynamicIslandWaveStyle = item
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = item.localizedLabel(s.language),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (selected) Color.White else neu.textPrimary,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    // Wave Color Palette Selector
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "پالت رنگ امواج"
                            FoxyLanguage.AR -> "لوحة ألوان الأمواج"
                            FoxyLanguage.EN -> "Wave Color Palette"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textSecondary,
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        com.vauth.foxyvpn.data.WaveColorMode.entries.forEach { mode ->
                            val selected = waveColorMode == mode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) neu.accent else neu.accent.copy(alpha = 0.12f))
                                    .clickable {
                                        waveColorMode = mode
                                        settingsStore.waveColorMode = mode
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = mode.localizedLabel(s.language),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (selected) Color.White else neu.textPrimary,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    // Wave Speed & Amplitude Sliders
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = when (s.language) {
                                FoxyLanguage.FA -> "سرعت حرکت امواج"
                                FoxyLanguage.AR -> "سرعة حركة الأمواج"
                                FoxyLanguage.EN -> "Wave Animation Speed"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = neu.textPrimary,
                        )
                        Text(
                            text = "${(waveSpeed * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                            color = neu.accent,
                        )
                    }
                    Slider(
                        value = waveSpeed,
                        onValueChange = {
                            waveSpeed = it
                            settingsStore.waveSpeedMultiplier = it
                        },
                        valueRange = 0.4f..2.5f,
                        colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = when (s.language) {
                                FoxyLanguage.FA -> "دامنه و بلندی امواج"
                                FoxyLanguage.AR -> "سعة وارتفاع الأمواج"
                                FoxyLanguage.EN -> "Wave Amplitude & Height"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = neu.textPrimary,
                        )
                        Text(
                            text = "${(waveAmplitude * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                            color = neu.accent,
                        )
                    }
                    Slider(
                        value = waveAmplitude,
                        onValueChange = {
                            waveAmplitude = it
                            settingsStore.waveAmplitudeScale = it
                        },
                        valueRange = 0.5f..1.8f,
                        colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                    )

                    IslandModalSwitchRow(
                        title = when (s.language) {
                            FoxyLanguage.FA -> "هاله نورانی امواج (Wave Glow)"
                            FoxyLanguage.AR -> "توهج الأمواج المضيء"
                            FoxyLanguage.EN -> "Luminous Wave Halo Glow"
                        },
                        checked = waveGlow,
                        onCheckedChange = {
                            waveGlow = it
                            settingsStore.waveGlowEnabled = it
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Island Background Opacity & Auto-Collapse
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "غلظت پس‌زمینه داینامیک آیلند"
                            FoxyLanguage.AR -> "شفافية خلفية الجزيرة التفاعلية"
                            FoxyLanguage.EN -> "Island Background Opacity"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = s.language.formatPercentLocalized(bgAlphaPercent.roundToInt()),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = bgAlphaPercent,
                    onValueChange = {
                        bgAlphaPercent = it
                        settingsStore.dynamicIslandBgAlphaPercent = it.roundToInt()
                    },
                    valueRange = 55f..100f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                Text(
                    text = when (s.language) {
                        FoxyLanguage.FA -> "بسته شدن خودکار کارت بازشده"
                        FoxyLanguage.AR -> "الإغلاق التلقائي للبطاقة الموسعة"
                        FoxyLanguage.EN -> "Auto-Collapse Expanded Sheet"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = neu.textSecondary,
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 5, 10, 15, 30).forEach { sec ->
                        val selected = autoCollapseSec == sec
                        val lbl = if (sec == 0) {
                            when (s.language) {
                                FoxyLanguage.FA -> "دستی"
                                FoxyLanguage.AR -> "يدوي"
                                FoxyLanguage.EN -> "Manual"
                            }
                        } else {
                            "${s.language.localizeNumber(sec)}s"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) neu.accent else neu.accent.copy(alpha = 0.12f))
                                .clickable {
                                    autoCollapseSec = sec
                                    settingsStore.dynamicIslandAutoCollapseSec = sec
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = lbl,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (selected) Color.White else neu.textPrimary,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                IslandModalSwitchRow(
                    title = s.islandShowTimerTitle,
                    checked = showTimer,
                    onCheckedChange = {
                        showTimer = it
                        settingsStore.dynamicIslandShowTimer = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandShowSpeedTitle,
                    checked = showSpeed,
                    onCheckedChange = {
                        showSpeed = it
                        settingsStore.dynamicIslandShowSpeed = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandShowPingTitle,
                    checked = showPing,
                    onCheckedChange = {
                        showPing = it
                        settingsStore.dynamicIslandShowPing = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandShowFlagTitle,
                    checked = showFlag,
                    onCheckedChange = {
                        showFlag = it
                        settingsStore.dynamicIslandShowFlag = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandShowServerNameTitle,
                    checked = showServerName,
                    onCheckedChange = {
                        showServerName = it
                        settingsStore.dynamicIslandShowServerName = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandShowSessionDataTitle,
                    checked = showSessionData,
                    onCheckedChange = {
                        showSessionData = it
                        settingsStore.dynamicIslandShowSessionData = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandShowIpBadgeTitle,
                    checked = showIpBadge,
                    onCheckedChange = {
                        showIpBadge = it
                        settingsStore.dynamicIslandShowIpBadge = it
                    },
                )
                IslandModalSwitchRow(
                    title = when (s.language) {
                        FoxyLanguage.FA -> "نمایش نشان پروتکل فعال (H2/H3/TLS)"
                        FoxyLanguage.AR -> "إظهار شارة البروتوكول النشط"
                        FoxyLanguage.EN -> "Show Active Protocol Badge (H2/H3/TLS)"
                    },
                    checked = showProtocolBadge,
                    onCheckedChange = {
                        showProtocolBadge = it
                        settingsStore.dynamicIslandShowProtocolBadge = it
                    },
                )
                IslandModalSwitchRow(
                    title = when (s.language) {
                        FoxyLanguage.FA -> "حالت فشرده کپسول بسته (Minimal Pill)"
                        FoxyLanguage.AR -> "الوضع المضغوط للكبسولة"
                        FoxyLanguage.EN -> "Compact Minimal Collapsed Pill"
                    },
                    checked = compactTelemetry,
                    onCheckedChange = {
                        compactTelemetry = it
                        settingsStore.dynamicIslandCompactTelemetry = it
                    },
                )
                IslandModalSwitchRow(
                    title = when (s.language) {
                        FoxyLanguage.FA -> "لرزش لمسی هنگام باز و بسته شدن (Haptic)"
                        FoxyLanguage.AR -> "اهتزاز لمسي عند الفتح والإغلاق"
                        FoxyLanguage.EN -> "Haptic Feedback on Expand/Collapse"
                    },
                    checked = hapticEnabled,
                    onCheckedChange = {
                        hapticEnabled = it
                        settingsStore.dynamicIslandHapticEnabled = it
                    },
                )
                IslandModalSwitchRow(
                    title = s.islandAnimationTitle,
                    checked = showAnim,
                    onCheckedChange = {
                        showAnim = it
                        settingsStore.dynamicIslandAnimation = it
                    },
                )

                HorizontalDivider(
                    color = neu.textSecondary.copy(alpha = 0.14f),
                    modifier = Modifier.padding(vertical = 14.dp),
                )

                // 6. Deep Micro-Detail Geometry, Physics & Interaction Studio
                Text(
                    text = when (s.language) {
                        FoxyLanguage.FA -> "تنظیمات مهندسی و عمیق جزئیات (Micro-Detail Studio)"
                        FoxyLanguage.AR -> "الإعدادات الهندسية العميقة للجزيرة"
                        FoxyLanguage.EN -> "Deep Micro-Detail Geometry & Physics Studio"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = neu.accent,
                )
                Spacer(Modifier.height(8.dp))

                // Collapsed Height Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "ارتفاع کپسول در حالت بسته (dp)"
                            FoxyLanguage.AR -> "ارتفاع الكبسولة المغلقة (dp)"
                            FoxyLanguage.EN -> "Collapsed Pill Height (dp)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${collapsedHeightDp.roundToInt()} dp",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = collapsedHeightDp,
                    onValueChange = {
                        collapsedHeightDp = it
                        settingsStore.dynamicIslandCollapsedHeightDp = it.roundToInt()
                    },
                    valueRange = 32f..56f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                // Expanded Sheet Width %
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "پهنای کارت در حالت بازشده (%)"
                            FoxyLanguage.AR -> "عرض البطاقة الموسعة (%)"
                            FoxyLanguage.EN -> "Expanded Sheet Width (%)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${expandedWidthPercent.roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = expandedWidthPercent,
                    onValueChange = {
                        expandedWidthPercent = it
                        settingsStore.dynamicIslandExpandedWidthPercent = it.roundToInt()
                    },
                    valueRange = 82f..100f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                // Font Scale %
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "مقیاس فونت و اعداد داخل داینامیک"
                            FoxyLanguage.AR -> "حجم الخط والأرقام داخل الجزيرة"
                            FoxyLanguage.EN -> "Typography & Telemetry Font Scale"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${fontScalePercent.roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = fontScalePercent,
                    onValueChange = {
                        fontScalePercent = it
                        settingsStore.dynamicIslandFontScalePercent = it.roundToInt()
                    },
                    valueRange = 80f..125f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                // Border Width (dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "ضخامت کادر دور داینامیک آیلند"
                            FoxyLanguage.AR -> "سمك إطار الجزيرة التفاعلية"
                            FoxyLanguage.EN -> "Border Stroke Thickness (dp)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f dp", borderWidthDp),
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = borderWidthDp,
                    onValueChange = {
                        borderWidthDp = it
                        settingsStore.dynamicIslandBorderWidthDp = it
                    },
                    valueRange = 0.5f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                // Shadow Depth (dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "عمق سایه سه‌بعدی (Shadow Elevation)"
                            FoxyLanguage.AR -> "عمق الظل ثلاثي الأبعاد"
                            FoxyLanguage.EN -> "3D Shadow Depth (dp)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${shadowDepthDp.roundToInt()} dp",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = shadowDepthDp,
                    onValueChange = {
                        shadowDepthDp = it
                        settingsStore.dynamicIslandShadowDepthDp = it.roundToInt()
                    },
                    valueRange = 0f..20f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                // Spring Physics Damping & Stiffness
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "ضریب نرمی فنر انیمیشن (Spring Damping)"
                            FoxyLanguage.AR -> "معامل نعومة الزنبرك"
                            FoxyLanguage.EN -> "Spring Physics Damping (%)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${springDampingPercent.roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = springDampingPercent,
                    onValueChange = {
                        springDampingPercent = it
                        settingsStore.dynamicIslandSpringDampingPercent = it.roundToInt()
                    },
                    valueRange = 55f..95f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "سرعت و چابکی فنر باز/بسته شدن (Stiffness)"
                            FoxyLanguage.AR -> "صلابة وسرعة الزنبرك"
                            FoxyLanguage.EN -> "Spring Physics Stiffness"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${springStiffness.roundToInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = springStiffness,
                    onValueChange = {
                        springStiffness = it
                        settingsStore.dynamicIslandSpringStiffness = it.roundToInt()
                    },
                    valueRange = 180f..600f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                // Quick App Icon Size & Show App Names
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "اندازه آیکون برنامه‌های سریع (dp)"
                            FoxyLanguage.AR -> "حجم أيقونات التطبيقات السريعة"
                            FoxyLanguage.EN -> "Quick-App Icon Scale (dp)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = neu.textPrimary,
                    )
                    Text(
                        text = "${quickAppIconSizeDp.roundToInt()} dp",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont, fontWeight = FontWeight.Bold),
                        color = neu.accent,
                    )
                }
                Slider(
                    value = quickAppIconSizeDp,
                    onValueChange = {
                        quickAppIconSizeDp = it
                        settingsStore.dynamicIslandQuickAppIconSizeDp = it.roundToInt()
                    },
                    valueRange = 28f..48f,
                    colors = SliderDefaults.colors(thumbColor = neu.accent, activeTrackColor = neu.accent),
                )

                IslandModalSwitchRow(
                    title = when (s.language) {
                        FoxyLanguage.FA -> "نمایش نام برنامه‌ها کنار آیکون در نوار سریع"
                        FoxyLanguage.AR -> "إظهار أسماء التطبيقات بجانب الأيقونة"
                        FoxyLanguage.EN -> "Show App Labels Next to Quick-App Icons"
                    },
                    checked = showAppNames,
                    onCheckedChange = {
                        showAppNames = it
                        settingsStore.dynamicIslandShowAppNames = it
                    },
                )

                Spacer(Modifier.height(6.dp))
                Text(
                    text = when (s.language) {
                        FoxyLanguage.FA -> "عملیات دو بار ضربه (Double-Tap) روی داینامیک آیلند"
                        FoxyLanguage.AR -> "إجراء النقر المزدوج على الجزيرة"
                        FoxyLanguage.EN -> "Double-Tap Action on Dynamic Island"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = neu.textSecondary,
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    com.vauth.foxyvpn.data.DynamicIslandDoubleTapAction.entries.forEach { action ->
                        val selected = doubleTapAction == action
                        val lbl = when (s.language) {
                            FoxyLanguage.FA -> action.labelFa
                            FoxyLanguage.AR -> action.labelAr
                            FoxyLanguage.EN -> action.labelEn
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) neu.accent else neu.accent.copy(alpha = 0.12f))
                                .clickable {
                                    doubleTapAction = action
                                    settingsStore.dynamicIslandDoubleTapAction = action
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = lbl,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (selected) Color.White else neu.textPrimary,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(s.save, fontWeight = FontWeight.Bold)
            }
        },
    )
}

@Composable
private fun IslandModalSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val neu = currentNeumorphicColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = neu.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = neu.accent,
            ),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DynamicIslandAppPickerDialog(
    current: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit,
) {
    val context = LocalContext.current
    val s = currentFoxyStrings
    val neu = currentNeumorphicColors
    val fonts = currentFoxyFonts
    var selected by remember { mutableStateOf(current) }
    var searchQuery by remember { mutableStateOf("") }

    // Query ALL launchable apps on the device (including pre-installed YouTube, Chrome, etc.)
    // plus merge known popular presets so the user can always toggle YouTube, Telegram, Chrome, Instagram, WhatsApp, X, Spotify, etc.
    val installedApps by produceState<List<IslandPinnedAppInfo>>(initialValue = emptyList()) {
        value = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolved = runCatching { pm.queryIntentActivities(launcherIntent, 0) }.getOrDefault(emptyList())
            val fromDevice = resolved
                .mapNotNull { resolveInfo ->
                    val pkg = resolveInfo.activityInfo?.packageName ?: return@mapNotNull null
                    if (pkg == context.packageName) return@mapNotNull null
                    val label = runCatching { resolveInfo.loadLabel(pm).toString() }.getOrDefault(pkg)
                    val iconBmp = runCatching { drawableToImageBitmap(resolveInfo.loadIcon(pm)) }.getOrNull()
                    IslandPinnedAppInfo(pkg, label, iconBmp)
                }
                .distinctBy { it.packageName }

            val presetExtras = KNOWN_APP_FALLBACK_LABELS.entries
                .filter { (pkg, _) -> fromDevice.none { it.packageName == pkg } }
                .map { (pkg, label) ->
                    val iconBmp = runCatching { drawableToImageBitmap(pm.getApplicationIcon(pkg)) }.getOrNull()
                    IslandPinnedAppInfo(pkg, label, iconBmp)
                }

            (presetExtras + fromDevice).sortedWith(
                compareByDescending<IslandPinnedAppInfo> { KNOWN_APP_FALLBACK_LABELS.containsKey(it.packageName) }
                    .thenBy { it.label.lowercase() }
            )
        }
    }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter {
            it.label.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = s.islandPinnedAppsTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = s.islandPinnedAppsSub,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        text = {
            Column(Modifier.height(420.dp)) {
                // Quick Popular App Chips
                Text(
                    text = when (s.language) {
                        AppLanguage.FA -> "برنامه‌های محبوب (انتخاب سریع):"
                        AppLanguage.AR -> "التطبيقات الشائعة (اختيار سريع):"
                        AppLanguage.EN -> "Popular Quick Shortcuts:"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = neu.textSecondary,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    KNOWN_APP_FALLBACK_LABELS.forEach { (pkg, label) ->
                        val isPinned = selected.contains(pkg)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isPinned) neu.accent else neu.accent.copy(alpha = 0.12f))
                                .clickable {
                                    selected = if (isPinned) selected - pkg else selected + pkg
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                QuickAppIconBadge(packageName = pkg, iconBitmap = null, size = 14.dp)
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = if (isPinned) "Added: $label" else "+ $label",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (isPinned) Color.White else neu.textPrimary,
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(s.dialogSearchAppsPlaceholder) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                if (installedApps.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(Modifier.weight(1f)) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            val isChecked = selected.contains(app.packageName)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selected = if (isChecked) selected - app.packageName
                                        else selected + app.packageName
                                    }
                                    .padding(vertical = 4.dp),
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { check ->
                                        selected = if (check) selected + app.packageName
                                        else selected - app.packageName
                                    },
                                )
                                QuickAppIconBadge(
                                    packageName = app.packageName,
                                    iconBitmap = app.iconBitmap,
                                    size = 24.dp,
                                )
                                Column(Modifier.padding(start = 10.dp)) {
                                    Text(app.label, maxLines = 1, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        app.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text(s.save, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(s.cancel)
            }
        },
    )
}

