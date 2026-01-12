package com.sixclassguys.maplecalendar.domain.model

data class CharacterBasic(
    val characterName: String,
    val worldName: String,
    val characterGender: String,
    val characterClass: String,
    val characterClassLevel: Int,
    val characterLevel: Long,
    val characterExp: Long,
    val characterExpRate: Double,
    val characterGuildName: String,
    val characterImage: String,
    val characterDateCreate: String,
    val characterAccessFlag: Boolean,
    val characterLiberationQuestClear: Int
)