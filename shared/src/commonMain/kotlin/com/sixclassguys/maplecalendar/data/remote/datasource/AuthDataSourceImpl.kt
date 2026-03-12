package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AuthAppleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthAppleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenRequest
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenResponse
import com.sixclassguys.maplecalendar.data.remote.dto.LoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthDataSourceImpl(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun loginWithApiKey(apiKey: String): LoginResponse {
        return try {
            httpClient.get("auth/characters") {
                header("x-nxopen-api-key", apiKey)

                contentType(ContentType.Application.Json)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun autoLogin(accessToken: String, request: FcmTokenRequest): AuthGoogleResponse {
        return try {
            httpClient.post("member/myinfo") {
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

    override suspend fun loginWithGoogle(request: AuthGoogleRequest): AuthGoogleResponse {
        Napier.d("Request: $request")

        return try {
            httpClient.post("auth/google") {
                setBody(request)

                contentType(ContentType.Application.Json)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun loginWithApple(request: AuthAppleRequest): AuthAppleResponse {
        Napier.d("Request: $request")

        return try {
            httpClient.post("auth/apple") {
                setBody(request)

                contentType(ContentType.Application.Json)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun reissueJwtToken(request: JwtTokenRequest): JwtTokenResponse {
        Napier.d("Request: $request")

        return try {
            httpClient.post("auth/reissue") {
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