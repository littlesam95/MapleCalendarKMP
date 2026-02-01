package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyResponse(
    val id: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val boss: Boss? = null,
    val difficulty: BossDifficulty? = null,
    val isPartyAlarmEnabled: Boolean = false,
    val isChatAlarmEnabled: Boolean = false,
    val leaderNickname: String? = null,
    val memberCount: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {

    fun toDomain(): BossParty {
        return BossParty(
            id = this.id ?: 0L,
            title = this.title ?: "",
            description = this.description ?: "",
            // null 체크와 기본값 설정만 해주면 끝!
            boss = this.boss ?: Boss.ZAKUM,
            difficulty = this.difficulty ?: BossDifficulty.NORMAL,
            isPartyAlarmEnabled = this.isPartyAlarmEnabled,
            isChatAlarmEnabled = this.isChatAlarmEnabled,
            leaderNickname = this.leaderNickname ?: "",
            memberCount = this.memberCount ?: 0,
            createdAt = this.createdAt ?: "1970-01-01",
            updatedAt = this.updatedAt ?: "1970-01-01"
        )
    }
}