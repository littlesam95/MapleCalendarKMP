package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    suspend fun submitRepresentativeCharacter(apiKey: String, ocid: String): Flow<ApiState<Unit>>

    suspend fun toggleGlobalAlarmStatus(apiKey: String): Flow<ApiState<Boolean>>

    suspend fun logout(): Flow<ApiState<Unit>>
}