package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun getGlobalAlarmStatus(): Flow<ApiState<Boolean>>

    suspend fun getSavedFcmToken(): Flow<ApiState<String?>>

    suspend fun getFcmToken(): String?

    fun registerToken(token: String): Flow<ApiState<Unit>>

    suspend fun unregisterToken(apiKey: String, token: String): Flow<ApiState<Unit>>
}