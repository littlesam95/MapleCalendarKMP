package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetTodayEventsUseCase(
    private val repository: EventRepository
) {

    suspend operator fun invoke(year: Int, month: Int, day: Int): Flow<ApiState<List<MapleEvent>>> {
        return repository.getTodayEvents(year, month, day)
    }
}