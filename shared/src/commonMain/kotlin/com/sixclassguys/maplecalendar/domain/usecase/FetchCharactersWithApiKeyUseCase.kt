package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import kotlinx.coroutines.flow.Flow

class FetchCharactersWithApiKeyUseCase(
    private val repository: MapleCharacterRepository
) {

    suspend operator fun invoke(
        apiKey: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> {
        return repository.fetchFromNexon(apiKey, allWorldNames)
    }
}