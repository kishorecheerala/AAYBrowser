# AAYBrowser Agent Rules & Project Guidelines

## 📌 Project Identity
- **Project Name:** AAYBrowser (Android Auto Web & YouTube Browser)
- **Developer & Owner:** Kishore Cheerala (`com.kishorecheerala.aaybrowser`)
- **Core Objective:** THIS IS NOT A CLONE of the original AABrowser. It is Kishore Cheerala's customized, feature-packed driving-friendly browser for Android Auto.
- **Current Version:** v2.3 (Version Code 9)

## 🚀 Mandatory Custom Features & Requirements
Whenever updating or refactoring code in this repository, you MUST preserve and enforce the following custom capabilities:

1. **Driving Playback Support (No Motion Lockouts):**
   - In `res/xml/automotive_app_desc.xml`, MUST use `<uses name="template" />`, `<uses name="navigation" />`, and `<uses name="media" />`.
   - In `AndroidManifest.xml`, use `android:appCategory="game"` on `<application>` for Android Auto launcher indexing while maintaining `distractionOptimized="true"` on all activity elements to bypass driving motion lockouts.
   - Keep `minCarApiLevel="1"` and `<uses-permission android:name="androidx.car.app.MAP_TEMPLATES" />` for full launcher compatibility.

2. **Continuous Focus & Page Visibility Playback:**
   - Overridden Page Visibility API (`document.hidden = false`, `document.visibilityState = 'visible'`) injected into WebViews.
   - Do NOT invoke `webView?.onPause()` in `MainActivity.onPause()` to prevent focus shifts from pausing video/audio playback while driving.

3. **Dedicated YouTube Mode (Driving Friendly):**
   - Automatic iPad / Mobile Safari User-Agent override for YouTube domains (`youtube.com`, `youtu.be`).
   - Dynamic Distraction Sweeper script injected to hide comments, sidebars, end screen popups, and promoter banners.

4. **Built-in Ad Blocking Engine & Settings Control:**
   - WebResource request interceptor blocking ad networks (`googleads`, `doubleclick`, `pagead2`, `ads.youtube.com`, `tpc.googlesyndication.com`).
   - Settings UI toggle switch for Ad Blocker controlled via `BrowserPreferences.isAdBlockerEnabled(context)`.

5. **Display, Settings & Power Optimizations:**
   - Screen Wake Lock (`FLAG_KEEP_SCREEN_ON`) active on car display and phone screen.
   - Pre-configured Speed Dials (YouTube, Google, Netflix, Prime Video, Twitch, Google Maps).
   - Light and AMOLED True Black themes for car displays.
   - Global display scaling (40% to 200%).
   - Clean Start Page & Settings UI: Absolutely zero sponsored content, QR popups, or external developer links. Settings includes a clean **About AAYBrowser** section credited to Kishore Cheerala (`https://github.com/kishorecheerala/AAYBrowser`).

6. **Clean Binary & Security Requirements:**
   - Zero telemetry / tracking SDKs (no UmamiTracker background network calls).
   - No `FreeDroidWarn` popup libraries or malware/PUP warning signatures.
   - Package / Namespace and Application ID: `com.kishorecheerala.aaybrowser` (prevents Bitdefender / Play Protect "unofficial copy app" warnings).
   - Release compilation via `./gradlew assembleRelease` generating `AAYBrowser-2.3.apk`.
