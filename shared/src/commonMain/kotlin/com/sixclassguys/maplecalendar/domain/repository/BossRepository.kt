package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.coroutines.flow.Flow

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

    suspend fun getChatMessage(bossPartyId: Long, page: Int): Flow<ApiState<BossPartyChatHistory>>

    suspend fun connect(partyId: String): Flow<ApiState<Unit>>

    fun observeMessages(): Flow<ApiState<BossPartyChat>>

    suspend fun sendMessage(partyId: Long, message: BossPartyChat): ApiState<Unit>

    suspend fun disconnect()
}