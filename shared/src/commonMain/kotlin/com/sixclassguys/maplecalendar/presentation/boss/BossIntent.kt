package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.BossPartyTab
import com.sixclassguys.maplecalendar.util.ReportReason
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

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

    data object ShowAlarmCreateDialog : BossIntent()

    data object DismissAlarmCreateDialog : BossIntent()

    data class UpdateAlarmTimeHour(val hour: String) : BossIntent()

    data class UpdateAlarmTimeMinute(val minute: String) : BossIntent()

    data class UpdateAlarmMessage(val message: String) : BossIntent()

    data class UpdateAlarmTimeSelectMode(val date: LocalDate) : BossIntent()

    data object CreateBossPartyAlarm : BossIntent()

    data class CreateBossPartyAlarmSuccess(val bossPartyAlarmTimes: List<BossPartyAlarmTime>) :
        BossIntent()

    data class CreateBossPartyAlarmFailed(val message: String) : BossIntent()

    data class UpdateAlarmTimePeriodMode(val dayOfWeek: DayOfWeek?) : BossIntent()

    data class UpdateThisWeekPeriodMode(val isImmediatelyAlarm: Boolean) : BossIntent()

    data object UpdateBossPartyAlarmPeriod : BossIntent()

    data class UpdateBossPartyAlarmPeriodSuccess(val bossPartyAlarmTimes: List<BossPartyAlarmTime>) :
        BossIntent()

    data class UpdateBossPartyAlarmPeriodFailed(val message: String) : BossIntent()

    data class DeleteBossPartyAlarm(val alarmId: Long) : BossIntent()

    data class DeleteBossPartyAlarmSuccess(val bossPartyAlarmTimes: List<BossPartyAlarmTime>) :
        BossIntent()

    data class DeleteBossPartyAlarmFailed(val message: String) : BossIntent()

    data object ToggleBossPartyAlarm : BossIntent()

    data class ToggleBossPartyAlarmSuccess(val enabled: Boolean) : BossIntent()

    data class ToggleBossPartyAlarmFailed(val message: String) : BossIntent()

    data object ConnectBossPartyChat : BossIntent()

    data object ToggleBossPartyChatAlarm : BossIntent()

    data class ToggleBossPartyChatAlarmSuccess(val enabled: Boolean) : BossIntent()

    data class ToggleBossPartyChatAlarmFailed(val message: String) : BossIntent()

    data class ReceiveRealTimeChat(val bossPartyChat: BossPartyChat) : BossIntent()

    data class ConnectBossPartyChatFailed(val message: String) : BossIntent()

    data class UpdateBossPartyChatMessage(val bossPartyChatMessage: String) : BossIntent()

    data object SendBossPartyChatMessage : BossIntent()

    data object SendBossPartyChatMessageSuccess : BossIntent()

    data class SendBossPartyChatMessageFailed(val message: String) : BossIntent()

    data class ShowBossPartyChatReportDialog(val chat: BossPartyChat) : BossIntent()

    data object DismissBossPartyChatReportDialog : BossIntent()

    data class ReportBossPartyChatMessage(
        val chatId: Long,
        val reason: ReportReason,
        val reasonDetail: String?
    ) : BossIntent()

    data object ReportBossPartyChatMessageSuccess : BossIntent()

    data class ReportBossPartyChatMessageFailed(val message: String) : BossIntent()

    data object FetchBossPartyChatHistory : BossIntent()

    data class FetchBossPartyChatHistorySuccess(val bossPartyChatHistory: BossPartyChatHistory) :
        BossIntent()

    data class FetchBossPartyChatHistoryFailed(val message: String) : BossIntent()

    data class HideBossPartyChatMessage(val bossPartyChatId: Long) : BossIntent()

    data class HideBossPartyChatMessageSuccess(val bossPartyChatId: Long) : BossIntent()

    data class HideBossPartyChatMessageFailed(val message: String) : BossIntent()

    data class DeleteBossPartyChatMessage(val bossPartyChatId: Long) : BossIntent()

    data class DeleteBossPartyChatMessageSuccess(val bossPartyChatId: Long) : BossIntent()

    data class DeleteBossPartyChatMessageFailed(val message: String) : BossIntent()

    data object DisconnectBossPartyChat : BossIntent()

    data class SelectBossPartyDetailMenu(val selectedBossPartyDetailMenu: BossPartyTab) :
        BossIntent()
}