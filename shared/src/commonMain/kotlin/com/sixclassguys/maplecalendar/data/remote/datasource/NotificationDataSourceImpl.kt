package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
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
        return httpClient.post("notifications/tokens") {
            setBody(request)

            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun unregisterToken(accessToken: String, request: FcmTokenRequest) {
        return try {
            httpClient.delete("notifications/tokens") {
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