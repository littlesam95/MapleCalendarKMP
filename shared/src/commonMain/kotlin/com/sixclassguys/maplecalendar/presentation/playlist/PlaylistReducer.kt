package com.sixclassguys.maplecalendar.presentation.playlist

import com.sixclassguys.maplecalendar.RepeatMode

class PlaylistReducer {

    fun reduce(currentState: PlaylistUiState, intent: PlaylistIntent): PlaylistUiState = when (intent) {
        is PlaylistIntent.PlayMapleBgm -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.InitSelectedPlaylist -> {
            currentState.copy(
                selectedPlaylist = null
            )
        }

        is PlaylistIntent.FetchMapleBgmDetailSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedBgm = intent.bgm,
                currentPlaylist = intent.bgms,
                showMiniPlayer = true,
                isPlayerMinimized = false,
                isPlaying = true
            )
        }

        is PlaylistIntent.FetchMapleBgmDetailFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.TogglePlayPause -> {
            currentState.copy(
                isLoading = false,
                isPlaying = !currentState.isPlaying
            )
        }

        is PlaylistIntent.SkipNext -> {
            currentState.copy(
                isLoading = false,
            )
        }

        is PlaylistIntent.SkipPrevious -> {
            currentState.copy(
                isLoading = false,
            )
        }

        is PlaylistIntent.ToggleShuffle -> {
            currentState.copy(
                isLoading = false,
                isShuffleEnabled = !currentState.isShuffleEnabled
            )
        }

        is PlaylistIntent.ToggleRepeat -> {
            val nextMode = when (intent.repeatMode) {
                RepeatMode.NONE -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.NONE
            }

            currentState.copy(
                isLoading = false,
                repeatMode = nextMode
            )
        }

        is PlaylistIntent.SeekTo -> {
            currentState.copy(
                isLoading = false,
            )
        }

        is PlaylistIntent.MaximizePlayer -> {
            currentState.copy(
                isLoading = false,
                isPlayerMinimized = false
            )
        }

        is PlaylistIntent.MinimizePlayer -> {
            currentState.copy(
                isLoading = false,
                isPlayerMinimized = true
            )
        }

        is PlaylistIntent.ClosePlayer -> {
            currentState.copy(
                showMiniPlayer = false,
                selectedBgm = null,
                isPlaying = false
            )
        }

        is PlaylistIntent.FetchTopMapleBgms -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.FetchTopMapleBgmsSuccess -> {
            val history = intent.topMapleBgmsHistory

            val combinedTopMapleBgms = (currentState.topMapleBgms + history.bgms)
                .distinctBy { it.id }

            currentState.copy(
                isLoading = false,
                topMapleBgms = combinedTopMapleBgms,
                isTopMapleBgmsLastPage = history.isLastPage,
                topMapleBgmsPage = currentState.topMapleBgmsPage + 1
            )
        }

        is PlaylistIntent.FetchTopMapleBgmsFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.FetchRecentMapleBgms -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.FetchRecentMapleBgmsSuccess -> {
            val history = intent.recentMapleBgmsHistory

            val combinedRecentMapleBgms = (currentState.recentMapleBgms + history.bgms)
                .distinctBy { it.id }

            currentState.copy(
                isLoading = false,
                recentMapleBgms = combinedRecentMapleBgms,
                isRecentMapleBgmsLastPage = history.isLastPage,
                recentMapleBgmsPage = currentState.recentMapleBgmsPage + 1
            )
        }

        is PlaylistIntent.FetchRecentMapleBgmsFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.SearchMapleBgms -> {
            when (intent.query == currentState.searchKeyword) {
                true -> {
                    currentState.copy(
                        isLoading = true,
                        searchKeyword = intent.query,
                    )
                }

                false -> {
                    currentState.copy(
                        isLoading = true,
                        searchKeyword = intent.query,
                        searchedMapleBgms = emptyList(),
                        isSearchedMapleBgmsLastPage = false,
                        searchedMapleBgmsPage = 0
                    )
                }
            }
        }

        is PlaylistIntent.SearchMapleBgmsSuccess -> {
            val history = intent.searchedMapleBgmsHistory

            val combinedSearchedMapleBgms = (currentState.searchedMapleBgms + history.bgms)
                .distinctBy { it.id }

            currentState.copy(
                isLoading = false,
                searchedMapleBgms = combinedSearchedMapleBgms,
                isSearchedMapleBgmsLastPage = history.isLastPage,
                searchedMapleBgmsPage = currentState.searchedMapleBgmsPage + 1
            )
        }

        is PlaylistIntent.SearchMapleBgmsFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.ToggleMapleBgmLikeStatus -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.ToggleMapleBgmLikeStatusSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedBgm = intent.mapleBgm
            )
        }

        is PlaylistIntent.ToggleMapleBgmLikeStatusFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.FetchMapleBgmPlaylists -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.FetchMapleBgmPlaylistsSuccess -> {
            currentState.copy(
                isLoading = false,
                myPlaylists = intent.playlists
            )
        }

        is PlaylistIntent.FetchMapleBgmPlaylistsFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.FetchMapleBgmPlaylistDetail -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.FetchMapleBgmPlaylistDetailSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedPlaylist = intent.playlist
            )
        }

        is PlaylistIntent.FetchMapleBgmPlaylistDetailFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.ShowNewPlaylistDialog -> {
            currentState.copy(
                showNewPlaylistDialog = true
            )
        }

        is PlaylistIntent.DismissNewPlaylistDialog -> {
            currentState.copy(
                showNewPlaylistDialog = false
            )
        }

        is PlaylistIntent.UpdateNewMapleBgmPlaylistName -> {
            currentState.copy(
                newPlaylistName = intent.name
            )
        }

        is PlaylistIntent.UpdateNewMapleBgmPlaylistDescription -> {
            currentState.copy(
                newPlaylistDescription = intent.description
            )
        }

        is PlaylistIntent.UpdateNewMapleBgmPlaylistPublicStatus -> {
            currentState.copy(
                isNewPlaylistPublic = !currentState.isNewPlaylistPublic
            )
        }

        is PlaylistIntent.CreateMapleBgmPlaylist -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.CreateMapleBgmPlaylistSuccess -> {
            currentState.copy(
                isLoading = false,
                myPlaylists = intent.playlists,
                showNewPlaylistDialog = false,
                newPlaylistName = "",
                newPlaylistDescription = "",
                isNewPlaylistPublic = false
            )
        }

        is PlaylistIntent.CreateMapleBgmPlaylistFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.DeleteMapleBgmPlaylist -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.DeleteMapleBgmPlaylistSuccess -> {
            currentState.copy(
                isLoading = false,
                myPlaylists = intent.playlists
            )
        }

        is PlaylistIntent.DeleteMapleBgmPlaylistFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.ShowAddMapleBgmToPlaylistDialog -> {
            currentState.copy(
                showAddMapleBgmToPlaylistDialog = true,
                selectedPlaylistToAdd = currentState.myPlaylists.first()
            )
        }

        is PlaylistIntent.DismissAddMapleBgmToPlaylistDialog -> {
            currentState.copy(
                showAddMapleBgmToPlaylistDialog = false
            )
        }

        is PlaylistIntent.UpdatePlaylistToAddMapleBgm -> {
            currentState.copy(
                selectedPlaylistToAdd = intent.playlist
            )
        }

        is PlaylistIntent.AddMapleBgmToPlaylist -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.AddMapleBgmToPlaylistSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedPlaylist = intent.playlist,
                showAddMapleBgmToPlaylistDialog = false,
                selectedPlaylistToAdd = null
            )
        }

        is PlaylistIntent.AddMapleBgmToPlaylistFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.RemoveMapleBgmFromPlaylist -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.RemoveMapleBgmFromPlaylistSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedPlaylist = intent.playlist,
                currentPlaylist = intent.playlist.bgms
            )
        }

        is PlaylistIntent.RemoveMapleBgmFromPlaylistFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.UpdateMapleBgmPlaylist -> {
            currentState.copy(
                isLoading = true
            )
        }

        is PlaylistIntent.UpdateMapleBgmPlaylistSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedPlaylist = intent.playlist,
                currentPlaylist = intent.playlist.bgms
            )
        }

        is PlaylistIntent.UpdateMapleBgmPlaylistFailure -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is PlaylistIntent.SelectPlaylistMenu -> {
            currentState.copy(
                selectedTab = intent.selectedPlaylistMenu
            )
        }

        is PlaylistIntent.InitErrorMessage -> {
            currentState.copy(
                isLoading = false,
                errorMessage = null
            )
        }
    }
}