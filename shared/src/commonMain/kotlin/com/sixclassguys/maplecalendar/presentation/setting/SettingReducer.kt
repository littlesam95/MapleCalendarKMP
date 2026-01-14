package com.sixclassguys.maplecalendar.presentation.setting

class SettingReducer {

    fun reduce(currentState: SettingUiState, intent: SettingIntent): SettingUiState = when (intent) {
        is SettingIntent.FetchNexonOpenApiKey -> {
            currentState.copy(
                isLoading = true
            )
        }

        is SettingIntent.FetchNexonOpenApiKeySuccess -> {
            currentState.copy(
                isLoading = false,
                nexonApiKey = intent.key
            )
        }

        is SettingIntent.FetchNexonOpenApiKeyFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is SettingIntent.FetchGlobalAlarmStatus -> {
            currentState.copy(
                isLoading = true
            )
        }

        is SettingIntent.FetchGlobalAlarmStatusSuccess -> {
            currentState.copy(
                isLoading = false,
                isGlobalAlarmEnabled = intent.isEnabled
            )
        }

        is SettingIntent.FetchGlobalAlarmStatusFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is SettingIntent.FetchFcmToken -> {
            currentState.copy(
                isLoading = true
            )
        }

        is SettingIntent.FetchFcmTokenSuccess -> {
            currentState.copy(
                isLoading = false,
                fcmToken = intent.fcmToken
            )
        }

        is SettingIntent.FetchFcmTokenFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is SettingIntent.ToggleGlobalAlarmStatus -> {
            currentState.copy(
                isLoading = true
            )
        }

        is SettingIntent.ToggleGlobalAlarmStatusSuccess -> {
            currentState.copy(
                isLoading = false,
                isGlobalAlarmEnabled = intent.isEnabled
            )
        }

        is SettingIntent.ToggleGlobalAlarmStatusFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is SettingIntent.Logout -> {
            currentState.copy(
                isLoading = true
            )
        }

        is SettingIntent.LogoutSuccess -> {
            currentState.copy(
                isLoading = false,
                nexonApiKey = null,
                fcmToken = null,
                isGlobalAlarmEnabled = false
            )
        }

        is SettingIntent.LogoutFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }
    }
}