package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyChatMessageRequest(
    val bossPartyId: Long,
    val characterId: Long,
    val content: String,
    val messageType: BossPartyChatMessageType
)