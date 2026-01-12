package com.sixclassguys.maplecalendar.presentation.login

import com.sixclassguys.maplecalendar.domain.model.AccountCharacter

data class LoginUiState(
    val isLoading: Boolean = false,
    val nexonApiKey: String = "",
    val isLoginSuccess: Boolean = false,
    val characters: Map<String, List<AccountCharacter>> = emptyMap(),
    val characterImages: Map<String, String?> = emptyMap(),
    val isWorldSheetOpen: Boolean = false, // BottomSheet 열림 상태
    val selectedWorld: String = "",
    val selectedCharacter: AccountCharacter? = null,
    val navigateToSelection: Boolean = false,
    val errorMessage: String? = null
)