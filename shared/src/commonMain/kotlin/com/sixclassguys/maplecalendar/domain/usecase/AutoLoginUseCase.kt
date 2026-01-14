package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.Member
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AutoLoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(apiKey: String, fcmToken: String): Flow<ApiState<Member>> {
        return authRepository.autoLogin(apiKey, fcmToken)
    }
}