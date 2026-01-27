package com.sixclassguys.maplecalendar.presentation.character

import com.sixclassguys.maplecalendar.domain.model.CharacterSummary

data class MapleCharacterUiState(
    val isLoading: Boolean = false,
    val selectedWorldGroup: String = "일반 월드",
    val selectedWorld: String = "스카니아",
    val characterSummeries: Map<String, Map<String, List<CharacterSummary>>> = emptyMap(),
    val nexonOpenApiKey: String = "",
    val isFetchStarted: Boolean = false,
    val newCharacterSummeries: Map<String, Map<String, List<CharacterSummary>>> = emptyMap(),
    val showFetchWorldSheet: Boolean = false,
    val selectedFetchWorldGroup: String = "일반 월드",
    val selectedFetchWorld: String = "스카니아",
    val selectedCharacterOcids: List<String> = emptyList(),
    val isSubmitSuccess: Boolean = false,
    val characterImages: Map<String, String?> = emptyMap(),
    val backgroundImageUrl: String? = null,
    val selectedCharacter: CharacterSummary? = null,
    val isSelectedCharacterOwned: Boolean = false,
    val isSelectedCharacterRepresentative: Boolean = false,
    val errorMessage: String? = null
)