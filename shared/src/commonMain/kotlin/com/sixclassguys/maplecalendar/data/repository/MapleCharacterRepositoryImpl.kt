package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.MapleCharacterDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.CharacterRegisterRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import com.sixclassguys.maplecalendar.util.handleApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class MapleCharacterRepositoryImpl(
    private val dataSource: MapleCharacterDataSource,
    private val dataStore: AppPreferences
) : MapleCharacterRepository {

    override suspend fun getCharacters(allWorldNames: List<String>): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> =
        flow {
            emit(ApiState.Loading)

            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getCharacters(accessToken)
            val characters = response.toDomain(allWorldNames)

            emit(ApiState.Success(characters))
        }.handleApiError()

    override suspend fun fetchFromNexon(
        apiKey: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.fetchFromNexon(accessToken, apiKey)
        val characters = response.toDomain(allWorldNames)

        emit(ApiState.Success(characters))
    }.handleApiError()

    override suspend fun registerCharacters(
        ocids: List<String>,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.registerCharacters(
            accessToken = accessToken,
            request = CharacterRegisterRequest(ocids)
        )
        val characters = response.toDomain(allWorldNames)

        emit(ApiState.Success(characters, "캐릭터 등록에 성공했어요."))
    }.handleApiError()

    override suspend fun checkAuthority(ocid: String): Flow<ApiState<Pair<Boolean, Boolean>>> =
        flow {
            emit(ApiState.Loading)

            val accessToken = dataStore.accessToken.first()
            val response = dataSource.checkAuthority(accessToken, ocid)

            emit(ApiState.Success(Pair(response.isOwner, response.isRepresentative)))
        }.handleApiError()

    override suspend fun setRepresentative(ocid: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.setRepresentative(accessToken, ocid)

        emit(ApiState.Success(Unit))
    }.handleApiError()

    override suspend fun deleteCharacter(ocid: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.deleteCharacter(accessToken, ocid)

        emit(ApiState.Success(Unit))
    }.handleApiError()

    override suspend fun searchCharacters(
        name: String,
        allWorldNames: List<String>
    ): Flow<ApiState<Map<String, Map<String, List<CharacterSummary>>>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.searchCharacters(
            accessToken = accessToken,
            name = name
        )
        val characters = response.toDomain(allWorldNames)

        emit(ApiState.Success(characters))
    }.handleApiError()
}