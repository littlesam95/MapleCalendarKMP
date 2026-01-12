package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.AccountCharacter
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.LoginResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val representativeOcid: String?,
    val characters: Map<String, List<AccountCharacterResponse>>
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
    val characterBasic: CharacterBasicResponse? = null
)

fun LoginResponse.toDomain(): LoginResult {
    return LoginResult(
        representativeOcid = this.representativeOcid,
        characters = this.characters.mapValues { entry ->
            entry.value.map { it.toDomain() }
        }
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

fun AutoLoginResponse.toDomain(): CharacterBasic? {
    return this.characterBasic?.toDomain()
}