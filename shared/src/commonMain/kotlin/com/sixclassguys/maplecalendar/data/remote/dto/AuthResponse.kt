package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.AccountCharacter
import com.sixclassguys.maplecalendar.domain.model.LoginResult
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

    val isGlobalAlarmEnabled: Boolean
)

fun LoginResponse.toDomain(): LoginResult {
    return LoginResult(
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
        isGlobalAlarmEnabled = this.isGlobalAlarmEnabled,
        characterBasic = this.characterBasic?.toDomain()
    )
}