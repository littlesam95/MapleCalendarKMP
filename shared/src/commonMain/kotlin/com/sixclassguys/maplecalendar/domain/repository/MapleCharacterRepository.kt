package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import kotlinx.coroutines.flow.Flow

interface MapleCharacterRepository {


    suspend fun getCharacters(allWorldNames: List<String>): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>>

    suspend fun fetchFromNexon(
        apiKey: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>>

    suspend fun registerCharacters(
        ocids: List<String>,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>>

    suspend fun checkAuthority(ocid: String): Flow<ApiState<Pair<Boolean, Boolean>>>

    suspend fun setRepresentative(ocid: String): Flow<ApiState<Unit>>

    suspend fun deleteCharacter(ocid: String): Flow<ApiState<Unit>>

    suspend fun searchCharacters(
        name: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>>
}