package com.sixclassguys.maplecalendar.presentation.notification

class NotificationReducer {

    fun reduce(currentState: NotificationUiState, intent: NotificationIntent): NotificationUiState {
        return when (intent) {
            is NotificationIntent.InitNotification -> {
                currentState.copy(isLoading = true)
            }

            is NotificationIntent.RegisterFCMTokenSuccess -> {
                currentState.copy(
                    isLoading = false,
                    registrationState = intent.apiState
                )
            }

            is NotificationIntent.RegisterFCMTokenFail -> {
                currentState.copy(
                    isLoading = false,
                    registrationState = intent.apiState
                )
            }

            is NotificationIntent.ToggleNotification -> {
                currentState.copy(
                    isLoading = false,
                    isNotificationEnabled = intent.isEnabled
                )
            }
        }
    }
}