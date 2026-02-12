package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.util.ReportReason
import kotlinx.coroutines.flow.Flow

interface ReportRepository {

    suspend fun chatReport(
        chatId: Long,
        reason: ReportReason,
        reasonDetail: String?
    ): Flow<ApiState<Unit>>
}