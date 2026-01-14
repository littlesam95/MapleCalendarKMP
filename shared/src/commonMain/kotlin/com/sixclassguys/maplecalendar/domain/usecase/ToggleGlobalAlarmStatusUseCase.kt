package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow

class ToggleGlobalAlarmStatusUseCase(
    private val repository: MemberRepository
) {

    suspend operator fun invoke(apiKey: String): Flow<ApiState<Boolean>> {
        return repository.toggleGlobalAlarmStatus(apiKey)
    }
}