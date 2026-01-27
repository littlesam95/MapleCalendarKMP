package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ReissueJwtTokenUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): Flow<ApiState<Unit>> {
        return repository.reissueJwtToken()
    }
}