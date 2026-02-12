package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class AcceptBossPartyInvitationUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long): Flow<ApiState<Long>> {
        return repository.acceptInvitation(bossPartyId)
    }
}