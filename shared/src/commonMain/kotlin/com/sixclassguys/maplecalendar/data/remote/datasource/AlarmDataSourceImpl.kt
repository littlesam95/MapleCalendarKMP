package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AlarmRequest
import com.sixclassguys.maplecalendar.data.remote.dto.EventResponse
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AlarmDataSourceImpl(
    private val httpClient: HttpClient
) : AlarmDataSource {

    override suspend fun submitEventAlarm(accessToken: String, request: AlarmRequest): EventResponse {
        return try {
            httpClient.post("alarms/event") {
                // 헤더 추가 부분
                header("Authorization", "Bearer $accessToken")
                setBody(request)

                // Content-Type 설정 (필요 시)
                contentType(ContentType.Application.Json)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e // 이미 파싱된 에러는 그대로 던짐
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun toggleEventAlarm(accessToken: String, eventId: Long): EventResponse {
        return try {
            httpClient.patch("alarms/toggle/$eventId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }
}