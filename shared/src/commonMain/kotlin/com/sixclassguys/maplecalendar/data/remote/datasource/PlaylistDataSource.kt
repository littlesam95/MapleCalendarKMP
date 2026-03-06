package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.CreateMapleBgmPlaylistRequest
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmPlaylistResponse
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmPlaylistUpdateRequests
import com.sixclassguys.maplecalendar.data.remote.dto.MapleBgmResponse
import com.sixclassguys.maplecalendar.data.remote.dto.SliceResponse
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus

interface PlaylistDataSource {

    suspend fun getMapleBgmDetail(
        accessToken: String,
        bgmId: Long
    ): MapleBgmResponse

    suspend fun getTopBgms(
        accessToken: String,
        page: Int,
        size: Int = 20
    ): SliceResponse<MapleBgmResponse>

    suspend fun getRecentBgms(
        accessToken: String,
        page: Int,
        size: Int = 20
    ): SliceResponse<MapleBgmResponse>

    suspend fun searchBgms(
        accessToken: String,
        query: String,
        page: Int,
        size: Int = 20
    ): SliceResponse<MapleBgmResponse>

    suspend fun toggleLike(
        accessToken: String,
        bgmId: Long,
        status: MapleBgmLikeStatus
    ): MapleBgmResponse

    suspend fun getMyPlaylists(
        accessToken: String
    ): List<MapleBgmPlaylistResponse>

    suspend fun getPlaylistDetail(
        accessToken: String,
        playlistId: Long
    ): MapleBgmPlaylistResponse

    suspend fun createPlaylist(
        accessToken: String,
        request: CreateMapleBgmPlaylistRequest
    ): List<MapleBgmPlaylistResponse>

    suspend fun deletePlaylist(
        accessToken: String,
        playlistId: Long
    ): List<MapleBgmPlaylistResponse>

    suspend fun addBgmToPlaylist(
        accessToken: String,
        playlistId: Long,
        bgmId: Long
    ): MapleBgmPlaylistResponse

    suspend fun removeBgmFromPlaylist(
        accessToken: String,
        playlistId: Long,
        bgmId: Long
    ): MapleBgmPlaylistResponse

    suspend fun updatePlaylist(
        accessToken: String,
        playlistId: Long,
        request: MapleBgmPlaylistUpdateRequests
    ): MapleBgmPlaylistResponse
}