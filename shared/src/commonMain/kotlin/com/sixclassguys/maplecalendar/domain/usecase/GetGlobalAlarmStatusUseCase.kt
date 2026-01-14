package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetGlobalAlarmStatusUseCase(
    private val repository: NotificationRepository
) {

    suspend operator fun invoke(): Flow<ApiState<Boolean>> {
        return repository.getGlobalAlarmStatus()
    }
}