package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class SetCharacterOcidUseCase(
    private val characterRepository: CharacterRepository
) {

    operator fun invoke(ocid: String): Flow<ApiState<Unit>> {
        return characterRepository.setCharacterOcid(ocid)
    }
}