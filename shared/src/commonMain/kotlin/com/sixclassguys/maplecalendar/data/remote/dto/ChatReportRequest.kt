package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.util.ReportReason
import kotlinx.serialization.Serializable

@Serializable
data class ChatReportRequest(
    val chatId: Long,
    val reason: ReportReason,
    val reasonDetail: String? = null
)