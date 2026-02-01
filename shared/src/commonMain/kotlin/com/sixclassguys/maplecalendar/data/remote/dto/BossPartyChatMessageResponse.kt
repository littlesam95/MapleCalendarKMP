package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyChatMessageResponse(
    val id: Long? = null,
    val senderId: Long? = null,
    val senderName: String? = null,
    val senderWorld: String? = null,
    val senderImage: String? = null,
    val content: String? = null,
    val messageType: BossPartyChatMessageType? = null,
    val isMine: Boolean = false, // 조회하는 사람의 ID와 비교하여 서버에서 계산해서 전달
    val isDeleted: Boolean = false,
    val createdAt: String? = null,
) {
    
    fun toDomain(): BossPartyChat {
        return BossPartyChat(
            id = this.id ?: 0L,
            senderId = this.senderId ?: 0L,
            senderName = this.senderName ?: "",
            senderWorld = this.senderWorld ?: "",
            senderImage = this.senderImage ?: "",
            content = this.content ?: "",
            messageType = this.messageType ?: BossPartyChatMessageType.TEXT,
            isMine = this.isMine,
            isDeleted = this.isDeleted,
            createdAt = this.createdAt ?: "1970-01-01"
        )
    }
}