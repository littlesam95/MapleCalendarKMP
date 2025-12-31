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

    // --- 캐릭터 정보 (OCID) 관련 ---
    val characterOcid: Flow<String?> = dataStore.data.map { it[KEY_CHARACTER_OCID] }
    suspend fun saveCharacterOcid(ocid: String) = dataStore.edit { it[KEY_CHARACTER_OCID] = ocid }

    // --- 다크모드 관련 ---
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[KEY_IS_DARK_MODE] ?: false }
    suspend fun setDarkMode(enabled: Boolean) = dataStore.edit { it[KEY_IS_DARK_MODE] = enabled }

    // 필요 시 모든 설정을 한 번에 초기화 (로그아웃 등)
    suspend fun clearAll() = dataStore.edit { it.clear() }

    companion object {

        private val KEY_LAST_SENT_FCM_TOKEN = stringPreferencesKey("last_sent_fcm_token")
        private val KEY_CHARACTER_OCID = stringPreferencesKey("character_ocid")
        private val KEY_IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }
}