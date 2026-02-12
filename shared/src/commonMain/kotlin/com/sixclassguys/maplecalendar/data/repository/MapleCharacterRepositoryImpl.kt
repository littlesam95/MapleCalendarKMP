package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.MapleCharacterDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.CharacterRegisterRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import com.sixclassguys.maplecalendar.util.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class MapleCharacterRepositoryImpl(
    private val dataSource: MapleCharacterDataSource,
    private val dataStore: AppPreferences
) : MapleCharacterRepository {

    override suspend fun getCharacters(allWorldNames: List<String>): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> =
        flow {
            emit(ApiState.Loading)

            try {
                val accessToken = dataStore.accessToken.first()
                val response = dataSource.getCharacters(accessToken)
                val characters = response.toDomain(allWorldNames)

                emit(ApiState.Success(characters))
            } catch (e: Exception) {
                emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
            }
        }.catch { e ->
            val errorState = when (e) {
                is ApiException -> ApiState.Error(e.message)
                else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
            emit(errorState)
        }

    override suspend fun fetchFromNexon(
        apiKey: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.fetchFromNexon(accessToken, apiKey)
            val characters = response.toDomain(allWorldNames)

            emit(ApiState.Success(characters))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun registerCharacters(
        ocids: List<String>,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.registerCharacters(
                accessToken = accessToken,
                request = CharacterRegisterRequest(ocids)
            )
            val characters = response.toDomain(allWorldNames)

            emit(ApiState.Success(characters))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun checkAuthority(ocid: String): Flow<ApiState<Pair<Boolean, Boolean>>> =
        flow {
            emit(ApiState.Loading)

            try {
                val accessToken = dataStore.accessToken.first()
                val response = dataSource.checkAuthority(accessToken, ocid)

                emit(ApiState.Success(Pair(response.isOwner, response.isRepresentative)))
            } catch (e: Exception) {
                emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
            }
        }.catch { e ->
            val errorState = when (e) {
                is ApiException -> ApiState.Error(e.message)
                else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
            emit(errorState)
        }

    override suspend fun setRepresentative(ocid: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            dataSource.setRepresentative(accessToken, ocid)

            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun deleteCharacter(ocid: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            dataSource.deleteCharacter(accessToken, ocid)

            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun searchCharacters(
        name: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.searchCharacters(
                accessToken = accessToken,
                name = name
            )
            val characters = response.toDomain(allWorldNames)

            emit(ApiState.Success(characters))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }
}