package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyBoardResponse(
    val id: Long = 0L,
    val characterId: Long = 0L,
    val characterName: String? = null,
    val characterImage: String? = null,
    val characterClass: String? = null,
    val characterLevel: Long = 0L,
    val content: String? = null,
    val createdAt: String? = null,
    val imageUrls: List<String> = emptyList(),
    val likeCount: Int = 0,
    val dislikeCount: Int = 0,
    val userLikeType: String? = null,
    val isAuthor: Boolean = false
) {

    fun toDomain(): BossPartyBoard {
        return BossPartyBoard(
            id = this.id,
            characterId = this.characterId,
            characterName = this.characterName ?: "",
            characterImage = this.characterImage ?: "",
            characterClass = this.characterClass ?: "",
            characterLevel = this.characterLevel,
            content = this.content ?: "",
            imageUrls = this.imageUrls,
            likeCount = this.likeCount,
            dislikeCount = this.dislikeCount,
            userLikeType = this.userLikeType ?: "",
            isAuthor = this.isAuthor,
            createdAt = this.createdAt ?: ""
        )
    }
}