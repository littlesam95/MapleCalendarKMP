package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.ReportRepository
import com.sixclassguys.maplecalendar.util.ReportReason
import kotlinx.coroutines.flow.Flow

class ReportBossPartyChatUseCase(
    private val repository: ReportRepository
) {

    suspend operator fun invoke(
        chatId: Long,
        reason: ReportReason,
        reasonDetail: String?
    ): Flow<ApiState<Unit>> {
        return repository.chatReport(chatId, reason, reasonDetail)
    }
}