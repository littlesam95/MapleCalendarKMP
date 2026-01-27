package com.sixclassguys.maplecalendar.domain.model

data class LoginResult(
    val member: Member,
    val isNewMember: Boolean,
    val accessToken: String,
    val refreshToken: String
)