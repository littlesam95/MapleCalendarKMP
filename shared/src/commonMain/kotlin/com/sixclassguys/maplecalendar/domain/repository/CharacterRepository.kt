package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    suspend fun getCharacterOcid(): Flow<ApiState<String>>

    fun setCharacterOcid(ocid: String): Flow<ApiState<Unit>>

    suspend fun getOpenApiKey(): Flow<ApiState<String>>

    fun setOpenApiKey(apiKey: String): Flow<ApiState<Unit>>

    suspend fun getCharacterBasic(ocid: String): Flow<ApiState<CharacterBasic>>
}