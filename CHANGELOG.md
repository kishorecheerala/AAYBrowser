# Changelog

All notable changes to **AAYBrowser** will be documented in this file.

## [v2.2] - 2026-07-28

### 🚗 Driving Playback & Safety Lock Bypasses
- **Removed Motion Lockouts**: Updated `automotive_app_desc.xml` to use `<uses name="template" />` and `<uses name="navigation" />` instead of `media` and removed `appCategory="game"`, eliminating the system *"Not available while driving"* restriction overlay on Android Auto.
- **Continuous Focus Playback**: Injected Page Visibility API overrides (`document.hidden = false`, `document.visibilityState = 'visible'`) and prevented activity `onPause()` from halting active WebViews, allowing uninterrupted video and audio playback while driving.
- **PNG Asset Fix**: Fixed AAPT PNG signature errors during release resource compilation.
- **Production Release Build**: Standardized production release compilation via `./gradlew assembleRelease` generating `AABrowser-2.2.apk`.

### 🔴 YouTube Enhancements (Driving Friendly)
- **Custom YouTube User-Agent**: Configured automatic iPad / Mobile Safari User-Agent override for YouTube (`youtube.com`, `youtu.be`) for optimal web layout.
- **Dynamic Distraction Sweeper**: Added real-time CSS script injection sweeping away YouTube comments, related video sidebars, end screen popups, and promoter banners for distraction-free viewing.

### 🛑 Built-in Ad-Block Engine
- Added WebResource request interceptor blocking major ad networks (`googleads`, `doubleclick`, `pagead2`, `ads.youtube.com`, `tpc.googlesyndication.com`).

### 💡 Car Display Screen Wake Lock
- Added `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` to prevent phone and car displays from dimming or sleeping during video playback.

### 🚀 Pre-configured Speed Dials
- Pre-loaded start page shortcuts for YouTube, Netflix, Prime Video, Twitch, Spotify, and Google Maps.

### 🎯 Automotive UI & Android Auto Stability
- Native `Presentation` & XML layout engine for Android Auto projected car head units, resolving the endless spinning loading screen.
- Fullscreen DRM video playback support (Widevine L3).
- Multi-tab management with session recovery on launch.
- Light and AMOLED Black themes for car displays.
- Global display scaling (40% to 200%).
