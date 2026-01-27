package com.sixclassguys.maplecalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val email: String,
    val nickname: String,
    val profileImageUrl: String,
    val isGlobalAlarmEnabled: Boolean,
    val characterBasic: CharacterBasic?,
    val characterPopularity: Int,
    val characterOverallRanking: CharacterRanking?,
    val characterServerRanking: CharacterRanking?,
    val characterUnionLevel: CharacterUnion?,
    val characterDojang: CharacterDojangRanking?
)