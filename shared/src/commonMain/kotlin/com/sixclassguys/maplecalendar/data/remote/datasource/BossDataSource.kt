package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmPeriodRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmTimeRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmTimeResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyDetailResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyResponse
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

    suspend fun getBossPartyAlarmTimes(
        accessToken: String,
        bossPartyId: Long
    ): List<BossPartyAlarmTimeResponse>

    suspend fun updateAlarmSetting(accessToken: String, bossPartyId: Long, enabled: Boolean)

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

    suspend fun getChatMessages(
        accessToken: String,
        bossPartyId: Long,
        page: Int,
        size: Int = 20
    ): SliceResponse<BossPartyChatMessageResponse>

    suspend fun connect(bossPartyId: String, token: String)

    suspend fun sendMessage(request: BossPartyChatMessageRequest)

    suspend fun deleteMessage(accessToken: String, bossPartyId: Long, messageId: Long)

    fun observeMessages(): Flow<Frame>

    suspend fun disconnect()
}