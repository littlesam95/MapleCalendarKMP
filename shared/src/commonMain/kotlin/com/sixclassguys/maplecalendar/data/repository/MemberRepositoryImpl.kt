package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.MemberDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.RepresentativeOcidRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import com.sixclassguys.maplecalendar.util.handleApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class MemberRepositoryImpl(
    private val dataStore: AppPreferences,
    private val dataSource: MemberDataSource
) : MemberRepository {

    override suspend fun submitRepresentativeCharacter(
        apiKey: String,
        ocid: String
    ): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        dataSource.submitRepresentativeCharacter(
            apiKey = apiKey,
            request = RepresentativeOcidRequest(ocid = ocid)
        )

        emit(ApiState.Success(Unit))
    }.handleApiError()

    override suspend fun toggleGlobalAlarmStatus(): Flow<ApiState<Boolean>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.toggleGlobalAlarmStatus(accessToken)

        dataStore.setNotificationMode(response)
        emit(ApiState.Success(response))
    }.handleApiError()

    override suspend fun logout(): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        dataStore.clearAll()
        emit(ApiState.Success(Unit))
    }.handleApiError()
}