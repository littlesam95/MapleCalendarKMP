package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.PlaylistDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.CreateMapleBgmPlaylistRequest
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmPlaylistUpdateRequests
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.domain.model.MapleBgmHistory
import com.sixclassguys.maplecalendar.domain.model.MapleBgmPlaylist
import com.sixclassguys.maplecalendar.domain.repository.PlaylistRepository
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dataSource: PlaylistDataSource,
    private val dataStore: AppPreferences
) : PlaylistRepository {

    override suspend fun getMapleBgmDetail(bgmId: Long): Flow<ApiState<MapleBgm>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getMapleBgmDetail(
                accessToken = accessToken,
                bgmId = bgmId
            )

            val bgm = response.toDomain()

            emit(ApiState.Success(bgm))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun getTopBgms(page: Int): Flow<ApiState<MapleBgmHistory>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getTopBgms(
                accessToken = accessToken,
                page = page
            )

            val bgms = response.content.map { it.toDomain() }

            emit(ApiState.Success(
                MapleBgmHistory(
                    bgms = bgms,
                    isLastPage = response.last
                )
            ))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun getRecentBgms(page: Int): Flow<ApiState<MapleBgmHistory>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getRecentBgms(
                accessToken = accessToken,
                page = page
            )

            val bgms = response.content.map { it.toDomain() }

            emit(ApiState.Success(
                MapleBgmHistory(
                    bgms = bgms,
                    isLastPage = response.last
                )
            ))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun searchBgms(query: String, page: Int): Flow<ApiState<MapleBgmHistory>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.searchBgms(
                accessToken = accessToken,
                query = query,
                page = page
            )

            val bgms = response.content.map { it.toDomain() }

            emit(ApiState.Success(
                MapleBgmHistory(
                    bgms = bgms,
                    isLastPage = response.last
                )
            ))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun toggleLike(
        bgmId: Long,
        status: MapleBgmLikeStatus
    ): Flow<ApiState<MapleBgm>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.toggleLike(
                accessToken = accessToken,
                bgmId = bgmId,
                status = status
            )

            val bgm = response.toDomain()

            emit(ApiState.Success(bgm))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun getMyPlaylists(): Flow<ApiState<List<MapleBgmPlaylist>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getMyPlaylists(accessToken)

            val playlists = response.map { it.toDomain() }

            emit(ApiState.Success(playlists))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun getPlaylistDetail(playlistId: Long): Flow<ApiState<MapleBgmPlaylist>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getPlaylistDetail(
                accessToken = accessToken,
                playlistId = playlistId
            )

            val playlist = response.toDomain()

            emit(ApiState.Success(playlist))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun createPlaylist(
        name: String,
        description: String,
        isPublic: Boolean
    ): Flow<ApiState<List<MapleBgmPlaylist>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.createPlaylist(
                accessToken = accessToken,
                request = CreateMapleBgmPlaylistRequest(
                    name = name,
                    description = description,
                    isPublic = isPublic
                ),
            )

            val playlists = response.map { it.toDomain() }

            emit(ApiState.Success(playlists))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun deletePlaylist(playlistId: Long): Flow<ApiState<List<MapleBgmPlaylist>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.deletePlaylist(
                accessToken = accessToken,
                playlistId = playlistId
            )

            val playlists = response.map { it.toDomain() }

            emit(ApiState.Success(playlists))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun addBgmToPlaylist(
        playlistId: Long,
        bgmId: Long
    ): Flow<ApiState<MapleBgmPlaylist>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.addBgmToPlaylist(
                accessToken = accessToken,
                playlistId = playlistId,
                bgmId = bgmId
            )

            val playlist = response.toDomain()

            emit(ApiState.Success(playlist))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun removeBgmFromPlaylist(
        playlistId: Long,
        bgmId: Long
    ): Flow<ApiState<MapleBgmPlaylist>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.removeBgmFromPlaylist(
                accessToken = accessToken,
                playlistId = playlistId,
                bgmId = bgmId
            )

            val playlist = response.toDomain()

            emit(ApiState.Success(playlist))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String?,
        isPublic: Boolean?,
        bgmIdOrder: List<Long>
    ): Flow<ApiState<MapleBgmPlaylist>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.updatePlaylist(
                accessToken = accessToken,
                playlistId = playlistId,
                request = MapleBgmPlaylistUpdateRequests(
                    name = name,
                    isPublic = isPublic,
                    bgmIdOrder = bgmIdOrder
                ),
            )

            val playlist = response.toDomain()

            emit(ApiState.Success(playlist))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }
}