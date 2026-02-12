package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.RegistrationMode

data class BossPartyAlarmTime(
    val id: Long,
    val date: String,
    val time: String,
    val message: String,
    val registrationMode: RegistrationMode
)