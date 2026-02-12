package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoardHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.model.BossPartySchedule
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

    suspend fun getBossPartySchedules(
        year: Int,
        month: Int,
        day: Int
    ): Flow<ApiState<List<BossPartySchedule>>>

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

    suspend fun inviteMember(bossPartyId: Long, characterId: Long): Flow<ApiState<Unit>>

    suspend fun acceptInvitation(bossPartyId: Long): Flow<ApiState<Long>>

    suspend fun declineInvitation(bossPartyId: Long): Flow<ApiState<List<BossParty>>>

    suspend fun kickMember(bossPartyId: Long, characterId: Long): Flow<ApiState<Unit>>

    suspend fun leaveParty(bossPartyId: Long): Flow<ApiState<List<BossParty>>>

    suspend fun transferLeader(bossPartyId: Long, targetCharacterId: Long): Flow<ApiState<Unit>>

    suspend fun getChatMessage(bossPartyId: Long, page: Int): Flow<ApiState<BossPartyChatHistory>>

    suspend fun connect(partyId: String): Flow<ApiState<Unit>>

    suspend fun updateChatAlarmSetting(bossPartyId: Long): Flow<ApiState<Boolean>>

    fun observeMessages(): Flow<ApiState<BossPartyChat>>

    suspend fun sendMessage(partyId: Long, message: BossPartyChat): ApiState<Unit>

    suspend fun hideMessage(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>>

    suspend fun deleteMessage(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>>

    suspend fun disconnect()

    suspend fun getBoardPosts(
        bossPartyId: Long,
        page: Int
    ): Flow<ApiState<BossPartyBoardHistory>>

    suspend fun createBoardPost(
        bossPartyId: Long,
        content: String, // 게시글 텍스트 내용
        images: List<ByteArray> // 이미지 데이터들
    ): Flow<ApiState<BossPartyBoard>>

    suspend fun toggleBoardLike(
        bossPartyId: Long,
        boardId: Long,
        likeType: String
    ): Flow<ApiState<BossPartyBoard>>
}