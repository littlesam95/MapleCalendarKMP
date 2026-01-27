package com.sixclassguys.maplecalendar.domain.model

data class CharacterSummary(
    val id: Long,
    val ocid: String,
    val characterName: String,
    val characterLevel: Long,
    val characterClass: String,
    val characterImage: String,
    val isRepresentativeCharacter: Boolean
)