package com.example.devicemonitor.data


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.devicemonitor.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "app_settings"
)

class SettingsRepository(
    private val context: Context
) {
    private object Keys {
        val SERVER_ADDRESS = stringPreferencesKey("server_address")
        val SERVER_PORT = stringPreferencesKey("server_port")
        val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            serverAddress = preferences[Keys.SERVER_ADDRESS] ?: "10.0.2.2",
            serverPort = preferences[Keys.SERVER_PORT] ?: "8080",
            autoRefresh = preferences[Keys.AUTO_REFRESH] ?: false
        )
    }

    suspend fun saveSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SERVER_ADDRESS] = settings.serverAddress
            preferences[Keys.SERVER_PORT] = settings.serverPort
            preferences[Keys.AUTO_REFRESH] = settings.autoRefresh
        }
    }
}