package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossPartyMember
import com.sixclassguys.maplecalendar.util.BossPartyRole
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyMemberDetailResponse(
    val characterId: Long? = null,
    val characterName: String? = null,
    val worldName: String? = null,
    val characterClass: String? = null,
    val characterLevel: Long? = null,
    val characterImage: String? = null,
    val role: BossPartyRole? = null,
    val isMyCharacter: Boolean = false,
    val joinedAt: String? = null
) {
    
    fun toDomain(): BossPartyMember {
        return BossPartyMember(
            characterId = this.characterId ?: 0L,
            characterName = this.characterName ?: "",
            worldName = this.worldName ?: "",
            characterClass = this.characterClass ?: "",
            characterLevel = this.characterLevel ?: 0L,
            characterImage = this.characterImage ?: "",
            role = this.role ?: BossPartyRole.MEMBER,
            isMyCharacter = this.isMyCharacter,
            joinedAt = this.joinedAt ?: "1970-01-01"
        )
    }
}