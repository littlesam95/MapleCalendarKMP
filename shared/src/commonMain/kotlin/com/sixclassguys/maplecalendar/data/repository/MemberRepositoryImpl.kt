package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.MemberDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.RepresentativeOcidRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
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

        try {
            dataSource.submitRepresentativeCharacter(
                apiKey = apiKey,
                request = RepresentativeOcidRequest(ocid = ocid)
            )

            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }

    override suspend fun toggleGlobalAlarmStatus(apiKey: String): Flow<ApiState<Boolean>> = flow {
        emit(ApiState.Loading)

        try {
            val response = dataSource.toggleGlobalAlarmStatus(apiKey)

            dataStore.setNotificationMode(response)
            emit(ApiState.Success(response))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }

    override suspend fun logout(): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val response = dataStore.clearAll()
        emit(ApiState.Success(Unit))
    }
}