package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class DeclineBossPartyInvitationUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long): Flow<ApiState<List<BossParty>>> {
        return repository.declineInvitation(bossPartyId)
    }
}