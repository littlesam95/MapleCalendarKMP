package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow

class SubmitRepresentativeCharacterUseCase(
    private val repository: MemberRepository
) {

    suspend operator fun invoke(apiKey: String, ocid: String): Flow<ApiState<Unit>> {
        return repository.submitRepresentativeCharacter(apiKey, ocid)
    }
}