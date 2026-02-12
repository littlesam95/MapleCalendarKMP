package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import kotlinx.coroutines.flow.Flow

class SearchCharactersUseCase(
    private val repository: MapleCharacterRepository
) {

    suspend operator fun invoke(
        name: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> {
        return repository.searchCharacters(name, allWorldNames)
    }
}