package com.vauth.foxyvpn.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vauth.foxyvpn.ui.theme.AppLanguage
import com.vauth.foxyvpn.ui.theme.ThemeMode
import com.vauth.foxyvpn.vpn.FoxyVpnService
import com.vauth.foxyvpn.widget.QuickConnectWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

enum class FoxyColorTheme(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
    val primaryLightHex: Long,
    val primaryDarkHex: Long,
    val secondaryAccentHex: Long,
) {
    CYBER_FOX(
        label = "Cyber Fox Blue",
        persianLabel = "آبی و سفید رسمی فاکسی",
        arabicLabel = "أزرق فوكسي الرسمي",
        primaryLightHex = 0xFF0284C7,
        primaryDarkHex = 0xFF00B0FF,
        secondaryAccentHex = 0xFF00E5FF,
    ),
    OCEAN_SAPPHIRE(
        label = "Ocean Sapphire",
        persianLabel = "آبی اقیانوسی آرام",
        arabicLabel = "ياقوت المحيط الأزرق",
        primaryLightHex = 0xFF0284C7,
        primaryDarkHex = 0xFF38BDF8,
        secondaryAccentHex = 0xFF2DD4BF,
    ),
    EMERALD_AURORA(
        label = "Emerald Aurora",
        persianLabel = "زمرد شفق قطبی",
        arabicLabel = "زمرد الشفق القطبي",
        primaryLightHex = 0xFF059669,
        primaryDarkHex = 0xFF34D399,
        secondaryAccentHex = 0xFF06B6D4,
    ),
    ROYAL_AMETHYST(
        label = "Royal Amethyst",
        persianLabel = "بنفش سلطنتی یاقوتی",
        arabicLabel = "جمشت ملكي بنفسجي",
        primaryLightHex = 0xFF7C3AED,
        primaryDarkHex = 0xFFA78BFA,
        secondaryAccentHex = 0xFFEC4899,
    ),
    ROSE_VELVET(
        label = "Rose Velvet",
        persianLabel = "رز گلد مخملی",
        arabicLabel = "وردي مخملي",
        primaryLightHex = 0xFFE11D48,
        primaryDarkHex = 0xFFFB7185,
        secondaryAccentHex = 0xFFF59E0B,
    ),
    SOLAR_GOLD(
        label = "Solar Amber Gold",
        persianLabel = "طلایی کهربایی مدرن",
        arabicLabel = "ذهبي كهرماني",
        primaryLightHex = 0xFFD97706,
        primaryDarkHex = 0xFFFBBF24,
        secondaryAccentHex = 0xFF14B8A6,
    ),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }
}

enum class FoxyUiDesignStyle(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
    val subtitleEn: String,
    val subtitleFa: String,
    val subtitleAr: String,
) {
    PIXEL_GOOGLE(
        label = "Google Pixel UI",
        persianLabel = "طرح پیکسلی گوگل",
        arabicLabel = "تصميم جوجل بيكسل",
        subtitleEn = "Authentic Google Pixel Material You pastel tonal pills & expressive shapes",
        subtitleFa = "طراحی رسمی گوشی‌های گوگل پیکسل با کپسول‌های رنگی و نرم",
        subtitleAr = "تصميم هواتف جوجل بيكسل الرسمي بألوان الباستيل الناعمة",
    ),
    CLAYMORPHISM(
        label = "Claymorphism",
        persianLabel = "کلی‌مورفیسم",
        arabicLabel = "كليمورفيزم ثلاثي الأبعاد",
        subtitleEn = "3D inflated matte clay cards with dual inner bevels",
        subtitleFa = "کارت‌های سه‌بعدی برجسته و نرم با لبه‌های حجمی",
        subtitleAr = "بطاقات ثلاثية الأبعاد ناعمة بحواف بارزة",
    ),
    DARK_LUXURY(
        label = "Dark Luxury",
        persianLabel = "دارک لاکشری",
        arabicLabel = "الفخامة الداكنة والذهبي",
        subtitleEn = "Obsidian velvet surfaces with brushed champagne gold borders",
        subtitleFa = "سطوح مخملی مشکی با قاب و خطوط طلایی لوکس",
        subtitleAr = "أسطح مخملية داكنة مع إطارات ذهبية فاخرة",
    ),
    NEO_BRUTALISM(
        label = "Neo-Brutalism",
        persianLabel = "نئو بروتالیسم",
        arabicLabel = "نيو بروتاليزم عصري",
        subtitleEn = "Bold ink borders, high-contrast geometry & hard offset shadows",
        subtitleFa = "خطوط ضخیم کنتراست بالا و سایه‌های هندسی تیز و مدرن",
        subtitleAr = "حدود جريئة عالية التباين وظلال هندسية حادة",
    ),
    BENTO_UI(
        label = "Bento UI",
        persianLabel = "بنتو",
        arabicLabel = "واجهات بنتو الشبكية",
        subtitleEn = "Modular Apple-style Bento compartments & ultra-clean glass hairlines",
        subtitleFa = "چیدمان ماژولار و تمیز به سبک باکس‌های بنتو",
        subtitleAr = "تصميم شبكي معياري نظيف بأسلوب أبل بنتو",
    ),
    EDITORIAL_DESIGN(
        label = "Editorial Design",
        persianLabel = "ادیتوریال",
        arabicLabel = "التصميم التحريري الفاخر",
        subtitleEn = "Architectural magazine grid, fine rule lines & warm gallery tones",
        subtitleFa = "سبک مجله‌ای و معماری با خطوط ظریف و کنتراست تایپوگرافی",
        subtitleAr = "تصميم تحريري معماري بخطوط دقيقة وتباين أنيق",
    ),
    NEUMORPHISM(
        label = "Soft Neumorphism",
        persianLabel = "نئومورفیسم نرم",
        arabicLabel = "نيومورفيزم كلاسيكي",
        subtitleEn = "Classic soft dual-shadow tactile extruded surfaces",
        subtitleFa = "سایه‌های نرم دوگانه و برجسته کلاسیک",
        subtitleAr = "ظلال مزدوجة ناعمة كلاسيكية",
    ),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }

    fun localizedSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> subtitleEn
        AppLanguage.FA -> subtitleFa
        AppLanguage.AR -> subtitleAr
    }
}

enum class HeroWaveAnimationStyle(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
) {
    HARMONIC_ORBIT("Harmonic Waves & Orbit", "امواج هارمونیک و مدار", "موجات توافقية ومدار"),
    SONAR_PULSE("Deep Sonar Radar", "رادار سونار عمیق", "نبض السونار العميق"),
    PLASMA_VORTEX("Plasma Energy Vortex", "گرداب انرژی پلاسما", "دوامة طاقة البلازما"),
    STARFIELD_NOVA("Cosmic Nova Burst", "ابرنواختر کیهانی", "انفجار النوفا الكوني"),
    CYBER_HEX_SHIELD("Cyber Hexagon Shield", "سپر شش‌ضلعی سایبری", "درع السداسي السيبراني"),
    QUANTUM_HELIX("Quantum Double Helix", "مارپیچ کوانتومی دوگانه", "حلزون كمومي مزدوج"),
    LIQUID_SPECTRUM("3D Acoustic Spectrum", "طیف آکوستیک سه‌بعدی", "طيف الموجات الصوتية"),
    AURORA_CROWN("Magnetic Crown Rings", "تاج مغناطیسی شفق", "حلقات التاج المغناطيسي"),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }
}

enum class WaveColorMode(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
    val colorHex: Long,
) {
    AUTO_STATUS("Auto Status Color", "هماهنگ با وضعیت اتصال", "تلقائي حسب الحالة", 0L),
    SUNSET_ORANGE("Foxy Electric Blue", "آبی درخشان فاکسی", "أزرق فوكسي المتوهج", 0xFF00B0FF),
    ROYAL_BLUE("Electric Royal Blue", "آبی الکتریک سلطنتی", "أزرق ملكي", 0xFF2E65F3),
    EMERALD_MINT("Neon Emerald Mint", "سبز زمردی نئون", "أخضر زمردي", 0xFF18C964),
    CYBER_VIOLET("Cyber Amethyst", "بنفش سایبری", "بنفسجي سيبراني", 0xFF9333EA),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }
}

enum class DynamicIslandLedStyle(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
) {
    CYBER_RAINBOW("RGB Spectrum Chase", "نوار ال‌ای‌دی رنگین‌کمان", "إضاءة طيف آر جي بي"),
    FOXY_FIRE("Foxy Fire Turbo", "توربو آتشین فاکسی", "توربو ناري فوكسي"),
    EMERALD_PULSE("Emerald Matrix Wave", "موج زمردی متحرک", "موجة زمردية متحركة"),
    POLICE_STROBE("Police Strobe Red/Blue", "چراغ پلیسی قرمز و آبی", "إضاءة شرطة أحمر وأزرق"),
    SOLAR_GOLD("Sport Gold & Crimson", "اسپرت طلایی و قرمز", "ذهبي وأحمر رياضي"),
    AURORA_BOREALIS("Aurora Borealis Silk", "شفق قطبی ابریشمی", "الشفق القطبي الحريري"),
    OCEAN_BIOLUME("Ocean Bioluminescence", "درخشش اعماق اقیانوس", "توهج أعماق المحيط"),
    ROYAL_NEBULA("Galaxy Purple Nebula", "سحابی بنفش کهکشانی", "سديم المجرة البنفسجي"),
    CYBERPUNK_SYNTH("Cyberpunk Synthwave", "نئون سایبرپانک", "نيون سايبر بانك"),
    LIQUID_MERCURY("Liquid Chrome Silver", "نقره‌ای جیوه مایع", "فضة الكروم السائل"),
    ARCTIC_FROST("Arctic Ice Crystal", "کریستال یخی قطبی", "كريستال الجليد القطبي"),
    SUNSET_MIAMI("Miami Sunset Horizon", "افق غروب میامی", "غروب ميامي المتدرج"),
    QUANTUM_PLASMA("Quantum Plasma Beam", "پرتو پلاسمای کوانتومی", "شعاع البلازما الكمومي"),
    SAKURA_BLOSSOM("Sakura Rose Glow", "درخشش شکوفه ساکورا", "توهج زهور الساكورا"),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }
}

enum class DynamicIslandWaveStyle(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
) {
    SIGNAL_TOWER("Signal Tower", "موج دکل مخابراتی", "موجة برج الإشارة"),
    EQUALIZER_BARS("Audio Equalizer", "موج اکولایزر صوتی", "موازن الصوت المتحرك"),
    SINE_WAVE("Harmonic Sine Wave", "موج سینوسی روان", "موجة جيبية انسيابية"),
    RADAR_PULSE("Radar Sonar Rings", "پالس راداری دایره‌ای", "نبض الرادار الدائري"),
    ORBIT_DOTS("Orbital Particles", "ذرات چرخشی مدار", "جسيمات مدارية متحركة"),
    HEARTBEAT_WAVE("Live Heartbeat", "موج ضربان قلب", "موجة نبض القلب"),
    DUAL_HELIX("DNA Double Helix", "موج مارپیچ دوگانه", "موجة الحلزون المزدوج"),
    CYBER_SPECTRUM("Studio Spectrum", "آنالایزر طیفی استودیو", "محلل الطيف الاحترافي"),
    SONIC_RIPPLE("Sonic Concentric", "موج صوتی متمرکز", "تموجات صوتية متمركزة"),
    LASER_MATRIX("Laser Matrix Pulse", "ماتریکس لیزری زنده", "نبض مصفوفة الليزر"),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }
}

enum class ConnectionProtocolMode(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val descriptionAr: String,
    val isSupported: Boolean = true,
) {
    AUTO(
        label = "Smart Auto (HTTP/2 + HTTP/1.1 TLS)",
        persianLabel = "خودکار هوشمند (HTTP/2 و HTTP/1.1 TLS)",
        arabicLabel = "تلقائي ذكي (HTTP/2 و HTTP/1.1 TLS)",
        descriptionEn = "Automatically negotiates ALPN h2 over TLS 1.3 with HTTP/1.1 fallback",
        descriptionFa = "مذاکره خودکار ALPN h2 روی TLS 1.3 همراه با پشتیبان HTTP/1.1",
        descriptionAr = "يختار تلقائياً أسرع بروتوكول TCP/TLS متوافق لكل خادم",
        isSupported = true,
    ),
    HTTP2_TCP(
        label = "HTTP/2 Multiplexed (TCP/TLS)",
        persianLabel = "پروتکل HTTP/2 مالتی‌پلکس (TCP/TLS)",
        arabicLabel = "بروتوكول HTTP/2 المتعدد (TCP/TLS)",
        descriptionEn = "Multiplexed TLS 1.3 ALPN h2 tunnel with high firewall compatibility",
        descriptionFa = "تونل رمزنگاری‌شده چندجریانی TLS 1.3 با بیشترین پایداری",
        descriptionAr = "نفق TCP مشفر بأعلى توافق مع الشبكات المقيدة",
        isSupported = true,
    ),
    HTTP11_TLS(
        label = "HTTP/1.1 Direct TLS (Anti-Throttle)",
        persianLabel = "پروتکل HTTP/1.1 مستقیم (ضد محدودیت)",
        arabicLabel = "بروتوكول HTTP/1.1 المباشر (مضاد للتقييد)",
        descriptionEn = "ALPN http/1.1 + SNI fragmentation compatibility mode",
        descriptionFa = "ارتباط ALPN http/1.1 همراه با تکه‌تکه‌سازی SNI برای دور زدن محدودیت",
        descriptionAr = "اتصال TLS مباشر لتجاوز تقييد الشبكات",
        isSupported = true,
    ),
    HTTP3_QUIC(
        label = "HTTP/3 QUIC (UDP — Unsupported by Relay)",
        persianLabel = "پروتکل HTTP/3 QUIC (غیرفعال — عدم پشتیبانی رله)",
        arabicLabel = "بروتوكول HTTP/3 QUIC (غير مدعوم حالياً)",
        descriptionEn = "Mozilla Fastly relays use TCP CONNECT; native QUIC/UDP transport is not supported",
        descriptionFa = "رله‌های فعلی موزیلا فقط روی TCP کار می‌کنند؛ اتصال واقعی QUIC/UDP پشتیبانی نمی‌شود",
        descriptionAr = "خوادم موزيلا الحالية تعمل عبر TCP فقط؛ لا يدعم QUIC/UDP",
        isSupported = false,
    ),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }

    fun localizedDescription(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> descriptionEn
        AppLanguage.FA -> descriptionFa
        AppLanguage.AR -> descriptionAr
    }

    val shortBadge: String
        get() = when (this) {
            AUTO -> "AUTO (H2)"
            HTTP2_TCP -> "HTTP/2"
            HTTP3_QUIC -> "H3 (N/A)"
            HTTP11_TLS -> "HTTP/1.1"
        }
}

enum class DynamicIslandDoubleTapAction(
    val labelEn: String,
    val labelFa: String,
    val labelAr: String,
) {
    EXPAND("Expand / Collapse", "باز و بسته کردن پنل", "فتح وإغلاق اللوحة"),
    TOGGLE_VPN("Quick Connect / Disconnect", "قطع و وصل سریع فیلترشکن", "اتصال أو قطع سريع"),
    OPEN_SERVERS("Open Server List", "باز کردن لیست سرورها", "فتح قائمة الخوادم"),
    TOGGLE_KILLSWITCH("Toggle Kill Switch", "فعال/غیرفعال‌سازی کیل‌سوئیچ", "تبديل مفتاح القفل"),
    SPEED_TEST("Run Real Latency Probe", "آزمایش فوری پینگ و تاخیر", "اختبار الاستجابة السريع"),
    COPY_EXIT_IP("Copy Protected IP", "کپی آی‌پی محافظت‌شده", "نسخ عنوان الآي بي"),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> labelEn
        AppLanguage.FA -> labelFa
        AppLanguage.AR -> labelAr
    }
}

enum class DynamicIslandIndicatorStyle(
    val labelEn: String,
    val labelFa: String,
    val labelAr: String,
) {
    TOWER_WAVE("Tower Wave", "موج دکل زنده", "موجة البرج"),
    PULSE_ORB("Pulse Orb", "گوی نوری تپنده", "الكرة النابضة"),
    RADAR_SWEEP("Radar Sweep", "اسکنر راداری", "ماسح الرادار"),
    EQUALIZER_BARS("Equalizer", "اکولایزر شبکه", "موازن الشبكة"),
    SHIELD_GUARD("Cyber Shield", "سپر امنیتی", "الدرع الأمني"),
    SIGNAL_BARS("Signal Antenna", "آنتن سیگنال", "أبراج الإشارة"),
    MINIMAL_RING("Minimal Ring", "حلقه نئون", "حلقة نيون"),
    QUANTUM_ORBIT("Quantum Dual Orbit", "مدار دوگانه کوانتومی", "مدار كمومي مزدوج"),
    HEX_MATRIX("Hexagon Cyber Shield", "سپر شش‌ضلعی سایبر", "درع سداسي سيبراني"),
    SONIC_RIPPLE("Sonic Echo Ripple", "امواج پژواک صوتی", "صدى الموجات الصوتية"),
    NEON_COMPASS("Cyber Gyro Compass", "قطب‌نمای ژیروسکوپ نئون", "بوصلة الجيروسكوب"),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> labelEn
        AppLanguage.FA -> labelFa
        AppLanguage.AR -> labelAr
    }
}

enum class DynamicIslandMotionStyle(
    val label: String,
    val persianLabel: String,
    val arabicLabel: String,
) {
    LIQUID_SPRING("Liquid Silk 120Hz", "فیزیک مایع ابریشمی ۱۲۰ هرتز", "فيزياء الحرير السائل ١٢٠ هرتز"),
    ZERO_GRAVITY("Zero-Gravity Float", "شناور بی‌وزنی فضایی", "طفو انعدام الجاذبية"),
    BREATHING_HALO("Breathing Aura Halo", "تنفس نوری آرامش‌بخش", "توهج التنفس الهادئ"),
    SONIC_WAVE_SYNC("Sonic Wave Sync", "موج صوتی هماهنگ با ترافیک", "موجة صوتية متزامنة"),
    HEARTBEAT_PULSE("Live Heartbeat Pulse", "ضربان قلب زنده", "نبض القلب الحي"),
    PRISM_SHIMMER("Crystal Prism Shimmer", "درخشش منشوری کریستال", "بريق المنشور الكريستالي"),
    MAGNETIC_ELASTIC("Magnetic Elastic Morph", "کشسانی مغناطیسی نرم", "مرونة مغناطيسية ناعمة"),
    STARLIGHT_ORBIT("Starlight Orbital", "مدار ستاره‌ای درخشان", "مدار النجوم المضيئة"),
    CYBER_GLITCH_PULSE("Cyber Matrix Pulse", "پالس ماتریکس سایبری", "نبض المصفوفة السيبرانية"),
    AURORA_WAVE_FLOW("Aurora Ribbon Flow", "موج شفق قطبی روان", "تدفق الشفق القطبي"),
    GRAVITY_PENDULUM("Smooth Pendulum Swing", "نوسان آونگی نرم", "تأرجح البندول الناعم"),
    QUANTUM_PARTICLES("Quantum Energy Field", "میدان ذرات کوانتومی", "حقل الجسيمات الكمومية"),
    ;

    fun localizedLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> label
        AppLanguage.FA -> persianLabel
        AppLanguage.AR -> arabicLabel
    }
}

class SettingsStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("foxyvpn_settings", Context.MODE_PRIVATE)
    private val _islandConfigRevision = MutableStateFlow(0)
    val islandConfigRevision: StateFlow<Int> = _islandConfigRevision

    private val credentialsPrefs by lazy {
        createSafeEncryptedPrefs(appContext, "foxyvpn_settings_credentials")
    }

    var appLanguage: AppLanguage
        get() {
            val saved = prefs.getString(KEY_APP_LANGUAGE, null)
            return AppLanguage.entries.firstOrNull { it.name == saved || it.code == saved }
                ?: when (Locale.getDefault().language) {
                    "fa" -> AppLanguage.FA
                    "ar" -> AppLanguage.AR
                    else -> AppLanguage.EN
                }
        }
        set(value) {
            prefs.edit().putString(KEY_APP_LANGUAGE, value.name).apply()
            _islandConfigRevision.value++
        }

    var themeMode: ThemeMode
        get() = ThemeMode.entries.firstOrNull {
            it.name == prefs.getString(KEY_THEME_MODE, null)
        } ?: ThemeMode.SYSTEM
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value.name).apply()
            QuickConnectWidgetProvider.updateAll(appContext, FoxyVpnService.state.value)
            _islandConfigRevision.value++
        }

    var appColorTheme: FoxyColorTheme
        get() = FoxyColorTheme.entries.firstOrNull {
            it.name == prefs.getString(KEY_APP_COLOR_THEME, null)
        } ?: FoxyColorTheme.CYBER_FOX
        set(value) {
            prefs.edit().putString(KEY_APP_COLOR_THEME, value.name).apply()
            _islandConfigRevision.value++
        }

    var appUiDesignStyle: FoxyUiDesignStyle
        get() = FoxyUiDesignStyle.entries.firstOrNull {
            it.name == prefs.getString(KEY_APP_UI_DESIGN_STYLE, null)
        } ?: FoxyUiDesignStyle.NEUMORPHISM
        set(value) {
            prefs.edit().putString(KEY_APP_UI_DESIGN_STYLE, value.name).apply()
            _islandConfigRevision.value++
        }

    var uiDesignStyle: FoxyUiDesignStyle
        get() = appUiDesignStyle
        set(value) { appUiDesignStyle = value }

    var heroWaveAnimationStyle: HeroWaveAnimationStyle
        get() = HeroWaveAnimationStyle.entries.firstOrNull {
            it.name == prefs.getString(KEY_HERO_WAVE_ANIM_STYLE, null)
        } ?: HeroWaveAnimationStyle.HARMONIC_ORBIT
        set(value) {
            prefs.edit().putString(KEY_HERO_WAVE_ANIM_STYLE, value.name).apply()
            _islandConfigRevision.value++
        }

    var waveSpeedMultiplier: Float
        get() = runCatching { prefs.getFloat("wave_speed_multiplier", 1.0f) }.getOrDefault(1.0f).coerceIn(0.4f, 2.5f)
        set(value) {
            prefs.edit().putFloat("wave_speed_multiplier", value.coerceIn(0.4f, 2.5f)).apply()
            _islandConfigRevision.value++
        }

    var waveAmplitudeScale: Float
        get() = runCatching { prefs.getFloat("wave_amplitude_scale", 1.0f) }.getOrDefault(1.0f).coerceIn(0.5f, 1.8f)
        set(value) {
            prefs.edit().putFloat("wave_amplitude_scale", value.coerceIn(0.5f, 1.8f)).apply()
            _islandConfigRevision.value++
        }

    var waveGlowEnabled: Boolean
        get() = prefs.getBoolean("wave_glow_enabled", true)
        set(value) {
            prefs.edit().putBoolean("wave_glow_enabled", value).apply()
            _islandConfigRevision.value++
        }

    var waveColorMode: WaveColorMode
        get() = WaveColorMode.entries.firstOrNull {
            it.name == prefs.getString("wave_color_mode", null)
        } ?: WaveColorMode.AUTO_STATUS
        set(value) {
            prefs.edit().putString("wave_color_mode", value.name).apply()
            _islandConfigRevision.value++
        }

    var connectionProtocolMode: ConnectionProtocolMode
        get() {
            val raw = ConnectionProtocolMode.entries.firstOrNull {
                it.name == prefs.getString("connection_protocol_mode", null)
            } ?: ConnectionProtocolMode.AUTO
            return if (raw.isSupported) raw else ConnectionProtocolMode.AUTO
        }
        set(value) {
            val safeValue = if (value.isSupported) value else ConnectionProtocolMode.AUTO
            prefs.edit().putString("connection_protocol_mode", safeValue.name).apply()
            _islandConfigRevision.value++
        }

    var adBlockEnabled: Boolean
        get() = prefs.getBoolean("ad_block_enabled", false)
        set(value) {
            prefs.edit().putBoolean("ad_block_enabled", value).apply()
        }

    var killSwitchEnabled: Boolean
        get() = prefs.getBoolean("kill_switch_enabled", false)
        set(value) {
            prefs.edit().putBoolean("kill_switch_enabled", value).apply()
        }

    var autoSwitchAccountByQuotaEnabled: Boolean
        get() = prefs.getBoolean("auto_switch_account_by_quota", true)
        set(value) {
            prefs.edit().putBoolean("auto_switch_account_by_quota", value).apply()
        }

    var autoSwitchQuotaThresholdPercent: Int
        get() = prefs.getInt("auto_switch_quota_threshold_percent", 85).coerceIn(50, 99)
        set(value) {
            prefs.edit().putInt("auto_switch_quota_threshold_percent", value.coerceIn(50, 99)).apply()
        }

    var dynamicIslandWaveStyle: DynamicIslandWaveStyle
        get() = DynamicIslandWaveStyle.entries.firstOrNull {
            it.name == prefs.getString("dynamic_island_wave_style", null)
        } ?: DynamicIslandWaveStyle.SIGNAL_TOWER
        set(value) {
            prefs.edit().putString("dynamic_island_wave_style", value.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandBgAlphaPercent: Int
        get() = prefs.getInt("dynamic_island_bg_alpha_percent", 96).coerceIn(60, 100)
        set(value) {
            prefs.edit().putInt("dynamic_island_bg_alpha_percent", value.coerceIn(60, 100)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandAutoCollapseSec: Int
        get() = prefs.getInt("dynamic_island_auto_collapse_sec", 0).coerceIn(0, 60)
        set(value) {
            prefs.edit().putInt("dynamic_island_auto_collapse_sec", value.coerceIn(0, 60)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandCompactTelemetry: Boolean
        get() = prefs.getBoolean("dynamic_island_compact_telemetry", false)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_compact_telemetry", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowProtocolBadge: Boolean
        get() = prefs.getBoolean("dynamic_island_show_protocol_badge", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_show_protocol_badge", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandHapticEnabled: Boolean
        get() = prefs.getBoolean("dynamic_island_haptic_enabled", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_haptic_enabled", value).apply()
            _islandConfigRevision.value++
        }

    /** Enables a restrained status-colored glow around the Dynamic Island. */
    var dynamicIslandGlowEnabled: Boolean
        get() = prefs.getBoolean("dynamic_island_glow_enabled", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_glow_enabled", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandLedCustomColorHex: Long
        get() = prefs.getLong("dynamic_island_led_custom_color_hex", 0L)
        set(value) {
            prefs.edit().putLong("dynamic_island_led_custom_color_hex", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandCollapsedHeightDp: Int
        get() = prefs.getInt("dynamic_island_collapsed_height_dp", 44).coerceIn(32, 56)
        set(value) {
            prefs.edit().putInt("dynamic_island_collapsed_height_dp", value.coerceIn(32, 56)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandExpandedWidthPercent: Int
        get() = prefs.getInt("dynamic_island_expanded_width_percent", 96).coerceIn(82, 100)
        set(value) {
            prefs.edit().putInt("dynamic_island_expanded_width_percent", value.coerceIn(82, 100)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandFontScalePercent: Int
        get() = prefs.getInt("dynamic_island_font_scale_percent", 100).coerceIn(80, 125)
        set(value) {
            prefs.edit().putInt("dynamic_island_font_scale_percent", value.coerceIn(80, 125)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandBorderWidthDp: Float
        get() = runCatching { prefs.getFloat("dynamic_island_border_width_dp", 1.2f) }.getOrDefault(1.2f).coerceIn(0.5f, 3.0f)
        set(value) {
            prefs.edit().putFloat("dynamic_island_border_width_dp", value.coerceIn(0.5f, 3.0f)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShadowDepthDp: Int
        get() = prefs.getInt("dynamic_island_shadow_depth_dp", 8).coerceIn(0, 20)
        set(value) {
            prefs.edit().putInt("dynamic_island_shadow_depth_dp", value.coerceIn(0, 20)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandSpringDampingPercent: Int
        get() = prefs.getInt("dynamic_island_spring_damping_percent", 82).coerceIn(55, 95)
        set(value) {
            prefs.edit().putInt("dynamic_island_spring_damping_percent", value.coerceIn(55, 95)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandSpringStiffness: Int
        get() = prefs.getInt("dynamic_island_spring_stiffness", 340).coerceIn(180, 600)
        set(value) {
            prefs.edit().putInt("dynamic_island_spring_stiffness", value.coerceIn(180, 600)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandQuickAppIconSizeDp: Int
        get() = prefs.getInt("dynamic_island_quick_app_icon_size_dp", 36).coerceIn(28, 48)
        set(value) {
            prefs.edit().putInt("dynamic_island_quick_app_icon_size_dp", value.coerceIn(28, 48)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowAppNames: Boolean
        get() = prefs.getBoolean("dynamic_island_show_app_names", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_show_app_names", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandDoubleTapAction: DynamicIslandDoubleTapAction
        get() = DynamicIslandDoubleTapAction.entries.firstOrNull {
            it.name == prefs.getString("dynamic_island_double_tap_action", null)
        } ?: DynamicIslandDoubleTapAction.EXPAND
        set(value) {
            prefs.edit().putString("dynamic_island_double_tap_action", value.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandIndicatorStyle: DynamicIslandIndicatorStyle
        get() = DynamicIslandIndicatorStyle.entries.firstOrNull {
            it.name == prefs.getString("dynamic_island_indicator_style", null)
        } ?: DynamicIslandIndicatorStyle.TOWER_WAVE
        set(value) {
            prefs.edit().putString("dynamic_island_indicator_style", value.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowJitterBadge: Boolean
        get() = prefs.getBoolean("dynamic_island_show_jitter_badge", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_show_jitter_badge", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowSecurityBadge: Boolean
        get() = prefs.getBoolean("dynamic_island_show_security_badge", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_show_security_badge", value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowNetworkActions: Boolean
        get() = prefs.getBoolean("dynamic_island_show_network_actions", true)
        set(value) {
            prefs.edit().putBoolean("dynamic_island_show_network_actions", value).apply()
            _islandConfigRevision.value++
        }

    var exitCheckEnabled: Boolean
        get() = prefs.getBoolean(KEY_EXIT_CHECK, true)
        set(value) { prefs.edit().putBoolean(KEY_EXIT_CHECK, value).apply() }

    var mtuModeAuto: Boolean
        get() = prefs.getBoolean("mtu_mode_auto", true)
        set(value) { prefs.edit().putBoolean("mtu_mode_auto", value).apply() }

    /** Preset profile label. Actual behavior is applied explicitly by the Network Tools UI. */
    var performanceProfile: String
        get() = prefs.getString(KEY_PERFORMANCE_PROFILE, PROFILE_CUSTOM)
            ?.takeIf { it == PROFILE_STABLE || it == PROFILE_FAST } ?: PROFILE_CUSTOM
        set(value) {
            if (value == PROFILE_STABLE || value == PROFILE_FAST) {
                prefs.edit().putString(KEY_PERFORMANCE_PROFILE, value).apply()
            }
        }

    var mtu: Int
        get() = prefs.getInt(KEY_CUSTOM_MTU, DEFAULT_MTU).coerceIn(MIN_MTU, MAX_MTU)
        set(value) { prefs.edit().putInt(KEY_CUSTOM_MTU, value.coerceIn(MIN_MTU, MAX_MTU)).apply() }

    val effectiveMtu: Int
        get() = if (mtuModeAuto) {
            if (gamingModeEnabled) 1360 else DEFAULT_MTU
        } else {
            mtu.coerceIn(1280, 1500)
        }

    var socketBufferModeAuto: Boolean
        get() = prefs.getBoolean("socket_buffer_mode_auto", true)
        set(value) { prefs.edit().putBoolean("socket_buffer_mode_auto", value).apply() }

    var gamingModeEnabled: Boolean
        get() = prefs.getBoolean("gaming_mode_enabled", false)
        set(value) {
            prefs.edit().putBoolean("gaming_mode_enabled", value).apply()
            _islandConfigRevision.value++
        }

    var telegramProxyModeEnabled: Boolean
        get() = prefs.getBoolean("telegram_proxy_mode_enabled", false)
        set(value) {
            prefs.edit().putBoolean("telegram_proxy_mode_enabled", value).apply()
            if (value) {
                // Force loopback binding so local Telegram proxy is never exposed on public networks
                socksBindAddress = DEFAULT_SOCKS_BIND_ADDRESS
            }
            _islandConfigRevision.value++
        }

    fun resetNetworkTuningToSafeDefaults() {
        prefs.edit()
            .putBoolean("mtu_mode_auto", true)
            .putInt(KEY_CUSTOM_MTU, DEFAULT_MTU)
            .putBoolean("socket_buffer_mode_auto", true)
            .putBoolean(KEY_TCP_BUFFER_BOOST, true)
            .putString(KEY_DNS_PROVIDER, DnsProvider.SYSTEM_DEFAULT.name)
            .putString(KEY_BOOTSTRAP_DNS_PROVIDER, BootstrapDnsProvider.AUTOMATIC.name)
            .putString(KEY_TLS_SPLIT_MODE, TlsSplitMode.OFF.name)
            .putString(KEY_CUSTOM_EDGE_ADDRESS, "")
            .putString(KEY_SOCKS_BIND_ADDRESS, DEFAULT_SOCKS_BIND_ADDRESS)
            .apply()
        _islandConfigRevision.value++
    }

    var cachedRealIp: String?
        get() = prefs.getString(KEY_CACHED_REAL_IP, null)
        set(value) { prefs.edit().putString(KEY_CACHED_REAL_IP, value).apply() }

    var cachedRealCountry: String?
        get() = prefs.getString(KEY_CACHED_REAL_COUNTRY, null)
        set(value) { prefs.edit().putString(KEY_CACHED_REAL_COUNTRY, value).apply() }

    var cachedQuotaRemaining: Long?
        get() = if (prefs.contains(KEY_CACHED_QUOTA_REMAINING)) prefs.getLong(KEY_CACHED_QUOTA_REMAINING, -1L).takeIf { it >= 0 } else null
        set(value) {
            if (value != null && value >= 0) prefs.edit().putLong(KEY_CACHED_QUOTA_REMAINING, value).apply()
            else prefs.edit().remove(KEY_CACHED_QUOTA_REMAINING).apply()
        }

    var cachedQuotaMax: Long?
        get() = if (prefs.contains(KEY_CACHED_QUOTA_MAX)) prefs.getLong(KEY_CACHED_QUOTA_MAX, -1L).takeIf { it >= 0 } else null
        set(value) {
            if (value != null && value >= 0) prefs.edit().putLong(KEY_CACHED_QUOTA_MAX, value).apply()
            else prefs.edit().remove(KEY_CACHED_QUOTA_MAX).apply()
        }

    var cachedIsUnlimited: Boolean
        get() = prefs.getBoolean(KEY_CACHED_IS_UNLIMITED, false)
        set(value) { prefs.edit().putBoolean(KEY_CACHED_IS_UNLIMITED, value).apply() }

    fun clearAccountSession() {
        prefs.edit()
            .remove(KEY_CACHED_REAL_IP)
            .remove(KEY_CACHED_REAL_COUNTRY)
            .remove(KEY_CACHED_QUOTA_REMAINING)
            .remove(KEY_CACHED_QUOTA_MAX)
            .remove(KEY_CACHED_IS_UNLIMITED)
            .apply()
    }

    var socksBindAddress: String
        get() = prefs.getString(KEY_SOCKS_BIND_ADDRESS, DEFAULT_SOCKS_BIND_ADDRESS) ?: DEFAULT_SOCKS_BIND_ADDRESS
        set(value) {
            prefs.edit().putString(KEY_SOCKS_BIND_ADDRESS, value.ifBlank { DEFAULT_SOCKS_BIND_ADDRESS }).apply()
        }

    var socksPort: Int
        get() = prefs.getInt(KEY_SOCKS_PORT, DEFAULT_SOCKS_PORT)
        set(value) { prefs.edit().putInt(KEY_SOCKS_PORT, value.coerceIn(1, 65_535)).apply() }

    var httpProxyEnabled: Boolean
        get() = prefs.getBoolean(KEY_HTTP_PROXY_ENABLED, false)
        set(value) { prefs.edit().putBoolean(KEY_HTTP_PROXY_ENABLED, value).apply() }

    var httpProxyPort: Int
        get() = prefs.getInt(KEY_HTTP_PROXY_PORT, DEFAULT_HTTP_PROXY_PORT)
        set(value) { prefs.edit().putInt(KEY_HTTP_PROXY_PORT, value.coerceIn(1, 65_535)).apply() }

    var dnsProvider: DnsProvider
        get() = DnsProvider.entries.firstOrNull {
            it.name == prefs.getString(KEY_DNS_PROVIDER, null)
        } ?: DnsProvider.SYSTEM_DEFAULT
        set(value) { prefs.edit().putString(KEY_DNS_PROVIDER, value.name).apply() }

    var customDohUrl: String
        get() = prefs.getString(KEY_CUSTOM_DOH_URL, "") ?: ""
        set(value) {
            val trimmed = value.trim()
            if (trimmed.isNotEmpty() && !isValidDohUrl(trimmed)) return
            prefs.edit().putString(KEY_CUSTOM_DOH_URL, trimmed).apply()
        }

    val effectiveDohUrl: String?
        get() = when {
            dnsProvider == DnsProvider.CUSTOM -> customDohUrl.takeIf { isValidDohUrl(it) }
            dnsProvider != DnsProvider.SYSTEM_DEFAULT -> dnsProvider.dohUrl
            adBlockEnabled -> DnsProvider.ADGUARD.dohUrl
            else -> null
        }

    var bootstrapDnsProvider: BootstrapDnsProvider
        get() = BootstrapDnsProvider.entries.firstOrNull {
            it.name == prefs.getString(KEY_BOOTSTRAP_DNS_PROVIDER, null)
        } ?: BootstrapDnsProvider.AUTOMATIC
        set(value) { prefs.edit().putString(KEY_BOOTSTRAP_DNS_PROVIDER, value.name).apply() }

    var customBootstrapIp: String
        get() = prefs.getString(KEY_CUSTOM_BOOTSTRAP_IP, "") ?: ""
        set(value) {
            val trimmed = value.trim()
            if (trimmed.isNotEmpty() && !isValidIpAddress(trimmed)) return
            prefs.edit().putString(KEY_CUSTOM_BOOTSTRAP_IP, trimmed).apply()
        }

    val effectiveBootstrapAddresses: List<String>
        get() = when (bootstrapDnsProvider) {
            BootstrapDnsProvider.CUSTOM -> {
                val ip = customBootstrapIp.trim()
                if (isValidIpAddress(ip)) listOf(ip) else BootstrapDnsProvider.AUTOMATIC.addresses
            }
            BootstrapDnsProvider.SYSTEM -> emptyList()
            else -> bootstrapDnsProvider.addresses
        }

    var proxyOnlyMode: Boolean
        get() = prefs.getBoolean(KEY_PROXY_ONLY_MODE, false)
        set(value) { prefs.edit().putBoolean(KEY_PROXY_ONLY_MODE, value).apply() }

    var customEdgeAddress: String
        get() = prefs.getString(KEY_CUSTOM_EDGE_ADDRESS, "") ?: ""
        set(value) {
            val normalized = value.trim().lowercase()
            if (normalized.isNotEmpty() && !isValidEdgeHost(normalized)) return
            prefs.edit().putString(KEY_CUSTOM_EDGE_ADDRESS, normalized).apply()
        }

    val effectiveCustomEdgeAddress: String?
        get() = customEdgeAddress.takeIf { isValidEdgeHost(it) }

    var upstreamProxyEnabled: Boolean
        get() = prefs.getBoolean(KEY_UPSTREAM_PROXY_ENABLED, false)
        set(value) { prefs.edit().putBoolean(KEY_UPSTREAM_PROXY_ENABLED, value).apply() }

    var upstreamProxyType: UpstreamProxyType
        get() = UpstreamProxyType.entries.firstOrNull {
            it.name == prefs.getString(KEY_UPSTREAM_PROXY_TYPE, null)
        } ?: UpstreamProxyType.SOCKS5
        set(value) { prefs.edit().putString(KEY_UPSTREAM_PROXY_TYPE, value.name).apply() }

    var upstreamProxyHost: String
        get() = prefs.getString(KEY_UPSTREAM_PROXY_HOST, "") ?: ""
        set(value) { prefs.edit().putString(KEY_UPSTREAM_PROXY_HOST, value.trim()).apply() }

    var upstreamProxyPort: Int
        get() = prefs.getInt(KEY_UPSTREAM_PROXY_PORT, DEFAULT_UPSTREAM_PROXY_PORT)
        set(value) { prefs.edit().putInt(KEY_UPSTREAM_PROXY_PORT, value.coerceIn(1, 65_535)).apply() }

    var upstreamProxyUsername: String
        get() = credentialsPrefs.getString(KEY_UPSTREAM_PROXY_USERNAME, "") ?: ""
        set(value) { credentialsPrefs.edit().putString(KEY_UPSTREAM_PROXY_USERNAME, value).apply() }

    var upstreamProxyPassword: String
        get() = credentialsPrefs.getString(KEY_UPSTREAM_PROXY_PASSWORD, "") ?: ""
        set(value) { credentialsPrefs.edit().putString(KEY_UPSTREAM_PROXY_PASSWORD, value).apply() }

    var excludedApps: Set<String>
        get() = prefs.getStringSet(KEY_EXCLUDED_APPS, emptySet())?.toSet() ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_EXCLUDED_APPS, HashSet(value)).apply()
        }

    var tlsSplitMode: TlsSplitMode
        get() = TlsSplitMode.entries.firstOrNull {
            it.name == prefs.getString(KEY_TLS_SPLIT_MODE, null)
        } ?: TlsSplitMode.OFF
        set(value) { prefs.edit().putString(KEY_TLS_SPLIT_MODE, value.name).apply() }

    var dynamicIslandEnabled: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_ENABLED, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandOverlayEnabled: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_OVERLAY_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_OVERLAY_ENABLED, value).apply()
            _islandConfigRevision.value++
        }

    /**
     * Manual 0% to 100% linear size slider for Dynamic Island.
     */
    var dynamicIslandScalePercent: Int
        get() = prefs.getInt(KEY_DYNAMIC_ISLAND_SCALE_PERCENT, 55).coerceIn(0, 100)
        set(value) {
            val clamped = value.coerceIn(0, 100)
            prefs.edit().putInt(KEY_DYNAMIC_ISLAND_SCALE_PERCENT, clamped).apply()
            // Sync preset enum for backwards compatibility
            val preset = when {
                clamped < 34 -> DynamicIslandSize.COMPACT
                clamped < 68 -> DynamicIslandSize.REGULAR
                else -> DynamicIslandSize.EXPANDED
            }
            prefs.edit().putString(KEY_DYNAMIC_ISLAND_SIZE, preset.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandSize: DynamicIslandSize
        get() = DynamicIslandSize.entries.firstOrNull {
            it.name == prefs.getString(KEY_DYNAMIC_ISLAND_SIZE, null)
        } ?: DynamicIslandSize.REGULAR
        set(value) {
            prefs.edit().putString(KEY_DYNAMIC_ISLAND_SIZE, value.name).apply()
            val mappedPercent = when (value) {
                DynamicIslandSize.COMPACT -> 20
                DynamicIslandSize.REGULAR -> 55
                DynamicIslandSize.EXPANDED -> 90
            }
            prefs.edit().putInt(KEY_DYNAMIC_ISLAND_SCALE_PERCENT, mappedPercent).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandCornerRadius: Int
        get() = prefs.getInt(KEY_DYNAMIC_ISLAND_CORNER_RADIUS, 28)
        set(value) {
            prefs.edit().putInt(KEY_DYNAMIC_ISLAND_CORNER_RADIUS, value.coerceIn(14, 38)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandPosition: DynamicIslandPosition
        get() = DynamicIslandPosition.entries.firstOrNull {
            it.name == prefs.getString(KEY_DYNAMIC_ISLAND_POSITION, null)
        } ?: DynamicIslandPosition.TOP_CENTER
        set(value) {
            prefs.edit().putString(KEY_DYNAMIC_ISLAND_POSITION, value.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandTheme: DynamicIslandTheme
        get() = DynamicIslandTheme.entries.firstOrNull {
            it.name == prefs.getString(KEY_DYNAMIC_ISLAND_THEME, null)
        } ?: DynamicIslandTheme.DARK
        set(value) {
            prefs.edit().putString(KEY_DYNAMIC_ISLAND_THEME, value.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowSpeed: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_SPEED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_SPEED, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowPing: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_PING, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_PING, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowFlag: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_FLAG, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_FLAG, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowTimer: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_TIMER, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_TIMER, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandAnimation: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_ANIMATION, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_ANIMATION, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandPinnedApps: Set<String>
        get() = prefs.getStringSet(KEY_DYNAMIC_ISLAND_PINNED_APPS, null)?.toSet() ?: DEFAULT_PINNED_ISLAND_APPS
        set(value) {
            prefs.edit().putStringSet(KEY_DYNAMIC_ISLAND_PINNED_APPS, HashSet(value)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowQuickApps: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_QUICK_APPS, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_QUICK_APPS, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandLedBorderEnabled: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_LED_BORDER, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_LED_BORDER, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandLedStyle: DynamicIslandLedStyle
        get() = DynamicIslandLedStyle.entries.firstOrNull {
            it.name == prefs.getString(KEY_DYNAMIC_ISLAND_LED_STYLE, null)
        } ?: DynamicIslandLedStyle.CYBER_RAINBOW
        set(value) {
            prefs.edit().putString(KEY_DYNAMIC_ISLAND_LED_STYLE, value.name).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandLedSpeed: Float
        get() = runCatching { prefs.getFloat(KEY_DYNAMIC_ISLAND_LED_SPEED, 1.0f) }.getOrDefault(1.0f).coerceIn(0.3f, 2.5f)
        set(value) {
            prefs.edit().putFloat(KEY_DYNAMIC_ISLAND_LED_SPEED, value.coerceIn(0.3f, 2.5f)).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowTowerWave: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_TOWER_WAVE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_TOWER_WAVE, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowServerName: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_SERVER_NAME, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_SERVER_NAME, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowSessionData: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_SESSION_DATA, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_SESSION_DATA, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandShowIpBadge: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_SHOW_IP_BADGE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_SHOW_IP_BADGE, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandDraggable: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_ISLAND_DRAGGABLE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_DYNAMIC_ISLAND_DRAGGABLE, value).apply()
            _islandConfigRevision.value++
        }

    var dynamicIslandOffsetX: Float
        get() = runCatching { prefs.getFloat(KEY_DYNAMIC_ISLAND_OFFSET_X, 0f) }.getOrDefault(0f)
        set(value) {
            prefs.edit().putFloat(KEY_DYNAMIC_ISLAND_OFFSET_X, value).apply()
        }

    var dynamicIslandOffsetY: Float
        get() = runCatching { prefs.getFloat(KEY_DYNAMIC_ISLAND_OFFSET_Y, 0f) }.getOrDefault(0f)
        set(value) {
            prefs.edit().putFloat(KEY_DYNAMIC_ISLAND_OFFSET_Y, value).apply()
        }

    fun resetDynamicIslandPosition() {
        prefs.edit()
            .putFloat(KEY_DYNAMIC_ISLAND_OFFSET_X, 0f)
            .putFloat(KEY_DYNAMIC_ISLAND_OFFSET_Y, 0f)
            .apply()
        _islandConfigRevision.value++
    }

    // Scheduled Auto-Connect / Auto-Disconnect Timer
    var scheduleEnabled: Boolean
        get() = prefs.getBoolean(KEY_SCHEDULE_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_SCHEDULE_ENABLED, value).apply()
        }

    var scheduleStartHour: Int
        get() = prefs.getInt(KEY_SCHEDULE_START_HOUR, 22).coerceIn(0, 23)
        set(value) {
            prefs.edit().putInt(KEY_SCHEDULE_START_HOUR, value.coerceIn(0, 23)).apply()
        }

    var scheduleStartMinute: Int
        get() = prefs.getInt(KEY_SCHEDULE_START_MINUTE, 0).coerceIn(0, 59)
        set(value) {
            prefs.edit().putInt(KEY_SCHEDULE_START_MINUTE, value.coerceIn(0, 59)).apply()
        }

    var scheduleEndHour: Int
        get() = prefs.getInt(KEY_SCHEDULE_END_HOUR, 7).coerceIn(0, 23)
        set(value) {
            prefs.edit().putInt(KEY_SCHEDULE_END_HOUR, value.coerceIn(0, 23)).apply()
        }

    var scheduleEndMinute: Int
        get() = prefs.getInt(KEY_SCHEDULE_END_MINUTE, 0).coerceIn(0, 59)
        set(value) {
            prefs.edit().putInt(KEY_SCHEDULE_END_MINUTE, value.coerceIn(0, 59)).apply()
        }

    // Smart Idle Auto-Disconnect (Battery & Data Saver)
    var idleDisconnectEnabled: Boolean
        get() = prefs.getBoolean(KEY_IDLE_DISCONNECT_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_IDLE_DISCONNECT_ENABLED, value).apply()
        }

    var idleTimeoutMinutes: Int
        get() = prefs.getInt(KEY_IDLE_TIMEOUT_MINUTES, 15).coerceIn(1, 180)
        set(value) {
            prefs.edit().putInt(KEY_IDLE_TIMEOUT_MINUTES, value.coerceIn(1, 180)).apply()
        }

    // Additional Smart Practical Options
    var sessionDataLimitEnabled: Boolean
        get() = prefs.getBoolean(KEY_SESSION_DATA_LIMIT_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_SESSION_DATA_LIMIT_ENABLED, value).apply()
        }

    var sessionDataLimitMb: Int
        get() = prefs.getInt(KEY_SESSION_DATA_LIMIT_MB, 1024).coerceIn(50, 50_000)
        set(value) {
            prefs.edit().putInt(KEY_SESSION_DATA_LIMIT_MB, value.coerceIn(50, 50_000)).apply()
        }

    var smartPingAutoSwitch: Boolean
        get() = prefs.getBoolean(KEY_SMART_PING_AUTO_SWITCH, true)
        set(value) {
            prefs.edit().putBoolean(KEY_SMART_PING_AUTO_SWITCH, value).apply()
        }

    var autoConnectOnLaunch: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CONNECT_ON_LAUNCH, false)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_CONNECT_ON_LAUNCH, value).apply()
        }

    var stealthNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_STEALTH_NOTIFICATION_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_STEALTH_NOTIFICATION_ENABLED, value).apply()
        }

    var keepAliveEnabled: Boolean
        get() = prefs.getBoolean(KEY_KEEP_ALIVE_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_KEEP_ALIVE_ENABLED, value).apply()
        }

    var keepAliveIntervalSec: Int
        get() = prefs.getInt(KEY_KEEP_ALIVE_INTERVAL_SEC, 20).coerceIn(10, 120)
        set(value) {
            prefs.edit().putInt(KEY_KEEP_ALIVE_INTERVAL_SEC, value.coerceIn(10, 120)).apply()
        }

    var autoReconnectEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RECONNECT_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_RECONNECT_ENABLED, value).apply()
        }

    var dynamicIslandMotionStyle: DynamicIslandMotionStyle
        get() = DynamicIslandMotionStyle.entries.firstOrNull {
            it.name == prefs.getString(KEY_DYNAMIC_ISLAND_MOTION_STYLE, null)
        } ?: DynamicIslandMotionStyle.LIQUID_SPRING
        set(value) {
            prefs.edit().putString(KEY_DYNAMIC_ISLAND_MOTION_STYLE, value.name).apply()
            _islandConfigRevision.value++
        }

    /**
     * Unmetered Tunnel Speed Booster: Marks the Android VPN network interface as unmetered
     * via VpnService.Builder.setMetered(false) (Android 10+) so the OS and apps do not apply
     * metered cellular data throttling or background socket restrictions.
     */
    var unmeteredVpnModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_UNMETERED_VPN_MODE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_UNMETERED_VPN_MODE, value).apply()
            _islandConfigRevision.value++
        }

    /**
     * High-Throughput TCP/UDP Socket Buffer Booster: Expands hev-socks5-tunnel TCP buffer window
     * to 256 KB and UDP receive buffer to 1 MB with 32 copy buffers for faster multi-stream throughput.
     */
    var tcpBufferBoostEnabled: Boolean
        get() = prefs.getBoolean(KEY_TCP_BUFFER_BOOST, true)
        set(value) {
            prefs.edit().putBoolean(KEY_TCP_BUFFER_BOOST, value).apply()
            _islandConfigRevision.value++
        }

    /**
     * Smart Battery Saver / Eco Mode: Adaptively throttles background telemetry & ping polling
     * when traffic is idle or screen is off, releases continuous high-drain Wi-Fi locks,
     * and optimizes shader frame pacing.
     */
    var batterySaverModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_BATTERY_SAVER_MODE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_BATTERY_SAVER_MODE, value).apply()
            _islandConfigRevision.value++
        }


    enum class DynamicIslandSize(
        val label: String,
        val persianLabel: String,
        val arabicLabel: String,
        val heightDp: Int,
        val widthDp: Int,
    ) {
        COMPACT("Compact", "کوچک", "مصغّر", 36, 170),
        REGULAR("Regular", "متوسط", "قياسي", 44, 230),
        EXPANDED("Large", "بزرگ", "كبير", 52, 280),
        ;

        fun localizedLabel(isRtl: Boolean): String = if (isRtl) persianLabel else label
        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> label
            AppLanguage.FA -> persianLabel
            AppLanguage.AR -> arabicLabel
        }
    }

    enum class DynamicIslandPosition(
        val label: String,
        val persianLabel: String,
        val arabicLabel: String,
    ) {
        TOP_CENTER("Top Center", "بالا وسط", "أعلى الوسط"),
        TOP_START("Top Start", "بالا ابتدا", "أعلى البداية"),
        TOP_END("Top End", "بالا انتها", "أعلى النهاية"),
        ;

        fun localizedLabel(isRtl: Boolean): String = if (isRtl) persianLabel else label
        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> label
            AppLanguage.FA -> persianLabel
            AppLanguage.AR -> arabicLabel
        }
    }

    enum class DynamicIslandTheme(
        val label: String,
        val persianLabel: String,
        val arabicLabel: String,
    ) {
        DARK("Native Deep Glass", "شیشه‌ای تیره نیتیو", "زجاجي داكن أصلي"),
        NEUMORPHIC("Soft Translucent", "نیمه‌شفاف نرم", "شفاف ناعم"),
        ACCENT("Brand Accent Glow", "درخشش رنگ برند", "توهج لون العلامة"),
        ;

        fun localizedLabel(isRtl: Boolean): String = if (isRtl) persianLabel else label
        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> label
            AppLanguage.FA -> persianLabel
            AppLanguage.AR -> arabicLabel
        }
    }

    enum class TlsSplitMode(
        val label: String,
        val persianLabel: String,
        val arabicLabel: String,
        val description: String,
        val persianDescription: String,
        val arabicDescription: String,
    ) {
        OFF(
            label = "Disabled",
            persianLabel = "غیرفعال",
            arabicLabel = "معطّل",
            description = "Standard TLS handshake without fragmentation",
            persianDescription = "ارتباط استاندارد بدون تکه‌تکه‌سازی بسته",
            arabicDescription = "اتصال قياسي بدون تجزئة الحزم",
        ),
        FIRST_BYTE(
            label = "First Byte",
            persianLabel = "بایت اول",
            arabicLabel = "البايت الأول",
            description = "Splits TLS record header at byte 0",
            persianDescription = "برش هدر بسته در بایت اول برای عبور از فیلترینگ",
            arabicDescription = "تقسيم ترويسة الحزمة عند البايت الأول لتجاوز الحجب",
        ),
        SNI(
            label = "Domain Fragment",
            persianLabel = "تکه‌تکه‌سازی دامنه",
            arabicLabel = "تجزئة اسم النطاق",
            description = "Fragments the hostname extension across packets",
            persianDescription = "تکه‌تکه‌سازی نام دامنه برای دور زدن بازرسی عمیق بسته",
            arabicDescription = "تجزئة امتداد اسم النطاق عبر الحزم لتجاوز الفحص العميق",
        ),
        ;

        fun localizedLabel(isRtl: Boolean): String = if (isRtl) persianLabel else label
        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> label
            AppLanguage.FA -> persianLabel
            AppLanguage.AR -> arabicLabel
        }

        fun localizedDescription(isRtl: Boolean): String = if (isRtl) persianDescription else description
        fun localizedDescription(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> description
            AppLanguage.FA -> persianDescription
            AppLanguage.AR -> arabicDescription
        }
    }

    enum class UpstreamProxyType(
        val enLabel: String,
        val faLabel: String,
        val arLabel: String,
    ) {
        SOCKS5("SOCKS5", "پراکسی ساکس ۵", "وكيل سوكس ٥"),
        HTTP("HTTP", "پراکسی وب", "وكيل ويب"),
        ;

        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> enLabel
            AppLanguage.FA -> faLabel
            AppLanguage.AR -> arLabel
        }
    }

    enum class DnsProvider(
        val label: String,
        val persianLabel: String,
        val arabicLabel: String,
        val dohUrl: String?,
    ) {
        SYSTEM_DEFAULT(
            label = "Default",
            persianLabel = "پیش‌فرض سرور",
            arabicLabel = "افتراضي الخادم",
            dohUrl = null,
        ),
        CLOUDFLARE(
            label = "Cloudflare",
            persianLabel = "کلودفلر",
            arabicLabel = "كلاود فلير",
            dohUrl = "https://1.1.1.1/dns-query",
        ),
        GOOGLE(
            label = "Google",
            persianLabel = "گوگل",
            arabicLabel = "جوجل",
            dohUrl = "https://8.8.8.8/dns-query",
        ),
        QUAD9(
            label = "Quad9",
            persianLabel = "کواد۹",
            arabicLabel = "كواد ٩",
            dohUrl = "https://9.9.9.9/dns-query",
        ),
        ADGUARD(
            label = "AdGuard",
            persianLabel = "ادگارد",
            arabicLabel = "أدجارد",
            dohUrl = "https://dns.adguard-dns.com/dns-query",
        ),
        CUSTOM(
            label = "Custom DoH",
            persianLabel = "دی‌ان‌اس امن سفارشی",
            arabicLabel = "نظام أسماء مشفّر مخصص",
            dohUrl = null,
        ),
        ;

        fun localizedLabel(isRtl: Boolean): String = if (isRtl) persianLabel else label
        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> label
            AppLanguage.FA -> persianLabel
            AppLanguage.AR -> arabicLabel
        }
    }

    enum class BootstrapDnsProvider(
        val label: String,
        val persianLabel: String,
        val arabicLabel: String,
        val description: String,
        val persianDescription: String,
        val arabicDescription: String,
        val addresses: List<String>,
    ) {
        AUTOMATIC(
            label = "Automatic",
            persianLabel = "خودکار (پیشنهادی)",
            arabicLabel = "تلقائي (موصى به)",
            description = "Races all default DoH endpoints",
            persianDescription = "استفاده همزمان از سریع‌ترین سرورهای دی‌ان‌اس",
            arabicDescription = "يختار أسرع خوادم نظام الأسماء تلقائياً",
            addresses = listOf("9.9.9.9", "1.1.1.1", "8.8.8.8"),
        ),
        QUAD9(
            label = "Quad9",
            persianLabel = "کواد۹",
            arabicLabel = "كواد ٩",
            description = "9.9.9.9, 149.112.112.112",
            persianDescription = "۹.۹.۹.۹ ، ۱۴۹.۱۱۲.۱۱۲.۱۱۲",
            arabicDescription = "٩.٩.٩.٩ ، ١٤٩.١١٢.١١٢.١١٢",
            addresses = listOf("9.9.9.9", "149.112.112.112"),
        ),
        CLOUDFLARE(
            label = "Cloudflare",
            persianLabel = "کلودفلر",
            arabicLabel = "كلاود فلير",
            description = "1.1.1.1, 1.0.0.1",
            persianDescription = "۱.۱.۱.۱ ، ۱.۰.۰.۱",
            arabicDescription = "١.١.١.١ ، ١.٠.٠.١",
            addresses = listOf("1.1.1.1", "1.0.0.1"),
        ),
        GOOGLE(
            label = "Google",
            persianLabel = "گوگل",
            arabicLabel = "جوجل",
            description = "8.8.8.8, 8.8.4.4",
            persianDescription = "۸.۸.۸.۸ ، ۸.۸.۴.۴",
            arabicDescription = "٨.٨.٨.٨ ، ٨.٨.٤.٤",
            addresses = listOf("8.8.8.8", "8.8.4.4"),
        ),
        SYSTEM(
            label = "System DNS",
            persianLabel = "دی‌ان‌اس سیستم",
            arabicLabel = "نظام أسماء الجهاز",
            description = "Local network DNS",
            persianDescription = "استفاده از دی‌ان‌اس شبکه فعلی دستگاه",
            arabicDescription = "استخدام نظام الأسماء المحلي للشبكة",
            addresses = emptyList(),
        ),
        CUSTOM(
            label = "Custom IP",
            persianLabel = "آی‌پی سفارشی",
            arabicLabel = "عنوان آي بي مخصص",
            description = "Specify a custom DoH IP resolver address",
            persianDescription = "وارد کردن آدرس آی‌پی دلخواه برای دی‌ان‌اس",
            arabicDescription = "تحديد عنوان آي بي مخصص لمحلل الأسماء",
            addresses = emptyList(),
        ),
        ;

        fun localizedLabel(isRtl: Boolean): String = if (isRtl) persianLabel else label
        fun localizedLabel(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> label
            AppLanguage.FA -> persianLabel
            AppLanguage.AR -> arabicLabel
        }

        fun localizedDescription(isRtl: Boolean): String = if (isRtl) persianDescription else description
        fun localizedDescription(lang: AppLanguage): String = when (lang) {
            AppLanguage.EN -> description
            AppLanguage.FA -> persianDescription
            AppLanguage.AR -> arabicDescription
        }
    }

    companion object {
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_EXIT_CHECK = "exit_check_enabled"
        const val PROFILE_STABLE = "stable"
        const val PROFILE_FAST = "fast"
        const val PROFILE_CUSTOM = "custom"
        private const val KEY_PERFORMANCE_PROFILE = "performance_profile"
        private const val KEY_CUSTOM_MTU = "custom_mtu"
        private const val KEY_CACHED_REAL_IP = "cached_real_ip"
        private const val KEY_CACHED_REAL_COUNTRY = "cached_real_country"
        private const val KEY_CACHED_QUOTA_REMAINING = "cached_quota_remaining"
        private const val KEY_CACHED_QUOTA_MAX = "cached_quota_max"
        private const val KEY_CACHED_IS_UNLIMITED = "cached_is_unlimited"
        const val DEFAULT_MTU = 1400
        const val MIN_MTU = 1280
        const val MAX_MTU = 1500
        private const val KEY_SOCKS_BIND_ADDRESS = "socks_bind_address"
        private const val KEY_SOCKS_PORT = "socks_port"
        private const val KEY_HTTP_PROXY_ENABLED = "http_proxy_enabled"
        private const val KEY_HTTP_PROXY_PORT = "http_proxy_port"
        private const val KEY_DNS_PROVIDER = "dns_provider"
        private const val KEY_CUSTOM_DOH_URL = "custom_doh_url"
        private const val KEY_BOOTSTRAP_DNS_PROVIDER = "bootstrap_dns_provider"
        private const val KEY_CUSTOM_BOOTSTRAP_IP = "custom_bootstrap_ip"
        private const val KEY_PROXY_ONLY_MODE = "proxy_only_mode"
        private const val KEY_CUSTOM_EDGE_ADDRESS = "custom_edge_address"
        private const val KEY_UPSTREAM_PROXY_ENABLED = "upstream_proxy_enabled"
        private const val KEY_UPSTREAM_PROXY_TYPE = "upstream_proxy_type"
        private const val KEY_UPSTREAM_PROXY_HOST = "upstream_proxy_host"
        private const val KEY_UPSTREAM_PROXY_PORT = "upstream_proxy_port"
        private const val KEY_UPSTREAM_PROXY_USERNAME = "upstream_proxy_username"
        private const val KEY_UPSTREAM_PROXY_PASSWORD = "upstream_proxy_password"
        private const val KEY_EXCLUDED_APPS = "excluded_apps"
        private const val KEY_TLS_SPLIT_MODE = "tls_split_mode"
        private const val KEY_DYNAMIC_ISLAND_ENABLED = "dynamic_island_enabled"
        private const val KEY_DYNAMIC_ISLAND_OVERLAY_ENABLED = "dynamic_island_overlay_enabled"
        private const val KEY_DYNAMIC_ISLAND_SCALE_PERCENT = "dynamic_island_scale_percent"
        private const val KEY_DYNAMIC_ISLAND_SIZE = "dynamic_island_size"
        private const val KEY_DYNAMIC_ISLAND_CORNER_RADIUS = "dynamic_island_corner_radius"
        private const val KEY_DYNAMIC_ISLAND_POSITION = "dynamic_island_position"
        private const val KEY_DYNAMIC_ISLAND_THEME = "dynamic_island_theme"
        private const val KEY_DYNAMIC_ISLAND_SHOW_SPEED = "dynamic_island_show_speed"
        private const val KEY_DYNAMIC_ISLAND_SHOW_PING = "dynamic_island_show_ping"
        private const val KEY_DYNAMIC_ISLAND_SHOW_FLAG = "dynamic_island_show_flag"
        private const val KEY_DYNAMIC_ISLAND_SHOW_TIMER = "dynamic_island_show_timer"
        private const val KEY_DYNAMIC_ISLAND_ANIMATION = "dynamic_island_animation"
        private const val KEY_DYNAMIC_ISLAND_PINNED_APPS = "dynamic_island_pinned_apps"
        private const val KEY_DYNAMIC_ISLAND_LED_BORDER = "dynamic_island_led_border"
        private const val KEY_DYNAMIC_ISLAND_LED_STYLE = "dynamic_island_led_style"
        private const val KEY_DYNAMIC_ISLAND_LED_SPEED = "dynamic_island_led_speed"
        private const val KEY_DYNAMIC_ISLAND_SHOW_TOWER_WAVE = "dynamic_island_show_tower_wave"
        private const val KEY_DYNAMIC_ISLAND_SHOW_SERVER_NAME = "dynamic_island_show_server_name"
        private const val KEY_DYNAMIC_ISLAND_SHOW_SESSION_DATA = "dynamic_island_show_session_data"
        private const val KEY_DYNAMIC_ISLAND_SHOW_IP_BADGE = "dynamic_island_show_ip_badge"
        private const val KEY_DYNAMIC_ISLAND_DRAGGABLE = "dynamic_island_draggable"
        private const val KEY_DYNAMIC_ISLAND_OFFSET_X = "dynamic_island_offset_x"
        private const val KEY_DYNAMIC_ISLAND_OFFSET_Y = "dynamic_island_offset_y"
        private const val KEY_SCHEDULE_ENABLED = "schedule_enabled"
        private const val KEY_SCHEDULE_START_HOUR = "schedule_start_hour"
        private const val KEY_SCHEDULE_START_MINUTE = "schedule_start_minute"
        private const val KEY_SCHEDULE_END_HOUR = "schedule_end_hour"
        private const val KEY_SCHEDULE_END_MINUTE = "schedule_end_minute"
        private const val KEY_IDLE_DISCONNECT_ENABLED = "idle_disconnect_enabled"
        private const val KEY_IDLE_TIMEOUT_MINUTES = "idle_timeout_minutes"
        private const val KEY_SESSION_DATA_LIMIT_ENABLED = "session_data_limit_enabled"
        private const val KEY_SESSION_DATA_LIMIT_MB = "session_data_limit_mb"
        private const val KEY_SMART_PING_AUTO_SWITCH = "smart_ping_auto_switch"
        private const val KEY_AUTO_CONNECT_ON_LAUNCH = "auto_connect_on_launch"
        private const val KEY_STEALTH_NOTIFICATION_ENABLED = "stealth_notification_enabled"
        private const val KEY_KEEP_ALIVE_ENABLED = "keep_alive_enabled"
        private const val KEY_KEEP_ALIVE_INTERVAL_SEC = "keep_alive_interval_sec"
        private const val KEY_AUTO_RECONNECT_ENABLED = "auto_reconnect_enabled"
        private const val KEY_APP_COLOR_THEME = "app_color_theme"
        private const val KEY_APP_UI_DESIGN_STYLE = "app_ui_design_style"
        private const val KEY_HERO_WAVE_ANIM_STYLE = "hero_wave_anim_style"
        private const val KEY_DYNAMIC_ISLAND_SHOW_QUICK_APPS = "dynamic_island_show_quick_apps"
        private const val KEY_DYNAMIC_ISLAND_MOTION_STYLE = "dynamic_island_motion_style"
        private const val KEY_UNMETERED_VPN_MODE = "unmetered_vpn_mode_enabled"
        private const val KEY_TCP_BUFFER_BOOST = "tcp_buffer_boost_enabled"
        private const val KEY_BATTERY_SAVER_MODE = "battery_saver_mode_enabled"
        const val DEFAULT_SOCKS_BIND_ADDRESS = "127.0.0.1"
        const val DEFAULT_SOCKS_PORT = 1080
        const val DEFAULT_HTTP_PROXY_PORT = 8080
        const val DEFAULT_UPSTREAM_PROXY_PORT = 1080

        val DEFAULT_PINNED_ISLAND_APPS = setOf(
            "com.android.chrome",
            "org.telegram.messenger",
            "com.google.android.youtube",
        )

        val SOCKS_BIND_ADDRESS_PRESETS = listOf(
            "127.0.0.1" to "Loopback only (127.0.0.1)",
            "0.0.0.0" to "All interfaces (0.0.0.0)",
        )

        fun socksBindPresetsFor(lang: AppLanguage): List<Pair<String, String>> = when (lang) {
            AppLanguage.EN -> SOCKS_BIND_ADDRESS_PRESETS
            AppLanguage.FA -> listOf(
                "127.0.0.1" to "فقط دستگاه فعلی (۱۲۷.۰.۰.۱)",
                "0.0.0.0" to "تمام کارت‌های شبکه (۰.۰.۰.۰)",
            )
            AppLanguage.AR -> listOf(
                "127.0.0.1" to "الجهاز الحالي فقط (١٢٧.٠.٠.١)",
                "0.0.0.0" to "جميع واجهات الشبكة (٠.٠.٠.٠)",
            )
        }

        fun isValidDohUrl(value: String): Boolean {
            val trimmed = value.trim()
            if (!trimmed.startsWith("https://", ignoreCase = true)) return false
            return runCatching {
                val uri = java.net.URI.create(trimmed)
                !uri.host.isNullOrBlank()
            }.getOrDefault(false)
        }

        fun isValidIpAddress(value: String): Boolean {
            return if (android.os.Build.VERSION.SDK_INT >= 29) {
                android.net.InetAddresses.isNumericAddress(value)
            } else {
                @Suppress("DEPRECATION")
                android.util.Patterns.IP_ADDRESS.matcher(value).matches()
            }
        }

        fun isValidHostname(value: String): Boolean {
            val host = value.trim().removeSuffix(".")
            if (host.isEmpty() || host.length > 253) return false

            if (host.contains(':')) return false
            if (host.all { it.isDigit() || it == '.' }) return false
            val labels = host.split('.')
            if (labels.size < 2) return false
            return labels.all { label ->
                label.isNotEmpty() &&
                    label.length <= 63 &&
                    !label.startsWith('-') &&
                    !label.endsWith('-') &&
                    label.all { char ->
                        char == '-' || (char.code < 128 && (char.isLetterOrDigit()))
                    }
            }
        }

        fun isValidEdgeHost(value: String): Boolean {
            val host = value.trim().removeSuffix(".")
            return isValidHostname(host) || isValidIpAddress(host)
        }
    }
}
