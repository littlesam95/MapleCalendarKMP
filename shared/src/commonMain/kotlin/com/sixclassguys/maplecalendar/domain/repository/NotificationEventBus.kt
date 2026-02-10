package com.sixclassguys.maplecalendar.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface NotificationEventBus {

    val events: SharedFlow<Long>

    val bossPartyId: SharedFlow<Long>

    suspend fun emitEvent(eventId: Long)

    suspend fun emitBossPartyId(bossPartyId: Long)
}