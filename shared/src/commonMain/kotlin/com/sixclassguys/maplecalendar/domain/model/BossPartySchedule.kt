package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty

data class BossPartySchedule(
    val bossPartyId: Long,
    val boss: Boss,
    val bossDifficulty: BossDifficulty,
    val members: List<BossPartyMember>,
    val time: String
)