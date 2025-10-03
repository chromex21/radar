package com.yourpackage.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.data.storage.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application.applicationContext)

    val settings: StateFlow<PrivacySettings> = settingsRepository.settingsFlow

    fun updateSettings(newSettings: PrivacySettings) {
        viewModelScope.launch {
            settingsRepository.saveSettings(newSettings)
        }
    }
}
