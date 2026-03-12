package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.ReportDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.ChatReportRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.ReportRepository
import com.sixclassguys.maplecalendar.util.ReportReason
import com.sixclassguys.maplecalendar.util.handleApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ReportRepositoryImpl(
    private val dataSource: ReportDataSource,
    private val dataStore: AppPreferences
) : ReportRepository {

    override suspend fun chatReport(
        chatId: Long,
        reason: ReportReason,
        reasonDetail: String?
    ): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.reportChat(
            accessToken = accessToken,
            request = ChatReportRequest(
                chatId = chatId,
                reason = reason,
                reasonDetail = reasonDetail
            )
        )

        emit(ApiState.Success(Unit, "파티원을 신고했어요."))
    }.handleApiError()
}