package com.sixclassguys.maplecalendar.domain.model

data class BossPartyBoardHistory(
    val boards: List<BossPartyBoard>,
    val isLastPage: Boolean
)