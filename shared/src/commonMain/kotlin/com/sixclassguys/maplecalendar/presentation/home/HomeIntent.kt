package com.sixclassguys.maplecalendar.presentation.home

import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.CharacterDojangRanking
import com.sixclassguys.maplecalendar.domain.model.CharacterRanking
import com.sixclassguys.maplecalendar.domain.model.CharacterUnion
import com.sixclassguys.maplecalendar.domain.model.MapleEvent

sealed class HomeIntent {

    data object LoadApiKey : HomeIntent()

    data object LoadEmptyApiKey : HomeIntent()

    data class LoadApiKeyFailed(val message: String) : HomeIntent()

    data class LoadCharacterBasic(val apiKey: String) : HomeIntent()

    data class LoadCharacterBasicSuccess(
        val characterBasic: CharacterBasic?,
        val characterDojangRanking: CharacterDojangRanking?,
        val characterOverallRanking: CharacterRanking?,
        val characterServerRanking: CharacterRanking?,
        val characterUnion: CharacterUnion?,
        val isGlobalAlarmEnabled: Boolean
    ) : HomeIntent()

    data class LoadCharacterBasicFailed(val message: String) : HomeIntent()

    data object LoadEvents : HomeIntent()

    data class LoadEventsSuccess(val events: List<MapleEvent>) : HomeIntent()

    data class LoadEventsFailed(val message: String) : HomeIntent()

    data object SyncNotificationWithSystem : HomeIntent()

    data object ToggleGlobalAlarmStatus : HomeIntent()

    data class ToggleGlobalAlarmStatusSuccess(val isEnabled: Boolean) : HomeIntent()

    data class ToggleGlobalAlarmStatusFailed(val message: String) : HomeIntent()

    data object Login : HomeIntent()

    data object Logout : HomeIntent()

    data object NavigationHandled : HomeIntent()
}