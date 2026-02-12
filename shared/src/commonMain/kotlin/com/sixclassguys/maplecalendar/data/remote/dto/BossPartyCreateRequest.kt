package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.serialization.Serializable

@Serializable
data class BossPartyCreateRequest(
    val boss: Boss,
    val difficulty: BossDifficulty,
    val title: String,
    val description: String,
    val characterId: Long,
)