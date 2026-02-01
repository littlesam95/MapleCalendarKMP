package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class GetBossPartiesUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(): Flow<ApiState<List<BossParty>>> {
        return repository.getBossParties()
    }
}