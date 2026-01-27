package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.CharacterBasicResponse
import com.sixclassguys.maplecalendar.util.ApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NexonOpenApiDataSourceImpl(
    private val httpClient: HttpClient
) : NexonOpenApiDataSource {

    override suspend fun getCharacterBasic(ocid: String): CharacterBasicResponse {
        val response = try {
            httpClient.get("character/basic") {
                parameter("ocid", ocid)

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
}