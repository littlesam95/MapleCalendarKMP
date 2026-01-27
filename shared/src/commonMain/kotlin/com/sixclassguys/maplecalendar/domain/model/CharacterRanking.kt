package com.sixclassguys.maplecalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterRanking(
    val rank: Int,
    val characterName: String,
    val worldName: String,
    val className: String,
    val subClassName: String,
    val characterLevel: Int,
    val characterExp: Long,
    val characterPopularity: Int,
    val characterGuildName: String
)