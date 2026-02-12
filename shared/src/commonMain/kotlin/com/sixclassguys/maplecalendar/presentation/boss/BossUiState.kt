package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
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
    val bossPartiesInvited: List<BossParty> = emptyList(),
    val showBossInvitationDialog: Boolean = false,
    val characters: List<Pair<String, CharacterSummary>> = emptyList(),
    val selectedRegion: String = "그란디스",
    val selectedBoss: Boss = Boss.SEREN,
    val selectedBossDifficulty: BossDifficulty? = null,
    val showCreateDialog: Boolean = false,
    val bossPartyCreateCharacter: CharacterSummary? = characters.firstOrNull()?.second,
    val bossPartyCreateTitle: String = "",
    val bossPartyCreateDescription: String = "",
    val createdPartyId: Long? = null,
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
    val showCharacterInvitationDialog: Boolean = false,
    val searchKeyword: String = "",
    val searchCharacters: List<Pair<String, CharacterSummary>> = emptyList(),
    val bossPartyChats: List<BossPartyChat> = emptyList(),
    val bossPartyChatUiItems: List<BossPartyChatUiItem> = emptyList(),
    val bossPartyChatPage: Int = 0,
    val isBossPartyChatLastPage: Boolean = false,
    val bossPartyChatMessage: String = "",
    val showBossPartyChatReport: Boolean = false,
    val selectBossPartyChatToReport: BossPartyChat? = null,
    val showBossPartyBoardDialog: Boolean = false,
    val bossPartyBoards: List<BossPartyBoard> = emptyList(),
    val bossPartyBoardPage: Int = 0,
    val isBossPartyBoardLastPage: Boolean = false,
    val uploadImage: List<ByteArray> = emptyList(),
    val uploadComment: String = "",
    val uploadSuccessEvent: Long = 0L,
    val errorMessage: String? = null
)