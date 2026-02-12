package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetEventDetailUseCase(
    private val repository: EventRepository
) {

    suspend operator fun invoke(eventId: Long): Flow<ApiState<MapleEvent?>> {
        return repository.getEventDetail(eventId)
    }
}