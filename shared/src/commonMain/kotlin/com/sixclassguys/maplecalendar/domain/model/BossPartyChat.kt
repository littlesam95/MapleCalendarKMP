package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType

data class BossPartyChat(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val senderWorld: String,
    val senderImage: String,
    val content: String,
    val unreadCount: Int,
    val messageType: BossPartyChatMessageType,
    val isMine: Boolean,
    val isDeleted: Boolean,
    val isHidden: Boolean,
    val createdAt: String
)