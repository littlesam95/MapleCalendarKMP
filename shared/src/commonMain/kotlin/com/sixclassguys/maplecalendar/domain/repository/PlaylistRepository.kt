package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.domain.model.MapleBgmHistory
import com.sixclassguys.maplecalendar.domain.model.MapleBgmPlaylist
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun getMapleBgmDetail(bgmId: Long): Flow<ApiState<MapleBgm>>

    suspend fun getTopBgms(page: Int): Flow<ApiState<MapleBgmHistory>>

    suspend fun getRecentBgms(page: Int): Flow<ApiState<MapleBgmHistory>>

    suspend fun searchBgms(query: String, page: Int): Flow<ApiState<MapleBgmHistory>>

    suspend fun toggleLike(bgmId: Long, status: MapleBgmLikeStatus): Flow<ApiState<MapleBgm>>

    suspend fun getMyPlaylists(): Flow<ApiState<List<MapleBgmPlaylist>>>

    suspend fun getPlaylistDetail(playlistId: Long): Flow<ApiState<MapleBgmPlaylist>>

    suspend fun createPlaylist(
        name: String,
        description: String,
        isPublic: Boolean
    ): Flow<ApiState<List<MapleBgmPlaylist>>>

    suspend fun deletePlaylist(playlistId: Long): Flow<ApiState<List<MapleBgmPlaylist>>>

    suspend fun addBgmToPlaylist(playlistId: Long, bgmId: Long): Flow<ApiState<MapleBgmPlaylist>>

    suspend fun removeBgmFromPlaylist(
        playlistId: Long,
        bgmId: Long
    ): Flow<ApiState<MapleBgmPlaylist>>

    suspend fun updatePlaylist(
        playlistId: Long,
        name: String?,
        isPublic: Boolean?,
        bgmIdOrder: List<Long>
    ): Flow<ApiState<MapleBgmPlaylist>>
}