# AAYBrowser (Android Auto YouTube Browser)

AAYBrowser is a lightweight, customizable Android web browser designed for high usability, optimized to support modern Jetpack Compose UI patterns. It is built as a successor and fork of the open-source **[AABrowser by kododake](https://github.com/kododake/AABrowser)**.

---

## 📦 Downloads & Latest Releases

The compiled application packages (APKs) are available directly on GitHub:

* **[Download Latest APK (v1.0.0)](https://github.com/kishorecheerala/AAYBrowser/releases/tag/v1.0.0)** - Contains the pre-compiled, driving-friendly `AAYBrowser-debug.apk` browser package.

---

## 🚀 Key Features

AAYBrowser is enriched with features designed for seamless mobile and in-car browsing experiences:

- **Clean Jetpack Compose Interface**: A modern, high-contrast Material 3 dynamic interface built entirely on modern Jetpack design paradigms.
- **Dedicated YouTube Mode (Driving Friendly)**:
  - **Custom User Agent**: Mimics stable iPad and high-efficiency standard Mobile Safari viewports when visiting YouTube.
  - **Dynamic CSS Injection**: Dynamic real-time script injection actively sweeps away distracting web components (such as comments, heavy video recommendation sidebars, promoter widgets, and popups).
  - **Optimized UI**: Keeps focus squarely on the media player rendering and controls for minimal distraction during static or parked sessions.
- **Multitasking Tabs & Bookmarks**: Full multi-tab browser structure paired with customizable user bookmark shortcuts.
- **Dark & Light Themes**: Sleek dark and light styling matching the system configuration or desktop settings toggles.
- **Extra Control Configurations**: Easily toggle JavaScript, Ad Blocking emulation, and Desktop user-agent override values.

---

## 🛠️ How to Build the APK from Git

Follow these instructions to fetch, compile, and run the **AAYBrowser** APK on your computer using command-line Gradle tools.

### Prerequisite Checklist
1. **Java Development Kit (JDK)**: Make sure you have **JDK 11** or higher (tested up to **JDK 25**) installed and configured in your environment (`JAVA_HOME`).
2. **Android SDK**: Install standard Android Build Tools and SDK Platform APIs (SDK API level 34 or above).

### 📦 Build Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/kishorecheerala/AAYBrowser.git
cd AAYBrowser
```

#### 2. Build a Debug APK
To create a fully functional debug-signed APK suitable for local installation and manual testing:
1. Ensure you have `debug.keystore` in the root folder (if not present, you can generate it using `keytool` with password `android` and alias `androiddebugkey`).
2. Run the build command:
   ```bash
   ./gradlew assembleDebug
   ```
   *(Note: If Gradle is not installed locally, you can use the cached gradle binary from your Gradle home).*

The output file will be generated at:
`app/build/outputs/apk/debug/AAYBrowser-debug.apk`

#### 3. Build a Release APK (or App Bundle)
For release compilation, configure your keystore in `app/build.gradle.kts` and run:
```bash
./gradlew assembleRelease
```

---

## 🙏 Credits & Special Thanks

We would like to express our sincere gratitude and deep appreciation to the developer **kododake** (creator of [AABrowser](https://github.com/kododake/AABrowser)) for providing the exceptional foundation and inspiration that made this project possible. Their commitment to open-source developer toolings empowers developers worldwide.
