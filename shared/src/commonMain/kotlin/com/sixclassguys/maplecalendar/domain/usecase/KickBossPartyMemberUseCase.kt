package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class KickBossPartyMemberUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long, characterId: Long): Flow<ApiState<Unit>> {
        return repository.kickMember(bossPartyId, characterId)
    }
}