package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class TransferBossPartyLeaderUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long, targetCharacterId: Long): Flow<ApiState<Unit>> {
        return repository.transferLeader(bossPartyId, targetCharacterId)
    }
}