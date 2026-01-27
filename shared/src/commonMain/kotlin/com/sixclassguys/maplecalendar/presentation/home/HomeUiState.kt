package com.sixclassguys.maplecalendar.presentation.home

import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.CharacterDojangRanking
import com.sixclassguys.maplecalendar.domain.model.CharacterRanking
import com.sixclassguys.maplecalendar.domain.model.CharacterUnion
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.domain.model.Member

data class HomeUiState(
    val isLoading: Boolean = false,
    val isAutoLoginFinished: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val member: Member? = null,
    val nexonApiKey: String? = null,
    val characterBasic: CharacterBasic? = null,
    val characterDojangRanking: CharacterDojangRanking? = null,
    val characterOverallRanking: CharacterRanking? = null,
    val characterServerRanking: CharacterRanking? = null,
    val characterUnion: CharacterUnion? = null,
    val isGlobalAlarmEnabled: Boolean = false,
    val events: List<MapleEvent> = emptyList(),
    val bossSchedules: List<Boolean> = emptyList(),
    val isNavigateToLogin: Boolean = false,
    val errorMessage: String? = null
)