package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterRegisterRequest(
    val ocids: List<String>
)