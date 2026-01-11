package com.sixclassguys.maplecalendar.presentation.login

class LoginReducer {

    fun reduce(currentState: LoginUiState, intent: LoginIntent): LoginUiState = when (intent) {
        is LoginIntent.ErrorMessageConsumed -> {
            currentState.copy(
                errorMessage = null
            )
        }

        is LoginIntent.UpdateApiKey -> {
            currentState.copy(
                isLoading = false,
                nexonApiKey = intent.apiKey
            )
        }

        is LoginIntent.ClickLogin -> {
            currentState.copy(
                isLoading = true
            )
        }

        is LoginIntent.SelectRepresentativeCharacter -> {
            val defaultWorld = intent.characters.keys.firstOrNull() ?: "스카니아"

            currentState.copy(
                isLoading = false,
                characters = intent.characters,
                selectedWorld = defaultWorld,
                navigateToSelection = true
            )
        }

        is LoginIntent.LoginSuccess -> {
            currentState.copy(
                isLoading = false,
                isLoginSuccess = true
            )
        }

        is LoginIntent.LoginFailed -> {
            currentState.copy(
                isLoading = false,
                isLoginSuccess = false,
                errorMessage = intent.message
            )
        }

        is LoginIntent.NavigationConsumed -> {
            currentState.copy(
                navigateToSelection = false     // 이동 신호만 끔 (데이터는 유지)
            )
        }

        is LoginIntent.ShowWorldSheet -> {
            currentState.copy(
                isWorldSheetOpen = intent.isShow
            )
        }

        is LoginIntent.SelectWorld -> {
            currentState.copy(
                isLoading = false,
                selectedWorld = intent.worldName,
                selectedCharacter = null, // 월드가 바뀌면 기존 선택된 캐릭터 해제
                isWorldSheetOpen = false // 선택 시 자동으로 닫음
            )
        }

        is LoginIntent.SelectCharacter -> {
            if (intent.character.ocid == currentState.selectedCharacter?.ocid) {
                currentState.copy(
                    selectedCharacter = null
                )
            } else {
                currentState.copy(
                    selectedCharacter = intent.character
                )
            }
        }

        is LoginIntent.SubmitRepresentativeCharacter -> {
            currentState.copy(
                isLoading = true
            )
        }

        is LoginIntent.SubmitRepresentativeCharacterFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is LoginIntent.SetOpenApiKey -> {
            currentState.copy(
                isLoading = true
            )
        }

        is LoginIntent.SetOpenApiKeyFailed -> {
            currentState.copy(
                isLoading = false,
                isLoginSuccess = false,
                errorMessage = intent.message
            )
        }
    }
}