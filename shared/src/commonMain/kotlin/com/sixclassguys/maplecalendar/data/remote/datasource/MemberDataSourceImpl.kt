package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.RepresentativeOcidRequest
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MemberDataSourceImpl(
    private val httpClient: HttpClient
) : MemberDataSource {

    override suspend fun submitRepresentativeCharacter(
        apiKey: String,
        request: RepresentativeOcidRequest
    ) {
        return try {
            httpClient.patch("member/representative") {
                header("x-nxopen-api-key", apiKey)
                setBody(request)

                contentType(ContentType.Application.Json)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun toggleGlobalAlarmStatus(accessToken: String): Boolean {
        return try {
            httpClient.patch("member/alarm-status") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }
}