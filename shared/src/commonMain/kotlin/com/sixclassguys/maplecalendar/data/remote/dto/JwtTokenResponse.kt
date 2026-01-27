package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class JwtTokenResponse(
    val accessToken: String,
    val refreshToken: String
)