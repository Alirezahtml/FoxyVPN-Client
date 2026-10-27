package com.vauth.foxyvpn.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vauth.foxyvpn.data.FoxyUiDesignStyle
import com.vauth.foxyvpn.data.model.ConnectionState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class NeumorphicColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val shadowLight: Color,
    val shadowDark: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val isDark: Boolean,
    val designStyle: FoxyUiDesignStyle = FoxyUiDesignStyle.NEUMORPHISM,
)

val LocalNeumorphicColors = staticCompositionLocalOf {
    NeumorphicColors(
        background = NeuLightBg,
        surface = NeuLightSurface,
        surfaceElevated = NeuLightSurfaceElevated,
        shadowLight = NeuLightShadowLight,
        shadowDark = NeuLightShadowDark,
        accent = md_theme_light_primary,
        textPrimary = md_theme_light_onBackground,
        textSecondary = md_theme_light_onSurfaceVariant,
        isDark = false,
        designStyle = FoxyUiDesignStyle.NEUMORPHISM,
    )
}

val currentNeumorphicColors: NeumorphicColors
    @Composable
    @ReadOnlyComposable
    get() = LocalNeumorphicColors.current

/**
 * Renders style-adaptive outer shadow / bevel effects for Soft Neumorphism.
 */
fun Modifier.neumorphicRaised(
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 6.dp,
    isPressed: Boolean = false,
    lightShadowColor: Color? = null,
    darkShadowColor: Color? = null,
    designStyle: FoxyUiDesignStyle = FoxyUiDesignStyle.NEUMORPHISM,
    isDark: Boolean = false,
    accentColor: Color = Color(0xFF00B0FF),
): Modifier = this.then(
    Modifier.drawBehind {
        if (size.width <= 2f || size.height <= 2f || !size.width.isFinite() || !size.height.isFinite()) return@drawBehind
        val radiusPx = cornerRadius.toPx().coerceAtLeast(0f)

        val lightColor = lightShadowColor ?: if (isDark) Color(0xFF2F343F).copy(alpha = 0.55f) else Color.White.copy(alpha = 0.92f)
        val darkColor = darkShadowColor ?: if (isDark) Color(0xFF0B0C10).copy(alpha = 0.85f) else Color(0xFFC2C9D6).copy(alpha = 0.65f)

        val currentElev = if (isPressed) (elevation * 0.25f) else elevation
        val blurPx = (currentElev * 1.35f).toPx()
        val offsetPx = (currentElev * 0.85f).toPx()

        drawIntoCanvas { canvas ->
            if (blurPx > 0.5f && blurPx.isFinite()) {
                val paintLight = Paint().asFrameworkPaint().apply {
                    color = lightColor.toArgb()
                    isAntiAlias = true
                    maskFilter = BlurMaskFilter(blurPx.coerceAtLeast(1f), BlurMaskFilter.Blur.NORMAL)
                }
                canvas.nativeCanvas.drawRoundRect(
                    -offsetPx,
                    -offsetPx,
                    size.width - offsetPx,
                    size.height - offsetPx,
                    radiusPx,
                    radiusPx,
                    paintLight,
                )

                val paintDark = Paint().asFrameworkPaint().apply {
                    color = darkColor.toArgb()
                    isAntiAlias = true
                    maskFilter = BlurMaskFilter(blurPx.coerceAtLeast(1f), BlurMaskFilter.Blur.NORMAL)
                }
                canvas.nativeCanvas.drawRoundRect(
                    offsetPx,
                    offsetPx,
                    size.width + offsetPx,
                    size.height + offsetPx,
                    radiusPx,
                    radiusPx,
                    paintDark,
                )
            }
        }
    }
)

/**
 * Renders tactile Soft Neumorphism inner top-left rim bevel and subtle gradient shading matching the reference images.
 */
fun Modifier.styleInnerTreatment(
    designStyle: FoxyUiDesignStyle,
    cornerRadius: Dp,
    isDark: Boolean,
    accentColor: Color,
): Modifier = this.then(
    Modifier.drawBehind {
        val w = size.width
        val h = size.height
        if (w <= 4f || h <= 4f || !w.isFinite() || !h.isFinite()) return@drawBehind
        val r = cornerRadius.toPx().coerceAtLeast(0f)

        // Tactile Soft Neumorphism subtle diagonal surface sheen + top-left rim highlight
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (isDark) 0.055f else 0.62f),
                    Color.Transparent,
                    Color.Black.copy(alpha = if (isDark) 0.24f else 0.06f),
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h),
            ),
            size = size,
            cornerRadius = CornerRadius(r, r),
        )
    }
)

/**
 * Renders a sunken / inset neumorphic groove for sliders, textboxes, and tracks.
 */
fun Modifier.neumorphicSunken(
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadius: Dp = 16.dp,
    depth: Dp = 4.dp,
    surfaceColor: Color = Color.Transparent,
): Modifier = this.then(
    Modifier
        .clip(shape)
        .drawBehind {
            val w = size.width
            val h = size.height
            if (w <= 2f || h <= 2f || !w.isFinite() || !h.isFinite()) return@drawBehind
            val r = cornerRadius.toPx().coerceAtLeast(0f)
            val d = depth.toPx().coerceAtLeast(1f)

            // Subtle base fill
            if (surfaceColor != Color.Transparent) {
                drawRoundRect(
                    color = surfaceColor,
                    size = size,
                    cornerRadius = CornerRadius(r, r),
                )
            }

            // Top-left inset dark shadow
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.32f), Color.Transparent),
                    start = Offset(0f, 0f),
                    end = Offset((d * 3f).coerceAtLeast(2f), (d * 3f).coerceAtLeast(2f)),
                ),
                size = size,
                cornerRadius = CornerRadius(r, r),
            )

            // Bottom-right inset light highlight
            val startX = (w - d * 2.4f).coerceAtMost(w - 2f)
            val startY = (h - d * 2.4f).coerceAtMost(h - 2f)
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.14f)),
                    start = Offset(startX, startY),
                    end = Offset(w, h),
                ),
                size = size,
                cornerRadius = CornerRadius(r, r),
            )
        }
)

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val neu = currentNeumorphicColors
    val style = FoxyUiDesignStyle.NEUMORPHISM
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val effectiveCorner = cornerRadius

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) elevation * 0.3f else elevation,
        animationSpec = spring(stiffness = 400f),
        label = "neu-card-elevation",
    )

    val shape = RoundedCornerShape(effectiveCorner)
    val borderWidth = 1.dp
    val borderColor = if (neu.isDark) Color(0xFF2D313B).copy(alpha = 0.72f) else Color.White.copy(alpha = 0.78f)

    Box(
        modifier = modifier
            .neumorphicRaised(
                shape = shape,
                cornerRadius = effectiveCorner,
                elevation = animatedElevation,
                isPressed = isPressed,
                lightShadowColor = neu.shadowLight.copy(alpha = if (neu.isDark) 0.50f else 0.90f),
                darkShadowColor = neu.shadowDark.copy(alpha = if (neu.isDark) 0.85f else 0.55f),
                designStyle = style,
                isDark = neu.isDark,
                accentColor = neu.accent,
            )
            .clip(shape)
            .background(neu.surface)
            .styleInnerTreatment(
                designStyle = style,
                cornerRadius = effectiveCorner,
                isDark = neu.isDark,
                accentColor = neu.accent,
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape,
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else Modifier
            ),
    ) {
        content()
    }
}

@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 5.dp,
    accentColor: Color? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val neu = currentNeumorphicColors
    val style = neu.designStyle
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current

    val effectiveCorner = when (style) {
        FoxyUiDesignStyle.PIXEL_GOOGLE -> 28.dp
        FoxyUiDesignStyle.CLAYMORPHISM -> 26.dp
        FoxyUiDesignStyle.NEO_BRUTALISM -> 8.dp
        FoxyUiDesignStyle.EDITORIAL_DESIGN -> 4.dp
        FoxyUiDesignStyle.BENTO_UI -> 20.dp
        else -> cornerRadius
    }

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed && enabled) 1.5.dp else elevation,
        label = "btn-elev",
    )
    val shape = RoundedCornerShape(effectiveCorner)
    val bgColor = accentColor ?: neu.surface

    val borderWidth = when (style) {
        FoxyUiDesignStyle.NEO_BRUTALISM -> 2.4.dp
        FoxyUiDesignStyle.DARK_LUXURY -> 1.4.dp
        else -> 1.dp
    }
    val borderColor = when (style) {
        FoxyUiDesignStyle.NEO_BRUTALISM -> if (neu.isDark) Color.White else Color(0xFF0B0F19)
        FoxyUiDesignStyle.DARK_LUXURY -> Color(0xFFE5B84B).copy(alpha = 0.65f)
        else -> if (neu.isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .neumorphicRaised(
                shape = shape,
                cornerRadius = effectiveCorner,
                elevation = animatedElevation,
                isPressed = isPressed && enabled,
                lightShadowColor = neu.shadowLight.copy(alpha = if (neu.isDark) 0.35f else 0.9f),
                darkShadowColor = neu.shadowDark.copy(alpha = if (neu.isDark) 0.75f else 0.5f),
                designStyle = style,
                isDark = neu.isDark,
                accentColor = neu.accent,
            )
            .clip(shape)
            .background(bgColor)
            .styleInnerTreatment(
                designStyle = style,
                cornerRadius = effectiveCorner,
                isDark = neu.isDark,
                accentColor = neu.accent,
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape,
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            )
            .padding(horizontal = 20.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Row wrapper prevents Icon + Text from overlapping on top of each other!
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}

@Composable
fun NeumorphicIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    isSelected: Boolean = false,
    content: @Composable () -> Unit,
) {
    val neu = currentNeumorphicColors
    val style = neu.designStyle
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current

    val shape = when (style) {
        FoxyUiDesignStyle.PIXEL_GOOGLE -> RoundedCornerShape(18.dp)
        FoxyUiDesignStyle.NEO_BRUTALISM -> RoundedCornerShape(8.dp)
        FoxyUiDesignStyle.BENTO_UI -> RoundedCornerShape(15.dp)
        FoxyUiDesignStyle.EDITORIAL_DESIGN -> RoundedCornerShape(4.dp)
        FoxyUiDesignStyle.DARK_LUXURY -> RoundedCornerShape(14.dp)
        else -> CircleShape
    }
    val cornerRadius = when (style) {
        FoxyUiDesignStyle.PIXEL_GOOGLE -> 18.dp
        FoxyUiDesignStyle.NEO_BRUTALISM -> 8.dp
        FoxyUiDesignStyle.BENTO_UI -> 15.dp
        FoxyUiDesignStyle.EDITORIAL_DESIGN -> 4.dp
        FoxyUiDesignStyle.DARK_LUXURY -> 14.dp
        else -> size / 2
    }

    val elevation by animateDpAsState(
        targetValue = if (isPressed || isSelected) 1.dp else 4.dp,
        label = "icon-btn-elev",
    )

    val borderWidth = when (style) {
        FoxyUiDesignStyle.NEO_BRUTALISM -> 2.2.dp
        FoxyUiDesignStyle.DARK_LUXURY -> 1.3.dp
        else -> 1.dp
    }
    val borderColor = when {
        isSelected -> neu.accent
        style == FoxyUiDesignStyle.PIXEL_GOOGLE -> neu.accent.copy(alpha = 0.28f)
        style == FoxyUiDesignStyle.NEO_BRUTALISM -> if (neu.isDark) neu.accent else Color(0xFF0B0F19)
        style == FoxyUiDesignStyle.DARK_LUXURY -> Color(0xFFE5B84B).copy(alpha = 0.50f)
        neu.isDark -> Color.White.copy(alpha = 0.08f)
        else -> Color.White.copy(alpha = 0.78f)
    }

    Box(
        modifier = modifier
            .size(size)
            .neumorphicRaised(
                shape = shape,
                cornerRadius = cornerRadius,
                elevation = elevation,
                isPressed = isPressed || isSelected,
                lightShadowColor = neu.shadowLight.copy(alpha = if (neu.isDark) 0.35f else 0.9f),
                darkShadowColor = neu.shadowDark.copy(alpha = if (neu.isDark) 0.75f else 0.5f),
                designStyle = style,
                isDark = neu.isDark,
                accentColor = neu.accent,
            )
            .clip(shape)
            .background(if (isSelected) neu.accent.copy(alpha = 0.16f) else neu.surface)
            .styleInnerTreatment(
                designStyle = style,
                cornerRadius = cornerRadius,
                isDark = neu.isDark,
                accentColor = neu.accent,
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Slide-to-Connect Neumorphic Component.
 * - Distinct gesture to Connect (drag thumb to end).
 * - Distinct gesture to Disconnect (drag thumb to start).
 * - Independent visual feedback and animations for Ready, Connecting, Connected, Disconnecting, Error.
 */
@Composable
fun NeumorphicSlideToConnect(
    state: ConnectionState,
    lastError: String?,
    onRequestConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val trackHeight = 68.dp
    val thumbSize = 56.dp
    val trackPadding = 6.dp
    val thumbRadius = 28.dp
    val trackCornerRadius = 34.dp

    var totalWidthPx by remember { mutableFloatStateOf(0f) }
    val maxDragPx = remember(totalWidthPx, density) {
        with(density) {
            (totalWidthPx - (thumbSize + trackPadding * 2).toPx()).coerceAtLeast(0f)
        }
    }

    // Drag offset in px
    val offsetX = remember { Animatable(0f) }

    var isDisconnecting by remember { mutableStateOf(false) }

    // When connection state changes, reset or position thumb
    LaunchedEffect(state, maxDragPx) {
        if (state != ConnectionState.CONNECTED) {
            isDisconnecting = false
        }
        if (maxDragPx > 0f) {
            when (state) {
                ConnectionState.CONNECTED -> {
                    offsetX.animateTo(maxDragPx, spring(stiffness = 300f))
                }
                ConnectionState.CONNECTING -> {
                    offsetX.animateTo(maxDragPx, spring(stiffness = 300f))
                }
                ConnectionState.DISCONNECTED -> {
                    offsetX.animateTo(0f, spring(stiffness = 400f))
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "slide-pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse-alpha",
    )

    val hasError = state == ConnectionState.DISCONNECTED && !lastError.isNullOrBlank()
    val errorColor = MaterialTheme.colorScheme.error

    val trackBgColor = when {
        isDisconnecting -> statusColors.connecting.copy(alpha = 0.14f)
        state == ConnectionState.CONNECTED -> statusColors.connected.copy(alpha = 0.12f)
        state == ConnectionState.CONNECTING -> statusColors.connecting.copy(alpha = 0.15f)
        hasError -> errorColor.copy(alpha = 0.10f)
        else -> neu.surface
    }

    val thumbAccentColor = when {
        isDisconnecting -> statusColors.connecting
        state == ConnectionState.CONNECTED -> statusColors.connected
        state == ConnectionState.CONNECTING -> statusColors.connecting
        hasError -> errorColor
        else -> neu.accent
    }

    val trackShape = RoundedCornerShape(trackCornerRadius)
    val thumbShape = CircleShape

    val dragProgress = if (maxDragPx > 0f) (offsetX.value / maxDragPx).coerceIn(0f, 1f) else 0f

    val dragModifier = Modifier.draggable(
        orientation = Orientation.Horizontal,
        reverseDirection = s.isRtl,
        enabled = state != ConnectionState.CONNECTING && !isDisconnecting,
        state = rememberDraggableState { delta ->
            if (maxDragPx > 0f) {
                coroutineScope.launch {
                    val newOffset = (offsetX.value + delta).coerceIn(0f, maxDragPx)
                    offsetX.snapTo(newOffset)
                }
            }
        },
        onDragStopped = {
            if (maxDragPx > 0f) {
                when (state) {
                    ConnectionState.DISCONNECTED -> {
                        if (offsetX.value >= maxDragPx * 0.55f) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            offsetX.animateTo(maxDragPx, spring(stiffness = 500f))
                            onRequestConnect()
                            kotlinx.coroutines.delay(350)
                            if (state == ConnectionState.DISCONNECTED) {
                                offsetX.animateTo(0f, spring(stiffness = 400f))
                            }
                        } else {
                            offsetX.animateTo(0f, spring(stiffness = 500f))
                        }
                    }
                    ConnectionState.CONNECTED -> {
                        if (offsetX.value <= maxDragPx * 0.45f) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            isDisconnecting = true
                            offsetX.animateTo(0f, spring(stiffness = 500f))
                            onDisconnect()
                            kotlinx.coroutines.delay(400)
                            isDisconnecting = false
                        } else {
                            offsetX.animateTo(maxDragPx, spring(stiffness = 500f))
                        }
                    }
                    ConnectionState.CONNECTING -> {
                        // During connecting, allow tap to cancel
                    }
                }
            }
        },
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .onSizeChanged { totalWidthPx = it.width.toFloat() }
            .then(dragModifier)
            .neumorphicRaised(
                shape = trackShape,
                cornerRadius = trackCornerRadius,
                elevation = 5.dp,
                lightShadowColor = neu.shadowLight.copy(alpha = if (neu.isDark) 0.35f else 0.85f),
                darkShadowColor = neu.shadowDark.copy(alpha = if (neu.isDark) 0.75f else 0.45f),
            )
            .clip(trackShape)
            .background(neu.surface)
            .border(
                width = 1.dp,
                color = when {
                    hasError -> errorColor.copy(alpha = 0.45f)
                    neu.isDark -> Color.White.copy(alpha = 0.05f)
                    else -> Color.White.copy(alpha = 0.65f)
                },
                shape = trackShape,
            )
            .neumorphicSunken(
                shape = trackShape,
                cornerRadius = trackCornerRadius,
                depth = 3.dp,
                surfaceColor = trackBgColor,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Track text and guidance
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isDisconnecting -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 48.dp),
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(18.dp),
                            color = statusColors.connecting,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = s.disconnecting,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColors.connecting,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
                state == ConnectionState.DISCONNECTED -> {
                    val labelAlpha = (1f - dragProgress * 1.4f).coerceIn(0f, 1f)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 60.dp, end = 12.dp)
                            .graphicsLayer { alpha = labelAlpha },
                    ) {
                        Text(
                            text = if (hasError) s.slideToRetry else s.slideToConnect,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.2.sp,
                            ),
                            color = if (hasError) errorColor else neu.textPrimary.copy(alpha = 0.88f),
                            maxLines = 1,
                            softWrap = false,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = (if (hasError) errorColor else neu.accent).copy(alpha = pulseAlpha),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                state == ConnectionState.CONNECTING -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 60.dp, start = 12.dp)
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDisconnect()
                            },
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(18.dp),
                            color = statusColors.connecting,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = s.connectingTapToCancel,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColors.connecting,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
                state == ConnectionState.CONNECTED -> {
                    val labelAlpha = (dragProgress * 1.4f - 0.4f).coerceIn(0f, 1f)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 60.dp, start = 12.dp)
                            .graphicsLayer { alpha = labelAlpha },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = statusColors.connected.copy(alpha = pulseAlpha),
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = s.slideToDisconnect,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.2.sp,
                            ),
                            color = statusColors.connected,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }

        // Draggable / Tappable Thumb
        // Note: Modifier.offset { IntOffset(x, y) } automatically mirrors x in RTL LayoutDirection!
        // Never manually negate currentOffsetPx for RTL or it will double-invert.
        val currentOffsetPx = offsetX.value

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(currentOffsetPx.roundToInt(), 0)
                }
                .padding(trackPadding)
                .size(thumbSize)
                .neumorphicRaised(
                    shape = thumbShape,
                    cornerRadius = thumbRadius,
                    elevation = 6.dp,
                    lightShadowColor = neu.shadowLight.copy(alpha = if (neu.isDark) 0.4f else 0.95f),
                    darkShadowColor = neu.shadowDark.copy(alpha = if (neu.isDark) 0.8f else 0.55f),
                )
                .clip(thumbShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            thumbAccentColor.copy(alpha = 0.95f),
                            thumbAccentColor,
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.45f),
                    shape = thumbShape,
                )
                .clickable {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    when (state) {
                        ConnectionState.DISCONNECTED -> {
                            coroutineScope.launch {
                                if (maxDragPx > 0f) {
                                    offsetX.animateTo(maxDragPx, spring(stiffness = 400f))
                                }
                                onRequestConnect()
                                kotlinx.coroutines.delay(350)
                                if (state == ConnectionState.DISCONNECTED) {
                                    offsetX.animateTo(0f, spring(stiffness = 400f))
                                }
                            }
                        }
                        ConnectionState.CONNECTED -> {
                            coroutineScope.launch {
                                isDisconnecting = true
                                offsetX.animateTo(0f, spring(stiffness = 400f))
                                onDisconnect()
                                kotlinx.coroutines.delay(400)
                                isDisconnecting = false
                            }
                        }
                        ConnectionState.CONNECTING -> {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring(stiffness = 400f))
                                onDisconnect()
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            when {
                isDisconnecting || state == ConnectionState.CONNECTING -> {
                    CircularProgressIndicator(
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                    )
                }
                state == ConnectionState.DISCONNECTED -> {
                    Icon(
                        imageVector = Icons.Filled.PowerSettingsNew,
                        contentDescription = "Slide to connect",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                state == ConnectionState.CONNECTED -> {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Connected",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

/**
 * Full-screen Background Poster & Architectural Atmosphere for each of the 7 UI Design Skins.
 * Transforms the entire app's visual poster when switching skins.
 */
@Composable
fun StyleAtmospherePosterOverlay(
    modifier: Modifier = Modifier,
) {
    val neu = currentNeumorphicColors
    val style = neu.designStyle
    val accent = neu.accent
    val isDark = neu.isDark

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 4f || h <= 4f) return@Canvas

        when (style) {
            FoxyUiDesignStyle.PIXEL_GOOGLE -> {
                // Google Pixel Material You organic pastel blobs & expressive stadium pills
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = if (isDark) 0.16f else 0.14f),
                            Color.Transparent,
                        ),
                        center = Offset(w * 0.85f, h * 0.12f),
                        radius = w * 0.65f,
                    ),
                    center = Offset(w * 0.85f, h * 0.12f),
                    radius = w * 0.65f,
                )
                drawRoundRect(
                    color = accent.copy(alpha = if (isDark) 0.07f else 0.08f),
                    topLeft = Offset(-w * 0.15f, h * 0.62f),
                    size = Size(w * 0.55f, h * 0.22f),
                    cornerRadius = CornerRadius(120f, 120f),
                )
            }
            FoxyUiDesignStyle.CLAYMORPHISM -> {
                // 3D Floating Clay Spheres in the background poster
                val spheres = listOf(
                    Triple(0.15f, 0.16f, 0.28f),
                    Triple(0.88f, 0.28f, 0.22f),
                    Triple(0.18f, 0.78f, 0.25f),
                )
                spheres.forEach { (cxRatio, cyRatio, rRatio) ->
                    val center = Offset(w * cxRatio, h * cyRatio)
                    val radius = w * rRatio
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = if (isDark) 0.14f else 0.12f),
                                accent.copy(alpha = if (isDark) 0.04f else 0.03f),
                                Color.Transparent,
                            ),
                            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                            radius = radius,
                        ),
                        center = center,
                        radius = radius,
                    )
                }
            }
            FoxyUiDesignStyle.DARK_LUXURY -> {
                // Champagne Gold Spotlight + Subtle Luxury Diamond Guilloché Grid
                val gold = Color(0xFFE5B84B)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            gold.copy(alpha = 0.14f),
                            accent.copy(alpha = 0.06f),
                            Color.Transparent,
                        ),
                        center = Offset(w * 0.5f, h * 0.28f),
                        radius = w * 0.85f,
                    ),
                    center = Offset(w * 0.5f, h * 0.28f),
                    radius = w * 0.85f,
                )
                val step = 64.dp.toPx().coerceAtLeast(32f)
                val lineColor = gold.copy(alpha = 0.045f)
                var x = -h
                while (x < w + h) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x, 0f),
                        end = Offset(x + h, h),
                        strokeWidth = 1f,
                    )
                    drawLine(
                        color = lineColor,
                        start = Offset(x + h, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f,
                    )
                    x += step
                }
            }
            FoxyUiDesignStyle.NEO_BRUTALISM -> {
                // Neo-Brutalist Halftone Dot-Matrix Poster Grid + Geometric Corner Accent
                val spacing = 26.dp.toPx().coerceAtLeast(16f)
                val dotColor = if (isDark) accent.copy(alpha = 0.15f) else Color(0xFF0B0F19).copy(alpha = 0.10f)
                var y = spacing
                while (y < h) {
                    var x = spacing
                    while (x < w) {
                        drawCircle(
                            color = dotColor,
                            radius = 1.6.dp.toPx(),
                            center = Offset(x, y),
                        )
                        x += spacing
                    }
                    y += spacing
                }
            }
            FoxyUiDesignStyle.BENTO_UI -> {
                // Modular Apple-style Bento Box Grid Hairlines
                val gridStep = 84.dp.toPx().coerceAtLeast(48f)
                val gridColor = if (isDark) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.035f)
                var x = gridStep
                while (x < w) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                    x += gridStep
                }
                var y = gridStep
                while (y < h) {
                    drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    y += gridStep
                }
            }
            FoxyUiDesignStyle.EDITORIAL_DESIGN -> {
                // Architectural Magazine Column Rules & Gallery Framing
                val ruleColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFF18181B).copy(alpha = 0.06f)
                val margin = 16.dp.toPx()
                drawLine(ruleColor, Offset(margin, 0f), Offset(margin, h), strokeWidth = 1.2f)
                drawLine(ruleColor, Offset(w - margin, 0f), Offset(w - margin, h), strokeWidth = 1.2f)
                drawLine(ruleColor, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = 0.8f)
            }
            FoxyUiDesignStyle.NEUMORPHISM -> Unit
        }
    }
}

/**
 * Standard Telegram-style Theme & Appearance Icon (Half Sun/Moon + Theme Palette Rays).
 */
@Composable
fun TelegramThemeAppearanceIcon(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = minOf(w, h) * 0.28f

        // Outer ring
        drawCircle(
            color = tint,
            radius = r,
            center = Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = minOf(w, h) * 0.095f),
        )

        // Half-filled crescent/hemisphere inside (Telegram theme/appearance symbol)
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2f, r * 2f),
        )

        // 8 crisp surrounding rays like Telegram's theme switcher icon
        val rayInner = minOf(w, h) * 0.37f
        val rayOuter = minOf(w, h) * 0.47f
        val strokeW = minOf(w, h) * 0.09f
        for (i in 0 until 8) {
            val angleRad = Math.toRadians((i * 45.0))
            val sx = cx + (kotlin.math.cos(angleRad) * rayInner).toFloat()
            val sy = cy + (kotlin.math.sin(angleRad) * rayInner).toFloat()
            val ex = cx + (kotlin.math.cos(angleRad) * rayOuter).toFloat()
            val ey = cy + (kotlin.math.sin(angleRad) * rayOuter).toFloat()
            drawLine(
                color = tint,
                start = Offset(sx, sy),
                end = Offset(ex, ey),
                strokeWidth = strokeW,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
            )
        }
    }
}

