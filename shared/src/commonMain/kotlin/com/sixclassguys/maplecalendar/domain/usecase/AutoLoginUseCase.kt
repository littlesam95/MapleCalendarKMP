package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AutoLoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(apiKey: String): Flow<ApiState<CharacterBasic>> {
        return authRepository.autoLogin(apiKey)
    }
}