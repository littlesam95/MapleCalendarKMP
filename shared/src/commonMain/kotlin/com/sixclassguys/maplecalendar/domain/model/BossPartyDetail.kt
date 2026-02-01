package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty

data class BossPartyDetail(
    val id: Long,
    val title: String,
    val description: String,
    val boss: Boss,
    val difficulty: BossDifficulty,
    val members: List<BossPartyMember>,
    val isLeader: Boolean,
    val isPartyAlarmEnabled: Boolean,
    val isChatAlarmEnabled: Boolean,
    val createdAt: String
)