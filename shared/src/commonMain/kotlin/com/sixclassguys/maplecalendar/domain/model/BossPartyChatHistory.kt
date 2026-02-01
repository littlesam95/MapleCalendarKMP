package com.sixclassguys.maplecalendar.domain.model

data class BossPartyChatHistory(
    val messages: List<BossPartyChat>,
    val isLastPage: Boolean
)