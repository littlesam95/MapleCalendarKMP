package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.LoginResult
import com.sixclassguys.maplecalendar.domain.model.Member
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AutoLoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(fcmToken: String): Flow<ApiState<LoginResult>> {
        return authRepository.autoLogin(fcmToken)
    }
}