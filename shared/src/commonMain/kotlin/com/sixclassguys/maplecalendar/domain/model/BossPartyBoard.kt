package com.sixclassguys.maplecalendar.domain.model

data class BossPartyBoard(
    val id: Long,
    val characterId: Long,
    val characterName: String,
    val characterImage: String,
    val characterClass: String,
    val characterLevel: Long,
    val content: String,
    val createdAt: String,
    val imageUrls: List<String>,
    val likeCount: Int,
    val dislikeCount: Int,
    val userLikeType: String,
    val isAuthor: Boolean
)