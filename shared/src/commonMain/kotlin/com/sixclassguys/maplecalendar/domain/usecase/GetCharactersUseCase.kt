package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlin.String

class GetCharactersUseCase(
    private val repository: MapleCharacterRepository
) {

    suspend operator fun invoke(allWorldNames: List<String>): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> {
        return repository.getCharacters(allWorldNames)
    }
}