package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.datetime.DayOfWeek

data class BossPartyDetail(
    val id: Long,
    val title: String,
    val description: String,
    val boss: Boss,
    val difficulty: BossDifficulty,
    val alarms: List<BossPartyAlarmTime>,
    val members: List<BossPartyMember>,
    val isLeader: Boolean,
    val isPartyAlarmEnabled: Boolean,
    val isChatAlarmEnabled: Boolean,
    val alarmDayOfWeek: DayOfWeek?,
    val alarmHour: Int?,
    val alarmMinute: Int?,
    val alarmMessage: String?,
    val createdAt: String
)