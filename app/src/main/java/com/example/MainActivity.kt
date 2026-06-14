package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Manage Window Keep Screen On Flag dynamically based on user settings
        lifecycleScope.launch {
            viewModel.isKeepScreenOn.collect { keepOn ->
                if (keepOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }

        setContent {
            val systemDarkMode = isSystemInDarkTheme()
            val themeMode = viewModel.themeMode.collectAsState().value
            val isDarkModeEnabled = when (themeMode) {
                BrowserViewModel.ThemeMode.LIGHT -> false
                BrowserViewModel.ThemeMode.DARK -> true
                BrowserViewModel.ThemeMode.AUTO_TIME -> systemDarkMode
            }

            MyApplicationTheme(darkTheme = isDarkModeEnabled) {
                BrowserApp(viewModel = viewModel)
            }
        }
    }
}
