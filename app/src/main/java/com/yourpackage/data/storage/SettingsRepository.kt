package com.yourpackage.data.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.yourpackage.settings.PrivacySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepository(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "privacy_settings",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<PrivacySettings> = _settingsFlow

    fun saveSettings(settings: PrivacySettings) {
        val json = gson.toJson(settings)
        sharedPreferences.edit().putString(KEY_PRIVACY_SETTINGS, json).apply()
        _settingsFlow.value = settings
    }

    private fun loadSettings(): PrivacySettings {
        val json = sharedPreferences.getString(KEY_PRIVACY_SETTINGS, null)
        return if (json != null) {
            gson.fromJson(json, PrivacySettings::class.java)
        } else {
            PrivacySettings() // Default settings
        }
    }

    companion object {
        private const val KEY_PRIVACY_SETTINGS = "privacy_settings"
    }
}
