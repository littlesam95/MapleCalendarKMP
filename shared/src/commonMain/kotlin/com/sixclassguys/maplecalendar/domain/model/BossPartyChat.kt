package com.sixclassguys.maplecalendar.domain.model

import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType

data class BossPartyChat(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val senderWorld: String,
    val senderImage: String,
    val content: String,
    val messageType: BossPartyChatMessageType,
    val isMine: Boolean,
    val isDeleted: Boolean,
    val createdAt: String
)