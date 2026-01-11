package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.EventResponse

interface EventDataSource {

    suspend fun fetchTodayEvents(year: Int, month: Int, day: Int): List<EventResponse>

    suspend fun fetchMonthlyEvents(year: Int, month: Int): List<EventResponse>
}