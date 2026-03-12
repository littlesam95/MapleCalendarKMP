package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.ChatReportRequest
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ReportDataSourceImpl(
    private val httpClient: HttpClient
) : ReportDataSource {

    override suspend fun reportChat(accessToken: String, request: ChatReportRequest) {
        return try {
            httpClient.post("reports/chat") {
                header("Authorization", "Bearer $accessToken")
                setBody(request)

                contentType(ContentType.Application.Json)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }
}