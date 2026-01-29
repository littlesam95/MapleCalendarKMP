package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty

sealed class BossIntent {

    data class FetchCharacters(val allWorldNames: List<String>) : BossIntent()

    data class FetchCharactersSuccess(val characters: Map<String, Map<String, List<CharacterSummary>>>) :
        BossIntent()

    data class FetchCharactersFailed(val message: String) : BossIntent()

    data class SelectRegion(val selectedRegion: String) : BossIntent()

    data class SelectBoss(val selectedBoss: Boss) : BossIntent()

    data class SelectBossDifficulty(val selectedBossDifficulty: BossDifficulty) : BossIntent()

    data object DismissDialog : BossIntent()

    data class SelectBossPartyCharacter(val character: CharacterSummary) : BossIntent()

    data class UpdateBossPartyTitle(val title: String) : BossIntent()

    data class UpdateBossPartyDescription(val description: String) : BossIntent()

    data object CreateBossParty : BossIntent()
}