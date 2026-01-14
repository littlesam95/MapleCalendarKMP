package com.sixclassguys.maplecalendar.presentation.home

import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.MapleEvent

data class HomeUiState(
    val isLoading: Boolean = false,
    val isAutoLoginFinished: Boolean = false,
    val nexonApiKey: String? = null,
    val characterBasic: CharacterBasic? = null,
    val isGlobalAlarmEnabled: Boolean = false,
    val events: List<MapleEvent> = emptyList(),
    val isNavigateToLogin: Boolean = false,
    val errorMessage: String? = null
)