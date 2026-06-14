package com.example

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserApp(viewModel: BrowserViewModel) {
    val currentTabIndex by viewModel.currentTabIndex.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isSystemDarkMode = androidx.compose.foundation.isSystemInDarkTheme()
    val isDarkMode = when (themeMode) {
        BrowserViewModel.ThemeMode.LIGHT -> false
        BrowserViewModel.ThemeMode.DARK -> true
        BrowserViewModel.ThemeMode.AUTO_TIME -> isSystemDarkMode
    }
    val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsState()
    val isDesktopMode by viewModel.isDesktopModeEnabled.collectAsState()
    val isJavaScriptEnabled by viewModel.isJavaScriptEnabled.collectAsState()
    val isYouTubeModeEnabled by viewModel.isYouTubeModeEnabled.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val tabs = viewModel.tabs
    var showTabsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var isImmersiveMode by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val currentTab = tabs.getOrNull(currentTabIndex) ?: return

    Scaffold(
        topBar = {
            if (!isImmersiveMode) {
                BrowserTopBar(
                    tabState = currentTab,
                    isBookmarked = viewModel.isBookmarked(currentTab.url),
                    onUrlSubmit = { url ->
                        viewModel.loadUrl(url)
                    },
                    onGoBack = { webViewRef?.goBack() },
                    onGoForward = { webViewRef?.goForward() },
                    onRefresh = { webViewRef?.reload() },
                    onToggleBookmark = { viewModel.toggleBookmark(currentTab.title, currentTab.url) },
                    onToggleImmersive = { isImmersiveMode = !isImmersiveMode }
                )
            }
        },
        bottomBar = {
            if (!isImmersiveMode) {
                BrowserBottomBar(
                    tabCount = tabs.size,
                    onShowTabs = { showTabsDialog = true },
                    onShowSettings = { showSettingsDialog = true },
                    onAddTab = { viewModel.addTab() },
                    onHome = { viewModel.loadUrl("") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(if (isImmersiveMode) PaddingValues(0.dp) else padding)) {
            if (currentTab.url.isEmpty()) {
                StartPage(
                    bookmarks = bookmarks,
                    onSearch = { query -> viewModel.loadUrl(query) },
                    onQuickLink = { url -> viewModel.loadUrl(url) }
                )
            } else {
                ConfiguredWebView(
                    url = currentTab.url,
                    isDarkMode = isDarkMode,
                    isYouTubeModeEnabled = isYouTubeModeEnabled,
                    isAdBlockEnabled = isAdBlockEnabled,
                    isDesktopMode = isDesktopMode,
                    isJavaScriptEnabled = isJavaScriptEnabled,
                    onUrlChange = { viewModel.onTabUrlChanged(it) },
                    onTitleChange = { viewModel.onTabTitleChanged(it) },
                    onNavigationStateChange = { back, forward ->
                        viewModel.onNavigationStateChanged(back, forward)
                    },
                    onLoadingStateChange = { loading, progress ->
                        viewModel.onLoadingStateChanged(loading, progress)
                    },
                    webViewRef = { webViewRef = it }
                )
                
                if (currentTab.isLoading) {
                    LinearProgressIndicator(
                        progress = { currentTab.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }

                if (isImmersiveMode) {
                    FloatingActionButton(
                        onClick = { isImmersiveMode = false },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(Icons.Filled.FullscreenExit, contentDescription = "Exit Fullscreen")
                    }
                }
            }
        }
    }

    if (showTabsDialog) {
        TabsDialog(
            tabs = tabs,
            currentTabIndex = currentTabIndex,
            onDismiss = { showTabsDialog = false },
            onSelectTab = { 
                viewModel.selectTab(it)
                showTabsDialog = false 
            },
            onCloseTab = { viewModel.closeTab(it) },
            onAddTab = { viewModel.addTab() }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            themeMode = themeMode,
            isAdBlockEnabled = isAdBlockEnabled,
            isDesktopMode = isDesktopMode,
            isJavaScriptEnabled = isJavaScriptEnabled,
            isYouTubeModeEnabled = isYouTubeModeEnabled,
            searchEngine = viewModel.searchEngine.collectAsState().value,
            onDismiss = { showSettingsDialog = false },
            onSetThemeMode = { viewModel.setThemeMode(it) },
            onToggleAdBlock = { viewModel.toggleAdBlock() },
            onToggleDesktopMode = { viewModel.toggleDesktopMode() },
            onToggleJavaScript = { viewModel.toggleJavaScript() },
            onToggleYouTubeMode = { viewModel.toggleYouTubeMode() },
            onSelectEngine = { viewModel.setSearchEngine(it) }
        )
    }
}

@Composable
fun BrowserTopBar(
    tabState: TabState,
    isBookmarked: Boolean,
    onUrlSubmit: (String) -> Unit,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onRefresh: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleImmersive: () -> Unit
) {
    var inputText by remember(tabState.url) { mutableStateOf(tabState.url) }
    var isEditing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onGoBack,
                enabled = tabState.canGoBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
            }
            
            IconButton(
                onClick = onGoForward,
                enabled = tabState.canGoForward,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go Forward")
            }

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable { 
                        isEditing = true
                        focusRequester.requestFocus()
                    },
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                            .onFocusChanged { if (!it.isFocused) isEditing = false },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                onUrlSubmit(inputText)
                                focusManager.clearFocus()
                                isEditing = false
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tabState.url.ifEmpty { "Search or type web address" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (tabState.url.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = onRefresh, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
            }

            IconButton(onClick = onToggleBookmark, modifier = Modifier.size(40.dp)) {
                Icon(if (isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = "Bookmark", tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onToggleImmersive, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Filled.Fullscreen, contentDescription = "Fullscreen")
            }
        }
    }
}

@Composable
fun BrowserBottomBar(
    tabCount: Int,
    onShowTabs: () -> Unit,
    onShowSettings: () -> Unit,
    onAddTab: () -> Unit,
    onHome: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(80.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = onHome)
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Home", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onShowTabs).padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tabs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onAddTab).padding(8.dp)) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "New Tab",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("New", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onShowSettings).padding(8.dp)) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("Settings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StartPage(
    bookmarks: List<Bookmark>,
    onSearch: (String) -> Unit,
    onQuickLink: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hero Section using Geometric Balance card style
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("Search the web...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearch(query) }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Links Section using Geometric Balance layout structure
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK LINKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickLinkItem("YouTube", Icons.Filled.PlayArrow) { onQuickLink("https://www.youtube.com") }
                    QuickLinkItem("Wiki", Icons.AutoMirrored.Filled.MenuBook) { onQuickLink("https://en.wikipedia.org") }
                    QuickLinkItem("Google", Icons.Filled.Public) { onQuickLink("https://www.google.com") }
                }
            }
        }
        
        if (bookmarks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOOKMARKS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        bookmarks.forEach { bookmark ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onQuickLink(bookmark.url) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(bookmark.title, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(bookmark.url, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickLinkItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
    }
}

@Composable
fun TabsDialog(
    tabs: List<TabState>,
    currentTabIndex: Int,
    onDismiss: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onCloseTab: (Int) -> Unit,
    onAddTab: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tabs", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    IconButton(onClick = onAddTab, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "New Tab", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (index == currentTabIndex) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (index == currentTabIndex) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTab(index) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = if (index == currentTabIndex) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = tab.url.ifEmpty { "Start Page" },
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onCloseTab(index) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Filled.Close, contentDescription = "Close Tab", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    themeMode: BrowserViewModel.ThemeMode,
    isAdBlockEnabled: Boolean,
    isDesktopMode: Boolean,
    isJavaScriptEnabled: Boolean,
    isYouTubeModeEnabled: Boolean,
    searchEngine: String,
    onDismiss: () -> Unit,
    onSetThemeMode: (BrowserViewModel.ThemeMode) -> Unit,
    onToggleAdBlock: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onToggleJavaScript: () -> Unit,
    onToggleYouTubeMode: () -> Unit,
    onSelectEngine: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Settings", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
                
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Column(modifier = Modifier.padding(24.dp)) {
                    Text("THEME", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val themes = listOf(
                        "Light" to BrowserViewModel.ThemeMode.LIGHT,
                        "Dark" to BrowserViewModel.ThemeMode.DARK,
                        "Auto" to BrowserViewModel.ThemeMode.AUTO_TIME
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        themes.forEach { (name, mode) ->
                            Surface(
                                modifier = Modifier.weight(1f).clickable { onSetThemeMode(mode) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (themeMode == mode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode == mode) MaterialTheme.colorScheme.primary else Color.Transparent)
                            ) {
                                Text(
                                    text = name,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (themeMode == mode) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium,
                                    color = if (themeMode == mode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Desktop Mode", style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        Switch(checked = isDesktopMode, onCheckedChange = { onToggleDesktopMode() })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable JavaScript", style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        Switch(checked = isJavaScriptEnabled, onCheckedChange = { onToggleJavaScript() })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ad-Block (Simple)", style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        Switch(checked = isAdBlockEnabled, onCheckedChange = { onToggleAdBlock() })
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isYouTubeModeEnabled) Color(0xFFFFE5E5) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isYouTubeModeEnabled) Color(0xFFFF0000) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "YouTube Mode",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    text = "Simplified layout for distraction-free driving",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(checked = isYouTubeModeEnabled, onCheckedChange = { onToggleYouTubeMode() })
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            android.webkit.WebStorage.getInstance().deleteAllData()
                            android.webkit.CookieManager.getInstance().removeAllCookies(null)
                            android.webkit.WebView(context).clearCache(true)
                            android.widget.Toast.makeText(context, "Browsing Data Cleared", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Browsing Data")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("SEARCH ENGINE", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val engines = listOf(
                        "DuckDuckGo" to "https://duckduckgo.com/?q=",
                        "Google" to "https://www.google.com/search?q=",
                        "Bing" to "https://www.bing.com/search?q="
                    )
                    
                    engines.forEach { (name, url) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = if (searchEngine == url) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (searchEngine == url) MaterialTheme.colorScheme.primary.copy(alpha=0.5f) else MaterialTheme.colorScheme.outline),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectEngine(url) }
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = searchEngine == url,
                                    onClick = { onSelectEngine(url) },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = if (searchEngine == url) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }
    }
}
