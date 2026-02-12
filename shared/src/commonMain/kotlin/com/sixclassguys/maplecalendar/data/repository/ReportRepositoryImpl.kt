package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.ReportDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.ChatReportRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.ReportRepository
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.ReportReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

        try {
            val accessToken = dataStore.accessToken.first()
            dataSource.reportChat(
                accessToken = accessToken,
                request = ChatReportRequest(
                    chatId = chatId,
                    reason = reason,
                    reasonDetail = reasonDetail
                )
            )

            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }
}