package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow

class LogoutUseCase(
    private val repository: MemberRepository
) {

    suspend operator fun invoke(): Flow<ApiState<Unit>> {
        return repository.logout()
    }
}