package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenRequest
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenResponse
import com.sixclassguys.maplecalendar.data.remote.dto.LoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import com.sixclassguys.maplecalendar.util.ApiException
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
        val response = try {
            httpClient.get("auth/characters") {
                // 헤더 추가 부분
                header("x-nxopen-api-key", apiKey)

                // Content-Type 설정 (필요 시)
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            throw ApiException(message = "$e: 인터넷 연결을 확인해주세요.")
        }

        return when (response.status.value) {
            in 200..299 -> {
                response.body()
            }
            400 -> throw ApiException(400, "잘못된 요청입니다. 입력값을 확인해주세요.")
            401 -> throw ApiException(401, "인증 정보가 만료되었습니다. 다시 로그인해주세요.")
            404 -> throw ApiException(404, "요청하신 이벤트 데이터를 찾을 수 없습니다.")
            in 500..599 -> {
                throw ApiException(response.status.value, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
            else -> {
                throw ApiException(response.status.value, "알 수 없는 오류가 발생했습니다. (Code: ${response.status.value})")
            }
        }
    }

    override suspend fun autoLogin(accessToken: String, request: FcmTokenRequest): AuthGoogleResponse {
        val response = try {
            httpClient.post("member/myinfo") {
                // 헤더 추가 부분
                header("Authorization", "Bearer $accessToken")
                setBody(request)

                // Content-Type 설정 (필요 시)
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            throw ApiException(message = "$e: 인터넷 연결을 확인해주세요.")
        }

        return when (response.status.value) {
            in 200..299 -> {
                response.body()
            }
            400 -> throw ApiException(400, "잘못된 요청입니다. 입력값을 확인해주세요.")
            401 -> throw ApiException(401, "인증 정보가 만료되었습니다. 다시 로그인해주세요.")
            404 -> throw ApiException(404, "요청하신 이벤트 데이터를 찾을 수 없습니다.")
            in 500..599 -> {
                throw ApiException(response.status.value, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
            else -> {
                throw ApiException(response.status.value, "알 수 없는 오류가 발생했습니다. (Code: ${response.status.value})")
            }
        }
    }

    override suspend fun loginWithGoogle(request: AuthGoogleRequest): AuthGoogleResponse {
        Napier.d("Request: $request")
        val response = try {
            httpClient.post("auth/google") {
                setBody(request)

                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            throw ApiException(message = "$e: 인터넷 연결을 확인해주세요.")
        }
        Napier.d("Status Code ${response.status.value}")

        return when (response.status.value) {
            in 200..299 -> {
                response.body()
            }
            400 -> throw ApiException(400, "잘못된 요청입니다. 입력값을 확인해주세요.")
            401 -> throw ApiException(401, "인증 정보가 만료되었습니다. 다시 로그인해주세요.")
            404 -> throw ApiException(404, "요청하신 이벤트 데이터를 찾을 수 없습니다.")
            in 500..599 -> {
                throw ApiException(response.status.value, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
            else -> {
                throw ApiException(response.status.value, "알 수 없는 오류가 발생했습니다. (Code: ${response.status.value})")
            }
        }
    }

    override suspend fun reissueJwtToken(request: JwtTokenRequest): JwtTokenResponse {
        Napier.d("Request: $request")
        val response = try {
            httpClient.post("auth/reissue") {
                setBody(request)

                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            throw ApiException(message = "$e: 인터넷 연결을 확인해주세요.")
        }
        Napier.d("Status Code ${response.status.value}")

        return when (response.status.value) {
            in 200..299 -> {
                response.body()
            }
            400 -> throw ApiException(400, "잘못된 요청입니다. 입력값을 확인해주세요.")
            401 -> throw ApiException(401, "인증 정보가 만료되었습니다. 다시 로그인해주세요.")
            404 -> throw ApiException(404, "요청하신 이벤트 데이터를 찾을 수 없습니다.")
            in 500..599 -> {
                throw ApiException(response.status.value, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
            }
            else -> {
                throw ApiException(response.status.value, "알 수 없는 오류가 발생했습니다. (Code: ${response.status.value})")
            }
        }
    }
}