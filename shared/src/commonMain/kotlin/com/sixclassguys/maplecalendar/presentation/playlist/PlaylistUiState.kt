package com.sixclassguys.maplecalendar.presentation.playlist

import com.sixclassguys.maplecalendar.RepeatMode
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.domain.model.MapleBgmPlaylist
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
import com.sixclassguys.maplecalendar.util.PlaylistTab

data class PlaylistUiState(
    val isLoading: Boolean = false,
    val selectedTab: PlaylistTab = PlaylistTab.TOP,
    val topMapleBgms: List<MapleBgm> = emptyList(),
    val isTopMapleBgmsLastPage: Boolean = false,
    val topMapleBgmsPage: Int = 0,
    val recentMapleBgms: List<MapleBgm> = emptyList(),
    val isRecentMapleBgmsLastPage: Boolean = false,
    val recentMapleBgmsPage: Int = 0,
    val myPlaylists: List<MapleBgmPlaylist> = emptyList(),
    val searchKeyword: String = "",
    val searchedMapleBgms: List<MapleBgm> = emptyList(),
    val isSearchedMapleBgmsLastPage: Boolean = false,
    val searchedMapleBgmsPage: Int = 0,

    // Player 관련 상태
    val isPlaying: Boolean = false,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,

    // 현재 재생중인 곡
    val currentTrackId: Long? = null,
    val selectedBgm: MapleBgm? = null,
    val currentPlaylist: List<MapleBgm> = emptyList(),

    // 곡 추가정보
    val likeCount: Long = 0,
    val dislikeCount: Long = 0,
    val myReaction: MapleBgmLikeStatus = MapleBgmLikeStatus.NONE,

    // 플레이어 최소화
    val isPlayerMinimized: Boolean = true, // 플레이어 최소화 여부
    val showMiniPlayer: Boolean = false,    // 미니 플레이어를 보여줄지 여부 (곡이 선택되면 true)

    // 내 플레이리스트
    val selectedPlaylist: MapleBgmPlaylist? = null,
    val showNewPlaylistDialog: Boolean = false,
    val newPlaylistName: String = "",
    val newPlaylistDescription: String = "",
    val isNewPlaylistPublic: Boolean = false,
    val showAddMapleBgmToPlaylistDialog: Boolean = false,
    val selectedPlaylistToAdd: MapleBgmPlaylist? = null,

    val errorMessage: String? = null
)