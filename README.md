# AAYBrowser (Android Auto YouTube Browser)

AAYBrowser is an Android web browser designed with high usability and custom features, optimized to support Jetpack Compose UI patterns. It is inspired by and built as a successor/fork of the original open-source concept: **[AABrowser by kododake](https://github.com/kododake/AABrowser)**.

---

## 🙏 Credits & Special Thanks

We would like to express our sincere gratitude and deep appreciation to the developer **kododake** (creator of [AABrowser](https://github.com/kododake/AABrowser)) for providing the exceptional foundation and inspiration that made this project possible. Their commitment to open-source developer toolings empowers developers worldwide to explore, extend, and innovate on helpful Android-centric web tools.

Thank you for your fantastic contribution to the developer community! 🌟

---

## 🚀 Key Features

AAYBrowser comes enriched with powerful features designed for seamless mobile and in-car browsing experiences:

- **Clean Jetpack Compose Interface**: A modern, high-contrast,Material 3 dynamic interface built entirely on modern Jetpack design paradigms.
- **Dedicated YouTube Mode (Driving Friendly)**:
  - **Custom User Agent**: Mimics stable iPad and high-efficiency standard mobile safari viewports when visiting YouTube.
  - **Dynamic CSS Injection**: Dynamic real-time script injection actively sweeps away distracting web components (such as comments, heavy video recommendations sidebar channels, promoter widgets, and popups).
  - **Optimized UI**: Keeps focus squarely on the media player rendering and controls for minimal distraction during static or parked sessions.
- **Multitasking Tabs & Bookmarks**: Full multi-tab browser structure paired with customizable user bookmark shortcuts.
- **Dark & Light Themes**: Sleek dark and light styling matching the system configuration or customizable settings toggles.
- **Extra Control Configurations**: Easily toggle Javascript, Ad Blocking emulation, and Desktop user-agent override values.

---

## 🛠️ How to Build the APK from Git

Follow these simple instructions to fetch, compile, and run the **AAYBrowser** APK on your computer using command-line Gradle tools.

### Prerequisite Checklist
1. **Java Development Kit (JDK)**: Make sure you have **JDK 11** or **JDK 17** installed and configured in your environment (`JAVA_HOME`).
2. **Android SDK**: Install standard Android Build Tools and SDK Platform APIs (SDK API level 34 or above).

### 📦 Build Instructions

#### 1. Clone the Repository
Clone the codebase to your local workstation:
```bash
git clone https://github.com/kododake/AABrowser.git
cd AABrowser
```

#### 2. Verify Your Environment
Before running full compilations, verify your development setup. You can verify dependencies using Gradle:
```bash
gradle tasks
```

#### 3. Build a Debug APK
To create a fully functional debug-signed APK suitable for local installation and manual testing:
```bash
gradle assembleDebug
```
The output file will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

#### 4. Build a Release APK (or App Bundle)
For release compilation, run standard tasks:
```bash
gradle assembleRelease
```
*Note: Make sure to configure your own keystore file under the `signingConfigs` block inside `app/build.gradle.kts` if you intend to sign the final build for generic app distribution.*

---

Enjoy your driving-friendly, tailored browser with **AAYBrowser**!
