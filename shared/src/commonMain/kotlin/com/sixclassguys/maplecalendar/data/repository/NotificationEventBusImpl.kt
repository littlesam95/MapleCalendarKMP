package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationEventBusImpl : NotificationEventBus {

    // 최근 1개의 신호만 유지하여 수신 측이 늦게 구독해도 받을 수 있게 설정 가능
    private val _events = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    override val events = _events.asSharedFlow()

    private val _bossPartyId = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    override val bossPartyId = _bossPartyId.asSharedFlow()

    private val _kickedPartyId = MutableSharedFlow<Long?>(extraBufferCapacity = 1)
    override val kickedPartyId = _kickedPartyId.asSharedFlow()

    override suspend fun emitEvent(eventId: Long) {
        _events.emit(eventId)
    }

    override suspend fun emitBossPartyId(bossPartyId: Long) {
        _bossPartyId.emit(bossPartyId)
    }

    override suspend fun emitKickedPartyId(partyId: Long?) {
        _kickedPartyId.emit(partyId)
    }
}