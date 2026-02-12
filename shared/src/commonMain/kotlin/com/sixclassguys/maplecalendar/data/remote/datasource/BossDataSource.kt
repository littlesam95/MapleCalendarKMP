package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmPeriodRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmTimeRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmTimeResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyBoardResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyDetailResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyScheduleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.SliceResponse
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.Flow

interface BossDataSource {

    suspend fun getBossParties(accessToken: String): List<BossPartyResponse>

    suspend fun createBossParty(
        accessToken: String,
        request: BossPartyCreateRequest
    ): BossPartyCreateResponse

    suspend fun getBossPartyDetail(accessToken: String, bossPartyId: Long): BossPartyDetailResponse

    suspend fun getBossPartySchedules(
        accessToken: String,
        year: Int,
        month: Int,
        day: Int
    ): List<BossPartyScheduleResponse>

    suspend fun getBossPartyAlarmTimes(
        accessToken: String,
        bossPartyId: Long
    ): List<BossPartyAlarmTimeResponse>

    suspend fun updateAlarmSetting(accessToken: String, bossPartyId: Long): Boolean

    suspend fun createBossAlarm(
        accessToken: String,
        bossPartyId: Long,
        request: BossPartyAlarmTimeRequest
    ): List<BossPartyAlarmTimeResponse>

    suspend fun updateBossAlarmPeriod(
        accessToken: String,
        bossPartyId: Long,
        request: BossPartyAlarmPeriodRequest
    ): List<BossPartyAlarmTimeResponse>

    suspend fun deleteBossAlarm(
        accessToken: String,
        bossPartyId: Long,
        alarmId: Long
    ): List<BossPartyAlarmTimeResponse>

    suspend fun inviteMember(
        accessToken: String,
        bossPartyId: Long,
        characterId: Long
    )

    suspend fun acceptInvitation(accessToken: String, bossPartyId: Long): Long

    suspend fun declineInvitation(
        accessToken: String,
        bossPartyId: Long
    ): List<BossPartyResponse>

    suspend fun kickMember(
        accessToken: String,
        bossPartyId: Long,
        characterId: Long
    )

    suspend fun leaveParty(accessToken: String, bossPartyId: Long): List<BossPartyResponse>

    suspend fun transferLeader(
        accessToken: String,
        bossPartyId: Long,
        targetCharacterId: Long
    )

    suspend fun getChatMessages(
        accessToken: String,
        bossPartyId: Long,
        page: Int,
        size: Int = 20
    ): SliceResponse<BossPartyChatMessageResponse>

    suspend fun connect(bossPartyId: String, token: String)

    suspend fun updateChatAlarmSetting(accessToken: String, bossPartyId: Long): Boolean

    suspend fun sendMessage(request: BossPartyChatMessageRequest)

    suspend fun hideMessage(accessToken: String, bossPartyId: Long, messageId: Long)

    suspend fun deleteMessage(accessToken: String, bossPartyId: Long, messageId: Long)

    fun observeMessages(): Flow<Frame>

    suspend fun disconnect()

    suspend fun getBoardPosts(
        accessToken: String,
        bossPartyId: Long,
        page: Int,
        size: Int = 5
    ): SliceResponse<BossPartyBoardResponse>

    suspend fun createBoardPost(
        accessToken: String,
        bossPartyId: Long,
        contentJson: String,
        imageFiles: List<ByteArray>
    ): BossPartyBoardResponse

    suspend fun toggleBoardLike(
        accessToken: String,
        bossPartyId: Long,
        boardId: Long,
        likeType: String
    ): BossPartyBoardResponse
}