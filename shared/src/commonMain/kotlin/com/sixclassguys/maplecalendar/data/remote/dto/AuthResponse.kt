package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.AccountCharacter
import com.sixclassguys.maplecalendar.domain.model.LoginInfo
import com.sixclassguys.maplecalendar.domain.model.Member
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val representativeOcid: String?,
    val characters: Map<String, List<AccountCharacterResponse>>,
    val isGlobalAlarmEnabled: Boolean
)

@Serializable
data class AccountCharacterResponse(
    val ocid: String,
    val characterName: String,
    val worldName: String,
    val characterClass: String,
    val characterLevel: Int
)

@Serializable
data class AutoLoginResponse(
    @SerialName("isSuccess")
    val isSuccess: Boolean,

    @SerialName("message")
    val message: String? = null,

    @SerialName("characterBasic")
    val characterBasic: CharacterBasicResponse? = null,

    @SerialName("isGlobalAlarmEnabled")
    val isGlobalAlarmEnabled: Boolean,

    @SerialName("characterPopularity")
    val characterPopularity: Int? = null,

    @SerialName("characterOverallRanking")
    val characterOverallRanking: RankingResponse? = null,

    @SerialName("characterServerRanking")
    val characterServerRanking: RankingResponse? = null,

    @SerialName("characterUnionLevel")
    val characterUnionLevel: UnionResponse? = null,

    @SerialName("characterDojang")
    val characterDojang: DojangRankingResponse? = null
)

fun LoginResponse.toDomain(): LoginInfo {
    return LoginInfo(
        representativeOcid = this.representativeOcid,
        characters = this.characters.mapValues { entry ->
            entry.value.map { it.toDomain() }
        },
        isGlobalAlarmEnabled = this.isGlobalAlarmEnabled
    )
}

fun AccountCharacterResponse.toDomain(): AccountCharacter {
    return AccountCharacter(
        ocid = this.ocid,
        characterName = this.characterName,
        characterClass = this.characterClass,
        characterLevel = this.characterLevel
    )
}

fun AutoLoginResponse.toDomain(): Member {
    return Member(
        email = "",
        nickname = "",
        profileImageUrl = "",
        isGlobalAlarmEnabled = this.isGlobalAlarmEnabled,
        characterBasic = this.characterBasic?.toDomain(),
        characterPopularity = this.characterPopularity ?: 0,
        characterOverallRanking = this.characterOverallRanking?.toDomain(),
        characterServerRanking = this.characterServerRanking?.toDomain(),
        characterUnionLevel = characterUnionLevel?.toDomain(),
        characterDojang = characterDojang?.toDomain(),
    )
}