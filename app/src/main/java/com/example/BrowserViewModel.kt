package com.example

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Bookmark(val title: String, val url: String)

data class TabState(
    val id: Long = System.currentTimeMillis(),
    var url: String = "",
    var title: String = "New Tab",
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false,
    var isLoading: Boolean = false,
    var progress: Float = 0f
)

class BrowserViewModel : ViewModel() {
    private val _tabs = mutableStateListOf<TabState>(TabState())
    val tabs: List<TabState> get() = _tabs

    private val _currentTabIndex = MutableStateFlow(0)
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    private val _searchEngine = MutableStateFlow("https://duckduckgo.com/?q=")
    val searchEngine: StateFlow<String> = _searchEngine.asStateFlow()
    
    enum class ThemeMode {
        LIGHT, DARK, AUTO_TIME
    }

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO_TIME)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isAdBlockEnabled = MutableStateFlow(true)
    val isAdBlockEnabled: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()

    private val _isDesktopModeEnabled = MutableStateFlow(false)
    val isDesktopModeEnabled: StateFlow<Boolean> = _isDesktopModeEnabled.asStateFlow()

    private val _isJavaScriptEnabled = MutableStateFlow(true)
    val isJavaScriptEnabled: StateFlow<Boolean> = _isJavaScriptEnabled.asStateFlow()

    private val _isYouTubeModeEnabled = MutableStateFlow(false)
    val isYouTubeModeEnabled: StateFlow<Boolean> = _isYouTubeModeEnabled.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    val currentTab: TabState?
        get() = _tabs.getOrNull(_currentTabIndex.value)

    fun loadUrl(url: String) {
        val finalUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else if (url.contains(".") && !url.contains(" ")) {
            "https://$url"
        } else {
            "${_searchEngine.value}${url}"
        }
        
        currentTab?.let {
            val updated = it.copy(url = finalUrl)
            _tabs[_currentTabIndex.value] = updated
        }
    }

    fun onTabUrlChanged(url: String) {
         currentTab?.let {
            val updated = it.copy(url = url)
            _tabs[_currentTabIndex.value] = updated
        }
    }

    fun onTabTitleChanged(title: String) {
         currentTab?.let {
            val updated = it.copy(title = title)
            _tabs[_currentTabIndex.value] = updated
        }
    }
    
    fun onNavigationStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
         currentTab?.let {
            val updated = it.copy(canGoBack = canGoBack, canGoForward = canGoForward)
            _tabs[_currentTabIndex.value] = updated
        }
    }
    
    fun onLoadingStateChanged(isLoading: Boolean, progress: Float) {
        currentTab?.let {
            val updated = it.copy(isLoading = isLoading, progress = progress)
            _tabs[_currentTabIndex.value] = updated
        }
    }

    fun addTab() {
        _tabs.add(TabState())
        _currentTabIndex.value = _tabs.size - 1
    }

    fun closeTab(index: Int) {
        if (_tabs.size > 1) {
            _tabs.removeAt(index)
            if (_currentTabIndex.value >= index && _currentTabIndex.value > 0) {
                _currentTabIndex.value -= 1
            }
        }
    }

    fun selectTab(index: Int) {
        if (index in _tabs.indices) {
            _currentTabIndex.value = index
        }
    }
    
    fun setSearchEngine(engineUrl: String) {
        _searchEngine.value = engineUrl
    }
    
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleAdBlock() {
        _isAdBlockEnabled.value = !_isAdBlockEnabled.value
    }

    fun toggleDesktopMode() {
        _isDesktopModeEnabled.value = !_isDesktopModeEnabled.value
    }

    fun toggleJavaScript() {
        _isJavaScriptEnabled.value = !_isJavaScriptEnabled.value
    }

    fun toggleYouTubeMode() {
        _isYouTubeModeEnabled.value = !_isYouTubeModeEnabled.value
    }

    fun toggleBookmark(title: String, url: String) {
        if (url.isEmpty()) return
        val current = _bookmarks.value
        if (current.any { it.url == url }) {
            _bookmarks.value = current.filter { it.url != url }
        } else {
            _bookmarks.value = current + Bookmark(title.ifEmpty { url }, url)
        }
    }

    fun isBookmarked(url: String): Boolean {
        return _bookmarks.value.any { it.url == url }
    }
}
