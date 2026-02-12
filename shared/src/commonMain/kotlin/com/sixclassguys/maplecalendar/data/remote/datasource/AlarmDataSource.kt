package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AlarmRequest
import com.sixclassguys.maplecalendar.data.remote.dto.EventResponse

interface AlarmDataSource {

    suspend fun submitEventAlarm(accessToken: String, request: AlarmRequest): EventResponse

    suspend fun toggleEventAlarm(accessToken: String, eventId: Long): EventResponse
}