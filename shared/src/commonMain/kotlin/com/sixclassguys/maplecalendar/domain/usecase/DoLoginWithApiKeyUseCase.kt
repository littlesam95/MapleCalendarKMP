package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.LoginInfo
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class DoLoginWithApiKeyUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(apiKey: String): Flow<ApiState<LoginInfo>> {
        return repository.loginWithApiKey(apiKey)
    }
}