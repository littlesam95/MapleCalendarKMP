package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlbum
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.BossPartyTab

data class BossUiState(
    val isLoading: Boolean = false,
    val bossParties: List<BossParty> = emptyList(),
    val characters: List<Pair<String, CharacterSummary>> = emptyList(),
    val selectedRegion: String = "그란디스",
    val selectedBoss: Boss = Boss.SEREN,
    val selectedBossDifficulty: BossDifficulty? = null,
    val showCreateDialog: Boolean = false,
    val bossPartyCreateCharacter: CharacterSummary? = characters.firstOrNull()?.second,
    val bossPartyCreateTitle: String = "",
    val bossPartyCreateDescription: String = "",
    val selectedBossParty: BossPartyDetail? = null,
    val selectedBossPartyDetailMenu: BossPartyTab = BossPartyTab.ALARM,
    val isBossPartyDetailAlarmOn: Boolean = true,
    val bossPartyAlarmTimes: List<BossPartyAlarmTime> = emptyList(),
    val bossPartyChats: List<BossPartyChat> = emptyList(),
    val bossPartyChatPage: Int = 0,
    val isBossPartyChatLastPage: Boolean = false,
    val bossPartyChatMessage: String = "",
    val bossPartyAlbums: List<BossPartyAlbum> = emptyList(),
    val errorMessage: String? = null
)