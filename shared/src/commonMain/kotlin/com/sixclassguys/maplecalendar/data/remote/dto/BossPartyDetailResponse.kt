package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyDetailResponse(
    val id: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val boss: Boss? = null,
    val difficulty: BossDifficulty? = null,
    val members: List<BossPartyMemberDetailResponse> = emptyList(),
    val isLeader: Boolean = false,
    val isPartyAlarmEnabled: Boolean = false,
    val isChatAlarmEnabled: Boolean = false,
    val createdAt: String? = null
) {
    
    fun toDomain(): BossPartyDetail {
        return BossPartyDetail(
            id = this.id ?: 0L,
            title = this.title ?: "",
            description = this.description ?: "",
            boss = this.boss ?: Boss.ZAKUM,
            difficulty = this.difficulty ?: BossDifficulty.NORMAL,
            members = this.members.map { it.toDomain() },
            isLeader = this.isLeader,
            isPartyAlarmEnabled = this.isPartyAlarmEnabled,
            isChatAlarmEnabled = this.isChatAlarmEnabled,
            createdAt = this.createdAt ?: "1970-01-01"
        )
    }
}