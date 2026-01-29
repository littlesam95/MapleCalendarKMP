package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty

data class BossUiState(
    val isLoading: Boolean = false,
    val characters: List<Pair<String, CharacterSummary>> = emptyList(),
    val selectedRegion: String = "그란디스",
    val selectedBoss: Boss = Boss.SEREN,
    val selectedBossDifficulty: BossDifficulty? = null,
    val showCreateDialog: Boolean = false,
    val bossPartyCreateCharacter: CharacterSummary? = characters.firstOrNull()?.second,
    val bossPartyCreateTitle: String = "",
    val bossPartyCreateDescription: String = "",
    val errorMessage: String? = null
)