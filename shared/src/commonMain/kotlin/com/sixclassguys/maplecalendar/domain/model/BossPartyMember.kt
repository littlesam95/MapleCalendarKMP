package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.BossPartyRole

data class BossPartyMember(
    val characterId: Long,
    val characterName: String,
    val worldName: String,
    val characterClass: String,
    val characterLevel: Long,
    val characterImage: String,
    val role: BossPartyRole,
    val isMyCharacter: Boolean,
    val joinedAt: String
)