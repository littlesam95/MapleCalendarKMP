package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.EventResponse

interface EventDataSource {

    suspend fun fetchEventDetail(accessToken: String, eventId: Long): EventResponse?

    suspend fun fetchTodayEvents(
        year: Int,
        month: Int,
        day: Int,
        accessToken: String
    ): List<EventResponse>

    suspend fun fetchMonthlyEvents(year: Int, month: Int, accessToken: String): List<EventResponse>
}