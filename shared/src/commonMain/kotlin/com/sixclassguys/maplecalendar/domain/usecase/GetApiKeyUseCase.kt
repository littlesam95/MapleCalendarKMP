package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetApiKeyUseCase(
    private val characterRepository: CharacterRepository
) {

    suspend operator fun invoke(): Flow<ApiState<String>> {
        return characterRepository.getOpenApiKey()
    }
}