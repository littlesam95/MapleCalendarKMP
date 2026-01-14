package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationEventBusImpl : NotificationEventBus {

    // 최근 1개의 신호만 유지하여 수신 측이 늦게 구독해도 받을 수 있게 설정 가능
    private val _events = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    override val events = _events.asSharedFlow()

    override suspend fun emitEvent(eventId: Long) {
        _events.emit(eventId)
    }
}