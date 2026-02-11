package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlbum
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.BossPartyChatUiItem
import com.sixclassguys.maplecalendar.util.BossPartyTab
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

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
    val isBossPartyDetailAlarmOn: Boolean = false,
    val bossPartyAlarmTimes: List<BossPartyAlarmTime> = emptyList(),
    val showBossAlarmDialog: Boolean = false,
    val selectedAlarmDate: LocalDate? = null,
    val selectedDayOfWeek: DayOfWeek? = null,
    val isImmediatelyAlarm: Boolean = false,
    val selectedHour: String = "",
    val selectedMinute: String = "",
    val alarmMessage: String = "",
    val isBossPartyChatAlarmOn: Boolean = false,
    val bossPartyChats: List<BossPartyChat> = emptyList(),
    val bossPartyChatUiItems: List<BossPartyChatUiItem> = emptyList(),
    val bossPartyChatPage: Int = 0,
    val isBossPartyChatLastPage: Boolean = false,
    val bossPartyChatMessage: String = "",
    val showBossPartyChatReport: Boolean = false,
    val selectBossPartyChatToReport: BossPartyChat? = null,
    val bossPartyAlbums: List<BossPartyAlbum> = emptyList(),
    val errorMessage: String? = null
)