package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.AlarmDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.AlarmRequest
import com.sixclassguys.maplecalendar.data.remote.dto.toDomain
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.domain.repository.AlarmRepository
import com.sixclassguys.maplecalendar.util.handleApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AlarmRepositoryImpl(
    private val dataSource: AlarmDataSource,
    private val dataStore: AppPreferences
) : AlarmRepository {

    override suspend fun submitEventAlarm(
        eventId: Long,
        isEnabled: Boolean,
        alarmTimes: List<String>
    ): Flow<ApiState<MapleEvent>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.submitEventAlarm(
            accessToken = accessToken,
            request = AlarmRequest(
                eventId = eventId,
                isEnabled = isEnabled,
                alarmTimes = alarmTimes
            )
        )
        val event = response.toDomain()

        emit(ApiState.Success(event, "알람을 예약했어요."))
    }.handleApiError()

    override suspend fun toggleEventAlarm(eventId: Long): Flow<ApiState<MapleEvent>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.toggleEventAlarm(accessToken, eventId)
        val event = response.toDomain()

        emit(ApiState.Success(event, "알람 수신 여부를 변경했어요."))
    }.handleApiError()
}