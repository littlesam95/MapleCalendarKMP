package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NotificationDataSourceImpl(
    private val httpClient: HttpClient
) : NotificationDataSource {

    override suspend fun registerToken(request: FcmTokenRequest): HttpResponse {
        // 백엔드 주소는 추후 상수로 관리하기
        return httpClient.post("v1/notifications/tokens") {
            setBody(request)

            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun unregisterToken(apiKey: String, request: FcmTokenRequest): HttpResponse {
        return httpClient.delete("v1/notifications/tokens") {
            header("x-nxopen-api-key", apiKey)
            setBody(request)

            contentType(ContentType.Application.Json)
        }
    }
}