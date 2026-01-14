package com.sixclassguys.maplecalendar.presentation.setting

sealed class SettingIntent {

    data object FetchNexonOpenApiKey : SettingIntent()

    data class FetchNexonOpenApiKeySuccess(val key: String) : SettingIntent()

    data class FetchNexonOpenApiKeyFailed(val message: String) : SettingIntent()

    data object FetchGlobalAlarmStatus : SettingIntent()

    data class FetchGlobalAlarmStatusSuccess(val isEnabled: Boolean) : SettingIntent()

    data class FetchGlobalAlarmStatusFailed(val message: String) : SettingIntent()

    data object FetchFcmToken : SettingIntent()

    data class FetchFcmTokenSuccess(val fcmToken: String?) : SettingIntent()

    data class FetchFcmTokenFailed(val message: String) : SettingIntent()

    data object ToggleGlobalAlarmStatus : SettingIntent()

    data class ToggleGlobalAlarmStatusSuccess(val isEnabled: Boolean) : SettingIntent()

    data class ToggleGlobalAlarmStatusFailed(val message: String) : SettingIntent()

    data object Logout : SettingIntent()

    data object LogoutSuccess : SettingIntent()

    data class LogoutFailed(val message: String) : SettingIntent()
}