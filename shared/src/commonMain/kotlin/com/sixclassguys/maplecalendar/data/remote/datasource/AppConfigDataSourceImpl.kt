package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AppVersionResponse
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AppConfigDataSourceImpl(
    private val httpClient: HttpClient
) : AppConfigDataSource {

    override suspend fun checkVersion(platform: String, versionCode: Int): AppVersionResponse {
        return try {
            httpClient.get("app-config/version-check") {
                parameter("platform", platform)
                parameter("versionCode", versionCode)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }
}