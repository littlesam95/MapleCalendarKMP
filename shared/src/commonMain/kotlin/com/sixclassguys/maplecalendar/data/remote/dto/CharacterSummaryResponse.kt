package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import kotlinx.serialization.Serializable

@Serializable
data class CharacterSummaryResponse(
    val id: Long,
    val ocid: String,
    val characterName: String,
    val characterLevel: Long,
    val characterClass: String,
    val characterImage: String?,
    val isRepresentativeCharacter: Boolean
) {
    
    fun toDomain(): CharacterSummary {
        return CharacterSummary(
            id = this.id,
            ocid = this.ocid,
            characterName = this.characterName,
            characterLevel = this.characterLevel,
            characterClass = this.characterClass,
            characterImage = this.characterImage ?: "",
            isRepresentativeCharacter = this.isRepresentativeCharacter
        )
    }
}