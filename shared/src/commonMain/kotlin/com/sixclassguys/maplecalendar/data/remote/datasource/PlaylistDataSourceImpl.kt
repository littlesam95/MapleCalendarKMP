package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.CreateMapleBgmPlaylistRequest
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmPlaylistResponse
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmPlaylistUpdateRequests
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmResponse
import com.sixclassguys.maplecalendar.data.remote.dto.SliceResponse
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
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

class PlaylistDataSourceImpl(
    private val httpClient: HttpClient
) : PlaylistDataSource {

    override suspend fun getMapleBgmDetail(accessToken: String, bgmId: Long): MapleBgmResponse {
        return try {
            httpClient.get("playlist/bgm/$bgmId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun getTopBgms(
        accessToken: String,
        page: Int,
        size: Int
    ): SliceResponse<MapleBgmResponse> {
        return try {
            httpClient.get("playlist/top") {
                header("Authorization", "Bearer $accessToken")
                parameter("page", page)
                parameter("size", size)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun getRecentBgms(
        accessToken: String,
        page: Int,
        size: Int
    ): SliceResponse<MapleBgmResponse> {
        return try {
            httpClient.get("playlist/recent") {
                header("Authorization", "Bearer $accessToken")
                parameter("page", page)
                parameter("size", size)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun searchBgms(
        accessToken: String,
        query: String,
        page: Int,
        size: Int
    ): SliceResponse<MapleBgmResponse> {
        return try {
            httpClient.get("playlist/search") {
                header("Authorization", "Bearer $accessToken")
                parameter("query", query)
                parameter("page", page)
                parameter("size", size)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun toggleLike(
        accessToken: String,
        bgmId: Long,
        status: MapleBgmLikeStatus
    ): MapleBgmResponse {
        return try {
            httpClient.post("playlist/bgm/$bgmId/like") {
                header("Authorization", "Bearer $accessToken")
                parameter("status", status)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun getMyPlaylists(accessToken: String): List<MapleBgmPlaylistResponse> {
        return try {
            httpClient.get("playlist/mylists") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun getPlaylistDetail(
        accessToken: String,
        playlistId: Long
    ): MapleBgmPlaylistResponse {
        return try {
            httpClient.get("playlist/mylists/$playlistId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun createPlaylist(
        accessToken: String,
        request: CreateMapleBgmPlaylistRequest
    ): List<MapleBgmPlaylistResponse> {
        return try {
            httpClient.post("playlist/mylists") {
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

    override suspend fun deletePlaylist(
        accessToken: String,
        playlistId: Long
    ): List<MapleBgmPlaylistResponse> {
        return try {
            httpClient.delete("playlist/mylists/$playlistId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun addBgmToPlaylist(
        accessToken: String,
        playlistId: Long,
        bgmId: Long
    ): MapleBgmPlaylistResponse {
        return try {
            httpClient.post("playlist/mylists/$playlistId/bgms/$bgmId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun removeBgmFromPlaylist(
        accessToken: String,
        playlistId: Long,
        bgmId: Long
    ): MapleBgmPlaylistResponse {
        return try {
            httpClient.delete("playlist/mylists/$playlistId/bgms/$bgmId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun updatePlaylist(
        accessToken: String,
        playlistId: Long,
        request: MapleBgmPlaylistUpdateRequests
    ): MapleBgmPlaylistResponse {
        return try {
            httpClient.patch("playlist/mylists/$playlistId") {
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