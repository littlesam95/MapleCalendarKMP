package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import kotlinx.coroutines.flow.Flow

class DeleteCharacterUseCase(
    private val repository: MapleCharacterRepository
) {

    suspend operator fun invoke(ocid: String): Flow<ApiState<Unit>> {
        return repository.deleteCharacter(ocid)
    }
}