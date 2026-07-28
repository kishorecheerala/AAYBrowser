package com.kishorecheerala.aaybrowser.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.kishorecheerala.aaybrowser.data.BrowserPreferences
import com.kishorecheerala.aaybrowser.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) {
            super.attachBaseContext(null)
            return
        }
        super.attachBaseContext(BrowserPreferences.createScaledContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(BrowserPreferences.getThemeMode(this).nightMode)
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finish() }
        binding.settingsContentRoot.addView(
            SettingsViews.createSettingsActivityView(this)
        )
    }
}
