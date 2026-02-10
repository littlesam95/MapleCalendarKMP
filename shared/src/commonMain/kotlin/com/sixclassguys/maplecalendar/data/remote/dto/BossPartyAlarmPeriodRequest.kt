package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyAlarmPeriodRequest(
    val dayOfWeek: DayOfWeek?,
    val hour: Int,
    val minute: Int,
    val message: String,
    val isImmediateApply: Boolean
)