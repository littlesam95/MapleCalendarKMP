package com.sixclassguys.maplecalendar.presentation.character

import com.sixclassguys.maplecalendar.domain.model.CharacterSummary

sealed class MapleCharacterIntent {

    data class FetchCharacters(val allWorldNames: List<String>) : MapleCharacterIntent()

    data class FetchCharactersSuccess(val characterSummeries: Map<String, Map<String, List<CharacterSummary>>>) :
        MapleCharacterIntent()

    data class FetchCharactersFailed(val message: String) : MapleCharacterIntent()

    data class SelectWorldGroup(val worldGroup: String, val world: String) : MapleCharacterIntent()

    data class SelectWorld(val world: String) : MapleCharacterIntent()

    data object InitApiKey : MapleCharacterIntent()

    data class UpdateApiKey(val apiKey: String) : MapleCharacterIntent()

    data class SubmitApiKey(val allWorldNames: List<String>) : MapleCharacterIntent()

    data class SubmitApiKeySuccess(val newCharacterSummeries: Map<String, Map<String, List<CharacterSummary>>>) : MapleCharacterIntent()

    data class SubmitApiKeyFailed(val message: String) : MapleCharacterIntent()

    data object LockIsFetchStarted : MapleCharacterIntent()

    data object InitNewCharacters : MapleCharacterIntent()

    data class ShowFetchWorldSheet(val isShow: Boolean) : MapleCharacterIntent()

    data class SelectFetchWorldGroup(val worldGroupName: String) : MapleCharacterIntent()

    data class SelectFetchWorld(val worldName: String) : MapleCharacterIntent()

    data class SelectNewCharacter(val ocid: String) : MapleCharacterIntent()

    data class SelectNewCharacterCancel(val ocid: String) : MapleCharacterIntent()

    data class SubmitNewCharacters(val allWorldNames: List<String>) : MapleCharacterIntent()

    data class SubmitNewCharactersSuccess(val newCharacterSummeries: Map<String, Map<String, List<CharacterSummary>>>) : MapleCharacterIntent()

    data class SubmitNewCharactersFailed(val message: String) : MapleCharacterIntent()
}