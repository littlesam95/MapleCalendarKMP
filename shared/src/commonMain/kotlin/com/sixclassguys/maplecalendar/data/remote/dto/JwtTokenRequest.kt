package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JwtTokenRequest(
    val refreshToken: String
)