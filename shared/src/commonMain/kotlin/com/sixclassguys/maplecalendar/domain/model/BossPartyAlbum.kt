package com.sixclassguys.maplecalendar.domain.model

data class BossPartyAlbum(
    val id: Long,
    val imageUrl: String,
    val author: CharacterSummary,
    val content: String,
    val date: String,
    val likeCount: Int,
    val dislikeCount: Int
)