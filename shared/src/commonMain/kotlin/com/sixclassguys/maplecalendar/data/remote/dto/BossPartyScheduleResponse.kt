package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossPartySchedule
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyScheduleResponse(
    val bossPartyId: Long? = null,
    val boss: Boss? = null,
    val bossDifficulty: BossDifficulty? = null,
    val members: List<BossPartyMemberDetailResponse> = emptyList(),
    val time: String? = null
) {

    fun toDomain(): BossPartySchedule {
        return BossPartySchedule(
            bossPartyId = this.bossPartyId ?: 0L,
            boss = this.boss ?: Boss.ZAKUM,
            bossDifficulty = this.bossDifficulty ?: BossDifficulty.NORMAL,
            members = this.members.map { it.toDomain() },
            time = this.time ?: "00:00"
        )
    }
}