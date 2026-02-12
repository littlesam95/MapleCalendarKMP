package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.JoinStatus

data class BossParty(
    val id: Long,
    val title: String,
    val description: String,
    val boss: Boss,
    val difficulty: BossDifficulty,
    val isPartyAlarmEnabled: Boolean,
    val isChatAlarmEnabled: Boolean,
    val leaderNickname: String,
    val memberCount: Int,
    val joinStatus: JoinStatus,
    val createdAt: String,
    val updatedAt: String
)