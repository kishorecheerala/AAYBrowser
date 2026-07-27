# AAYBrowser (Android Auto YouTube & Web Browser)

[![Android](https://img.shields.io/badge/Android-15%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg?style=for-the-badge)](https://www.gnu.org/licenses/gpl-3.0)
[![Latest Release](https://img.shields.io/badge/Release-v2.2-blue?style=for-the-badge&logo=github)](https://github.com/kishorecheerala/AAYBrowser/releases/tag/v2.2)

**AAYBrowser** is a custom, feature-rich Android Auto web browser built specifically for in-car entertainment, YouTube viewing, and web browsing on car head units. Built as a successor and enhanced fork of the open-source **[AABrowser by kododake](https://github.com/kododake/AABrowser)**, it combines full Android Auto `Presentation` projection with custom distraction-free YouTube enhancements.

---

## 📦 Downloads & Latest Release

* **[Download Latest APK (v2.2 Custom)](https://github.com/kishorecheerala/AAYBrowser/releases/download/v2.2/AAYBrowser-2.2-custom.apk)** - Pre-compiled, driving-friendly `AAYBrowser` package.
* **[GitHub Releases Page](https://github.com/kishorecheerala/AAYBrowser/releases)**

---

## ✨ Key Features & Customizations

- 🎯 **Native Automotive UI:** Designed specifically for car head unit displays using Android Auto native presentation projection.
- 🔴 **Dedicated YouTube Mode (Driving Friendly):**
  - **Custom User-Agent:** Automatically uses iPad / Mobile Safari User-Agent when visiting YouTube for optimal video layout.
  - **Dynamic Distraction Sweeper:** Real-time JavaScript injection sweeps away comments, related video sidebars, end screen popups, and promoter banners.
- 🛑 **Built-in Ad Blocking:** Integrated request interceptor that blocks major ad network domains (`googleads`, `doubleclick`, `ads.youtube.com`, etc.).
- 💡 **Car Display Screen Wake Lock:** Keeps both phone screen and car display awake during video playback so your display never dims or sleeps.
- 🚀 **Pre-configured Speed Dials:** Quick access shortcuts for YouTube, Netflix, Prime Video, Twitch, Spotify, and Google Maps.
- 🎬 **Immersive DRM Video:** Watch DRM-protected fullscreen video (Widevine L3) — ideal during EV charging or static parked sessions.
- 🎨 **Light & AMOLED Black Themes:** Switch between bright light mode and true-black AMOLED dark mode tailored for car displays.
- 🗂️ **Multi-Tab & Session Restore:** Full multi-tab browser structure paired with automatic tab session recovery on launch.
- 🧭 **Persistent URL Bar & Quick Action Button:** Customizable floating button and persistent address bar layout options.
- 🔎 **Global Display Scale:** Adjust UI and page rendering scale (from 40% to 200%) to fit any car screen resolution.

---

## 🛠️ Quick Start & Setup 🚦

#### 🛑 Safety Notice
* **Driver's Duty:** If you're driving, **DO NOT LOOK AT THIS APP.** Keep your eyes on the road.
* **Passenger's Use:** This app is intended for passengers or when safely parked.

---

#### 🛠️ How to Enable Unknown Sources on Android Auto

To run side-loaded apps on Android Auto:

1. **Open Android Auto Settings:** Search for "Android Auto" in your phone's settings.
2. **Unlock Developer Mode:** Scroll to the bottom and **tap the "Version" section 10 times**. Tap **OK** on the pop-up.
3. **Open Developer Settings:** Tap the **three-dot menu (⋮)** in top-right corner $\rightarrow$ **Developer settings**.
4. **Enable Unknown Sources:** Check the **Unknown sources** box.
5. Install `AAYBrowser-2.2-custom.apk` on your phone and connect to your car!

---

## 🛠️ How to Build from Source

### Prerequisites
1. **JDK 17+** configured in environment (`JAVA_HOME`).
2. **Android SDK API Level 36/37**.

```bash
git clone https://github.com/kishorecheerala/AAYBrowser.git
cd AAYBrowser
./gradlew assembleDebug
```

The APK will be generated at:
`app/build/renamedApks/debug/AABrowser-2.2_debug.apk`

---

## 🙏 Credits & Attribution

Special thanks and full credit to **kododake** (creator of [AABrowser](https://github.com/kododake/AABrowser)) for providing the open-source foundation and inspiration for this project.

---

## 🛡️ License

Distributed under the **GPL-3.0 License**.
