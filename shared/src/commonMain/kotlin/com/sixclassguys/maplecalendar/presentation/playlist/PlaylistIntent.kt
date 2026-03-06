package com.sixclassguys.maplecalendar.presentation.playlist

import com.sixclassguys.maplecalendar.RepeatMode
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.domain.model.MapleBgmHistory
import com.sixclassguys.maplecalendar.domain.model.MapleBgmPlaylist
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
import com.sixclassguys.maplecalendar.util.PlaylistTab

sealed class PlaylistIntent {

    data class PlayMapleBgm(val bgm: MapleBgm, val bgms: List<MapleBgm>) : PlaylistIntent()

    data object InitSelectedPlaylist : PlaylistIntent()

    data class FetchMapleBgmDetailSuccess(val bgm: MapleBgm, val bgms: List<MapleBgm>) : PlaylistIntent()

    data class FetchMapleBgmDetailFailure(val message: String) : PlaylistIntent()

    data class TogglePlayPause(val isPlaying: Boolean) : PlaylistIntent()

    data object SkipNext : PlaylistIntent()

    data object SkipPrevious : PlaylistIntent()

    data object ToggleShuffle : PlaylistIntent()

    data class ToggleRepeat(val repeatMode: RepeatMode) : PlaylistIntent()

    data class SeekTo(val position: Long) : PlaylistIntent()

    data object MaximizePlayer : PlaylistIntent()

    data object MinimizePlayer : PlaylistIntent()

    data object ClosePlayer : PlaylistIntent()

    data object FetchTopMapleBgms : PlaylistIntent()

    data class FetchTopMapleBgmsSuccess(val topMapleBgmsHistory: MapleBgmHistory) : PlaylistIntent()

    data class FetchTopMapleBgmsFailure(val message: String) : PlaylistIntent()

    data object FetchRecentMapleBgms : PlaylistIntent()

    data class FetchRecentMapleBgmsSuccess(val recentMapleBgmsHistory: MapleBgmHistory) : PlaylistIntent()

    data class FetchRecentMapleBgmsFailure(val message: String) : PlaylistIntent()

    data class SearchMapleBgms(val query: String) : PlaylistIntent()

    data class SearchMapleBgmsSuccess(val searchedMapleBgmsHistory: MapleBgmHistory) : PlaylistIntent()

    data class SearchMapleBgmsFailure(val message: String) : PlaylistIntent()

    data class ToggleMapleBgmLikeStatus(val status: MapleBgmLikeStatus) : PlaylistIntent()

    data class ToggleMapleBgmLikeStatusSuccess(val mapleBgm: MapleBgm) : PlaylistIntent()

    data class ToggleMapleBgmLikeStatusFailure(val message: String) : PlaylistIntent()

    data object FetchMapleBgmPlaylists : PlaylistIntent()

    data class FetchMapleBgmPlaylistsSuccess(val playlists: List<MapleBgmPlaylist>) : PlaylistIntent()

    data class FetchMapleBgmPlaylistsFailure(val message: String) : PlaylistIntent()

    data class FetchMapleBgmPlaylistDetail(val playlistId: Long) : PlaylistIntent()

    data class FetchMapleBgmPlaylistDetailSuccess(val playlist: MapleBgmPlaylist) : PlaylistIntent()

    data class FetchMapleBgmPlaylistDetailFailure(val message: String) : PlaylistIntent()

    data object ShowNewPlaylistDialog : PlaylistIntent()

    data object DismissNewPlaylistDialog : PlaylistIntent()

    data class UpdateNewMapleBgmPlaylistName(val name: String) : PlaylistIntent()

    data class UpdateNewMapleBgmPlaylistDescription(val description: String) : PlaylistIntent()

    data object UpdateNewMapleBgmPlaylistPublicStatus : PlaylistIntent()

    data object CreateMapleBgmPlaylist : PlaylistIntent()

    data class CreateMapleBgmPlaylistSuccess(val playlists: List<MapleBgmPlaylist>) : PlaylistIntent()

    data class CreateMapleBgmPlaylistFailure(val message: String) : PlaylistIntent()

    data class DeleteMapleBgmPlaylist(val playlistId: Long) : PlaylistIntent()

    data class DeleteMapleBgmPlaylistSuccess(val playlists: List<MapleBgmPlaylist>) : PlaylistIntent()

    data class DeleteMapleBgmPlaylistFailure(val message: String) : PlaylistIntent()

    data object ShowAddMapleBgmToPlaylistDialog : PlaylistIntent()

    data object DismissAddMapleBgmToPlaylistDialog : PlaylistIntent()

    data class UpdatePlaylistToAddMapleBgm(val playlist: MapleBgmPlaylist) : PlaylistIntent()

    data class AddMapleBgmToPlaylist(val playlistId: Long, val bgmId: Long) : PlaylistIntent()

    data class AddMapleBgmToPlaylistSuccess(val playlist: MapleBgmPlaylist) : PlaylistIntent()

    data class AddMapleBgmToPlaylistFailure(val message: String) : PlaylistIntent()

    data class RemoveMapleBgmFromPlaylist(val playlistId: Long, val bgmId: Long) : PlaylistIntent()

    data class RemoveMapleBgmFromPlaylistSuccess(val playlist: MapleBgmPlaylist) : PlaylistIntent()

    data class RemoveMapleBgmFromPlaylistFailure(val message: String) : PlaylistIntent()

    data class UpdateMapleBgmPlaylist(val playlistId: Long, val from: Int, val to: Int) : PlaylistIntent()

    data class UpdateMapleBgmPlaylistSuccess(val playlist: MapleBgmPlaylist) : PlaylistIntent()

    data class UpdateMapleBgmPlaylistFailure(val message: String) : PlaylistIntent()

    data class SelectPlaylistMenu(val selectedPlaylistMenu: PlaylistTab) : PlaylistIntent()

    data object InitErrorMessage : PlaylistIntent()
}