package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.NexonOpenApiDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.toDomain
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CharacterRepositoryImpl(
    private val dataStore: AppPreferences,
    private val dataSource: NexonOpenApiDataSource
) : CharacterRepository {
    
    override suspend fun getCharacterOcid(): Flow<ApiState<String>> = flow { 
        emit(ApiState.Loading)
        
        val ocid = dataStore.characterOcid.first()
        
        if (ocid == null) {
            emit(ApiState.Error("No OCID found"))
        } else {
            emit(ApiState.Success(ocid))
        }
    }

    override fun setCharacterOcid(ocid: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)
        
        try {
            dataStore.saveCharacterOcid(ocid)
            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getOpenApiKey(): Flow<ApiState<String>> = flow {
        emit(ApiState.Loading)

        val apiKey = dataStore.openApiKey.first()

        if (apiKey == null) {
            emit(ApiState.Error("No API Key found"))
        } else {
            emit(ApiState.Success(apiKey))
        }
    }

    override fun setOpenApiKey(apiKey: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        try {
            dataStore.saveOpenApiKey(apiKey)
            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getCharacterBasic(ocid: String): Flow<ApiState<CharacterBasic>> = flow {
        emit(ApiState.Loading)

        try {
            val response = dataSource.getCharacterBasic(ocid)
            val basic = response.toDomain()
            emit(ApiState.Success(basic))
        } catch (e: Exception) {
            // [수정 포인트] 이 예외가 'Flow를 그만 보낼게!'라는 신호라면 가로채지 말고 던져줌
            if (e is kotlinx.coroutines.CancellationException) throw e

            emit(ApiState.Error(e.message ?: "Unknown error"))
        }
    }
}