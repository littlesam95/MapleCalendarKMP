package com.sixclassguys.maplecalendar.domain.model

data class BossPartyAlarmTime(
    val date: String,
    val time: String,
    val message: String,
    val isEnabled: Boolean
)