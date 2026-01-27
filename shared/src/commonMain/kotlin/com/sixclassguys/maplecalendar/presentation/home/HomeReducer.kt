package com.sixclassguys.maplecalendar.presentation.home

import io.github.aakira.napier.Napier

class HomeReducer {

    fun reduce(currentState: HomeUiState, intent: HomeIntent): HomeUiState = when (intent) {
        is HomeIntent.AutoLogin -> {
            currentState.copy(
                isLoading = true
            )
        }

        is HomeIntent.AutoLoginSuccess -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                isLoginSuccess = true,
                member = intent.member
            )
        }

        is HomeIntent.EmptyAccessToken -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                isLoginSuccess = false
            )
        }

        is HomeIntent.ReissueJwtToken -> {
            currentState.copy(
                isLoading = true
            )
        }

        is HomeIntent.AutoLoginFailed -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                isLoginSuccess = false,
                errorMessage = intent.message
            )
        }

        is HomeIntent.LoginSuccess -> {
            currentState.copy(
                isLoading = false,
                isLoginSuccess = intent.isLoginSuccess,
                member = intent.member,
                errorMessage = null
            )
        }

        is HomeIntent.LoadApiKey -> {
            currentState.copy(
                isLoading = true
            )
        }

        is HomeIntent.LoadEmptyApiKey -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                isLoginSuccess = false
            )
        }

        is HomeIntent.LoadApiKeyFailed -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                isLoginSuccess = false,
                errorMessage = intent.message
            )
        }

        is HomeIntent.LoadCharacterBasic -> {
            currentState.copy(
                isLoading = true,
                nexonApiKey = intent.apiKey,
            )
        }

        is HomeIntent.LoadCharacterBasicSuccess -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                isLoginSuccess = true,
                characterBasic = intent.characterBasic,
                characterDojangRanking = intent.characterDojangRanking,
                characterOverallRanking = intent.characterOverallRanking,
                characterServerRanking = intent.characterServerRanking,
                characterUnion = intent.characterUnion,
                isGlobalAlarmEnabled = intent.isGlobalAlarmEnabled
            )
        }

        is HomeIntent.LoadCharacterBasicFailed -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
                errorMessage = intent.message
            )
        }

        is HomeIntent.LoadEvents -> {
            currentState.copy(
                isLoading = true,
            )
        }

        is HomeIntent.LoadEventsSuccess -> {
            currentState.copy(
                isLoading = false,
                events = intent.events
            )
        }

        is HomeIntent.LoadEventsFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is HomeIntent.SyncNotificationWithSystem -> {
            currentState.copy(

            )
        }

        is HomeIntent.ToggleGlobalAlarmStatus -> {
            currentState.copy(
                isLoading = true
            )
        }

        is HomeIntent.ToggleGlobalAlarmStatusSuccess -> {
            Napier.d("알림 ON: ${intent.isEnabled}")
            currentState.copy(
                isLoading = false,
                isGlobalAlarmEnabled = intent.isEnabled
            )
        }

        is HomeIntent.ToggleGlobalAlarmStatusFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is HomeIntent.Login -> {
            currentState.copy(isNavigateToLogin = true)
        }

        is HomeIntent.Logout -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = false,
                isLoginSuccess = false,
                nexonApiKey = null,
                characterBasic = null,
                characterDojangRanking = null,
                characterOverallRanking = null,
                characterServerRanking = null,
                characterUnion = null,
                isGlobalAlarmEnabled = false
            )
        }

        is HomeIntent.NavigationHandled -> {
            currentState.copy(isNavigateToLogin = false)
        }
    }
}