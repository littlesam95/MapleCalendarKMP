package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.util.RegistrationMode
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyAlarmTimeResponse(
    val id: Long,
    val alarmTime: LocalDateTime,
    val message: String,
    val isSent: Boolean,
    val registrationMode: RegistrationMode,
) {

    fun toDomain(): BossPartyAlarmTime {
        return BossPartyAlarmTime(
            id = this.id,
            date = this.alarmTime.date.toString(),
            time = this.alarmTime.time.toString(),
            message = this.message,
            registrationMode = this.registrationMode
        )
    }
}