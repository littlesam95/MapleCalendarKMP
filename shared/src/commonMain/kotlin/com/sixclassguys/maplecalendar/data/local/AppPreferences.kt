package com.sixclassguys.maplecalendar.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(
    private val dataStore: DataStore<Preferences>
) {

    // --- FCM 토큰 관련 ---
    val lastSentToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_LAST_SENT_FCM_TOKEN] }
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_SENT_FCM_TOKEN] = token
        }
    }
    suspend fun deleteToken() = dataStore.edit { it.remove(KEY_LAST_SENT_FCM_TOKEN) }

    // --- 로그인 관련 ---
    val openApiKey: Flow<String?> = dataStore.data.map { it[OPEN_API_KEY] }
    suspend fun saveOpenApiKey(key: String) = dataStore.edit { it[OPEN_API_KEY] = key }

    val characterOcid: Flow<String?> = dataStore.data.map { it[KEY_CHARACTER_OCID] }
    suspend fun saveCharacterOcid(ocid: String) = dataStore.edit { it[KEY_CHARACTER_OCID] = ocid }

    // --- 다크모드 관련 ---
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[KEY_IS_DARK_MODE] ?: false }
    suspend fun setDarkMode(enabled: Boolean) = dataStore.edit { it[KEY_IS_DARK_MODE] = enabled }

    // --- 알림 설정 관련 ---
    val isNotificationMode: Flow<Boolean> =
        dataStore.data.map { it[KEY_IS_NOTIFICATION_MODE] ?: false }

    suspend fun setNotificationMode(enabled: Boolean) =
        dataStore.edit { it[KEY_IS_NOTIFICATION_MODE] = enabled }

    // 필요 시 모든 설정을 한 번에 초기화 (로그아웃 등)
    suspend fun clearAll() = dataStore.edit { preferences ->
        preferences.remove(KEY_LAST_SENT_FCM_TOKEN)
        preferences.remove(OPEN_API_KEY)
        preferences.remove(KEY_CHARACTER_OCID)
        preferences.remove(KEY_IS_NOTIFICATION_MODE)
    }

    companion object {

        private val KEY_LAST_SENT_FCM_TOKEN = stringPreferencesKey("last_sent_fcm_token")
        private val OPEN_API_KEY = stringPreferencesKey("open_api_key")
        private val KEY_CHARACTER_OCID = stringPreferencesKey("character_ocid")
        private val KEY_IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val KEY_IS_NOTIFICATION_MODE = booleanPreferencesKey("is_notification_mode")
    }
}