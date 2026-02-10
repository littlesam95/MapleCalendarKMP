package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyAlarmTimeRequest(
    val hour: Int,
    val minute: Int,
    val date: LocalDate,
    val message: String
)