package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class HideBossPartyChatUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>> {
        return repository.hideMessage(bossPartyId, chatId)
    }
}