package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
