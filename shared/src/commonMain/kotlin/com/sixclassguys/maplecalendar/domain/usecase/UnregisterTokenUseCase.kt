package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class UnregisterTokenUseCase(
    private val repository: NotificationRepository
) {

    suspend operator fun invoke(apiKey: String, token: String): Flow<ApiState<Unit>> {
        return repository.unregisterToken(apiKey, token)
    }
}