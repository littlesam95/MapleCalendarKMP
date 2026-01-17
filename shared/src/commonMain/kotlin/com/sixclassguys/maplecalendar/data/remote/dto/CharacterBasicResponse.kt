package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterBasicResponse(
    @SerialName("date") val date: String? = null,
    @SerialName("character_name") val characterName: String? = null,
    @SerialName("world_name") val worldName: String? = null,
    @SerialName("character_gender") val characterGender: String? = null,
    @SerialName("character_class") val characterClass: String? = null,
    @SerialName("character_class_level") val characterClassLevel: String? = null,
    @SerialName("character_level") val characterLevel: Long? = null,
    @SerialName("character_exp") val characterExp: Long? = null,
    @SerialName("character_exp_rate") val characterExpRate: String? = null,
    @SerialName("character_guild_name") val characterGuildName: String? = null,
    @SerialName("character_image") val characterImage: String? = null,
    @SerialName("character_date_create") val characterDateCreate: String? = null,
    @SerialName("access_flag") val accessFlag: String? = null,
    @SerialName("liberation_quest_clear") val liberationQuestClear: String? = null
) {
    fun toDomain(): CharacterBasic {
        return CharacterBasic(
            characterName = this.characterName ?: "김창섭",
            worldName = this.worldName ?: "테스트월드1",
            characterGender = this.characterGender ?: "남",
            characterClass = this.characterClass ?: "초보자",
            characterClassLevel = (this.characterClassLevel ?: "0").toInt(),
            characterLevel = this.characterLevel ?: 0L,
            characterExp = this.characterExp ?: 0L,
            characterExpRate = (this.characterExpRate ?: "0.0").toDouble(),
            characterGuildName = this.characterGuildName ?: "6반남",
            characterImage = this.characterImage ?: "",
            characterDateCreate = this.characterDateCreate ?: "2003-04-29",
            characterAccessFlag = this.accessFlag.toBoolean(),
            characterLiberationQuestClear = (this.liberationQuestClear ?: "0").toInt()
        )
    }
}

