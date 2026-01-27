package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterAuthorityResponse(
    val isOwner: Boolean,
    val isRepresentative: Boolean
)