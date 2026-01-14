package com.sixclassguys.maplecalendar.presentation.home

import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.MapleEvent

sealed class HomeIntent {

    data object LoadApiKey : HomeIntent()

    data class LoadApiKeyFailed(val message: String) : HomeIntent()

    data class LoadCharacterBasic(val apiKey: String) : HomeIntent()

    data class LoadCharacterBasicSuccess(
        val characterBasic: CharacterBasic?,
        val isGlobalAlarmEnabled: Boolean
    ) : HomeIntent()

    data class LoadCharacterBasicFailed(val message: String) : HomeIntent()

    data object LoadEvents : HomeIntent()

    data class LoadEventsSuccess(val events: List<MapleEvent>) : HomeIntent()

    data class LoadEventsFailed(val message: String) : HomeIntent()

    data object Login : HomeIntent()

    data object Logout : HomeIntent()

    data object NavigationHandled : HomeIntent()
}