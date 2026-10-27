package com.vauth.foxyvpn.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vauth.foxyvpn.data.FxaAuthRepository
import com.vauth.foxyvpn.data.TokenStore
import com.vauth.foxyvpn.ui.theme.FoxyLanguage
import com.vauth.foxyvpn.ui.theme.NeumorphicButton
import com.vauth.foxyvpn.ui.theme.NeumorphicCard
import com.vauth.foxyvpn.ui.theme.NeumorphicIconButton
import com.vauth.foxyvpn.ui.theme.StyleAtmospherePosterOverlay
import com.vauth.foxyvpn.ui.theme.currentFoxyFonts
import com.vauth.foxyvpn.ui.theme.currentFoxyStrings
import com.vauth.foxyvpn.ui.theme.currentNeumorphicColors
import com.vauth.foxyvpn.ui.theme.shakeOnTrigger
import com.vauth.foxyvpn.ui.theme.staggeredEntrance
import com.vauth.foxyvpn.ui.theme.subtleFloatAnimation
import kotlinx.coroutines.launch

private const val MOZILLA_SIGNUP_WEB_URL = "https://accounts.firefox.com/signup"

@Composable
fun LoginScreen(
    authRepository: FxaAuthRepository,
    onSignedIn: () -> Unit,
) {
    val context = LocalContext.current
    val tokenStore = remember { TokenStore(context) }
    var savedAccounts by remember { mutableStateOf(tokenStore.loadAccounts()) }
    var isSignUpMode by rememberSaveable { mutableStateOf(false) }
    var showInAppMozillaWeb by rememberSaveable { mutableStateOf(false) }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var awaitingTwoFactor by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val neu = currentNeumorphicColors
    val s = currentFoxyStrings
    val fonts = currentFoxyFonts

    DisposableEffect(Unit) {
        if (!awaitingTwoFactor) {
            authRepository.clearPendingSession()
        }
        onDispose { }
    }

    BackHandler(enabled = showInAppMozillaWeb || awaitingTwoFactor) {
        if (showInAppMozillaWeb) {
            showInAppMozillaWeb = false
        } else {
            authRepository.clearPendingSession()
            awaitingTwoFactor = false
            errorMessage = null
            code = ""
        }
    }

    fun doSubmitAuth() {
        val cleanEmail = FxaAuthRepository.normalizeEmail(email)
        if (cleanEmail.isBlank() || password.isBlank() || isLoading) return
        errorMessage = null
        isLoading = true
        scope.launch {
            val result = if (isSignUpMode) {
                authRepository.startSignUp(cleanEmail, password)
            } else {
                authRepository.startLogin(cleanEmail, password)
            }
            isLoading = false
            result.onSuccess { needsVerification ->
                if (needsVerification) awaitingTwoFactor = true else onSignedIn()
            }.onFailure { errorMessage = it.message ?: "Authentication failed" }
        }
    }

    fun doSubmitTwoFactor() {
        val cleanCode = FxaAuthRepository.sanitizeCode(code)
        if (cleanCode.isBlank() || isLoading) return
        errorMessage = null
        isLoading = true
        scope.launch {
            val result = authRepository.submitTwoFactorCode(cleanCode)
            isLoading = false
            result.onSuccess { onSignedIn() }
                .onFailure {
                    errorMessage = it.message ?: "Verification failed"
                    if (authRepository.pendingSessionToken == null) {
                        awaitingTwoFactor = false
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(neu.background)
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        StyleAtmospherePosterOverlay(modifier = Modifier.fillMaxSize())

        if (showInAppMozillaWeb) {
            MozillaInAppWebPortal(
                initialUrl = MOZILLA_SIGNUP_WEB_URL,
                onCapturedEmail = { captured ->
                    if (captured.isNotBlank() && captured.contains("@")) {
                        email = captured.trim().lowercase()
                    }
                },
                onCloseAndSwitchToLogin = {
                    showInAppMozillaWeb = false
                    isSignUpMode = false
                },
                onClose = {
                    showInAppMozillaWeb = false
                },
            )
        } else {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Logo Badge
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .subtleFloatAnimation()
                        .staggeredEntrance(0)
                        .clip(CircleShape)
                        .background(neu.surfaceElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isSignUpMode) Icons.Filled.PersonAdd else Icons.Filled.Shield,
                        contentDescription = null,
                        tint = neu.accent,
                        modifier = Modifier.size(44.dp),
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = if (isSignUpMode) {
                        when (s.language) {
                            FoxyLanguage.FA -> "ساخت حساب جدید موزیلا"
                            FoxyLanguage.AR -> "إنشاء حساب موزيلا جديد"
                            FoxyLanguage.EN -> "Create Mozilla Account"
                        }
                    } else {
                        s.loginTitle
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = fonts.headingFont,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = neu.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isSignUpMode) {
                        when (s.language) {
                            FoxyLanguage.FA -> "می‌توانید مستقیم از همین‌جا یا از داخل سایت رسمی موزیلا در همین برنامه حساب بسازید"
                            FoxyLanguage.AR -> "أنشئ حسابك مباشرة هنا أو عبر موقع موزيلا الرسمي داخل التطبيق"
                            FoxyLanguage.EN -> "Create your account directly below or via the embedded Mozilla portal inside the app"
                        }
                    } else {
                        s.loginSubtitle
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = fonts.bodyFont),
                    color = neu.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                Spacer(Modifier.height(20.dp))

                // Sign-In / Sign-Up Segmented Switcher
                if (!awaitingTwoFactor) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(neu.surfaceElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        val signInLabel = when (s.language) {
                            FoxyLanguage.FA -> "ورود به حساب"
                            FoxyLanguage.AR -> "تسجيل الدخول"
                            FoxyLanguage.EN -> "Sign In"
                        }
                        val signUpLabel = when (s.language) {
                            FoxyLanguage.FA -> "ساخت حساب جدید"
                            FoxyLanguage.AR -> "إنشاء حساب"
                            FoxyLanguage.EN -> "Sign Up"
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isSignUpMode) neu.accent else Color.Transparent)
                                .clickable {
                                    isSignUpMode = false
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = signInLabel,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = if (!isSignUpMode) Color.White else neu.textSecondary,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSignUpMode) neu.accent else Color.Transparent)
                                .clickable {
                                    isSignUpMode = true
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = signUpLabel,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = if (isSignUpMode) Color.White else neu.textSecondary,
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }

                NeumorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(1)
                        .shakeOnTrigger(errorMessage),
                    cornerRadius = 24.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                    ) {
                        if (!awaitingTwoFactor) {
                            if (isSignUpMode) {
                                // Primary In-App Mozilla Site Account Creation Button (Connected to Mozilla inside the app)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(neu.accent.copy(alpha = 0.14f))
                                        .border(1.5.dp, neu.accent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                                        .clickable { showInAppMozillaWeb = true }
                                        .padding(horizontal = 14.dp, vertical = 13.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Language,
                                            contentDescription = null,
                                            tint = neu.accent,
                                            modifier = Modifier.size(20.dp),
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = when (s.language) {
                                                    FoxyLanguage.FA -> "ساخت اکانت در سایت رسمی موزیلا (داخل برنامه)"
                                                    FoxyLanguage.AR -> "إنشاء حساب عبر موقع موزيلا الرسمي (داخل التطبيق)"
                                                    FoxyLanguage.EN -> "Create Account on Official Mozilla Site (In-App)"
                                                },
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontFamily = fonts.headingFont,
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                                color = neu.accent,
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = when (s.language) {
                                                    FoxyLanguage.FA -> "اتصال مستقیم به سایت موزیلا بدون خروج از برنامه"
                                                    FoxyLanguage.AR -> "اتصال مباشر بموقع موزيلا دون مغادرة التطبيق"
                                                    FoxyLanguage.EN -> "Direct connection to accounts.firefox.com inside Foxy VPN"
                                                },
                                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.bodyFont),
                                                color = neu.textSecondary,
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))
                                HorizontalDivider(color = neu.textSecondary.copy(alpha = 0.14f))
                                Spacer(Modifier.height(14.dp))
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it.replace(Regex("[\\p{Z}\\u200B-\\u200D\\uFEFF\\u00A0\\s]+"), "").lowercase()
                                },
                                label = { Text(s.loginEmailLabel) },
                                leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null, tint = neu.textSecondary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next,
                                    autoCorrectEnabled = false,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it.replace(Regex("[\\u200B-\\u200D\\uFEFF]"), "")
                                },
                                label = { Text(s.loginPasswordLabel) },
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = neu.textSecondary) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        doSubmitAuth()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Spacer(Modifier.height(22.dp))

                            NeumorphicButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    doSubmitAuth()
                                },
                                accentColor = neu.accent,
                                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White,
                                    )
                                } else {
                                    Text(
                                        text = if (isSignUpMode) {
                                            when (s.language) {
                                                FoxyLanguage.FA -> "ساخت حساب و دریافت کد تایید"
                                                FoxyLanguage.AR -> "إنشاء الحساب وإرسال الرمز"
                                                FoxyLanguage.EN -> "Create Account & Send Code"
                                            }
                                        } else {
                                            s.loginContinueButton
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                    )
                                }
                            }
                        } else {
                            Text(
                                s.loginTwoFactorPrompt,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = neu.textPrimary,
                            )
                            Spacer(Modifier.height(14.dp))
                            OutlinedTextField(
                                value = code,
                                onValueChange = {
                                    code = it.replace(Regex("[\\p{Z}\\u200B-\\u200D\\uFEFF\\u00A0\\s]+"), "")
                                },
                                label = { Text(s.loginTwoFactorCodeLabel) },
                                leadingIcon = { Icon(Icons.Filled.VpnKey, contentDescription = null, tint = neu.textSecondary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        doSubmitTwoFactor()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Spacer(Modifier.height(24.dp))

                            NeumorphicButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    doSubmitTwoFactor()
                                },
                                accentColor = neu.accent,
                                enabled = !isLoading && code.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White,
                                    )
                                } else {
                                    Text(
                                        s.loginVerifyButton,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            authRepository.clearPendingSession()
                                            awaitingTwoFactor = false
                                            errorMessage = null
                                            code = ""
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Text(
                                        text = when (s.language) {
                                            FoxyLanguage.FA -> "بازگشت به ورود عادی"
                                            FoxyLanguage.AR -> "العودة لتسجيل الدخول"
                                            FoxyLanguage.EN -> "Back to Sign In"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = neu.textSecondary,
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            scope.launch {
                                                authRepository.resendVerificationCodeIfNeeded()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Text(
                                        text = when (s.language) {
                                            FoxyLanguage.FA -> "ارسال مجدد کد"
                                            FoxyLanguage.AR -> "إعادة إرسال الرمز"
                                            FoxyLanguage.EN -> "Resend Code"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = neu.accent,
                                    )
                                }
                            }
                        }

                        errorMessage?.let {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }

                // Saved Multi-Accounts Quick Switcher (if accounts already exist on device)
                if (savedAccounts.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .staggeredEntrance(2),
                        cornerRadius = 22.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                        ) {
                            Text(
                                text = when (s.language) {
                                    FoxyLanguage.FA -> "حساب‌های ذخیره‌شده (ورود سریع با یک لمس)"
                                    FoxyLanguage.AR -> "الحسابات المحفوظة (تبديل سريع)"
                                    FoxyLanguage.EN -> "SAVED ACCOUNTS (1-TAP SWITCH)"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = neu.textSecondary,
                            )
                            Spacer(Modifier.height(10.dp))
                            savedAccounts.forEachIndexed { idx, acct ->
                                if (idx > 0) {
                                    HorizontalDivider(
                                        color = neu.textSecondary.copy(alpha = 0.12f),
                                        modifier = Modifier.padding(vertical = 8.dp),
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (tokenStore.switchToAccount(acct.email)) {
                                                savedAccounts = tokenStore.loadAccounts()
                                                onSignedIn()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AccountCircle,
                                            contentDescription = null,
                                            tint = neu.accent,
                                            modifier = Modifier.size(22.dp),
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = acct.email,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = fonts.monoFont,
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            color = neu.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(neu.accent.copy(alpha = 0.14f))
                                            .border(1.dp, neu.accent.copy(alpha = 0.30f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                    ) {
                                        Text(
                                            text = when (s.language) {
                                                FoxyLanguage.FA -> "انتخاب"
                                                FoxyLanguage.AR -> "اختيار"
                                                FoxyLanguage.EN -> "Switch"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = fonts.headingFont,
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            color = neu.accent,
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

/**
 * Embedded In-App Mozilla Account Portal connected directly to https://accounts.firefox.com
 * inside the app without opening an external browser.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MozillaInAppWebPortal(
    initialUrl: String = MOZILLA_SIGNUP_WEB_URL,
    onCapturedEmail: (String) -> Unit,
    onCloseAndSwitchToLogin: () -> Unit,
    onClose: () -> Unit,
) {
    val neu = currentNeumorphicColors
    val s = currentFoxyStrings
    val fonts = currentFoxyFonts
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var loadProgress by remember { mutableIntStateOf(0) }
    var isPageLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.stopLoading()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(neu.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        // Top In-App Mozilla Browser Header
        NeumorphicCard(
            cornerRadius = 18.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        NeumorphicIconButton(
                            size = 36.dp,
                            onClick = {
                                val wv = webViewRef
                                if (wv != null && wv.canGoBack()) {
                                    wv.goBack()
                                } else {
                                    onClose()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = neu.textPrimary,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (s.language) {
                                    FoxyLanguage.FA -> "سایت رسمی ساخت اکانت موزیلا (داخل برنامه)"
                                    FoxyLanguage.AR -> "موقع موزيلا الرسمي لإنشاء الحساب"
                                    FoxyLanguage.EN -> "Official Mozilla Account Portal"
                                },
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = fonts.headingFont,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = neu.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = neu.accent,
                                    modifier = Modifier.size(12.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = currentUrl,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = fonts.monoFont),
                                    color = neu.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        NeumorphicIconButton(
                            size = 36.dp,
                            onClick = { webViewRef?.reload() },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                tint = neu.accent,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                        NeumorphicIconButton(
                            size = 36.dp,
                            onClick = onClose,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = neu.textPrimary,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isPageLoading && loadProgress in 1..99,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = neu.accent,
                        trackColor = neu.surfaceElevated,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Embedded WebView Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, neu.textSecondary.copy(alpha = 0.18f), RoundedCornerShape(20.dp)),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewRef = this
                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadsImagesAutomatically = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress
                                isPageLoading = newProgress < 100
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                if (!url.isNullOrBlank()) currentUrl = url
                                isPageLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                if (!url.isNullOrBlank()) currentUrl = url
                                isPageLoading = false
                                // Capture email entered inside Mozilla signup/signin form so it auto-fills in Foxy VPN
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        var el = document.querySelector('input[type="email"], input[name="email"]');
                                        if (el && el.value) return el.value;
                                        return '';
                                    })();
                                    """.trimIndent()
                                ) { rawResult ->
                                    val cleaned = rawResult?.trim()?.trim('"').orEmpty()
                                    if (cleaned.contains("@")) {
                                        onCapturedEmail(cleaned)
                                    }
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean {
                                val nextUrl = request?.url?.toString() ?: return false
                                currentUrl = nextUrl
                                return false
                            }
                        }
                        loadUrl(initialUrl)
                    }
                },
            )
        }

        Spacer(Modifier.height(10.dp))

        // Bottom Helper Bar: Done Creating Account -> Return to App Login with Pre-filled Email
        NeumorphicButton(
            onClick = {
                webViewRef?.evaluateJavascript(
                    """
                    (function() {
                        var el = document.querySelector('input[type="email"], input[name="email"]');
                        if (el && el.value) return el.value;
                        return '';
                    })();
                    """.trimIndent()
                ) { rawResult ->
                    val cleaned = rawResult?.trim()?.trim('"').orEmpty()
                    if (cleaned.contains("@")) {
                        onCapturedEmail(cleaned)
                    }
                    onCloseAndSwitchToLogin()
                }
            },
            accentColor = neu.accent,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = when (s.language) {
                    FoxyLanguage.FA -> "اکانت را ساختم • بازگشت و ورود به برنامه"
                    FoxyLanguage.AR -> "تم إنشاء الحساب • العودة لتسجيل الدخول"
                    FoxyLanguage.EN -> "Account Created • Return to Sign In"
                },
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = fonts.headingFont,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
            )
        }
    }
}
