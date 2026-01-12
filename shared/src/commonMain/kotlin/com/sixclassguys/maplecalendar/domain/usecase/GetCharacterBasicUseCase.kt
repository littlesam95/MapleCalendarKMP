package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetCharacterBasicUseCase(
    private val repository: CharacterRepository
) {

    suspend operator fun invoke(ocid: String): Flow<ApiState<CharacterBasic>> {
        return repository.getCharacterBasic(ocid)
    }
}