package com.vauth.foxyvpn.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vauth.foxyvpn.data.DynamicIslandLedStyle
import com.vauth.foxyvpn.data.DynamicIslandMotionStyle
import com.vauth.foxyvpn.data.HeroWaveAnimationStyle
import com.vauth.foxyvpn.data.WaveColorMode
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 1. Staggered Entrance Modifier (Fade + Slide Up + Subtle Scale)
 */
fun Modifier.staggeredEntrance(
    index: Int,
    delayPerItemMs: Int = 55,
    durationMs: Int = 420,
): Modifier = composed {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((index.coerceAtLeast(0) * delayPerItemMs).toLong())
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing),
        )
    }
    this.graphicsLayer {
        alpha = anim.value
        translationY = (1f - anim.value) * 32f
        val s = 0.96f + (0.04f * anim.value)
        scaleX = s
        scaleY = s
    }
}

/**
 * 2. Tactile Neumorphic Press-Bounce Modifier
 */
fun Modifier.neumorphicTouchBounce(
    pressedScale: Float = 0.965f,
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "touch-bounce-scale",
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

/**
 * 3. Shake-on-Error Horizontal Keyframe Modifier
 */
fun Modifier.shakeOnTrigger(triggerKey: Any?): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    LaunchedEffect(triggerKey) {
        if (triggerKey != null) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 460
                    0f at 0
                    (-18f) at 60
                    18f at 120
                    (-14f) at 180
                    14f at 240
                    (-8f) at 310
                    8f at 380
                    0f at 460
                },
            )
        }
    }
    this.graphicsLayer {
        translationX = offsetX.value
    }
}

/**
 * 4. Floating / Levitating Idle Animation Modifier
 */
fun Modifier.subtleFloatAnimation(
    amplitudePx: Float = 8f,
    durationMs: Int = 2200,
): Modifier = composed {
    val infinite = rememberInfiniteTransition(label = "float-idle")
    val dy by infinite.animateFloat(
        initialValue = -amplitudePx,
        targetValue = amplitudePx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float-dy",
    )
    this.graphicsLayer {
        translationY = dy
    }
}

/**
 * 4b. Automotive Sequential Chasing RGB LED Border Modifier (14 Ultra-Smooth 120Hz Styles)
 * Uses continuous frame-delta integration so changing speed or style never stutters or resets.
 */
fun Modifier.automotiveLedBorder(
    enabled: Boolean = true,
    style: DynamicIslandLedStyle = DynamicIslandLedStyle.CYBER_RAINBOW,
    customColorHex: Long = 0L,
    speedMultiplier: Float = 1.0f,
    cornerRadius: Dp = 28.dp,
    borderWidth: Dp = 2.4.dp,
    glowRadius: Dp = 8.dp,
): Modifier = composed {
    if (!enabled) return@composed this

    val clampedSpeed = speedMultiplier.coerceIn(0.3f, 2.5f)
    val rotationDurationMs = (2400f / clampedSpeed).toInt().coerceIn(750, 8000)

    val infinite = rememberInfiniteTransition(label = "automotive-led-border")
    val angleDeg by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = rotationDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "led-angle",
    )
    val pulseGlow by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "led-pulse-glow",
    )

    val palette = remember(style, customColorHex) {
        if (customColorHex != 0L) {
            val base = Color(customColorHex)
            listOf(
                base,
                Color.White,
                base.copy(alpha = 0.85f),
                base.copy(alpha = 0.22f),
                base.copy(alpha = 0.90f),
                base,
            )
        } else when (style) {
            DynamicIslandLedStyle.CYBER_RAINBOW -> listOf(
                Color(0xFF00F5D4),
                Color(0xFF00BBF9),
                Color(0xFF7B2CBF),
                Color(0xFFFF007F),
                Color(0xFFFF6D00),
                Color(0xFFFFEA00),
                Color(0xFF00F5D4),
            )
            DynamicIslandLedStyle.FOXY_FIRE -> listOf(
                Color(0xFFFF3D00),
                Color(0xFFFF6D00),
                Color(0xFFFFAB00),
                Color(0xFFFFEA00),
                Color(0xFFFF1744),
                Color(0xFFFF3D00),
            )
            DynamicIslandLedStyle.EMERALD_PULSE -> listOf(
                Color(0xFF00E676),
                Color(0xFF00BFA5),
                Color(0xFF18FFFF),
                Color(0xFF10B981),
                Color(0xFF69F0AE),
                Color(0xFF00E676),
            )
            DynamicIslandLedStyle.POLICE_STROBE -> listOf(
                Color(0xFFFF1744),
                Color(0xFFFF5252),
                Color(0xFFFFFFFF),
                Color(0xFF2979FF),
                Color(0xFF00E5FF),
                Color(0xFFFF1744),
            )
            DynamicIslandLedStyle.SOLAR_GOLD -> listOf(
                Color(0xFFFFD700),
                Color(0xFFFFA000),
                Color(0xFFFFF59D),
                Color(0xFFFF6F00),
                Color(0xFFFFD700),
            )
            DynamicIslandLedStyle.AURORA_BOREALIS -> listOf(
                Color(0xFF00F5A0),
                Color(0xFF00D9F5),
                Color(0xFF6366F1),
                Color(0xFFA855F7),
                Color(0xFF10B981),
                Color(0xFF00F5A0),
            )
            DynamicIslandLedStyle.OCEAN_BIOLUME -> listOf(
                Color(0xFF00E5FF),
                Color(0xFF0284C7),
                Color(0xFF38BDF8),
                Color(0xFF67E8F9),
                Color(0xFF1D4ED8),
                Color(0xFF00E5FF),
            )
            DynamicIslandLedStyle.ROYAL_NEBULA -> listOf(
                Color(0xFFC084FC),
                Color(0xFF7C3AED),
                Color(0xFFEC4899),
                Color(0xFF6366F1),
                Color(0xFFF472B6),
                Color(0xFFC084FC),
            )
            DynamicIslandLedStyle.CYBERPUNK_SYNTH -> listOf(
                Color(0xFFFF007F),
                Color(0xFF00F0FF),
                Color(0xFFFCEE09),
                Color(0xFF7928CA),
                Color(0xFFFF007F),
            )
            DynamicIslandLedStyle.LIQUID_MERCURY -> listOf(
                Color(0xFFFFFFFF),
                Color(0xFF94A3B8),
                Color(0xFFE2E8F0),
                Color(0xFF38BDF8),
                Color(0xFFCBD5E1),
                Color(0xFFFFFFFF),
            )
            DynamicIslandLedStyle.ARCTIC_FROST -> listOf(
                Color(0xFFE0F2FE),
                Color(0xFF7DD3FC),
                Color(0xFF38BDF8),
                Color(0xFFBAE6FD),
                Color(0xFF0284C7),
                Color(0xFFE0F2FE),
            )
            DynamicIslandLedStyle.SUNSET_MIAMI -> listOf(
                Color(0xFFFF5E62),
                Color(0xFFFF9966),
                Color(0xFFF43F5E),
                Color(0xFFA855F7),
                Color(0xFFFB7185),
                Color(0xFFFF5E62),
            )
            DynamicIslandLedStyle.QUANTUM_PLASMA -> listOf(
                Color(0xFF22D3EE),
                Color(0xFF818CF8),
                Color(0xFFF43F5E),
                Color(0xFF10B981),
                Color(0xFF22D3EE),
            )
            DynamicIslandLedStyle.SAKURA_BLOSSOM -> listOf(
                Color(0xFFFFB7C5),
                Color(0xFFF472B6),
                Color(0xFFFBCFE8),
                Color(0xFFEC4899),
                Color(0xFFFDA4AF),
                Color(0xFFFFB7C5),
            )
        }
    }

    this.drawWithContent {
        drawContent()
        val w = size.width
        val h = size.height
        if (!w.isFinite() || !h.isFinite()) return@drawWithContent
        val strokePx = borderWidth.toPx().coerceAtLeast(1f)
        if (w <= strokePx * 2.5f || h <= strokePx * 2.5f) return@drawWithContent

        val cornerPx = cornerRadius.toPx().coerceIn(0f, minOf(w, h) / 2f)
        val center = Offset(w / 2f, h / 2f)

        val ledBrush = runCatching {
            val shader = SweepGradientShader(
                center = center,
                colors = palette,
                colorStops = null,
            )
            val matrix = android.graphics.Matrix().apply {
                setRotate(angleDeg, center.x, center.y)
            }
            shader.setLocalMatrix(matrix)
            ShaderBrush(shader)
        }.getOrElse {
            Brush.sweepGradient(
                colors = palette,
                center = center,
            )
        }
        val rectTopLeft = Offset(strokePx / 2f, strokePx / 2f)
        val rectSize = Size((w - strokePx).coerceAtLeast(1f), (h - strokePx).coerceAtLeast(1f))
        val cr = CornerRadius(cornerPx, cornerPx)

        // Outer soft ambient neon diffuser layer (HWUI-safe multi-pass stroke)
        drawRoundRect(
            brush = ledBrush,
            topLeft = rectTopLeft,
            size = rectSize,
            cornerRadius = cr,
            alpha = (0.24f * pulseGlow).coerceIn(0.08f, 0.40f),
            style = Stroke(width = strokePx * 2.6f, cap = StrokeCap.Round),
        )
        // Mid neon bloom layer
        drawRoundRect(
            brush = ledBrush,
            topLeft = rectTopLeft,
            size = rectSize,
            cornerRadius = cr,
            alpha = (0.48f * pulseGlow).coerceIn(0.18f, 0.65f),
            style = Stroke(width = strokePx * 1.6f, cap = StrokeCap.Round),
        )
        // Crisp high-brightness LED core tube
        drawRoundRect(
            brush = ledBrush,
            topLeft = rectTopLeft,
            size = rectSize,
            cornerRadius = cr,
            alpha = 0.98f,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
        )
    }
}

/**
 * 4c. Ultra-Smooth 12-Mode Dynamic Island Motion & Shader Physics Modifier
 * Combines 60/120Hz frame-synchronized motion physics + surface shaders so the Dynamic Island
 * feels alive, fluid, and responsive.
 */
fun Modifier.dynamicIslandMotionEffect(
    enabled: Boolean = true,
    style: DynamicIslandMotionStyle = DynamicIslandMotionStyle.LIQUID_SPRING,
    isConnected: Boolean = false,
    isExpanded: Boolean = false,
    trafficRateBytesPerSec: Long = 0L,
    accentColor: Color = Color(0xFF00B0FF),
    cornerRadius: Dp = 28.dp,
): Modifier = composed {
    if (!enabled) return@composed this

    val infinite = rememberInfiniteTransition(label = "island-motion-physics")
    val phaseRad by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isConnected) 2600 else 3800,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "island-phase-rad",
    )
    val shimmerProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "island-shimmer-prog",
    )

    val trafficFactor = (trafficRateBytesPerSec.toFloat() / (512f * 1024f)).coerceIn(0f, 1f)

    this
        .graphicsLayer {
            when (style) {
                DynamicIslandMotionStyle.LIQUID_SPRING -> {
                    val s = if (isExpanded) 1f else 1f + 0.012f * sin(phaseRad)
                    scaleX = s
                    scaleY = s
                    translationY = if (isExpanded) 0f else sin(phaseRad) * 2.0f
                }
                DynamicIslandMotionStyle.ZERO_GRAVITY -> {
                    translationY = if (isExpanded) 0f else sin(phaseRad) * 4.5f
                    translationX = if (isExpanded) 0f else cos(phaseRad) * 2.2f
                    rotationZ = if (isExpanded) 0f else sin(phaseRad) * 0.65f
                }
                DynamicIslandMotionStyle.BREATHING_HALO -> {
                    val breath = 1f + 0.018f * ((sin(phaseRad) + 1f) * 0.5f)
                    scaleX = if (isExpanded) 1f else breath
                    scaleY = if (isExpanded) 1f else breath
                }
                DynamicIslandMotionStyle.SONIC_WAVE_SYNC -> {
                    val dynamicAmp = 0.008f + trafficFactor * 0.030f
                    val s = if (isExpanded) 1f else 1f + sin(phaseRad * 2f) * dynamicAmp
                    scaleX = s
                    scaleY = s
                }
                DynamicIslandMotionStyle.HEARTBEAT_PULSE -> {
                    val t = (phaseRad / (2f * PI.toFloat())).coerceIn(0f, 1f)
                    val beat = when {
                        t < 0.14f -> sin((t / 0.14f) * PI.toFloat()) * 0.024f
                        t in 0.20f..0.34f -> sin(((t - 0.20f) / 0.14f) * PI.toFloat()) * 0.015f
                        else -> 0f
                    }
                    val s = if (isExpanded) 1f else 1f + beat
                    scaleX = s
                    scaleY = s
                }
                DynamicIslandMotionStyle.PRISM_SHIMMER -> {
                    translationY = if (isExpanded) 0f else sin(phaseRad) * 1.6f
                }
                DynamicIslandMotionStyle.MAGNETIC_ELASTIC -> {
                    val stretchX = if (isExpanded) 1f else 1f + sin(phaseRad) * 0.015f
                    val stretchY = if (isExpanded) 1f else 1f - sin(phaseRad) * 0.011f
                    scaleX = stretchX
                    scaleY = stretchY
                }
                DynamicIslandMotionStyle.STARLIGHT_ORBIT -> {
                    translationY = if (isExpanded) 0f else sin(phaseRad) * 2.0f
                }
                DynamicIslandMotionStyle.CYBER_GLITCH_PULSE -> {
                    val microPulse = sin(phaseRad * 3f)
                    val s = if (isExpanded) 1f else 1f + (if (microPulse > 0.85f) 0.016f else 0.004f * microPulse)
                    scaleX = s
                    scaleY = s
                    translationX = if (isExpanded) 0f else (if (microPulse > 0.92f) 2.2f else 0f)
                }
                DynamicIslandMotionStyle.AURORA_WAVE_FLOW -> {
                    translationY = if (isExpanded) 0f else sin(phaseRad) * 2.6f
                    val s = if (isExpanded) 1f else 1f + 0.008f * cos(phaseRad)
                    scaleX = s
                    scaleY = s
                }
                DynamicIslandMotionStyle.GRAVITY_PENDULUM -> {
                    rotationZ = if (isExpanded) 0f else sin(phaseRad) * 1.35f
                    translationX = if (isExpanded) 0f else sin(phaseRad) * 3.4f
                    translationY = if (isExpanded) 0f else abs(cos(phaseRad)) * 1.8f
                }
                DynamicIslandMotionStyle.QUANTUM_PARTICLES -> {
                    val s = if (isExpanded) 1f else 1f + 0.010f * sin(phaseRad * 2f)
                    scaleX = s
                    scaleY = s
                }
            }
        }
        .drawWithContent {
            drawContent()
            val w = size.width
            val h = size.height
            if (w <= 8f || h <= 8f) return@drawWithContent
            val rPx = cornerRadius.toPx().coerceAtLeast(0f)

            when (style) {
                DynamicIslandMotionStyle.PRISM_SHIMMER,
                DynamicIslandMotionStyle.LIQUID_SPRING -> {
                    val sweepX = (-0.35f + shimmerProgress * 1.7f) * w
                    val shimmerAlpha = if (style == DynamicIslandMotionStyle.PRISM_SHIMMER) 0.18f else 0.08f
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = shimmerAlpha),
                                accentColor.copy(alpha = shimmerAlpha * 0.65f),
                                Color.Transparent,
                            ),
                            start = Offset(sweepX - w * 0.25f, 0f),
                            end = Offset((sweepX + w * 0.25f).coerceAtLeast(sweepX - w * 0.25f + 1f), h),
                        ),
                        cornerRadius = CornerRadius(rPx, rPx),
                    )
                }
                DynamicIslandMotionStyle.AURORA_WAVE_FLOW -> {
                    val shift = sin(phaseRad) * w * 0.3f
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00F5D4).copy(alpha = 0.12f),
                                accentColor.copy(alpha = 0.14f),
                                Color(0xFFA855F7).copy(alpha = 0.12f),
                            ),
                            start = Offset(shift, 0f),
                            end = Offset(w + shift, h),
                        ),
                        cornerRadius = CornerRadius(rPx, rPx),
                    )
                }
                DynamicIslandMotionStyle.CYBER_GLITCH_PULSE -> {
                    val scanY = (shimmerProgress * h).coerceIn(0f, h)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                accentColor.copy(alpha = 0.16f),
                                Color.Transparent,
                            ),
                            startY = (scanY - 10.dp.toPx()).coerceAtLeast(0f),
                            endY = (scanY + 10.dp.toPx()).coerceAtMost(h),
                        ),
                        cornerRadius = CornerRadius(rPx, rPx),
                    )
                }
                DynamicIslandMotionStyle.BREATHING_HALO,
                DynamicIslandMotionStyle.SONIC_WAVE_SYNC -> {
                    val inset = 1.5.dp.toPx()
                    if (w > inset * 2f && h > inset * 2f) {
                        val haloAlpha = (0.14f + 0.12f * sin(phaseRad) + trafficFactor * 0.14f).coerceIn(0.05f, 0.36f)
                        drawRoundRect(
                            color = accentColor.copy(alpha = haloAlpha),
                            topLeft = Offset(inset, inset),
                            size = Size(w - inset * 2f, h - inset * 2f),
                            cornerRadius = CornerRadius(rPx, rPx),
                            style = Stroke(width = 1.6.dp.toPx()),
                        )
                    }
                }
                DynamicIslandMotionStyle.STARLIGHT_ORBIT,
                DynamicIslandMotionStyle.QUANTUM_PARTICLES -> {
                    val cx = w / 2f
                    val cy = h / 2f
                    val rx = ((w / 2f) - 4.dp.toPx()).coerceAtLeast(1f)
                    val ry = ((h / 2f) - 4.dp.toPx()).coerceAtLeast(1f)
                    val count = if (style == DynamicIslandMotionStyle.QUANTUM_PARTICLES) 6 else 4
                    for (idx in 0 until count) {
                        val a = phaseRad * (if (idx % 2 == 0) 1f else -1.2f) + idx * (2f * PI.toFloat() / count)
                        val px = cx + rx * cos(a) * (if (style == DynamicIslandMotionStyle.QUANTUM_PARTICLES) 0.92f else 1f)
                        val py = cy + ry * sin(a) * (if (style == DynamicIslandMotionStyle.QUANTUM_PARTICLES) 0.85f else 1f)
                        drawCircle(
                            color = accentColor.copy(alpha = 0.48f),
                            radius = 4.0.dp.toPx(),
                            center = Offset(px, py),
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.90f),
                            radius = 1.7.dp.toPx(),
                            center = Offset(px, py),
                        )
                    }
                }
                else -> Unit
            }
        }
}

/**
 * 5. Enlarged Screen-Spanning Multi-Mode Connection Wave & Radar Pulse (5 Distinct Styles)
 */
@Composable
fun NeumorphicRadarPulse(
    color: Color,
    isActive: Boolean,
    isConnecting: Boolean = false,
    size: Dp = 320.dp,
    waveStyle: HeroWaveAnimationStyle = HeroWaveAnimationStyle.HARMONIC_ORBIT,
    speedMultiplier: Float = 1.0f,
    amplitudeScale: Float = 1.0f,
    glowEnabled: Boolean = true,
    colorMode: WaveColorMode = WaveColorMode.AUTO_STATUS,
    modifier: Modifier = Modifier,
    centerContent: @Composable () -> Unit = {},
) {
    val effectiveColor = if (colorMode == WaveColorMode.AUTO_STATUS || colorMode.colorHex == 0L) {
        color
    } else {
        Color(colorMode.colorHex)
    }
    val safeSpeed = speedMultiplier.coerceIn(0.4f, 2.5f)
    val safeAmp = amplitudeScale.coerceIn(0.5f, 1.8f)

    val infinite = rememberInfiniteTransition(label = "harmonic-wave-2026")
    val wavePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((if (isConnecting) 1400 else 3000) / safeSpeed).toInt().coerceIn(450, 7500),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave-phase",
    )
    val rippleProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((if (isConnecting) 1250 else 2500) / safeSpeed).toInt().coerceIn(450, 6500),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ripple-progress",
    )
    val breathScale by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.09f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath-scale",
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxRadius = (this.size.minDimension / 2f)
            if (maxRadius <= 4f || !maxRadius.isFinite()) return@Canvas

            // Ambient soft radial aura glow spanning almost full screen width
            if (glowEnabled) {
                val auraAlpha = when {
                    isActive -> 0.28f
                    isConnecting -> 0.24f
                    else -> 0.12f
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            effectiveColor.copy(alpha = auraAlpha),
                            effectiveColor.copy(alpha = auraAlpha * 0.45f),
                            Color.Transparent,
                        ),
                        center = center,
                        radius = maxRadius * breathScale,
                    ),
                    radius = maxRadius * breathScale,
                    center = center,
                )
            }

            when (waveStyle) {
                HeroWaveAnimationStyle.HARMONIC_ORBIT -> {
                    val layerCount = 5
                    for (layer in 0 until layerCount) {
                        val layerOffset = layer * 0.20f
                        val progress = (rippleProgress + layerOffset) % 1f
                        val baseR = maxRadius * (0.30f + 0.68f * progress)
                        val alphaEnvelope = sin(progress * PI.toFloat()).coerceIn(0f, 1f)
                        val ringAlpha = alphaEnvelope * (if (isActive || isConnecting) 0.62f else 0.28f)

                        val wavePath = Path()
                        val steps = 88
                        val lobeCount = if (layer % 2 == 0) 6 else 8
                        val amplitude = maxRadius * (if (isActive || isConnecting) 0.038f else 0.018f) * (1f + layer * 0.20f) * safeAmp
                        val dir = if (layer % 2 == 0) 1f else -1f

                        for (i in 0..steps) {
                            val theta = (i.toFloat() / steps) * (2f * PI.toFloat())
                            val harmonic = sin(lobeCount * theta + dir * wavePhase + layer * 0.9f) * amplitude +
                                cos((lobeCount / 2) * theta - dir * wavePhase * 0.75f) * (amplitude * 0.48f)
                            val r = baseR + harmonic
                            val x = center.x + r * cos(theta)
                            val y = center.y + r * sin(theta)
                            if (i == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                        }
                        wavePath.close()

                        drawPath(
                            path = wavePath,
                            color = effectiveColor.copy(alpha = ringAlpha),
                            style = Stroke(
                                width = (2.8f - layer * 0.36f).coerceAtLeast(1.1f).dp.toPx(),
                                cap = StrokeCap.Round,
                            ),
                        )
                    }
                }
                HeroWaveAnimationStyle.SONAR_PULSE -> {
                    val ringCount = 5
                    for (ring in 0 until ringCount) {
                        val p = (rippleProgress + ring * 0.20f) % 1f
                        val r = maxRadius * (0.26f + 0.72f * p * safeAmp.coerceIn(0.75f, 1.15f))
                        val alpha = (1f - p) * (if (isActive || isConnecting) 0.58f else 0.26f)
                        drawCircle(
                            color = effectiveColor.copy(alpha = alpha),
                            radius = r,
                            center = center,
                            style = Stroke(
                                width = (2.6f * (1f - p * 0.5f)).dp.toPx(),
                                pathEffect = if (ring % 2 == 1) {
                                    PathEffect.dashPathEffect(floatArrayOf(18f, 12f), phase = wavePhase * 40f)
                                } else null,
                            ),
                        )
                    }
                    val sweepDeg = (wavePhase / (2f * PI.toFloat())) * 360f
                    rotate(sweepDeg, pivot = center) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0f to Color.Transparent,
                                0.75f to effectiveColor.copy(alpha = 0.02f),
                                1f to effectiveColor.copy(alpha = if (isActive || isConnecting) 0.35f else 0.16f),
                                center = center,
                            ),
                            startAngle = 0f,
                            sweepAngle = 95f,
                            useCenter = true,
                            topLeft = Offset(center.x - maxRadius * 0.90f, center.y - maxRadius * 0.90f),
                            size = Size(maxRadius * 1.80f, maxRadius * 1.80f),
                        )
                    }
                }
                HeroWaveAnimationStyle.PLASMA_VORTEX -> {
                    val arms = 4
                    for (arm in 0 until arms) {
                        val path = Path()
                        val steps = 72
                        val armAngleOffset = arm * (2f * PI.toFloat() / arms)
                        for (s in 0..steps) {
                            val t = s.toFloat() / steps
                            val r = maxRadius * (0.28f + 0.68f * t)
                            val theta = armAngleOffset + t * 3.6f * safeAmp - wavePhase * 1.4f
                            val x = center.x + r * cos(theta)
                            val y = center.y + r * sin(theta)
                            if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = effectiveColor.copy(alpha = if (isActive || isConnecting) 0.46f else 0.22f),
                            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
                HeroWaveAnimationStyle.STARFIELD_NOVA -> {
                    val rayCount = 16
                    for (ray in 0 until rayCount) {
                        val angle = (ray.toFloat() / rayCount) * (2f * PI.toFloat()) + wavePhase * 0.35f
                        val pulse = (sin(wavePhase * 2f + ray * 0.8f) + 1f) * 0.5f * safeAmp.coerceIn(0.6f, 1.3f)
                        val innerR = maxRadius * 0.32f
                        val outerR = maxRadius * (0.58f + 0.38f * pulse)
                        drawLine(
                            color = effectiveColor.copy(alpha = ((0.18f + 0.36f * pulse) * (if (isActive || isConnecting) 1f else 0.55f)).coerceIn(0.05f, 0.95f)),
                            start = Offset(center.x + innerR * cos(angle), center.y + innerR * sin(angle)),
                            end = Offset(center.x + outerR * cos(angle), center.y + outerR * sin(angle)),
                            strokeWidth = 2.2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    for (ring in 0 until 3) {
                        val p = (rippleProgress + ring * 0.33f) % 1f
                        val r = maxRadius * (0.34f + 0.62f * p)
                        drawCircle(
                            color = effectiveColor.copy(alpha = sin(p * PI.toFloat()) * 0.42f),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.6.dp.toPx()),
                        )
                    }
                }
                HeroWaveAnimationStyle.CYBER_HEX_SHIELD -> {
                    val hexLayers = 4
                    for (layer in 0 until hexLayers) {
                        val p = (rippleProgress + layer * 0.25f) % 1f
                        val r = maxRadius * (0.34f + 0.62f * p)
                        val hexPath = Path()
                        val sides = 6
                        val rot = wavePhase * (if (layer % 2 == 0) 0.4f else -0.4f)
                        for (i in 0..sides) {
                            val a = rot + i * (2f * PI.toFloat() / sides)
                            val x = center.x + r * cos(a)
                            val y = center.y + r * sin(a)
                            if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
                        }
                        hexPath.close()
                        drawPath(
                            path = hexPath,
                            color = effectiveColor.copy(alpha = sin(p * PI.toFloat()) * (if (isActive || isConnecting) 0.55f else 0.25f)),
                            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
                HeroWaveAnimationStyle.QUANTUM_HELIX -> {
                    val strands = 2
                    val steps = 96
                    val baseR = maxRadius * 0.68f
                    val amp = maxRadius * 0.14f * safeAmp
                    for (strand in 0 until strands) {
                        val path = Path()
                        val phaseShift = strand * PI.toFloat()
                        for (i in 0..steps) {
                            val theta = (i.toFloat() / steps) * (2f * PI.toFloat())
                            val r = baseR + sin(theta * 8f + wavePhase * 2f + phaseShift) * amp
                            val x = center.x + r * cos(theta)
                            val y = center.y + r * sin(theta)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(
                            path = path,
                            color = effectiveColor.copy(alpha = if (isActive || isConnecting) 0.58f else 0.28f),
                            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                    for (node in 0 until 16) {
                        val theta = (node.toFloat() / 16f) * (2f * PI.toFloat())
                        val r1 = baseR + sin(theta * 8f + wavePhase * 2f) * amp
                        val r2 = baseR + sin(theta * 8f + wavePhase * 2f + PI.toFloat()) * amp
                        drawLine(
                            color = effectiveColor.copy(alpha = if (isActive || isConnecting) 0.36f else 0.18f),
                            start = Offset(center.x + r1 * cos(theta), center.y + r1 * sin(theta)),
                            end = Offset(center.x + r2 * cos(theta), center.y + r2 * sin(theta)),
                            strokeWidth = 1.4.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
                HeroWaveAnimationStyle.LIQUID_SPECTRUM -> {
                    val barCount = 48
                    val innerR = maxRadius * 0.44f
                    for (i in 0 until barCount) {
                        val theta = (i.toFloat() / barCount) * (2f * PI.toFloat())
                        val wave = ((sin(theta * 6f + wavePhase * 2.2f) + cos(theta * 4f - wavePhase * 1.5f)) * 0.25f + 0.5f)
                            .coerceIn(0.08f, 1f)
                        val barLen = maxRadius * (if (isActive || isConnecting) 0.42f else 0.18f) * wave * safeAmp.coerceIn(0.6f, 1.35f)
                        val outerR = (innerR + barLen).coerceAtMost(maxRadius * 0.96f)
                        drawLine(
                            color = effectiveColor.copy(alpha = (0.28f + 0.58f * wave) * (if (isActive || isConnecting) 1f else 0.55f)),
                            start = Offset(center.x + innerR * cos(theta), center.y + innerR * sin(theta)),
                            end = Offset(center.x + outerR * cos(theta), center.y + outerR * sin(theta)),
                            strokeWidth = 3.2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
                HeroWaveAnimationStyle.AURORA_CROWN -> {
                    for (ring in 0 until 4) {
                        val p = (rippleProgress + ring * 0.25f) % 1f
                        val r = maxRadius * (0.36f + 0.58f * p)
                        val rot = wavePhase * (if (ring % 2 == 0) 1f else -1.2f) * 57.2958f
                        rotate(rot, pivot = center) {
                            for (arcIdx in 0 until 3) {
                                drawArc(
                                    color = effectiveColor.copy(
                                        alpha = sin(p * PI.toFloat()) * (if (isActive || isConnecting) 0.65f else 0.30f),
                                    ),
                                    startAngle = arcIdx * 120f + 12f,
                                    sweepAngle = 78f * safeAmp.coerceIn(0.7f, 1.2f),
                                    useCenter = false,
                                    topLeft = Offset(center.x - r, center.y - r),
                                    size = Size(r * 2f, r * 2f),
                                    style = Stroke(width = (3.0f - ring * 0.4f).dp.toPx(), cap = StrokeCap.Round),
                                )
                            }
                        }
                    }
                }
            }

            // Inner subtle precision ring + rotating orbital satellites
            val innerRingR = maxRadius * 0.36f * breathScale
            drawCircle(
                color = effectiveColor.copy(alpha = if (isActive || isConnecting) 0.34f else 0.16f),
                radius = innerRingR,
                center = center,
                style = Stroke(width = 1.5.dp.toPx()),
            )

            if (glowEnabled) {
                for (orb in 0 until 4) {
                    val orbAngle = wavePhase * (if (orb % 2 == 0) 1f else -0.8f) + (orb * 2f * PI.toFloat() / 4f)
                    val orbRadius = maxRadius * (0.48f + orb * 0.13f)
                    val ox = center.x + orbRadius * cos(orbAngle)
                    val oy = center.y + orbRadius * sin(orbAngle)
                    drawCircle(
                        color = effectiveColor.copy(alpha = if (isActive || isConnecting) 0.72f else 0.32f),
                        radius = 3.5.dp.toPx(),
                        center = Offset(ox, oy),
                    )
                }
            }

            if (isConnecting) {
                val deg = (wavePhase / (2f * PI.toFloat())) * 360f
                rotate(deg, pivot = center) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to Color.Transparent,
                            0.60f to effectiveColor.copy(alpha = 0.08f),
                            1f to effectiveColor.copy(alpha = 0.58f),
                            center = center,
                        ),
                        startAngle = 0f,
                        sweepAngle = 135f,
                        useCenter = false,
                        topLeft = Offset(center.x - maxRadius * 0.78f, center.y - maxRadius * 0.78f),
                        size = Size(maxRadius * 1.56f, maxRadius * 1.56f),
                        style = Stroke(width = 3.4.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }
        }
        centerContent()
    }
}

/**
 * 6. 3D Interactive World Globe & Cyber Projection Background
 * Renders a large, screen-spanning 3D spherical globe centered behind the hero area.
 */
private val WORLD_MAP_BITMAP: List<String> = listOf(
    "................................................",
    "......#####....####..............#####..........",
    "....########..#####...........#############.....",
    "...##########...##..........################....",
    "...###########.............##################...",
    "....#########......###....###################...",
    ".....######.......#####...###################...",
    "......####........#####....#################....",
    ".......###.........#######..###############.....",
    "........###........#######...#############......",
    ".........#####.....#######....###########.......",
    "..........######....######.....########..##.....",
    "..........#######...######......#####....###....",
    "...........######....#####.......###............",
    "...........#####.....####.................####..",
    "............####......###................######.",
    "............###.......##.................######.",
    ".............##...........................####..",
    "................................................",
)

// (Lon in radians -PI..PI, Lat in radians -PI/2..PI/2) for major global VPN hubs
private val GLOBE_HUB_COORDS: List<Pair<Float, Float>> = listOf(
    Pair(-1.29f, 0.71f),  // New York
    Pair(-2.13f, 0.65f),  // Los Angeles
    Pair(0.15f, 0.88f),   // Frankfurt / Brussels
    Pair(-0.02f, 0.90f),  // London / Amsterdam
    Pair(0.43f, 1.05f),   // Helsinki / Stockholm
    Pair(1.81f, 0.02f),   // Singapore
    Pair(2.44f, 0.62f),   // Tokyo
    Pair(2.64f, -0.59f),  // Sydney
)

@Composable
fun AnimatedWorldMapBackground(
    isConnected: Boolean,
    isConnecting: Boolean,
    accentColor: Color,
    isDark: Boolean,
    scrollOffsetPx: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "world-globe-3d")
    val autoRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isConnecting) 16000 else 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "globe-auto-rot",
    )
    val scanWave by infinite.animateFloat(
        initialValue = -0.25f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isConnecting) 2800 else 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "globe-scan-wave",
    )
    val nodePulse by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "globe-node-pulse",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 16f || h <= 16f || !w.isFinite() || !h.isFinite()) return@Canvas

        // Scroll-driven 3D camera angles
        val safeScrollPx = if (scrollOffsetPx.isFinite()) scrollOffsetPx else 0f
        val scrollNormalized = (safeScrollPx / 900f)
        val tiltAngle = (0.30f + scrollNormalized * 0.65f).coerceIn(-0.65f, 0.85f)
        val cosTilt = cos(tiltAngle)
        val sinTilt = sin(tiltAngle)
        val yawAngle = autoRotation + scrollNormalized * 1.35f

        // Enlarged 3D Globe spanning the screen width prominently and centered in the hero viewport
        val minGlobeR = minOf(185.dp.toPx(), minOf(w, h) * 0.52f).coerceAtLeast(24f)
        val maxGlobeR = maxOf(340.dp.toPx(), minGlobeR + 1f)
        val globeRadius = (w * 0.49f).coerceIn(minGlobeR, maxGlobeR)

        val minCenterY = minOf(170.dp.toPx(), h * 0.28f).coerceAtLeast(16f)
        val maxCenterY = maxOf(h * 0.48f, minCenterY + 1f)
        val globeCenter = Offset(
            x = w * 0.5f,
            y = (h * 0.34f - safeScrollPx * 0.07f).coerceIn(minCenterY, maxCenterY),
        )

        // Helper to project 3D spherical coordinates (lon, lat) onto 2D screen with depth z (-1..1)
        fun project3D(lon: Float, lat: Float, radiusScale: Float = 1f): Triple<Float, Float, Float> {
            val relLon = lon + yawAngle
            val x3 = cos(lat) * sin(relLon)
            val y3 = sin(lat)
            val z3 = cos(lat) * cos(relLon)

            val yRot = y3 * cosTilt - z3 * sinTilt
            val zRot = y3 * sinTilt + z3 * cosTilt

            val r = globeRadius * radiusScale
            val sx = globeCenter.x + x3 * r
            val sy = globeCenter.y - yRot * r
            return Triple(sx, sy, zRot)
        }

        // 1. Ambient 3D Planetary Aura Glow
        val auraAlpha = if (isConnected) 0.20f else if (isConnecting) 0.17f else 0.09f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = auraAlpha),
                    accentColor.copy(alpha = auraAlpha * 0.38f),
                    Color.Transparent,
                ),
                center = globeCenter,
                radius = globeRadius * 1.32f,
            ),
            radius = globeRadius * 1.32f,
            center = globeCenter,
        )

        // Outer 3D Sphere Rim
        drawCircle(
            color = accentColor.copy(alpha = if (isDark) 0.20f else 0.17f),
            radius = globeRadius,
            center = globeCenter,
            style = Stroke(width = 1.4.dp.toPx()),
        )

        // 2. 3D Latitude & Longitude Wireframe Meridians
        val wireAlpha = if (isDark) 0.10f else 0.11f
        for (latIdx in -2..2) {
            val lat = latIdx * (PI.toFloat() / 6f)
            val path = Path()
            var started = false
            val steps = 48
            for (s in 0..steps) {
                val lon = -PI.toFloat() + (s.toFloat() / steps) * (2f * PI.toFloat())
                val (sx, sy, z) = project3D(lon, lat)
                if (z >= -0.15f) {
                    if (!started) {
                        path.moveTo(sx, sy)
                        started = true
                    } else {
                        path.lineTo(sx, sy)
                    }
                } else {
                    started = false
                }
            }
            drawPath(
                path = path,
                color = accentColor.copy(alpha = wireAlpha),
                style = Stroke(width = 0.95.dp.toPx()),
            )
        }

        for (lonIdx in 0 until 8) {
            val lon = (lonIdx.toFloat() / 8f) * (2f * PI.toFloat())
            val path = Path()
            var started = false
            val steps = 36
            for (s in 0..steps) {
                val lat = -(PI.toFloat() / 2f) + (s.toFloat() / steps) * PI.toFloat()
                val (sx, sy, z) = project3D(lon, lat)
                if (z >= -0.15f) {
                    if (!started) {
                        path.moveTo(sx, sy)
                        started = true
                    } else {
                        path.lineTo(sx, sy)
                    }
                } else {
                    started = false
                }
            }
            drawPath(
                path = path,
                color = accentColor.copy(alpha = wireAlpha),
                style = Stroke(width = 0.85.dp.toPx()),
            )
        }

        // 3. 3D Continental Dot Matrix Mapped onto Rotating Sphere
        val rows = WORLD_MAP_BITMAP.size
        val cols = WORLD_MAP_BITMAP.first().length
        val baseDotRadius = 2.4.dp.toPx()

        for (r in 0 until rows) {
            val rowStr = WORLD_MAP_BITMAP[r]
            val normY = r.toFloat() / (rows - 1).toFloat()
            val lat = ((0.5f - normY) * PI.toFloat() * 0.82f)
            for (c in 0 until cols) {
                val isLand = rowStr[c] == '#'
                if (!isLand && (r + c) % 6 != 0) continue

                val normX = c.toFloat() / cols.toFloat()
                val lon = (normX - 0.5f) * (2f * PI.toFloat())
                val (sx, sy, z) = project3D(lon, lat)

                if (z > -0.25f) {
                    val depthFactor = ((z + 0.25f) / 1.25f).coerceIn(0.1f, 1f)
                    val distToWave = abs(((normX + normY * 0.2f) % 1f) - scanWave)
                    val waveGlow = (1f - (distToWave / 0.20f)).coerceIn(0f, 1f)

                    if (isLand) {
                        val baseAlpha = if (isDark) 0.22f else 0.20f
                        val activeBoost = if (isConnected || isConnecting) 0.34f else 0.16f
                        val dotAlpha = ((baseAlpha + waveGlow * activeBoost) * depthFactor).coerceIn(0.04f, 0.78f)
                        drawCircle(
                            color = accentColor.copy(alpha = dotAlpha),
                            radius = baseDotRadius * (0.65f + 0.55f * depthFactor + waveGlow * 0.25f),
                            center = Offset(sx, sy),
                        )
                    } else if (z > 0.1f) {
                        drawCircle(
                            color = accentColor.copy(alpha = (if (isDark) 0.045f else 0.05f) * depthFactor),
                            radius = baseDotRadius * 0.42f,
                            center = Offset(sx, sy),
                        )
                    }
                }
            }
        }

        // 4. 3D Orbital Satellite Ring & Global VPN Hub Arcs
        val orbitPath = Path()
        val orbitSteps = 60
        for (i in 0..orbitSteps) {
            val theta = (i.toFloat() / orbitSteps) * (2f * PI.toFloat())
            val (ox, oy, _) = project3D(theta, sin(theta) * 0.28f, radiusScale = 1.14f)
            if (i == 0) orbitPath.moveTo(ox, oy) else orbitPath.lineTo(ox, oy)
        }
        drawPath(
            path = orbitPath,
            color = accentColor.copy(alpha = if (isConnected || isConnecting) 0.20f else 0.09f),
            style = Stroke(
                width = 1.1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), phase = scanWave * 220f),
            ),
        )

        val projectedHubs = GLOBE_HUB_COORDS.map { (lon, lat) ->
            project3D(lon, lat, radiusScale = 1.01f)
        }

        val primaryHub = projectedHubs[2] // Europe hub
        val arcAlpha = if (isConnected) 0.36f else if (isConnecting) 0.28f else 0.13f
        projectedHubs.forEachIndexed { idx, target ->
            if (idx != 2 && (primaryHub.third > -0.2f || target.third > -0.2f)) {
                val midX = (primaryHub.first + target.first) / 2f
                val dist = sqrt(
                    (target.first - primaryHub.first) * (target.first - primaryHub.first) +
                        (target.second - primaryHub.second) * (target.second - primaryHub.second)
                )
                val midY = minOf(primaryHub.second, target.second) - dist * 0.26f
                val arcPath = Path().apply {
                    moveTo(primaryHub.first, primaryHub.second)
                    quadraticTo(midX, midY, target.first, target.second)
                }
                drawPath(
                    path = arcPath,
                    color = accentColor.copy(alpha = arcAlpha),
                    style = Stroke(
                        width = 1.4.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 8f),
                            phase = (scanWave * 180f + idx * 28f),
                        ),
                    ),
                )
            }
        }

        projectedHubs.forEachIndexed { idx, (hx, hy, hz) ->
            if (hz > -0.15f) {
                val isPrimary = idx == 2 || idx == 3
                val visibility = ((hz + 0.2f) / 1.2f).coerceIn(0.3f, 1f)
                val pulseR = (if (isPrimary) 8.5.dp.toPx() else 5.5.dp.toPx()) * nodePulse * visibility
                drawCircle(
                    color = accentColor.copy(alpha = (if (isPrimary) 0.34f else 0.20f) * nodePulse * visibility),
                    radius = pulseR,
                    center = Offset(hx, hy),
                )
                drawCircle(
                    color = accentColor.copy(alpha = (if (isPrimary) 0.85f else 0.55f) * visibility),
                    radius = (if (isPrimary) 3.4.dp.toPx() else 2.4.dp.toPx()) * visibility,
                    center = Offset(hx, hy),
                )
            }
        }
    }
}

/**
 * 7. Odometer-style Animated Text for Timers & Counters
 */
@Composable
fun AnimatedOdometerText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        text.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    (slideInVertically { height -> height / 2 } + fadeIn(tween(180)))
                        .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(tween(180)))
                        .using(SizeTransform(clip = false))
                },
                label = "odometer-char-$index",
            ) { targetChar ->
                Text(
                    text = targetChar.toString(),
                    style = style,
                    color = color,
                )
            }
        }
    }
}

/**
 * 8. Dual-Line Live Traffic Waveform Sparkline Canvas
 */
@Composable
fun LiveTrafficSparkline(
    rxHistory: List<Long>,
    txHistory: List<Long>,
    rxColor: Color,
    txColor: Color,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "wave-phase")
    val waveShift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave-shift",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val maxVal = maxOf(
            rxHistory.maxOrNull() ?: 1024L,
            txHistory.maxOrNull() ?: 1024L,
            4096L,
        ).toFloat()

        fun buildSmoothPath(points: List<Long>, phaseOffset: Float): Path {
            val path = Path()
            val count = points.size.coerceAtLeast(2)
            val stepX = w / (count - 1).toFloat()
            for (i in 0 until count) {
                val sample = points.getOrElse(i) { 0L }.toFloat()
                val normalized = (sample / maxVal).coerceIn(0f, 1f)
                val idleBreath = sin(waveShift + phaseOffset + i * 0.45f) * 0.04f
                val yFactor = (normalized * 0.78f + 0.10f + idleBreath).coerceIn(0.06f, 0.92f)
                val x = i * stepX
                val y = h - (yFactor * h)
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    val prevX = (i - 1) * stepX
                    val prevSample = points.getOrElse(i - 1) { 0L }.toFloat()
                    val prevNorm = (prevSample / maxVal).coerceIn(0f, 1f)
                    val prevBreath = sin(waveShift + phaseOffset + (i - 1) * 0.45f) * 0.04f
                    val prevY = h - ((prevNorm * 0.78f + 0.10f + prevBreath).coerceIn(0.06f, 0.92f) * h)
                    val ctrlX = (prevX + x) / 2f
                    path.cubicTo(ctrlX, prevY, ctrlX, y, x, y)
                }
            }
            return path
        }

        val rxPath = buildSmoothPath(rxHistory, 0f)
        val txPath = buildSmoothPath(txHistory, 1.6f)

        // Fill under RX curve
        val fillPath = Path().apply {
            addPath(rxPath)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(rxColor.copy(alpha = 0.28f), Color.Transparent),
                startY = 0f,
                endY = h,
            ),
        )

        drawPath(
            path = txPath,
            color = txColor.copy(alpha = 0.75f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
        drawPath(
            path = rxPath,
            color = rxColor,
            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

/**
 * 9a. Animated Broadcasting Signal Tower Wave (For Dynamic Island)
 * Renders a central radio/cell signal tower mast with glowing beacon and outward-propagating
 * electromagnetic wave arcs on both sides ((( T ))).
 */
@Composable
fun AnimatedSignalTowerWave(
    active: Boolean,
    color: Color,
    waveStyle: com.vauth.foxyvpn.data.DynamicIslandWaveStyle = com.vauth.foxyvpn.data.DynamicIslandWaveStyle.SIGNAL_TOWER,
    speedMultiplier: Float = 1.0f,
    amplitudeScale: Float = 1.0f,
    glowEnabled: Boolean = true,
    colorMode: WaveColorMode = WaveColorMode.AUTO_STATUS,
    modifier: Modifier = Modifier,
) {
    val effectiveColor = if (colorMode == WaveColorMode.AUTO_STATUS || colorMode.colorHex == 0L) {
        color
    } else {
        Color(colorMode.colorHex)
    }
    val safeSpeed = speedMultiplier.coerceIn(0.4f, 2.5f)
    val safeAmp = amplitudeScale.coerceIn(0.5f, 1.8f)

    val infinite = rememberInfiniteTransition(label = "signal-tower-wave")
    val waveExpand by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ((if (active) 1350 else 2600) / safeSpeed).toInt().coerceIn(380, 6000),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "tower-wave-expand",
    )
    val beaconPulse by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (550 / safeSpeed).toInt().coerceIn(220, 1500),
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "tower-beacon-pulse",
    )

    Canvas(
        modifier = modifier
            .width(26.dp)
            .height(18.dp),
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        if (glowEnabled && active) {
            drawCircle(
                color = effectiveColor.copy(alpha = 0.20f * beaconPulse),
                radius = minOf(w, h) * 0.52f,
                center = Offset(cx, cy),
            )
        }

        when (waveStyle) {
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.EQUALIZER_BARS -> {
                val barCount = 5
                val barW = 2.6.dp.toPx()
                val gap = (w - barCount * barW) / (barCount + 1).coerceAtLeast(1)
                for (i in 0 until barCount) {
                    val phase = (waveExpand * 2f * PI.toFloat()) + i * 0.95f
                    val amp = if (active) ((0.28f + 0.68f * ((sin(phase) + 1f) * 0.5f)) * safeAmp).coerceIn(0.2f, 0.98f) else 0.25f
                    val barH = (h * amp).coerceAtLeast(3.dp.toPx())
                    val x = gap + i * (barW + gap)
                    drawRoundRect(
                        color = effectiveColor.copy(alpha = if (active) 0.92f else 0.45f),
                        topLeft = Offset(x, (h - barH) / 2f),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(barW / 2f, barW / 2f),
                    )
                }
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.SINE_WAVE -> {
                val path = Path()
                val steps = 20
                val phaseRad = waveExpand * 2f * PI.toFloat()
                for (i in 0..steps) {
                    val t = i.toFloat() / steps
                    val x = t * w
                    val amp = (if (active) h * 0.34f else h * 0.12f) * safeAmp.coerceIn(0.6f, 1.3f)
                    val y = cy + sin(t * 2.2f * PI.toFloat() + phaseRad) * amp
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path,
                    color = effectiveColor.copy(alpha = if (active) 0.95f else 0.48f),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.RADAR_PULSE -> {
                val maxR = minOf(w, h) * 0.46f * safeAmp.coerceIn(0.75f, 1.15f)
                drawCircle(
                    color = effectiveColor.copy(alpha = if (active) beaconPulse else 0.45f),
                    radius = 2.4.dp.toPx(),
                    center = Offset(cx, cy),
                )
                for (i in 0 until 3) {
                    val p = (waveExpand + i / 3f) % 1f
                    val r = 2.5.dp.toPx() + p * (maxR - 2.5.dp.toPx())
                    drawCircle(
                        color = effectiveColor.copy(alpha = (1f - p) * (if (active) 0.88f else 0.30f)),
                        radius = r,
                        center = Offset(cx, cy),
                        style = Stroke(width = 1.4.dp.toPx()),
                    )
                }
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.ORBIT_DOTS -> {
                val rx = w * 0.38f * safeAmp.coerceIn(0.7f, 1.2f)
                val ry = h * 0.36f * safeAmp.coerceIn(0.7f, 1.2f)
                drawCircle(
                    color = effectiveColor.copy(alpha = if (active) 0.90f else 0.45f),
                    radius = 2.4.dp.toPx(),
                    center = Offset(cx, cy),
                )
                for (i in 0 until 3) {
                    val angle = waveExpand * 2f * PI.toFloat() + i * (2f * PI.toFloat() / 3f)
                    val px = cx + cos(angle) * rx
                    val py = cy + sin(angle) * ry
                    drawCircle(
                        color = effectiveColor.copy(alpha = if (active) 0.92f else 0.40f),
                        radius = 2.0.dp.toPx(),
                        center = Offset(px, py),
                    )
                }
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.HEARTBEAT_WAVE -> {
                val path = Path()
                val pulseShift = (waveExpand - 0.5f) * w * 0.5f
                val midX = (cx + pulseShift).coerceIn(w * 0.25f, w * 0.75f)
                val peak = (if (active) h * 0.38f * beaconPulse else h * 0.12f) * safeAmp.coerceIn(0.6f, 1.25f)
                path.moveTo(0f, cy)
                path.lineTo(midX - 5.dp.toPx(), cy)
                path.lineTo(midX - 2.dp.toPx(), cy - peak)
                path.lineTo(midX + 2.dp.toPx(), cy + peak * 0.85f)
                path.lineTo(midX + 5.dp.toPx(), cy)
                path.lineTo(w, cy)
                drawPath(
                    path = path,
                    color = effectiveColor.copy(alpha = if (active) 0.95f else 0.48f),
                    style = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round),
                )
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.DUAL_HELIX -> {
                val steps = 18
                val phaseRad = waveExpand * 2f * PI.toFloat()
                val amp = (if (active) h * 0.32f else h * 0.12f) * safeAmp.coerceIn(0.6f, 1.25f)
                val path1 = Path()
                val path2 = Path()
                for (i in 0..steps) {
                    val t = i.toFloat() / steps
                    val x = t * w
                    val y1 = cy + sin(t * 2f * PI.toFloat() + phaseRad) * amp
                    val y2 = cy + sin(t * 2f * PI.toFloat() + phaseRad + PI.toFloat()) * amp
                    if (i == 0) {
                        path1.moveTo(x, y1)
                        path2.moveTo(x, y2)
                    } else {
                        path1.lineTo(x, y1)
                        path2.lineTo(x, y2)
                    }
                }
                drawPath(path1, effectiveColor.copy(alpha = if (active) 0.95f else 0.45f), style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
                drawPath(path2, effectiveColor.copy(alpha = if (active) 0.55f else 0.25f), style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round))
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.CYBER_SPECTRUM -> {
                val barCount = 7
                val barW = 2.0.dp.toPx()
                val gap = (w - barCount * barW) / (barCount + 1).coerceAtLeast(1)
                for (i in 0 until barCount) {
                    val centerWeight = 1f - abs(i - 3) * 0.18f
                    val phase = (waveExpand * 2f * PI.toFloat() * 1.5f) + i * 0.7f
                    val rawAmp = if (active) (0.25f + 0.70f * ((sin(phase) + 1f) * 0.5f) * centerWeight * safeAmp) else 0.22f
                    val barH = (h * rawAmp.coerceIn(0.18f, 0.96f)).coerceAtLeast(2.5.dp.toPx())
                    val x = gap + i * (barW + gap)
                    drawRoundRect(
                        color = effectiveColor.copy(alpha = if (active) 0.94f else 0.42f),
                        topLeft = Offset(x, (h - barH) / 2f),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(barW / 2f, barW / 2f),
                    )
                }
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.SONIC_RIPPLE -> {
                for (i in 0 until 3) {
                    val p = (waveExpand + i / 3f) % 1f
                    val rx = w * (0.14f + 0.34f * p * safeAmp.coerceIn(0.75f, 1.15f))
                    val ry = h * (0.18f + 0.30f * p * safeAmp.coerceIn(0.75f, 1.15f))
                    drawOval(
                        color = effectiveColor.copy(alpha = (1f - p) * (if (active) 0.90f else 0.34f)),
                        topLeft = Offset(cx - rx, cy - ry),
                        size = Size(rx * 2f, ry * 2f),
                        style = Stroke(width = 1.4.dp.toPx()),
                    )
                }
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.LASER_MATRIX -> {
                val cols = 4
                val rows = 3
                val dotR = 1.6.dp.toPx()
                for (c in 0 until cols) {
                    for (r in 0 until rows) {
                        val x = w * (0.20f + c * 0.20f)
                        val y = h * (0.24f + r * 0.26f)
                        val wave = (sin(waveExpand * 2f * PI.toFloat() + c * 0.9f + r * 0.6f) + 1f) * 0.5f
                        val alpha = if (active) (0.25f + 0.72f * wave) else 0.28f
                        drawCircle(
                            color = effectiveColor.copy(alpha = alpha),
                            radius = dotR * (0.8f + 0.35f * wave * safeAmp.coerceIn(0.7f, 1.2f)),
                            center = Offset(x, y),
                        )
                    }
                }
            }
            com.vauth.foxyvpn.data.DynamicIslandWaveStyle.SIGNAL_TOWER -> {
                val towerCy = h * 0.42f
                val maxWaveRadius = w * 0.46f * safeAmp.coerceIn(0.75f, 1.15f)
                val towerBottom = h * 0.94f
                val towerBaseHalfW = w * 0.14f
                val towerPath = Path().apply {
                    moveTo(cx, towerCy)
                    lineTo(cx - towerBaseHalfW, towerBottom)
                    moveTo(cx, towerCy)
                    lineTo(cx + towerBaseHalfW, towerBottom)
                    val crossY = towerCy + (towerBottom - towerCy) * 0.58f
                    val crossHalfW = towerBaseHalfW * 0.6f
                    moveTo(cx - crossHalfW, crossY)
                    lineTo(cx + crossHalfW, crossY)
                }
                drawPath(
                    path = towerPath,
                    color = effectiveColor.copy(alpha = if (active) 0.95f else 0.5f),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
                )
                drawCircle(
                    color = effectiveColor.copy(alpha = if (active) beaconPulse else 0.45f),
                    radius = 2.3.dp.toPx(),
                    center = Offset(cx, towerCy),
                )
                val waveCount = 3
                for (i in 0 until waveCount) {
                    val phase = (waveExpand + i / waveCount.toFloat()) % 1f
                    val r = 3.dp.toPx() + phase * (maxWaveRadius - 3.dp.toPx())
                    val alpha = (1f - phase) * (if (active) 0.92f else 0.32f)
                    val arcTopLeft = Offset(cx - r, towerCy - r)
                    val arcSize = Size(r * 2f, r * 2f)
                    val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(
                        color = effectiveColor.copy(alpha = alpha),
                        startAngle = -42f,
                        sweepAngle = 84f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = stroke,
                    )
                    drawArc(
                        color = effectiveColor.copy(alpha = alpha),
                        startAngle = 138f,
                        sweepAngle = 84f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = stroke,
                    )
                }
            }
        }
    }
}

/**
 * 9b. Animated Equalizer Activity Bars (Secondary helper)
 */
@Composable
fun AnimatedTrafficEqualizer(
    active: Boolean,
    color: Color,
    barCount: Int = 4,
    modifier: Modifier = Modifier,
) {
    AnimatedSignalTowerWave(
        active = active,
        color = color,
        modifier = modifier,
    )
}

/**
 * 10. Neumorphic Speedometer Analog + Digital Gauge Canvas
 * Needle is drawn in the outer ring band (0.34R..0.74R) so it never overlaps the center/bottom text.
 */
@Composable
fun NeumorphicSpeedometerGauge(
    speedMbps: Float,
    maxMbps: Float = 100f,
    accentColor: Color,
    trackColor: Color,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speedMbps.coerceIn(0f, maxMbps),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "speed-gauge-needle",
    )

    val infinite = rememberInfiniteTransition(label = "gauge-glow")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gauge-glow-val",
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val diameter = size.minDimension - strokeWidth * 2f
        if (diameter <= 8f || !diameter.isFinite()) return@Canvas
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = Size(diameter, diameter)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = diameter / 2f

        // Background track arc (135 deg to 405 deg -> 270 deg sweep)
        drawArc(
            color = trackColor,
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )

        // Tick marks
        val tickCount = 28
        for (i in 0..tickCount) {
            val fraction = i.toFloat() / tickCount
            val angleDeg = 135f + fraction * 270f
            val angleRad = angleDeg * (PI / 180.0)
            val isMajor = i % 4 == 0
            val innerR = radius - strokeWidth * (if (isMajor) 1.4f else 1.1f)
            val outerR = radius - strokeWidth * 0.7f
            val start = Offset(
                x = center.x + (innerR * cos(angleRad)).toFloat(),
                y = center.y + (innerR * sin(angleRad)).toFloat(),
            )
            val end = Offset(
                x = center.x + (outerR * cos(angleRad)).toFloat(),
                y = center.y + (outerR * sin(angleRad)).toFloat(),
            )
            drawLine(
                color = trackColor.copy(alpha = if (isMajor) 0.9f else 0.45f),
                start = start,
                end = end,
                strokeWidth = if (isMajor) 2.5.dp.toPx() else 1.2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        val sweepFraction = (animatedSpeed / maxMbps).coerceIn(0f, 1f)
        val activeSweep = sweepFraction * 270f

        if (activeSweep > 0.5f) {
            drawArc(
                color = accentColor.copy(alpha = if (isRunning) glowPulse else 0.9f),
                startAngle = 135f,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        // Outer-band floating needle (starts outside center readout zone so text never overlaps)
        val needleAngleRad = (135f + activeSweep) * (PI / 180.0)
        val needleStartLen = radius * 0.42f
        val needleEndLen = radius * 0.76f
        val needleBase = Offset(
            x = center.x + (needleStartLen * cos(needleAngleRad)).toFloat(),
            y = center.y + (needleStartLen * sin(needleAngleRad)).toFloat(),
        )
        val needleTip = Offset(
            x = center.x + (needleEndLen * cos(needleAngleRad)).toFloat(),
            y = center.y + (needleEndLen * sin(needleAngleRad)).toFloat(),
        )
        drawLine(
            color = accentColor,
            start = needleBase,
            end = needleTip,
            strokeWidth = 3.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = accentColor,
            radius = 4.2.dp.toPx(),
            center = needleBase,
        )
    }
}
