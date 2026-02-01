package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.BossPartyTab

sealed class BossIntent {

    data object FetchBossParties : BossIntent()

    data class FetchBossPartiesSuccess(val bossParties: List<BossParty>) : BossIntent()

    data class FetchBossPartiesFailed(val message: String) : BossIntent()

    data class FetchCharacters(val allWorldNames: List<String>) : BossIntent()

    data class FetchCharactersSuccess(val characters: Map<String, Map<String, List<CharacterSummary>>>) :
        BossIntent()

    data class FetchCharactersFailed(val message: String) : BossIntent()

    data class SelectRegion(val selectedRegion: String) : BossIntent()

    data class SelectBoss(val selectedBoss: Boss) : BossIntent()

    data class SelectBossDifficulty(val selectedBossDifficulty: BossDifficulty) : BossIntent()

    data object DismissBossPartyCreateDialog : BossIntent()

    data class SelectBossPartyCharacter(val character: CharacterSummary) : BossIntent()

    data class UpdateBossPartyTitle(val title: String) : BossIntent()

    data class UpdateBossPartyDescription(val description: String) : BossIntent()

    data object CreateBossParty : BossIntent()

    data class CreateBossPartySuccess(val bossPartyId: Long) : BossIntent()

    data class CreateBossPartyFailed(val message: String) : BossIntent()

    data class FetchBossPartyDetail(val bossPartyId: Long) : BossIntent()

    data class FetchBossPartyDetailSuccess(val bossPartyDetail: BossPartyDetail) : BossIntent()

    data class FetchBossPartyDetailFailed(val message: String) : BossIntent()

    data object ConnectBossPartyChat : BossIntent()

    data class ReceiveRealTimeChat(val bossPartyChat: BossPartyChat) : BossIntent()

    data class ConnectBossPartyChatFailed(val message: String) : BossIntent()

    data class UpdateBossPartyChatMessage(val bossPartyChatMessage: String) : BossIntent()

    data object SendBossPartyChatMessage : BossIntent()

    data object SendBossPartyChatMessageSuccess : BossIntent()

    data class SendBossPartyChatMessageFailed(val message: String) : BossIntent()

    data object FetchBossPartyChatHistory : BossIntent()

    data class FetchBossPartyChatHistorySuccess(val bossPartyChatHistory: BossPartyChatHistory) : BossIntent()

    data class FetchBossPartyChatHistoryFailed(val message: String) : BossIntent()

    data object DisconnectBossPartyChat : BossIntent()

    data class SelectBossPartyDetailMenu(val selectedBossPartyDetailMenu: BossPartyTab) : BossIntent()
}