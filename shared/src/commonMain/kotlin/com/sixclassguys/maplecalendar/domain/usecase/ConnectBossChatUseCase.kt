package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class ConnectBossChatUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(partyId: Long): Flow<ApiState<Unit>> =
        repository.connect(partyId.toString())
}