package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.CharacterAuthorityResponse
import com.sixclassguys.maplecalendar.data.remote.dto.CharacterRegisterRequest
import com.sixclassguys.maplecalendar.data.remote.dto.MapleCharacterListResponse
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MapleCharacterDataSourceImpl(
    private val httpClient: HttpClient
) : MapleCharacterDataSource {
    override suspend fun getCharacters(accessToken: String): MapleCharacterListResponse {
        return try {
            httpClient.get("character/list") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun fetchFromNexon(
        accessToken: String,
        apiKey: String
    ): MapleCharacterListResponse {
        return try {
            httpClient.get("character/fetch") {
                header("Authorization", "Bearer $accessToken")
                header("x-nxopen-api-key", apiKey)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun registerCharacters(
        accessToken: String,
        request: CharacterRegisterRequest
    ): MapleCharacterListResponse {
        return try {
            httpClient.post("character/register") {
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

    override suspend fun checkAuthority(
        accessToken: String,
        ocid: String
    ): CharacterAuthorityResponse {
        return try {
            httpClient.get("character/${ocid}/check-authority") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun setRepresentative(accessToken: String, ocid: String) {
        return try {
            httpClient.patch("character/${ocid}/representative") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun deleteCharacter(accessToken: String, ocid: String) {
        return try {
            httpClient.delete("character/$ocid") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun searchCharacters(
        accessToken: String,
        name: String
    ): MapleCharacterListResponse {
        return try {
            httpClient.get("character/search") {
                header("Authorization", "Bearer $accessToken")
                parameter("name", name)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }
}