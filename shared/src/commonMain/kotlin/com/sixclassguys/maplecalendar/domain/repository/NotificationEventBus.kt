package com.sixclassguys.maplecalendar.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface NotificationEventBus {

    val events: SharedFlow<Long>

    suspend fun emitEvent(eventId: Long)
}