package com.vauth.foxyvpn.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.input.pointer.pointerInput
import java.util.Locale
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vauth.foxyvpn.FoxyVpnApp
import com.vauth.foxyvpn.R
import com.vauth.foxyvpn.data.GUARDIAN_ENDPOINT_DEFAULT
import com.vauth.foxyvpn.data.RECOMMENDED_COUNTRY_CODE
import com.vauth.foxyvpn.data.ServerListClient
import com.vauth.foxyvpn.data.model.ConnectionState
import com.vauth.foxyvpn.data.model.ProxyCandidate
import com.vauth.foxyvpn.data.model.emojiFlag
import com.vauth.foxyvpn.ui.components.DynamicIsland
import com.vauth.foxyvpn.ui.theme.AnimatedOdometerText
import com.vauth.foxyvpn.ui.theme.AnimatedWorldMapBackground
import com.vauth.foxyvpn.ui.theme.FoxyLanguage
import com.vauth.foxyvpn.ui.theme.LanguageController
import com.vauth.foxyvpn.ui.theme.LiveTrafficSparkline
import com.vauth.foxyvpn.ui.theme.LocalFoxyStatusColors
import com.vauth.foxyvpn.ui.theme.NeumorphicCard
import com.vauth.foxyvpn.ui.theme.NeumorphicIconButton
import com.vauth.foxyvpn.ui.theme.NeumorphicRadarPulse
import com.vauth.foxyvpn.ui.theme.NeumorphicSlideToConnect
import com.vauth.foxyvpn.ui.theme.StyleAtmospherePosterOverlay
import com.vauth.foxyvpn.ui.theme.TelegramThemeAppearanceIcon
import com.vauth.foxyvpn.ui.theme.ThemeController
import com.vauth.foxyvpn.ui.theme.ThemeMode
import com.vauth.foxyvpn.ui.theme.currentFoxyFonts
import com.vauth.foxyvpn.ui.theme.currentFoxyStrings
import com.vauth.foxyvpn.ui.theme.currentNeumorphicColors
import com.vauth.foxyvpn.ui.theme.formatBytesLocalized
import com.vauth.foxyvpn.ui.theme.formatClockTimeLocalized
import com.vauth.foxyvpn.ui.theme.formatDurationLocalized
import com.vauth.foxyvpn.ui.theme.formatMinutesLocalized
import com.vauth.foxyvpn.ui.theme.formatPingLocalized
import com.vauth.foxyvpn.ui.theme.formatSpeedLocalized
import com.vauth.foxyvpn.ui.theme.localizeCountryName
import com.vauth.foxyvpn.ui.theme.localizeDigits
import com.vauth.foxyvpn.ui.theme.neumorphicSunken
import com.vauth.foxyvpn.ui.theme.neumorphicTouchBounce
import com.vauth.foxyvpn.ui.theme.shakeOnTrigger
import com.vauth.foxyvpn.ui.theme.staggeredEntrance
import com.vauth.foxyvpn.ui.theme.subtleFloatAnimation
import com.vauth.foxyvpn.vpn.ExitCheck
import com.vauth.foxyvpn.vpn.FoxyVpnService
import com.vauth.foxyvpn.vpn.IpLocationInfo
import com.vauth.foxyvpn.vpn.PingUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    app: FoxyVpnApp,
    themeController: ThemeController,
    languageController: LanguageController,
    onRequestConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenServers: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTools: (Int) -> Unit = {},
) {
    val state by FoxyVpnService.state.collectAsState()
    val lastError by FoxyVpnService.lastError.collectAsState()
    val liveStats by FoxyVpnService.vpnLiveStats.collectAsState()
    val connectedAtEpochMs by FoxyVpnService.connectedAtEpochMs.collectAsState()
    val pingLatency by FoxyVpnService.pingLatencyMs.collectAsState()
    val vpnEdgeIp by FoxyVpnService.vpnEdgeIp.collectAsState()

    val selectedProxy by app.proxyStateStore.selectedProxyFlow.collectAsState()

    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val fonts = currentFoxyFonts
    val systemInDarkTheme = isSystemInDarkTheme()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var realIp by remember {
        mutableStateOf<IpLocationInfo?>(
            app.settingsStore.cachedRealIp?.let {
                IpLocationInfo(it, app.settingsStore.cachedRealCountry.orEmpty())
            },
        )
    }
    var isPinging by remember { mutableStateOf(false) }
    var disconnectedPingLatency by remember { mutableStateOf<Int?>(null) }
    var quotaRemaining by remember { mutableStateOf(app.settingsStore.cachedQuotaRemaining) }
    var quotaMax by remember { mutableStateOf(app.settingsStore.cachedQuotaMax) }
    var isUnlimited by remember { mutableStateOf(app.settingsStore.cachedIsUnlimited) }

    // Rolling 24-sample history for live traffic waveform
    val rxHistory = remember { mutableStateListOf<Long>().apply { repeat(24) { add(0L) } } }
    val txHistory = remember { mutableStateListOf<Long>().apply { repeat(24) { add(0L) } } }

    LaunchedEffect(liveStats, state) {
        if (state == ConnectionState.CONNECTED) {
            if (rxHistory.size >= 24) rxHistory.removeAt(0)
            rxHistory.add(liveStats.rxRateBytesPerSec)
            if (txHistory.size >= 24) txHistory.removeAt(0)
            txHistory.add(liveStats.txRateBytesPerSec)
        } else {
            for (i in 0 until rxHistory.size) rxHistory[i] = 0L
            for (i in 0 until txHistory.size) txHistory[i] = 0L
        }
    }

    LaunchedEffect(state) {
        if (state == ConnectionState.CONNECTED) {
            quotaRemaining = app.settingsStore.cachedQuotaRemaining
            quotaMax = app.settingsStore.cachedQuotaMax
            isUnlimited = app.settingsStore.cachedIsUnlimited
        }
    }

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

    LaunchedEffect(Unit) {
        launch {
            ExitCheck().fetchDirectIpInfo().onSuccess { info ->
                realIp = info
                app.settingsStore.cachedRealIp = info.ip
                app.settingsStore.cachedRealCountry = info.countryCode
            }
        }
        launch {
            val token = app.authRepository.currentAccessToken()
            if (token != null) {
                runCatching { app.guardianClient.fetchUserInfo(GUARDIAN_ENDPOINT_DEFAULT, token) }
                    .onSuccess { entitlement ->
                        val unlimited = !entitlement.limitedBandwidth
                        isUnlimited = unlimited
                        app.settingsStore.cachedIsUnlimited = unlimited
                        entitlement.quotaRemaining?.let {
                            quotaRemaining = it
                            app.settingsStore.cachedQuotaRemaining = it
                        }
                        entitlement.maxBytes?.let {
                            quotaMax = it
                            app.settingsStore.cachedQuotaMax = it
                        }
                        app.tokenStore.updateActiveAccountQuota(
                            remaining = entitlement.quotaRemaining,
                            max = entitlement.maxBytes,
                            unlimited = unlimited,
                        )
                    }
            }
        }
    }

    val themeIconRotation by animateFloatAsState(
        targetValue = when (themeController.mode) {
            ThemeMode.SYSTEM -> 0f
            ThemeMode.LIGHT -> 180f
            ThemeMode.DARK -> 360f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "theme-icon-rot",
    )

    val heroStatusColor by animateColorAsState(
        targetValue = when (state) {
            ConnectionState.CONNECTED -> statusColors.connected
            ConnectionState.CONNECTING -> statusColors.connecting
            ConnectionState.DISCONNECTED -> neu.accent
        },
        animationSpec = tween(420),
        label = "hero-accent-color",
    )

    Scaffold(
        containerColor = neu.background,
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            val scrollState = rememberScrollState()

            // 1. 3D Interactive World Globe & Map Background (tilts & rotates with scroll)
            AnimatedWorldMapBackground(
                isConnected = state == ConnectionState.CONNECTED,
                isConnecting = state == ConnectionState.CONNECTING,
                accentColor = heroStatusColor,
                isDark = neu.isDark,
                scrollOffsetPx = scrollState.value.toFloat(),
                modifier = Modifier.fillMaxSize(),
            )

            // 1B. Full-Screen Skin / Poster Atmosphere Overlay (Google Pixel UI, Claymorphism, Dark Luxury, Neo-Brutalism, Bento UI, Editorial, Neumorphism)
            StyleAtmospherePosterOverlay(
                modifier = Modifier.fillMaxSize(),
            )

            var isMagazineOpen by remember { mutableStateOf(false) }
            val magazineRotation by animateFloatAsState(
                targetValue = if (isMagazineOpen) 180f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
                label = "magazine-trigger-rotation",
            )

            Column(
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 2. TOP BRAND HEADER (Moved 20% higher to the standard top position above Dynamic Island, with larger Amsterdam logo & Collapsible Magazine Action Button)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 4.dp),
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            // Top-Left Brand Lockup: First App Icon (44.dp), then Enlarged "Foxy vpn" Amsterdam Signature (50.dp x 168.dp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                NeumorphicIconButton(
                                    size = 44.dp,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    modifier = Modifier.subtleFloatAnimation(amplitudePx = 3f, durationMs = 2600),
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_foxy_logo_clean),
                                        contentDescription = "Foxy vpn",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(31.dp),
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.foxy_brand_amsterdam),
                                    contentDescription = "Foxy vpn",
                                    colorFilter = ColorFilter.tint(neu.textPrimary),
                                    contentScale = ContentScale.Fit,
                                    alignment = Alignment.CenterStart,
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(168.dp),
                                )
                            }

                            // Top-Right Controls: Day/Night Theme Toggle + Settings Button right next to it + Collapsible Magazine Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // 1. Day/Night Theme Toggle Button (Telegram-Style Appearance Icon)
                                val mode = themeController.mode
                                NeumorphicIconButton(
                                    size = 42.dp,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        themeController.toggle(systemInDarkTheme)
                                    },
                                    modifier = Modifier.neumorphicTouchBounce(),
                                ) {
                                    TelegramThemeAppearanceIcon(
                                        tint = neu.accent,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer { rotationZ = themeIconRotation },
                                    )
                                }

                                // 2. Settings Icon Button right next to Day/Night Theme Button
                                NeumorphicIconButton(
                                    size = 42.dp,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isMagazineOpen = false
                                        onOpenSettings()
                                    },
                                    modifier = Modifier.neumorphicTouchBounce(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = s.headerSettingsButton,
                                        tint = neu.textPrimary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }

                                // 3. Main Collapsible Magazine Action Button ("دکمه خشابی")
                                NeumorphicIconButton(
                                    size = 42.dp,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isMagazineOpen = !isMagazineOpen
                                    },
                                    modifier = Modifier.neumorphicTouchBounce(),
                                ) {
                                    Icon(
                                        imageVector = if (isMagazineOpen) Icons.Filled.Close else Icons.Filled.Tune,
                                        contentDescription = s.headerSettingsButton,
                                        tint = if (isMagazineOpen) neu.accent else neu.textPrimary,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer { rotationZ = magazineRotation },
                                    )
                                }
                            }
                        }
                    }

                    // Collapsible Magazine Action Bar ("خشاب ابزار با انیمیشن نرم")
                    AnimatedVisibility(
                        visible = isMagazineOpen,
                        enter = expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = spring(
                                dampingRatio = 0.82f,
                                stiffness = 360f,
                            ),
                        ) + fadeIn(tween(180)) + scaleIn(
                            initialScale = 0.92f,
                            animationSpec = spring(
                                dampingRatio = 0.82f,
                                stiffness = 360f,
                            ),
                        ),
                        exit = shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            animationSpec = spring(
                                dampingRatio = 0.86f,
                                stiffness = 420f,
                            ),
                        ) + fadeOut(tween(130)) + scaleOut(targetScale = 0.92f),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                        ) {
                            NeumorphicCard(
                                cornerRadius = 20.dp,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                ) {
                                    // Compact Single-Row Magazine Action Buttons Capsule ("خشاب دکمه‌های ابزار")
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        // Control 1: Connection Wave Animation Switcher
                                        NeumorphicIconButton(
                                            size = 42.dp,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                themeController.cycleHeroWaveStyle()
                                            },
                                            modifier = Modifier.neumorphicTouchBounce(),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Waves,
                                                contentDescription = s.heroWaveStyleTitle,
                                                tint = neu.accent,
                                                modifier = Modifier.size(19.dp),
                                            )
                                        }

                                        // Control 2: Speed Test
                                        NeumorphicIconButton(
                                            size = 42.dp,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                isMagazineOpen = false
                                                onOpenTools(0)
                                            },
                                            modifier = Modifier.neumorphicTouchBounce(),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Speed,
                                                contentDescription = s.headerSpeedButton,
                                                tint = neu.accent,
                                                modifier = Modifier.size(19.dp),
                                            )
                                        }

                                        // Control 3: Animated Language Switcher (EN -> فا -> عر)
                                        NeumorphicIconButton(
                                            size = 42.dp,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                languageController.toggle()
                                            },
                                            modifier = Modifier.neumorphicTouchBounce(),
                                        ) {
                                            AnimatedContent(
                                                targetState = languageController.language,
                                                transitionSpec = {
                                                    (slideInVertically { h -> h / 2 } + scaleIn(initialScale = 0.75f) + fadeIn(tween(200)))
                                                        .togetherWith(slideOutVertically { h -> -h / 2 } + scaleOut(targetScale = 0.75f) + fadeOut(tween(150)))
                                                },
                                                label = "lang-switch-micro",
                                            ) { lang ->
                                                Text(
                                                    text = lang.badge,
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontFamily = fonts.headingFont,
                                                        fontWeight = FontWeight.ExtraBold,
                                                    ),
                                                    color = neu.accent,
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

                // 3. Dynamic Island right below Top Brand Header
                DynamicIsland(
                    settingsStore = app.settingsStore,
                    selectedProxy = selectedProxy,
                    onDisconnect = onDisconnect,
                    onRequestConnect = onRequestConnect,
                    onOpenServers = onOpenServers,
                    isSystemOverlay = false,
                )

                Spacer(Modifier.height(6.dp))

                // 4. CONNECTION STATUS & TIMER HERO:
                // Enlarged 320.dp Screen-Filling Harmonic Wave Aura + Full-Width Single-Line Status Badge
                val statusText = when (state) {
                    ConnectionState.CONNECTED -> s.statusConnected
                    ConnectionState.CONNECTING -> s.statusConnecting
                    ConnectionState.DISCONNECTED -> s.statusDisconnected
                }
                val statusColor by animateColorAsState(
                    targetValue = when (state) {
                        ConnectionState.CONNECTED -> statusColors.connected
                        ConnectionState.CONNECTING -> statusColors.connecting
                        ConnectionState.DISCONNECTED -> neu.textSecondary
                    },
                    animationSpec = tween(400),
                    label = "hero-status-color",
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(0),
                ) {
                    // Enlarged 320.dp harmonic wave rings behind the timer & status badge
                    val waveConfigRev by app.settingsStore.islandConfigRevision.collectAsState()
                    @Suppress("UNUSED_EXPRESSION")
                    waveConfigRev
                    NeumorphicRadarPulse(
                        color = if (state == ConnectionState.DISCONNECTED) neu.accent else statusColor,
                        isActive = state == ConnectionState.CONNECTED,
                        isConnecting = state == ConnectionState.CONNECTING,
                        size = 320.dp,
                        waveStyle = themeController.heroWaveStyle,
                        speedMultiplier = app.settingsStore.waveSpeedMultiplier,
                        amplitudeScale = app.settingsStore.waveAmplitudeScale,
                        glowEnabled = app.settingsStore.waveGlowEnabled,
                        colorMode = app.settingsStore.waveColorMode,
                    )

                    // Overlay Column takes full width so SECURE & CONNECTED never truncates or wraps
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    ) {
                        val formattedTimer = if (state == ConnectionState.CONNECTED) {
                            s.language.formatDurationLocalized(elapsedSeconds, alwaysShowHours = true)
                        } else {
                            s.language.formatDurationLocalized(0L, alwaysShowHours = true)
                        }

                        AnimatedOdometerText(
                            text = formattedTimer,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = fonts.monoFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontFeatureSettings = "tnum",
                            ),
                            color = neu.textPrimary,
                        )

                        Spacer(Modifier.height(10.dp))

                        // Full-Width Single-Line Status Pill Directly Below Numeric Timer (Never Truncated)
                        AnimatedContent(
                            targetState = statusText,
                            transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(180)) },
                            label = "status-label-single-line",
                        ) { label ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(statusColor.copy(alpha = 0.14f))
                                    .border(
                                        width = 1.dp,
                                        color = statusColor.copy(alpha = 0.30f),
                                        shape = RoundedCornerShape(50),
                                    )
                                    .padding(horizontal = 18.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = if (s.isRtl) 0.sp else 1.0.sp,
                                    ),
                                    color = statusColor,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Visible,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 5. SLIDE-TO-CONNECT PRIMARY CONTROLLER
                NeumorphicSlideToConnect(
                    state = state,
                    lastError = lastError,
                    onRequestConnect = onRequestConnect,
                    onDisconnect = onDisconnect,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .staggeredEntrance(1)
                        .shakeOnTrigger(lastError),
                )

                val error = lastError
                AnimatedVisibility(
                    visible = error != null && state != ConnectionState.CONNECTING,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Text(
                        text = error.orEmpty(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                Spacer(Modifier.height(18.dp))

                // 6. 5-TOOL QUICK NETWORK & SECURITY TOOLKIT HUB
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(2),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = s.smartToolsSection,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = fonts.headingFont,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                            ),
                            color = neu.textSecondary,
                            maxLines = 1,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenTools(0) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = s.openAllTools,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = neu.accent,
                                maxLines = 1,
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = neu.accent,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        NetworkToolTab.entries.forEachIndexed { idx, tool ->
                            val toolTitle = tool.localizedTitle(s)
                            val toolSub = tool.localizedSubtitle(s)
                            NeumorphicCard(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onOpenTools(tool.id)
                                },
                                cornerRadius = 20.dp,
                                modifier = Modifier
                                    .width(206.dp)
                                    .heightIn(min = 138.dp)
                                    .staggeredEntrance(idx + 2, delayPerItemMs = 50)
                                    .neumorphicTouchBounce(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (tool == NetworkToolTab.TELEGRAM_PROXY) Color(0xFF229ED9).copy(alpha = 0.16f)
                                                else neu.accent.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (tool == NetworkToolTab.TELEGRAM_PROXY) {
                                            Image(
                                                painter = painterResource(R.drawable.ic_brand_telegram),
                                                contentDescription = toolTitle,
                                                modifier = Modifier.size(24.dp),
                                            )
                                        } else {
                                            Icon(
                                                imageVector = tool.icon,
                                                contentDescription = toolTitle,
                                                tint = neu.accent,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = toolTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = fonts.headingFont,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = neu.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = toolSub,
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                                        color = neu.textSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 7. SERVER LOCATION CARD
                NeumorphicCard(
                    onClick = onOpenServers,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(3)
                        .neumorphicTouchBounce(),
                    cornerRadius = 22.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val flag = selectedProxy?.emojiFlag
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(neu.surfaceElevated),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (!flag.isNullOrBlank()) {
                                Text(flag, fontSize = 24.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = null,
                                    tint = neu.accent,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.serverLocationLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = if (s.isRtl) 0.sp else 0.9.sp,
                                ),
                                color = neu.textSecondary,
                                maxLines = 1,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = selectedProxy?.let {
                                    s.language.localizeCountryName(it.countryName, it.countryCode)
                                } ?: s.recommendedFastest,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = neu.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = s.islandChangeServer,
                            tint = neu.textSecondary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 7B. ANIMATED WORLD MAP ORIGIN-TO-EXIT ROUTE CARD
                val liveProtocolBadge by FoxyVpnService.activeProtocolBadge.collectAsState()
                WorldRouteMapCard(
                    state = state,
                    realIp = realIp,
                    cachedRealIp = app.settingsStore.cachedRealIp,
                    cachedRealCountry = app.settingsStore.cachedRealCountry,
                    selectedProxy = selectedProxy,
                    vpnEdgeIp = vpnEdgeIp,
                    protocolLabel = if (state == ConnectionState.CONNECTED) liveProtocolBadge else app.settingsStore.connectionProtocolMode.shortBadge,
                    adBlockEnabled = app.settingsStore.adBlockEnabled,
                    onOpenServers = onOpenServers,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(3),
                )

                Spacer(Modifier.height(18.dp))

                // 8. CURRENT IP CARD:
                // - "CURRENT IP • TAP FOR LEAK TEST" on a single horizontal line
                // - Full IPv6/IPv4 address on a separate line inside a Quote Block / Framed Container
                NeumorphicCard(
                    onClick = { onOpenTools(NetworkToolTab.LEAK_INSPECTOR.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(4)
                        .neumorphicTouchBounce(),
                    cornerRadius = 22.dp,
                ) {
                    val isConnected = state == ConnectionState.CONNECTED
                    val rawIpDisplay = if (isConnected) {
                        vpnEdgeIp?.let {
                            if (it.countryCode.isNotEmpty()) "${it.ip} • ${it.countryCode}" else it.ip
                        } ?: s.verifying
                    } else {
                        realIp?.let {
                            if (it.countryCode.isNotEmpty()) "${it.ip} • ${it.countryCode}" else it.ip
                        } ?: app.settingsStore.cachedRealIp?.let { cachedIp ->
                            val cachedCountry = app.settingsStore.cachedRealCountry
                            if (!cachedCountry.isNullOrEmpty()) "$cachedIp • $cachedCountry" else cachedIp
                        } ?: s.checking
                    }
                    val ipDisplay = s.language.localizeDigits(rawIpDisplay)
                    val badgeColor = if (isConnected) statusColors.connected else MaterialTheme.colorScheme.error

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                    ) {
                        // Top Row: Shield Icon + Single-line "CURRENT IP • TAP FOR LEAK TEST" + Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnected) statusColors.connected.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = if (isConnected) Icons.Filled.Security else Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(20.dp),
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = s.currentIpTapLeakTest,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = if (s.isRtl) 0.sp else 0.5.sp,
                                ),
                                color = neu.textSecondary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(badgeColor.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            ) {
                                Text(
                                    text = if (isConnected) s.badgeProtected else s.badgeExposed,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.ExtraBold,
                                    ),
                                    color = badgeColor,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Separate Row Below: Styled Quote Block / Framed Box for IPv6 / IPv4 Address
                        val quoteShape = RoundedCornerShape(14.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .neumorphicSunken(
                                    shape = quoteShape,
                                    cornerRadius = 14.dp,
                                    depth = 2.5.dp,
                                    surfaceColor = neu.surfaceElevated,
                                )
                                .border(
                                    width = 1.dp,
                                    color = badgeColor.copy(alpha = 0.24f),
                                    shape = quoteShape,
                                )
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Quote Block Accent Bar
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(22.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(badgeColor),
                            )
                            Spacer(Modifier.width(12.dp))
                            AnimatedContent(
                                targetState = ipDisplay,
                                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                                label = "ip-quote-box-anim",
                                modifier = Modifier.weight(1f),
                            ) { targetIp ->
                                Text(
                                    text = targetIp,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = neu.textPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 9. TELEMETRY, LIVE WAVEFORM SPARKLINE & QUOTA CARD
                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(5),
                    cornerRadius = 22.dp,
                ) {
                    Column(Modifier.padding(20.dp)) {
                        val isConnected = state == ConnectionState.CONNECTED
                        val remaining = quotaRemaining
                        val max = quotaMax

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Data Used Sunken Box
                            val tileShape = RoundedCornerShape(14.dp)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .neumorphicSunken(
                                        shape = tileShape,
                                        cornerRadius = 14.dp,
                                        depth = 2.5.dp,
                                        surfaceColor = neu.surfaceElevated,
                                    )
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                            ) {
                                Column {
                                    Text(
                                        text = s.dataUsedLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = fonts.headingFont,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                                        ),
                                        color = neu.textSecondary,
                                        maxLines = 1,
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = if (isConnected) {
                                            s.language.formatBytesLocalized(liveStats.sessionBytes)
                                        } else {
                                            s.language.formatBytesLocalized(0L)
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = neu.textPrimary,
                                        maxLines = 1,
                                    )
                                }
                            }

                            // Latency / Ping Sunken Box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .neumorphicSunken(
                                        shape = tileShape,
                                        cornerRadius = 14.dp,
                                        depth = 2.5.dp,
                                        surfaceColor = neu.surfaceElevated,
                                    )
                                    .clickable(role = Role.Button) {
                                        if (!isPinging) {
                                            scope.launch {
                                                isPinging = true
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                if (state == ConnectionState.CONNECTED) {
                                                    FoxyVpnService.pingNow()
                                                } else {
                                                    val target = selectedProxy ?: app.proxyStateStore.load() ?: run {
                                                        val cached = app.serverListClient.loadCachedCountries()
                                                        ServerListClient.candidatesForCountry(cached, RECOMMENDED_COUNTRY_CODE).firstOrNull()
                                                            ?: cached.firstOrNull { it.code.isNotBlank() }?.let { country ->
                                                                ServerListClient.candidatesForCountry(cached, country.code).firstOrNull()
                                                            }
                                                    }
                                                    if (target != null) {
                                                        disconnectedPingLatency = PingUtil.measureTcpLatencyMs(target.host, target.port)
                                                    }
                                                }
                                                isPinging = false
                                            }
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = s.latencyLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = fonts.headingFont,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                                            ),
                                            color = neu.textSecondary,
                                            maxLines = 1,
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Filled.NetworkCheck,
                                            contentDescription = s.latencyLabel,
                                            tint = neu.textSecondary,
                                            modifier = Modifier.size(15.dp),
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    val currentPing = if (isConnected) pingLatency else disconnectedPingLatency
                                    Text(
                                        text = when {
                                            isPinging -> s.testing
                                            currentPing != null -> s.language.formatPingLocalized(currentPing)
                                            else -> "\u2014"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = fonts.monoFont,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = if (currentPing != null) statusColors.connected else neu.textPrimary,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Live Traffic Waveform Sparkline Graph (Header separated from Canvas to prevent overlap)
                        val waveShape = RoundedCornerShape(14.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .neumorphicSunken(
                                    shape = waveShape,
                                    cornerRadius = 14.dp,
                                    depth = 2.5.dp,
                                    surfaceColor = neu.surfaceElevated,
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "↓ ${s.language.formatSpeedLocalized(liveStats.rxRateBytesPerSec)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = if (isConnected) statusColors.connected else neu.textSecondary,
                                    maxLines = 1,
                                )
                                Text(
                                    text = "↑ ${s.language.formatSpeedLocalized(liveStats.txRateBytesPerSec)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = fonts.monoFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = statusColors.connecting,
                                    maxLines = 1,
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            LiveTrafficSparkline(
                                rxHistory = rxHistory,
                                txHistory = txHistory,
                                rxColor = if (isConnected) statusColors.connected else neu.accent,
                                txColor = statusColors.connecting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Quota Details & Sunken Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = s.monthlyQuotaLabel,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = fonts.bodyFont,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = neu.textSecondary,
                                modifier = Modifier.weight(1f),
                            )
                            val quotaText = when {
                                isUnlimited -> s.unlimitedQuota
                                remaining != null && max != null ->
                                    "${s.language.formatBytesLocalized(remaining)} / ${s.language.formatBytesLocalized(max)}"
                                remaining != null -> s.language.formatBytesLocalized(remaining)
                                max != null -> "${s.quotaLimitPrefix} ${s.language.formatBytesLocalized(max)}"
                                else -> "\u2014"
                            }
                            Text(
                                text = quotaText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = fonts.monoFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = neu.textPrimary,
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        val rawQuotaFraction = when {
                            isUnlimited -> 1f
                            max != null && max > 0L && remaining != null ->
                                (remaining.toFloat() / max.toFloat()).coerceIn(0f, 1f)
                            else -> 0f
                        }
                        val animatedQuotaFraction by animateFloatAsState(
                            targetValue = rawQuotaFraction,
                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                            label = "quota-bar-fill",
                        )

                        val progressShape = RoundedCornerShape(7.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .neumorphicSunken(
                                    shape = progressShape,
                                    cornerRadius = 7.dp,
                                    depth = 2.dp,
                                    surfaceColor = neu.surfaceElevated,
                                ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedQuotaFraction.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .clip(progressShape)
                                    .background(
                                        if (isConnected) statusColors.connected
                                        else neu.accent.copy(alpha = 0.8f),
                                    ),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 10. SPEED BOOSTER, BATTERY SAVER & SMART AUTOMATION CARD
                val configRev by app.settingsStore.islandConfigRevision.collectAsState()
                @Suppress("UNUSED_EXPRESSION")
                configRev
                var unmeteredBoost by remember(configRev) { mutableStateOf(app.settingsStore.unmeteredVpnModeEnabled) }
                var batteryEco by remember(configRev) { mutableStateOf(app.settingsStore.batterySaverModeEnabled) }
                var scheduleEnabled by remember(configRev) { mutableStateOf(app.settingsStore.scheduleEnabled) }
                var idleEnabled by remember(configRev) { mutableStateOf(app.settingsStore.idleDisconnectEnabled) }
                val idleSeconds by FoxyVpnService.idleTimerSeconds.collectAsState()

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(6),
                    cornerRadius = 22.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = s.sectionSmartAutomation,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = if (s.isRtl) 0.sp else 0.8.sp,
                                ),
                                color = neu.textSecondary,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = s.headerSettingsButton,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = neu.accent,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenSettings() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // Row 0a: Unmetered High-Speed Tunnel Mode + 256KB/1MB Turbo Buffer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.unmeteredSpeedBoostTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = neu.textPrimary,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = s.unmeteredSpeedBoostSub,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                                    color = if (unmeteredBoost) statusColors.connected else neu.textSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = unmeteredBoost,
                                onCheckedChange = {
                                    unmeteredBoost = it
                                    app.settingsStore.unmeteredVpnModeEnabled = it
                                    app.settingsStore.tcpBufferBoostEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = statusColors.connected,
                                ),
                            )
                        }

                        HorizontalDivider(
                            color = neu.textSecondary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(vertical = 10.dp),
                        )

                        // Row 0b: Smart Battery Saver (Eco Mode)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.batterySaverModeTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = neu.textPrimary,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = s.batterySaverModeSub,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.bodyFont),
                                    color = if (batteryEco) neu.accent else neu.textSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = batteryEco,
                                onCheckedChange = {
                                    batteryEco = it
                                    app.settingsStore.batterySaverModeEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = neu.accent,
                                ),
                            )
                        }

                        HorizontalDivider(
                            color = neu.textSecondary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(vertical = 10.dp),
                        )

                        // Row 1: Scheduled Auto-Connect / Auto-Disconnect
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.scheduleVpnTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = neu.textPrimary,
                                )
                                Spacer(Modifier.height(2.dp))
                                val startFmt = s.language.formatClockTimeLocalized(
                                    app.settingsStore.scheduleStartHour,
                                    app.settingsStore.scheduleStartMinute,
                                )
                                val endFmt = s.language.formatClockTimeLocalized(
                                    app.settingsStore.scheduleEndHour,
                                    app.settingsStore.scheduleEndMinute,
                                )
                                Text(
                                    text = "${s.scheduleFromLabel}: $startFmt  •  ${s.scheduleToLabel}: $endFmt",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.monoFont),
                                    color = if (scheduleEnabled) neu.accent else neu.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Switch(
                                checked = scheduleEnabled,
                                onCheckedChange = {
                                    scheduleEnabled = it
                                    app.settingsStore.scheduleEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = neu.accent,
                                ),
                            )
                        }

                        HorizontalDivider(
                            color = neu.textSecondary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(vertical = 10.dp),
                        )

                        // Row 2: Smart Idle Auto-Disconnect (Battery & Data Saver)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.idleDisconnectTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = fonts.headingFont,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = neu.textPrimary,
                                )
                                Spacer(Modifier.height(2.dp))
                                val timeoutFmt = s.language.formatMinutesLocalized(app.settingsStore.idleTimeoutMinutes)
                                val idleStatus = if (state == ConnectionState.CONNECTED && idleEnabled) {
                                    val remSec = (app.settingsStore.idleTimeoutMinutes * 60L - idleSeconds).coerceAtLeast(0L)
                                    "$timeoutFmt (${s.language.formatDurationLocalized(remSec, alwaysShowHours = false)})"
                                } else {
                                    "${s.idleTimeoutTitle}: $timeoutFmt"
                                }
                                Text(
                                    text = idleStatus,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = fonts.monoFont),
                                    color = if (idleEnabled) statusColors.connected else neu.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Switch(
                                checked = idleEnabled,
                                onCheckedChange = {
                                    idleEnabled = it
                                    app.settingsStore.idleDisconnectEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = statusColors.connected,
                                ),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Interactive Animated World Map Route Card displaying Origin Country & Real IP
 * connected via an animated glowing Bezier tunnel arc to the Exit Server Country & Tunnel IP.
 */
@Composable
private fun WorldRouteMapCard(
    state: ConnectionState,
    realIp: IpLocationInfo?,
    cachedRealIp: String?,
    cachedRealCountry: String?,
    selectedProxy: ProxyCandidate?,
    vpnEdgeIp: IpLocationInfo?,
    protocolLabel: String,
    adBlockEnabled: Boolean,
    onOpenServers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val neu = currentNeumorphicColors
    val statusColors = LocalFoxyStatusColors.current
    val s = currentFoxyStrings
    val fonts = currentFoxyFonts
    val haptics = LocalHapticFeedback.current
    val isConnected = state == ConnectionState.CONNECTED
    val isConnecting = state == ConnectionState.CONNECTING

    // Strictly use real detected or cached ISP country; never fallback to a fake country code
    val resolvedOriginCountryCode = (realIp?.countryCode?.takeIf { it.isNotBlank() && it != "—" }
        ?: cachedRealCountry?.takeIf { it.isNotBlank() && it != "—" })?.uppercase()
    val originIpRaw = realIp?.ip?.takeIf { it.isNotBlank() }
        ?: cachedRealIp?.takeIf { it.isNotBlank() }
    val originIpText = s.language.localizeDigits(originIpRaw ?: s.checking)

    // Strictly use real connected edge country or selected proxy country
    val resolvedExitCountryCode = (
        if (isConnected && vpnEdgeIp != null && vpnEdgeIp.countryCode.isNotBlank()) {
            vpnEdgeIp.countryCode
        } else {
            selectedProxy?.countryCode?.takeIf { it.isNotBlank() && it != "REC" }
                ?: vpnEdgeIp?.countryCode?.takeIf { it.isNotBlank() }
        }
    )?.uppercase()
    val resolvedExitCityCode = selectedProxy?.cityCode?.takeIf { it.isNotBlank() }

    val exitCountryName = when {
        selectedProxy != null && selectedProxy.countryCode.isNotBlank() && selectedProxy.countryCode != "REC" -> {
            val baseCountry = s.language.localizeCountryName(selectedProxy.countryName, selectedProxy.countryCode)
            if (!resolvedExitCityCode.isNullOrBlank()) "$baseCountry (${resolvedExitCityCode.uppercase()})" else baseCountry
        }
        resolvedExitCountryCode != null -> s.language.localizeCountryName(resolvedExitCountryCode, resolvedExitCountryCode)
        else -> s.recommendedFastest
    }

    val exitIpText = s.language.localizeDigits(
        when {
            isConnected && vpnEdgeIp != null -> vpnEdgeIp.ip
            selectedProxy != null -> "${selectedProxy.host}:${selectedProxy.port}"
            else -> s.recommendedFastest
        }
    )

    val isRouteVerifiedLive = isConnected && vpnEdgeIp != null && resolvedOriginCountryCode != null

    val infiniteTransition = rememberInfiniteTransition(label = "world-route-arc")
    val packetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isConnected) 2000 else 3200,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "route-packet-progress",
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.32f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "route-node-pulse",
    )

    val routeColor = when (state) {
        ConnectionState.CONNECTED -> statusColors.connected
        ConnectionState.CONNECTING -> statusColors.connecting
        ConnectionState.DISCONNECTED -> neu.accent
    }

    NeumorphicCard(
        onClick = onOpenServers,
        cornerRadius = 22.dp,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "نقشه مسیر تونل جهانی"
                            FoxyLanguage.AR -> "خريطة مسار النفق العالمي"
                            FoxyLanguage.EN -> "GLOBAL TUNNEL ROUTE MAP"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (isRouteVerifiedLive) {
                            when (s.language) {
                                FoxyLanguage.FA -> "مسیر فعال تأییدشده با IP • موقعیت تقریبی مرکز شهر/کشور"
                                FoxyLanguage.AR -> "مسار مؤكد عبر IP • موقع تقريبي لمركز المدينة/الدولة"
                                FoxyLanguage.EN -> "Verified Live IP Route • Approx. City/Country Center"
                            }
                        } else {
                            when (s.language) {
                                FoxyLanguage.FA -> "پیش‌نمایش مسیر سرور • موقعیت تقریبی مرکز شهر/کشور"
                                FoxyLanguage.AR -> "معاينة مسار الخادم • موقع تقريبي لمركز المدينة/الدولة"
                                FoxyLanguage.EN -> "Target Server Preview • Approx. City/Country Center"
                            }
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.bodyFont,
                            fontSize = 10.sp,
                        ),
                        color = if (isRouteVerifiedLive) statusColors.connected else neu.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (adBlockEnabled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColors.connected.copy(alpha = 0.14f))
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = if (s.language == FoxyLanguage.FA) "ضد تبلیغ" else "AdBlock",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                ),
                                color = statusColors.connected,
                                maxLines = 1,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(routeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = protocolLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fonts.monoFont,
                                fontWeight = FontWeight.ExtraBold,
                            ),
                            color = routeColor,
                            maxLines = 1,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Real Google Maps Global Tunnel Route Viewport + Vector Graticule Fallback
            var useSatelliteLayer by remember(neu.isDark) { mutableStateOf(neu.isDark) }
            val hlCode = when (s.language) {
                FoxyLanguage.FA -> "fa"
                FoxyLanguage.AR -> "ar"
                FoxyLanguage.EN -> "en"
            }
            val googleMapLayer = if (useSatelliteLayer) "y" else "m"
            val mapCacheKey = "$googleMapLayer-$hlCode"
            var googleWorldBitmap by remember(mapCacheKey) {
                mutableStateOf(GoogleWorldMapTileCache.get(mapCacheKey))
            }

            LaunchedEffect(mapCacheKey, isConnected) {
                if (googleWorldBitmap == null) {
                    val fetched = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        fetchGoogleWorldMapBitmap(layer = googleMapLayer, hl = hlCode)
                    }
                    if (fetched != null) {
                        GoogleWorldMapTileCache.put(mapCacheKey, fetched)
                        googleWorldBitmap = fetched
                    }
                }
            }

            val originNorm = resolvedOriginCountryCode?.let { locationToMercatorNorm(it, null) }
            val exitNorm = resolvedExitCountryCode?.let { locationToMercatorNorm(it, resolvedExitCityCode) }

            var mapZoomScale by remember { mutableFloatStateOf(1f) }
            var mapPanOffset by remember { mutableStateOf(Offset.Zero) }

            val animatedMapZoom by animateFloatAsState(
                targetValue = mapZoomScale,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.85f),
                label = "map-zoom-anim",
            )

            val mapShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .neumorphicSunken(
                        shape = mapShape,
                        cornerRadius = 16.dp,
                        depth = 2.5.dp,
                        surfaceColor = neu.surfaceElevated,
                    )
                    .clip(mapShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (mapZoomScale > 1.15f) {
                                    mapZoomScale = 1.0f
                                    mapPanOffset = Offset.Zero
                                } else {
                                    mapZoomScale = 2.4f
                                    if (exitNorm != null) {
                                        mapPanOffset = Offset(
                                            (0.5f - exitNorm.x) * 260f,
                                            (0.5f - exitNorm.y) * 140f
                                        )
                                    }
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val nextScale = (mapZoomScale * zoom).coerceIn(1.0f, 4.5f)
                            mapZoomScale = nextScale
                            if (nextScale <= 1.02f) {
                                mapPanOffset = Offset.Zero
                            } else {
                                val maxBoundX = 140f * (nextScale - 1f)
                                val maxBoundY = 80f * (nextScale - 1f)
                                mapPanOffset = Offset(
                                    (mapPanOffset.x + pan.x).coerceIn(-maxBoundX, maxBoundX),
                                    (mapPanOffset.y + pan.y).coerceIn(-maxBoundY, maxBoundY)
                                )
                            }
                        }
                    },
            ) {
                val loadedBitmap = googleWorldBitmap

                // Zoomable & Pannable Map Layer (Tiles + Geodesic Beziers + Cyber Nodes)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = animatedMapZoom
                            scaleY = animatedMapZoom
                            translationX = mapPanOffset.x
                            translationY = mapPanOffset.y
                        }
                ) {
                    if (loadedBitmap != null) {
                        Image(
                            bitmap = loadedBitmap,
                            contentDescription = "Google World Map",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (useSatelliteLayer) Color(0xFF05080E).copy(alpha = 0.28f)
                                    else Color(0xFF0F172A).copy(alpha = 0.10f)
                                ),
                        )
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Cyber Graticule Grid Lines (Mercator)
                        val gridColor = if (loadedBitmap != null) {
                            Color.White.copy(alpha = 0.09f)
                        } else {
                            neu.accent.copy(alpha = if (neu.isDark) 0.18f else 0.12f)
                        }
                        for (i in 1..7) {
                            val gx = w * (i / 8f)
                            drawLine(gridColor, Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
                        }
                        for (j in 1..3) {
                            val gy = h * (j / 4f)
                            drawLine(gridColor, Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
                        }

                        if (loadedBitmap == null) {
                            // Offline Continental Landmass Hubs
                            val continentColor = neu.textSecondary.copy(alpha = if (neu.isDark) 0.16f else 0.13f)
                            val hubs = listOf(
                                Offset(0.22f, 0.36f) to 28.dp.toPx(), // North America
                                Offset(0.32f, 0.72f) to 20.dp.toPx(), // South America
                                Offset(0.51f, 0.30f) to 24.dp.toPx(), // Europe
                                Offset(0.54f, 0.58f) to 24.dp.toPx(), // Africa
                                Offset(0.68f, 0.38f) to 32.dp.toPx(), // Asia
                                Offset(0.83f, 0.76f) to 18.dp.toPx(), // Oceania
                            )
                            hubs.forEach { (norm, rad) ->
                                drawCircle(
                                    color = continentColor,
                                    radius = rad,
                                    center = Offset(norm.x * w, norm.y * h),
                                )
                            }
                        }

                        if (originNorm != null && exitNorm != null) {
                            val startPt = Offset(originNorm.x * w, originNorm.y * h)
                            val endPt = Offset(exitNorm.x * w, exitNorm.y * h)

                            val midX = (startPt.x + endPt.x) * 0.5f
                            val arcLift = (kotlin.math.abs(endPt.x - startPt.x) * 0.32f).coerceIn(20.dp.toPx(), 54.dp.toPx())
                            val controlPt = Offset(midX, (minOf(startPt.y, endPt.y) - arcLift).coerceAtLeast(12.dp.toPx()))

                            val arcPath = Path().apply {
                                moveTo(startPt.x, startPt.y)
                                quadraticTo(controlPt.x, controlPt.y, endPt.x, endPt.y)
                            }

                            // Minimal route: restrained halo keeps the path legible without a heavy neon effect.
                            drawPath(
                                path = arcPath,
                                color = routeColor.copy(alpha = if (isConnected) 0.16f else 0.09f),
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                            )

                            // Fine route line; a subtle dash indicates that the tunnel is not connected yet.
                            drawPath(
                                path = arcPath,
                                color = routeColor.copy(alpha = if (isConnected) 0.95f else 0.72f),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = if (isConnected) null else PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                                ),
                            )

                            // One restrained moving packet avoids the previous crowded, high-glow effect.
                            if (isConnected || isConnecting) {
                                val t = packetProgress
                                val invT = 1f - t
                                val pktX = invT * invT * startPt.x + 2f * invT * t * controlPt.x + t * t * endPt.x
                                val pktY = invT * invT * startPt.y + 2f * invT * t * controlPt.y + t * t * endPt.y
                                drawCircle(
                                    color = routeColor.copy(alpha = 0.22f),
                                    radius = 5.dp.toPx(),
                                    center = Offset(pktX, pktY),
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.92f),
                                    radius = 1.8.dp.toPx(),
                                    center = Offset(pktX, pktY),
                                )
                            }
                        }

                        // Origin Node Pin
                        if (originNorm != null) {
                            val startPt = Offset(originNorm.x * w, originNorm.y * h)
                            drawCircle(
                                color = neu.accent.copy(alpha = 0.35f * pulseRadius),
                                radius = 11.dp.toPx() * pulseRadius,
                                center = startPt,
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 5.5.dp.toPx(),
                                center = startPt,
                            )
                            drawCircle(
                                color = neu.accent,
                                radius = 3.8.dp.toPx(),
                                center = startPt,
                            )
                        }

                        // Exit Server Node Pin with 2026 Multi-Ring Radar Beacon
                        if (exitNorm != null) {
                            val endPt = Offset(exitNorm.x * w, exitNorm.y * h)
                            // Expanding outer radar wave
                            drawCircle(
                                color = routeColor.copy(alpha = (0.28f * (1f - pulseRadius)).coerceAtLeast(0.04f)),
                                radius = 22.dp.toPx() * pulseRadius,
                                center = endPt,
                            )
                            // Inner radar wave
                            drawCircle(
                                color = routeColor.copy(alpha = (0.42f * (1f - pulseRadius * 0.5f)).coerceAtLeast(0.1f)),
                                radius = 14.dp.toPx() * pulseRadius,
                                center = endPt,
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = endPt,
                            )
                            drawCircle(
                                color = routeColor,
                                radius = 4.4.dp.toPx(),
                                center = endPt,
                            )
                        }
                    }
                }

                // 2026 Floating Glass HUD: Telemetry Chip (Top-Start)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF05080E).copy(alpha = 0.78f))
                        .border(0.8.dp, neu.accent.copy(alpha = 0.32f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) Color(0xFF10B981) else Color(0xFF00E5FF)),
                        )
                        val exitFlag = resolvedExitCountryCode?.let { com.vauth.foxyvpn.data.model.countryCodeToEmojiFlag(it) }?.takeIf { it.isNotBlank() }?.let { "$it " } ?: ""
                        Text(
                            text = if (resolvedExitCountryCode != null) {
                                "$exitFlag[${resolvedExitCountryCode.uppercase()}] ${resolvedExitCityCode?.uppercase() ?: ""} • ${String.format(Locale.US, "%.1fx", animatedMapZoom)}"
                            } else {
                                "GLOBAL TUNNEL • ${String.format(Locale.US, "%.1fx", animatedMapZoom)}"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fonts.headingFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }

                // 2026 Floating Glass HUD: Interactive Controls (Top-End)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Target Focus Button (Locks and zooms into destination country)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF05080E).copy(alpha = 0.84f))
                            .border(0.8.dp, neu.accent.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (exitNorm != null) {
                                    mapZoomScale = 2.4f
                                    mapPanOffset = Offset(
                                        (0.5f - exitNorm.x) * 260f,
                                        (0.5f - exitNorm.y) * 140f
                                    )
                                } else {
                                    mapZoomScale = 2.0f
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = "Target Focus",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(15.dp),
                        )
                    }

                    // Zoom In Button (+)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF05080E).copy(alpha = 0.84f))
                            .border(0.6.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                mapZoomScale = (mapZoomScale * 1.35f).coerceAtMost(4.5f)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Zoom In",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp),
                        )
                    }

                    // Zoom Out Button (-)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF05080E).copy(alpha = 0.84f))
                            .border(0.6.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
                            .clickable {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val next = (mapZoomScale / 1.35f).coerceAtLeast(1.0f)
                                mapZoomScale = next
                                if (next <= 1.05f) mapPanOffset = Offset.Zero
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Zoom Out",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp),
                        )
                    }

                    // Reset View Button
                    if (mapZoomScale > 1.08f || mapPanOffset != Offset.Zero) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF05080E).copy(alpha = 0.84f))
                                .border(0.6.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .clickable {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    mapZoomScale = 1.0f
                                    mapPanOffset = Offset.Zero
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RestartAlt,
                                contentDescription = "Reset View",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }
                }

                // Bottom Status Bar & Map/Satellite Layer Switcher
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.60f))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = if (loadedBitmap != null) "Google Maps • Pinch/Zoom" else "Mercator Grid • Pinch/Zoom",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fonts.headingFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                            ),
                            color = Color.White.copy(alpha = 0.92f),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.64f))
                            .clickable { useSatelliteLayer = !useSatelliteLayer }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = if (useSatelliteLayer) {
                                if (s.language == FoxyLanguage.FA) "ماهواره گوگل" else "Satellite"
                            } else {
                                if (s.language == FoxyLanguage.FA) "نقشه گوگل" else "Map"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fonts.headingFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Two-Column Origin vs Exit Server Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Origin Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(neu.surfaceElevated.copy(alpha = 0.65f))
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                ) {
                    val originCountryLocalized = resolvedOriginCountryCode?.let {
                        s.language.localizeCountryName(it, it)
                    } ?: s.checking
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "مبدأ واقعی ($originCountryLocalized)"
                            FoxyLanguage.AR -> "المصدر الحقيقي ($originCountryLocalized)"
                            FoxyLanguage.EN -> "ORIGIN ($originCountryLocalized)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = originIpText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = fonts.monoFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Exit Server Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(routeColor.copy(alpha = 0.10f))
                        .border(1.dp, routeColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                ) {
                    Text(
                        text = when (s.language) {
                            FoxyLanguage.FA -> "خروجی تونل ($exitCountryName)"
                            FoxyLanguage.AR -> "خروج النفق ($exitCountryName)"
                            FoxyLanguage.EN -> "EXIT ($exitCountryName)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fonts.headingFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = routeColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = exitIpText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = fonts.monoFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = neu.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private val Alignment.Companion.BottomBetweenRowAlignment: Alignment
    get() = Alignment.BottomCenter

private object GoogleWorldMapTileCache {
    private val map = java.util.concurrent.ConcurrentHashMap<String, androidx.compose.ui.graphics.ImageBitmap>()
    fun get(key: String) = map[key]
    fun put(key: String, bmp: androidx.compose.ui.graphics.ImageBitmap) {
        map[key] = bmp
    }
}

private const val GOOGLE_WORLD_CROP_Y_TOP = 210
private const val GOOGLE_WORLD_CROP_HEIGHT = 548

private fun fetchGoogleWorldMapBitmap(
    layer: String,
    hl: String,
): androidx.compose.ui.graphics.ImageBitmap? {
    return runCatching {
        val fullSize = 1024
        val tileSize = 256
        val fullBitmap = Bitmap.createBitmap(fullSize, fullSize, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(fullBitmap)
        var anyTileLoaded = false

        for (ty in 0..3) {
            for (tx in 0..3) {
                val tileUrl = "https://mt1.google.com/vt/lyrs=$layer&x=$tx&y=$ty&z=2&hl=$hl"
                runCatching {
                    val conn = (java.net.URL(tileUrl).openConnection() as java.net.HttpURLConnection).apply {
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android; FoxyVPN/1.0.8)")
                    }
                    conn.inputStream.use { stream ->
                        val tileBmp = BitmapFactory.decodeStream(stream)
                        if (tileBmp != null) {
                            canvas.drawBitmap(tileBmp, (tx * tileSize).toFloat(), (ty * tileSize).toFloat(), null)
                            anyTileLoaded = true
                        }
                    }
                    conn.disconnect()
                }
            }
        }

        if (!anyTileLoaded) return@runCatching null
        val cropped = Bitmap.createBitmap(
            fullBitmap,
            0,
            GOOGLE_WORLD_CROP_Y_TOP,
            fullSize,
            GOOGLE_WORLD_CROP_HEIGHT,
        )
        cropped.asImageBitmap()
    }.getOrNull()
}

private fun locationToMercatorNorm(countryCode: String, cityCode: String?): Offset {
    val cityCoords: Pair<Double, Double>? = when (cityCode?.lowercase()?.trim()) {
        "fra" -> 50.1109 to 8.6821      // Frankfurt
        "ber" -> 52.5200 to 13.4050     // Berlin
        "muc" -> 48.1351 to 11.5820     // Munich
        "dus" -> 51.2277 to 6.7735      // Dusseldorf
        "ams" -> 52.3676 to 4.9041      // Amsterdam
        "lon" -> 51.5074 to -0.1278     // London
        "man" -> 53.4808 to -2.2426     // Manchester
        "par" -> 48.8566 to 2.3522      // Paris
        "mrs" -> 43.2965 to 5.3698      // Marseille
        "zrh" -> 47.3769 to 8.5417      // Zurich
        "vie" -> 48.2082 to 16.3738     // Vienna
        "mil" -> 45.4642 to 9.1900      // Milan
        "rom" -> 41.9028 to 12.4964     // Rome
        "mad" -> 40.4168 to -3.7038     // Madrid
        "bcn" -> 41.3874 to 2.1686      // Barcelona
        "sto" -> 59.3293 to 18.0686     // Stockholm
        "mma" -> 55.6050 to 13.0038     // Malmo
        "osl" -> 59.9139 to 10.7522     // Oslo
        "hel" -> 60.1699 to 24.9384     // Helsinki
        "waw" -> 52.2297 to 21.0122     // Warsaw
        "prg" -> 50.0755 to 14.4378     // Prague
        "otp", "buh" -> 44.4268 to 26.1025 // Bucharest
        "nyc", "ewr" -> 40.7128 to -74.0060 // New York
        "was", "iad" -> 38.9072 to -77.0369 // Washington DC
        "chi", "ord" -> 41.8781 to -87.6298 // Chicago
        "mia" -> 25.7617 to -80.1918    // Miami
        "dal", "dfw" -> 32.7767 to -96.7970 // Dallas
        "lax" -> 34.0522 to -118.2437   // Los Angeles
        "sjc", "sfo" -> 37.3382 to -121.8863 // San Jose / SF
        "sea" -> 47.6062 to -122.3321   // Seattle
        "atl" -> 33.7490 to -84.3880    // Atlanta
        "den" -> 39.7392 to -104.9903   // Denver
        "tor", "yyz" -> 43.6532 to -79.3832 // Toronto
        "van", "yvr" -> 49.2827 to -123.1207 // Vancouver
        "mtl", "yul" -> 45.5017 to -73.5673 // Montreal
        "sao", "gru" -> -23.5505 to -46.6333 // Sao Paulo
        "sin" -> 1.3521 to 103.8198     // Singapore
        "tyo", "nrt" -> 35.6762 to 139.6503 // Tokyo
        "osa" -> 34.6937 to 135.5023    // Osaka
        "hkg" -> 22.3193 to 114.1694    // Hong Kong
        "syd" -> -33.8688 to 151.2093   // Sydney
        "mel" -> -37.8136 to 144.9631   // Melbourne
        "akl" -> -36.8485 to 174.7633   // Auckland
        else -> null
    }

    val (latDeg, lonDeg) = cityCoords ?: when (countryCode.uppercase()) {
        "US" -> 38.9072 to -77.0369
        "CA" -> 56.1304 to -106.3468
        "MX" -> 23.6345 to -102.5528
        "BR" -> -15.7975 to -47.8919
        "AR" -> -34.6037 to -58.3816
        "CL" -> -33.4489 to -70.6693
        "CO" -> 4.7110 to -74.0721
        "GB", "UK" -> 51.5074 to -0.1278
        "IE" -> 53.3498 to -6.2603
        "FR" -> 48.8566 to 2.3522
        "BE" -> 50.8503 to 4.3517
        "NL" -> 52.3676 to 4.9041
        "DE" -> 50.1109 to 8.6821
        "CH" -> 46.8182 to 8.2275
        "AT" -> 47.5162 to 14.5501
        "IT" -> 41.8719 to 12.5674
        "ES" -> 40.4637 to -3.7492
        "PT" -> 38.7223 to -9.1393
        "SE" -> 59.3293 to 18.0686
        "NO" -> 59.9139 to 10.7522
        "DK" -> 55.6761 to 12.5683
        "FI" -> 60.1699 to 24.9384
        "PL" -> 52.2297 to 21.0122
        "CZ" -> 50.0755 to 14.4378
        "HU" -> 47.4979 to 19.0402
        "RO" -> 44.4268 to 26.1025
        "BG" -> 42.6977 to 23.3219
        "GR" -> 37.9838 to 23.7275
        "UA" -> 50.4501 to 30.5234
        "TR" -> 39.9334 to 32.8597
        "IR" -> 35.6892 to 51.3890
        "IQ" -> 33.3152 to 44.3661
        "SA" -> 24.7136 to 46.6753
        "AE" -> 25.2048 to 55.2708
        "IL" -> 32.0853 to 34.7818
        "EG" -> 30.0444 to 31.2357
        "ZA" -> -26.2041 to 28.0473
        "IN" -> 20.5937 to 78.9629
        "PK" -> 30.3753 to 69.3451
        "SG" -> 1.3521 to 103.8198
        "MY" -> 3.1390 to 101.6869
        "TH" -> 13.7563 to 100.5018
        "ID" -> -6.2088 to 106.8456
        "JP" -> 35.6762 to 139.6503
        "HK" -> 22.3193 to 114.1694
        "TW" -> 25.0330 to 121.5654
        "KR" -> 37.5665 to 126.9780
        "AU" -> -33.8688 to 151.2093
        "NZ" -> -36.8485 to 174.7633
        "RU" -> 55.7558 to 37.6173
        else -> 48.0 to 12.0
    }
    val mercX = ((lonDeg + 180.0) / 360.0) * 1024.0
    val latRad = Math.toRadians(latDeg.coerceIn(-80.0, 80.0))
    val mercY = ((1.0 - kotlin.math.ln(kotlin.math.tan(latRad) + 1.0 / kotlin.math.cos(latRad)) / Math.PI) / 2.0) * 1024.0
    val normX = (mercX / 1024.0).toFloat().coerceIn(0.03f, 0.97f)
    val normY = ((mercY - GOOGLE_WORLD_CROP_Y_TOP) / GOOGLE_WORLD_CROP_HEIGHT.toDouble()).toFloat().coerceIn(0.06f, 0.94f)
    return Offset(normX, normY)
}

