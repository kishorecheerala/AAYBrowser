package com.example

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

private const val YOUTUBE_CSS_INJECTION = """
    (function() {
        var parent = document.getElementsByTagName('head').item(0);
        if (!parent) return;
        var style = document.getElementById('driving-mode-style');
        if (!style) {
            style = document.createElement('style');
            style.id = 'driving-mode-style';
            style.type = 'text/css';
            style.innerHTML = '#secondary, ytd-watch-next-secondary-results-renderer, ytm-item-section-renderer[section-identifier="related-items"], ytm-related-videos, #related, .related-list, #comments, ytd-comments, ytm-comments-entry-point-header-renderer, ytm-item-section-renderer[section-identifier="comments"], .comment-section, ytm-enrichment-section-target-renderer, .ytm-promoted-sparkles-web-renderer, .yt-spec-button-shape-next--call-to-action, ytd-mealbar-promo-renderer { display: none !important; }';
            parent.appendChild(style);
        }
    })()
"""

private const val YOUTUBE_CSS_REMOVAL = """
    (function() {
        var style = document.getElementById('driving-mode-style');
        if (style) {
            style.parentNode.removeChild(style);
        }
    })()
"""

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ConfiguredWebView(
    url: String,
    isDarkMode: Boolean,
    isYouTubeModeEnabled: Boolean,
    isAdBlockEnabled: Boolean,
    isDesktopMode: Boolean,
    isJavaScriptEnabled: Boolean,
    modifier: Modifier = Modifier,
    onUrlChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onNavigationStateChange: (Boolean, Boolean) -> Unit,
    onLoadingStateChange: (Boolean, Float) -> Unit,
    webViewRef: (WebView?) -> Unit
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            val webViewContext = context
            WebView(webViewContext).apply {
                val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                val mobileUserAgent = settings.userAgentString
                this.tag = mobileUserAgent
                
                val isYouTubeUrl = url.contains("youtube.com") || url.contains("youtu.be")
                val ytUserAgent = "Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1"

                settings.userAgentString = when {
                    isYouTubeModeEnabled && isYouTubeUrl -> ytUserAgent
                    isDesktopMode -> desktopUserAgent
                    else -> mobileUserAgent
                }
                settings.javaScriptEnabled = isJavaScriptEnabled
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    @Suppress("DEPRECATION")
                    WebSettingsCompat.setForceDark(
                        settings,
                        if (isDarkMode) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                    )
                }
                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, isDarkMode)
                }

                webViewClient = object : WebViewClient() {
                    val adServers = setOf(
                        "googleads.g.doubleclick.net",
                        "adclick.g.doubleclick.net",
                        "pagead2.googlesyndication.com",
                        "www.googleadservices.com",
                        "pubads.g.doubleclick.net",
                        "securepubads.g.doubleclick.net",
                        "ad.doubleclick.net",
                        "ads.youtube.com",
                        "s0.2mdn.net",
                        "tpc.googlesyndication.com"
                    )

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if (isAdBlockEnabled) {
                            val host = Uri.parse(request?.url?.toString() ?: "").host ?: ""
                            if (adServers.any { host.contains(it) }) {
                                return WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
                            }
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onUrlChange(url ?: "")
                        onLoadingStateChange(true, 0f)
                        view?.let {
                            onNavigationStateChange(it.canGoBack(), it.canGoForward())
                        }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onTitleChange(view?.title ?: "")
                        onLoadingStateChange(false, 1f)
                        view?.let { webView ->
                            onNavigationStateChange(webView.canGoBack(), webView.canGoForward())
                            val currentUrl = url ?: webView.url ?: ""
                            val isYouTubeUrl = currentUrl.contains("youtube.com") || currentUrl.contains("youtu.be")
                            if (isYouTubeModeEnabled && isYouTubeUrl) {
                                webView.evaluateJavascript(YOUTUBE_CSS_INJECTION, null)
                            }
                        }
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onLoadingStateChange(newProgress < 100, newProgress / 100f)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        onTitleChange(title ?: "")
                    }

                    override fun onPermissionRequest(request: android.webkit.PermissionRequest?) {
                        request?.grant(request?.resources)
                    }

                    override fun onGeolocationPermissionsShowPrompt(
                        origin: String?,
                        callback: android.webkit.GeolocationPermissions.Callback?
                    ) {
                        callback?.invoke(origin, true, false)
                    }
                }
                
                webViewRef(this)
                if (url.isNotEmpty()) {
                    loadUrl(url)
                }
            }
        },
        update = { webView ->
            val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            val currentUrl = webView.url ?: url
            val isYouTubeUrl = currentUrl.contains("youtube.com") || currentUrl.contains("youtu.be")
            val ytUserAgent = "Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1"

            // Use existing user agent if it evaluates, otherwise fallback
            val desiredUserAgent = when {
                isYouTubeModeEnabled && isYouTubeUrl -> ytUserAgent
                isDesktopMode -> desktopUserAgent
                else -> webView.tag as? String ?: "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
            }
            
            if (webView.settings.userAgentString != desiredUserAgent) {
                webView.settings.userAgentString = desiredUserAgent
                webView.reload()
            }

            if (webView.settings.javaScriptEnabled != isJavaScriptEnabled) {
                webView.settings.javaScriptEnabled = isJavaScriptEnabled
                webView.reload()
            }

            if (isYouTubeModeEnabled && isYouTubeUrl) {
                webView.evaluateJavascript(YOUTUBE_CSS_INJECTION, null)
            } else if (isYouTubeUrl) {
                webView.evaluateJavascript(YOUTUBE_CSS_REMOVAL, null)
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                @Suppress("DEPRECATION")
                WebSettingsCompat.setForceDark(
                    webView.settings,
                    if (isDarkMode) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                )
            }
            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.settings, isDarkMode)
            }

            if (url.isNotEmpty() && webView.url != url) {
                 // Check if it's practically the same url to avoid infinite loops
                 val strippedInternal = webView.url?.removeSuffix("/")
                 val strippedExternal = url.removeSuffix("/")
                 if (strippedInternal != strippedExternal) {
                     webView.loadUrl(url)
                 }
            }
        }
    )
}
