# AAYBrowser Agent Rules & Project Guidelines

## 📌 Project Identity
- **Project Name:** AAYBrowser (Android Auto Web & YouTube Browser)
- **Developer & Owner:** Kishore Cheerala (`com.kishorecheerala.aaybrowser`)
- **Core Objective:** THIS IS NOT A CLONE of the original AABrowser. It is Kishore Cheerala's customized, feature-packed driving-friendly browser for Android Auto.

## 🚀 Mandatory Custom Features & Requirements
Whenever updating or refactoring code in this repository, you MUST preserve and enforce the following custom capabilities:

1. **Driving Playback Support (No Motion Lockouts):**
   - In `res/xml/automotive_app_desc.xml`, MUST use `<uses name="template" />` and `<uses name="navigation" />`.
   - Never set `android:appCategory="game"` in `AndroidManifest.xml` (which triggers Car OS driving motion lockouts).
   - Ensure `distractionOptimized="true"` is declared on all application/activity elements.

2. **Continuous Focus & Page Visibility Playback:**
   - Overridden Page Visibility API (`document.hidden = false`, `document.visibilityState = 'visible'`) injected into WebViews.
   - Do NOT invoke `webView?.onPause()` in `MainActivity.onPause()` to prevent focus shifts from pausing video/audio playback while driving.

3. **Dedicated YouTube Mode (Driving Friendly):**
   - Automatic iPad / Mobile Safari User-Agent override for YouTube domains (`youtube.com`, `youtu.be`).
   - Dynamic Distraction Sweeper script injected to hide comments, sidebars, end screen popups, and promoter banners.

4. **Built-in Ad Blocking Engine:**
   - WebResource request interceptor blocking ad networks (`googleads`, `doubleclick`, `pagead2`, `ads.youtube.com`, `tpc.googlesyndication.com`).

5. **Display & Power Optimizations:**
   - Screen Wake Lock (`FLAG_KEEP_SCREEN_ON`) active on car display and phone screen.
   - Pre-configured Speed Dials (YouTube, Netflix, Prime Video, Twitch, Spotify, Google Maps).
   - Light and AMOLED True Black themes for car displays.
   - Global display scaling (40% to 200%).

6. **Clean Binary & Security Requirements:**
   - Zero telemetry / tracking SDKs (no UmamiTracker background network calls).
   - No `FreeDroidWarn` popup libraries or malware/PUP warning signatures.
   - Clean production release compilation via `./gradlew assembleRelease` generating `AABrowser-2.2.apk`.

7. **Package Structure:**
   - Package / Namespace: `com.kishorecheerala.aaybrowser`
   - Application ID: `com.kishorecheerala.aaybrowser`
