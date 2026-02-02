package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class DeleteBossPartyChatUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>> {
        return repository.deleteMessage(bossPartyId, chatId)
    }
}