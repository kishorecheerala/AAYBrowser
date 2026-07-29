# Changelog

All notable changes to **AAYBrowser** will be documented in this file.

## [v2.4] - 2026-07-29

### 🚀 Version 2.4 Driving Availability & Toast Error Fixes
- **Full Driving Mode Availability**: Extended `distractionOptimized="true"` across all activity components and expanded Android Auto intent categories (`CATEGORY_PROJECTION`, `CATEGORY_NAVIGATION`, `CATEGORY_GAME`, `CAR_MODE`, `CAR_DOCK`) to ensure `AAYBrowser` runs smoothly while driving without motion lockout overlays.
- **Silenced Subresource Toast Errors (`net::ERR_NAME_NOT_RESOLVED`)**: Fixed subresource network/DNS errors triggering annoying Toast error popups during video playback in parking and driving modes.
- **Continuous Driving Focus Playback**: Preserved fullscreen video state when Android Auto focus state changes occur while driving, and injected Page Visibility API overrides early on page start.

## [v2.3] - 2026-07-28

### 🚀 Version 2.3 Major Release
- **Start Page & UI Cleanup**: Renamed start page headline to **AAYBrowser**, removed legacy sponsor cards & QR codes, and pre-loaded custom speed dial shortcuts for YouTube, Netflix, Prime Video, Twitch, Spotify, and Google Maps.
- **Android Auto Dock Rail Pinning**: Configured `CATEGORY_MEDIA` and `automotive_app_desc.xml` media & navigation tags to pin `AAYBrowser` to the Android Auto bottom dock rail / status bar.
- **Unminified Full Build Option**: Provided full uncompressed binary compilation without R8 code stripping.
- **Package Refactoring**: Migrated source package structure to `com.kishorecheerala.aaybrowser`.

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
