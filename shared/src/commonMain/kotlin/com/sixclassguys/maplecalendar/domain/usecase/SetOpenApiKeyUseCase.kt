package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class SetOpenApiKeyUseCase(
    private val repository: CharacterRepository
) {

    operator fun invoke(apiKey: String): Flow<ApiState<Unit>> {
        return repository.setOpenApiKey(apiKey)
    }
}