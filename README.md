<div align="center">

# FoxyVPN — Soft Neumorphism Edition (v1.0.8)

<p align="center">
  <img src="docs/images/foxy_icon.png" alt="FoxyVPN Logo" width="140" style="border-radius: 50%;"/>
</p>

![Android](https://img.shields.io/badge/Android_8.0%E2%80%9315-212121?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin_2.0-212121?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose_M3-212121?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License_MIT-212121?style=for-the-badge)

*FoxyVPN connects seamlessly with a Firefox account, obtains a proxy pass from Mozilla's Guardian service, and tunnels device traffic through Firefox VPN's Fastly edge relays over HTTP/2 (TCP) or HTTP/1.1 + TLS. Built natively for Android in Kotlin and Jetpack Compose with a tactile Soft Neumorphism UI, Electric Blue & White brand palette, Quick Settings Tile, Notification Service integration, Deep Dynamic Island Customization, Standalone Telegram SOCKS5 Proxy, and Real-Time Diagnostics.*

</div>

---

## 📸 Screenshots & UI Showcase

<div align="center">
  <table>
    <tr>
      <td align="center"><b>Home & Slide to Connect</b></td>
      <td align="center"><b>Server Locations (Latency/Edge)</b></td>
    </tr>
    <tr>
      <td><img src="docs/images/screenshot_home.jpg" width="300" alt="Home Screen"/></td>
      <td><img src="docs/images/screenshot_servers.jpg" width="300" alt="Server Locations Screen"/></td>
    </tr>
    <tr>
      <td align="center"><b>Category Settings Hub</b></td>
      <td align="center"><b>Dynamic Island Studio</b></td>
    </tr>
    <tr>
      <td><img src="docs/images/screenshot_settings.jpg" width="300" alt="Settings Hub"/></td>
      <td><img src="docs/images/screenshot_dynamic_island.jpg" width="300" alt="Dynamic Island Studio"/></td>
    </tr>
  </table>
</div>

---

## 🎨 Design & Visual Identity Updates

1. **Official Blue & White Circular App Icon**:
   - Replaced legacy orange assets with the new stylized origami blue and white fox design.
   - Fully adaptive with standard circular mask (`ic_launcher_round`), 66dp safe-zone layering, and non-adaptive PNG fallbacks for all device densities (`mdpi` through `xxxhdpi`).
2. **Harmonized UI Color Palette**:
   - Cohesive Electric Cyan (`#00E5FF` / `#00B0FF`), Royal Azure (`#0284C7` / `#2563EB`), and Deep Obsidian Slate (`#14171E` / `#1B1F28`) replacing old orange tints across buttons, sliders, badges, and wave graphs.
3. **Redesigned Notification & Foreground Service**:
   - Clean circular monochrome vector silhouette (`ic_notification_foxy`) in the Android Status Bar.
   - High-resolution circular official fox logo displayed in the expanded notification shade and lockscreen cards.
4. **Quick Settings Tile (QS Tile)**:
   - One-tap toggle from Android's Control Center / Quick Settings panel using the official circular app icon.
   - Real-time active/connecting/disconnected state updates with subtitle telemetry.

---

## 📦 Multi-Architecture & Universal APK Builds

FoxyVPN ships native `libhev-socks5-tunnel.so` binaries for all Android CPU architectures and supports both **per-ABI split APKs** and a **Universal All-in-One APK**:

| APK Build Variant | Target CPU Architecture | Recommended Devices |
| :--- | :--- | :--- |
| `FoxyVPN-Neu-universal-release.apk` | **Universal (`arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`)** | All Android phones, tablets, Chromebooks & emulators |
| `FoxyVPN-Neu-arm64-v8a-release.apk` | **64-bit ARM (`arm64-v8a`)** | Modern Android smartphones & tablets (Android 10–15) |
| `FoxyVPN-Neu-armeabi-v7a-release.apk` | **32-bit ARM (`armeabi-v7a`)** | Older 32-bit ARM phones, Android TV boxes & routers |
| `FoxyVPN-Neu-x86_64-release.apk` | **64-bit Intel / AMD (`x86_64`)** | Chromebooks, Android Studio / PC emulators |
| `FoxyVPN-Neu-x86-release.apk` | **32-bit Intel (`x86`)** | Legacy x86 tablets & emulators |

To build all CPU-specific APKs plus the Universal APK from source:
```bash
gradle :app:assembleRelease -PenableAbiSplits=true --no-daemon
```

---

## ✨ Key Capabilities & Real Features

### 1. Tactile Soft Neumorphism Interface
- **Dual Soft Neumorphism Palette**: Warm Alabaster Day (`#ECEAF2`) & Deep Obsidian Night (`#14171E` / `#1B1F28`) with physical dual-shadow extrusion and recessed inner wells.
- **Paginated Settings Architecture**: Non-scrolling category dashboard with dedicated sub-pages (`1/3` and `1/4` steppers) for clean navigation across all screen sizes.
- **Trilingual Support**: Full RTL Persian (`فارسی`), Arabic (`العربية`), and LTR English (`English`) localization with localized digits and custom typography (`Vazirmatn`, `Cairo`, `Almarai`, `Plus Jakarta Sans`, `Inter`, `JetBrains Mono`).

### 2. Deep Dynamic Island Customization & Pro Wave Studio
- **Synchronized In-App & System Overlay Island**: Unified spring-physics morphing (`dampingRatio` & `stiffness` customizable in real time) both inside the app and as a floating overlay (`TYPE_APPLICATION_OVERLAY`) outside the app.
- **Granular Micro-Controls**:
  - Collapsed Pill Height (`32–56 dp`), Expanded Width (`82%–100%`), Corner Radius (`14–38 dp`), and Overall Scale (`0%–100%`).
  - Typography Scale (`80%–125%`), Border Stroke Width (`0.5–3.0 dp`), 3D Shadow Depth (`0–20 dp`), and Background Opacity (`60%–100%`).
  - Spring Physics Tuning: Adjustable Spring Damping (`55%–95%`) and Spring Stiffness (`180–600`).
  - Quick-Launch App Dock: Real application icons and names resolved via Android `PackageManager`.
- **Pro Wave Studio**: Acoustic wave styles with independent bar count (`4–16`), stroke thickness (`2–6 dp`), speed, sensitivity, amplitude, phase shift, glow intensity, and customizable color modes.

### 3. Evidence-Based VPN Engine & Protocols
- **Supported Tunnel Protocols**:
  - `Smart Auto (HTTP/2 + HTTP/1.1 TLS)`: Negotiates ALPN `h2` over TLS 1.3 with automatic fallback.
  - `HTTP/2 Multiplexed (TCP/TLS)`: Multiplexes concurrent streams over a single TLS 1.3 connection (`H2UpstreamSession`).
  - `HTTP/1.1 + TLS`: Dedicated `http/1.1` ALPN compatibility mode with automatic SNI fragmentation.
- **UDP & DNS Protection**:
  - The local SOCKS5 UDP relay (`Socks5UdpDnsRelay`) carries UDP Port 53 DNS queries over encrypted DoH inside the tunnel.
  - Automatic Safe Mode (`MTU 1400`, adaptive socket buffers, automatic DoH bootstrap) with live probe validation.

### 4. Standalone Telegram SOCKS5 Proxy Mode
- **Loopback-Protected SOCKS5 Server (`LocalSocks5Server`)**:
  - Runs on `127.0.0.1:<port>` and forwards traffic through the authenticated Mozilla Fastly `H2UpstreamSession` without routing loops.
  - One-tap copy buttons and direct **`tg://socks?server=127.0.0.1&port=...` ("Add to Telegram")** deep-link integration.

### 5. Security, Leak Verification & Diagnostics
- **Real In-Tunnel & Direct Leak Audit**: Compares direct ISP IP vs proxied exit IP, verifies in-tunnel DoH DNS resolution and latency, and checks IPv6 routing.
- **One-Tap Sanitized Diagnostic Report**: Generates a copyable report containing tunnel state, active protocol, latency measurement method, MTU/buffer configuration, and DNS status—while strictly redacting tokens and credentials.

---

## 📱 Platform Compatibility (Android 8.0–15 & OEM ROMs)

- **Android 14 / 15 (API 34–35)**: Uses `FOREGROUND_SERVICE_SPECIAL_USE` (`vpn` subtype) and `ServiceCompat.startForeground`.
- **Android 10+ (API 29+)**: Tracks underlying networks via `ConnectivityManager.NetworkCallback` (`NET_CAPABILITY_NOT_VPN`) and protects upstream sockets via `VpnService.protect()`.
- **Quick Settings Tile**: Android 7.0+ (API 24+) Quick Settings Tile Service with real-time subtitle telemetry.
- **MIUI / HyperOS, OneUI & ColorOS**: Built-in battery optimization exemption and background autostart shortcuts.

---

## 🚀 How to Use

1. **Create or use a free Firefox Account** at [accounts.firefox.com](https://accounts.firefox.com/signup).
2. **Sign in** inside FoxyVPN to obtain your Mozilla Guardian proxy pass.
3. **Pick a server** (or use **Telegram Proxy Mode**) and slide the connection toggle to connect.

---

## 📄 License
Licensed under the [MIT License](LICENSE).
