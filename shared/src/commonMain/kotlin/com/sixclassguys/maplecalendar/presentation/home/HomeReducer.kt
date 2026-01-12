package com.sixclassguys.maplecalendar.presentation.home

class HomeReducer {

    fun reduce(currentState: HomeUiState, intent: HomeIntent): HomeUiState = when (intent) {
        is HomeIntent.LoadApiKey -> {
            currentState.copy(
                isLoading = true
            )
        }

        is HomeIntent.LoadApiKeyFailed -> {
            currentState.copy(
                isLoading = false,
                isAutoLoginFinished = true,
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
                characterBasic = intent.characterBasic
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

        is HomeIntent.Login -> {
            currentState.copy(isNavigateToLogin = true)
        }

        is HomeIntent.NavigationHandled -> {
            currentState.copy(isNavigateToLogin = false)
        }
    }
}