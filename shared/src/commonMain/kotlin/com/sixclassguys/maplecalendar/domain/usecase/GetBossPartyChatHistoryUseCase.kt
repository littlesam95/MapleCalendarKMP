package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class GetBossPartyChatHistoryUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long, page: Int): Flow<ApiState<BossPartyChatHistory>> {
        return repository.getChatMessage(bossPartyId, page)
    }
}