package com.sixclassguys.maplecalendar.domain.model

data class Member(
    val isGlobalAlarmEnabled: Boolean = true,
    val characterBasic: CharacterBasic?
)