package com.sixclassguys.maplecalendar.domain.model

data class BossPartyChat(
    val characterSummary: CharacterSummary,
    val content: String,
    val isMine: Boolean
)