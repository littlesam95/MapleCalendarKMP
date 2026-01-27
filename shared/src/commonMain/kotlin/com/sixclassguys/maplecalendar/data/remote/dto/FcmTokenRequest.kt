package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    val token: String,
    val platform: String
)
