package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class ToggleEventAlarmUseCase(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(eventId: Long): Flow<ApiState<MapleEvent>> {
        return repository.toggleEventAlarm(eventId)
    }
}