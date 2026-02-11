package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

interface BossRepository {

    suspend fun getBossParties(): Flow<ApiState<List<BossParty>>>

    suspend fun createBossParty(
        boss: Boss,
        bossDifficulty: BossDifficulty,
        title: String,
        description: String,
        characterId: Long
    ): Flow<ApiState<Long>>

    suspend fun getBossPartyDetail(bossPartyId: Long): Flow<ApiState<BossPartyDetail>>

    suspend fun getBossPartyAlarmTimes(bossPartyId: Long): Flow<ApiState<List<BossPartyAlarmTime>>>

    suspend fun updateAlarmSetting(bossPartyId: Long): Flow<ApiState<Boolean>>

    suspend fun createBossAlarm(
        bossPartyId: Long,
        hour: Int,
        minute: Int,
        date: LocalDate,
        message: String
    ): Flow<ApiState<List<BossPartyAlarmTime>>>

    suspend fun updateBossAlarmPeriod(
        bossPartyId: Long,
        dayOfWeek: DayOfWeek?,
        hour: Int,
        minute: Int,
        message: String,
        isImmediateApply: Boolean
    ): Flow<ApiState<List<BossPartyAlarmTime>>>

    suspend fun deleteBossAlarm(
        bossPartyId: Long,
        alarmId: Long
    ): Flow<ApiState<List<BossPartyAlarmTime>>>

    suspend fun getChatMessage(bossPartyId: Long, page: Int): Flow<ApiState<BossPartyChatHistory>>

    suspend fun connect(partyId: String): Flow<ApiState<Unit>>

    suspend fun updateChatAlarmSetting(bossPartyId: Long): Flow<ApiState<Boolean>>

    fun observeMessages(): Flow<ApiState<BossPartyChat>>

    suspend fun sendMessage(partyId: Long, message: BossPartyChat): ApiState<Unit>

    suspend fun hideMessage(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>>

    suspend fun deleteMessage(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>>

    suspend fun disconnect()
}