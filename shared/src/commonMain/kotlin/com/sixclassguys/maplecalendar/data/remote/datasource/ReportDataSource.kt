package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.ChatReportRequest

interface ReportDataSource {

    suspend fun reportChat(accessToken: String, request: ChatReportRequest)
}