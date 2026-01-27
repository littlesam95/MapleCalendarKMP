package com.sixclassguys.maplecalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterDojangRanking(
    val characterClass: String,
    val worldName: String,
    val dojangBestFloor: String,
    val dateDojangRecord: String,
    val dojangBestTime: Long?,
)