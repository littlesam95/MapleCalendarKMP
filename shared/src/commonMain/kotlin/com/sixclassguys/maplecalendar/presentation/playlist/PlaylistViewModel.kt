package com.sixclassguys.maplecalendar.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.MusicPlayer
import com.sixclassguys.maplecalendar.RepeatMode
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.domain.usecase.AddMapleBgmToPlaylistUseCase
import com.sixclassguys.maplecalendar.domain.usecase.CreateMapleBgmPlaylistUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DeleteMapleBgmPlaylistUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMapleBgmDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMapleBgmPlaylistDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMapleBgmPlaylistsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetRecentMapleBgmsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetTopMapleBgmsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RemoveMapleBgmFromPlaylistUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SearchMapleBgmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleMapleBgmLikeUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UpdateMapleBgmPlaylistUseCase
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val reducer: PlaylistReducer,
    private val getMapleBgmDetailUseCase: GetMapleBgmDetailUseCase,
    private val getTopMapleBgmsUseCase: GetTopMapleBgmsUseCase,
    private val getRecentMapleBgmsUseCase: GetRecentMapleBgmsUseCase,
    private val searchMapleBgmUseCase: SearchMapleBgmUseCase,
    private val toggleMapleBgmLikeUseCase: ToggleMapleBgmLikeUseCase,
    private val getMapleBgmPlaylistsUseCase: GetMapleBgmPlaylistsUseCase,
    private val getMapleBgmPlaylistDetailUseCase: GetMapleBgmPlaylistDetailUseCase,
    private val createMapleBgmPlaylistUseCase: CreateMapleBgmPlaylistUseCase,
    private val deleteMapleBgmPlaylistUseCase: DeleteMapleBgmPlaylistUseCase,
    private val addMapleBgmToPlaylistUseCase: AddMapleBgmToPlaylistUseCase,
    private val removeMapleBgmFromPlaylistUseCase: RemoveMapleBgmFromPlaylistUseCase,
    private val updateMapleBgmPlaylistUseCase: UpdateMapleBgmPlaylistUseCase,
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState())
    val uiState = _uiState.asStateFlow()

    val currentPosition = musicPlayer.currentPosition
    val duration = musicPlayer.duration

    init {
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            // 재생 여부 관찰
            musicPlayer.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlaying = playing) }
            }
        }

        viewModelScope.launch {
            // 현재 곡 ID 관찰
            musicPlayer.currentTrackId.collect { trackId ->
                if (trackId != null) {
                    // [수정] currentPlaylist를 최우선으로 탐색해야 합니다.
                    val nextBgm = uiState.value.currentPlaylist.find { it.id == trackId }
                        ?: uiState.value.topMapleBgms.find { it.id == trackId }
                        ?: uiState.value.recentMapleBgms.find { it.id == trackId }

                    if (nextBgm != null) {
                        _uiState.update { it.copy(selectedBgm = nextBgm) }
                    }
                }
            }
        }
    }

    private fun playBgm(clickedBgm: MapleBgm, currentList: List<MapleBgm>) {
        val index = currentList.indexOf(clickedBgm)
        if (index != -1) {
            musicPlayer.play(currentList, index)
        }
    }

    private fun seekTo(positionMs: Long) {
        musicPlayer.seekTo(positionMs)
    }

    // 또는 비율(0.0 ~ 1.0)로 받고 싶을 때
    private fun seekToRatio(ratio: Float, totalDuration: Long) {
        val targetMs = (ratio * totalDuration).toLong()
        musicPlayer.seekTo(targetMs)
    }

    // PlaylistViewModel.kt 내부에 추가

    private fun togglePlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
        // isPlaying 상태는 musicPlayer.isPlaying Flow를 통해 자동으로 업데이트
    }

    private fun skipToNext() {
        musicPlayer.skipToNext()
    }

    private fun skipToPrevious() {
        musicPlayer.skipToPrevious()
    }

    private fun toggleShuffle() {
        musicPlayer.toggleShuffle()
        // 이전에 설정한 musicPlayer.isShuffleModeEnabled Flow가 UI를 갱신
    }

    private fun toggleRepeatMode(currentMode: RepeatMode) {
        val nextMode = when (currentMode) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        musicPlayer.setRepeatMode(nextMode)
        // 이 역시 musicPlayer.repeatMode Flow를 통해 UI에 반영됩니다.
    }

    private fun stopPlayer() {
        musicPlayer.stop()
    }

    private fun replacePlaylist(bgms: List<MapleBgm>) {
        musicPlayer.replaceQueue(bgms)
    }

    private fun fetchMapleBgmDetail(bgmId: Long, currentList: List<MapleBgm>) {
        viewModelScope.launch {
            getMapleBgmDetailUseCase(bgmId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.FetchMapleBgmDetailSuccess(state.data, currentList))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.FetchMapleBgmDetailFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchTopMapleBgms(page: Int) {
        viewModelScope.launch {
            getTopMapleBgmsUseCase(page).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.FetchTopMapleBgmsSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.FetchTopMapleBgmsFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchRecentMapleBgms(page: Int) {
        viewModelScope.launch {
            getRecentMapleBgmsUseCase(page).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.FetchRecentMapleBgmsSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.FetchRecentMapleBgmsFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun searchMapleBgms(query: String, page: Int) {
        viewModelScope.launch {
            searchMapleBgmUseCase(query, page).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.SearchMapleBgmsSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.SearchMapleBgmsFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun toggleMapleBgmLikeStatus(bgmId: Long, likeStatus: MapleBgmLikeStatus) {
        viewModelScope.launch {
            toggleMapleBgmLikeUseCase(bgmId, likeStatus).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.ToggleMapleBgmLikeStatusSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.ToggleMapleBgmLikeStatusFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchMapleBgmPlaylists() {
        viewModelScope.launch {
            getMapleBgmPlaylistsUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.FetchMapleBgmPlaylistsSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.FetchMapleBgmPlaylistsFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchMapleBgmPlaylistDetail(playlistId: Long) {
        viewModelScope.launch {
            getMapleBgmPlaylistDetailUseCase(playlistId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.FetchMapleBgmPlaylistDetailSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.FetchMapleBgmPlaylistDetailFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun createMapleBgmPlaylist(name: String, description: String, isPublic: Boolean) {
        viewModelScope.launch {
            createMapleBgmPlaylistUseCase(name, description, isPublic).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.CreateMapleBgmPlaylistSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.CreateMapleBgmPlaylistFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun deleteMapleBgmPlaylist(playlistId: Long) {
        viewModelScope.launch {
            deleteMapleBgmPlaylistUseCase(playlistId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.DeleteMapleBgmPlaylistSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.DeleteMapleBgmPlaylistFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun addMapleBgmToPlaylist(playlistId: Long, bgmId: Long) {
        viewModelScope.launch {
            addMapleBgmToPlaylistUseCase(playlistId, bgmId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.AddMapleBgmToPlaylistSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.AddMapleBgmToPlaylistFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun removeMapleBgmToPlaylist(playlistId: Long, bgmId: Long) {
        viewModelScope.launch {
            removeMapleBgmFromPlaylistUseCase(playlistId, bgmId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.RemoveMapleBgmFromPlaylistSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.RemoveMapleBgmFromPlaylistFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun updateMapleBgmToPlaylist(playlistId: Long, from: Int, to: Int) {
        if (playlistId == 0L) return

        viewModelScope.launch {
            _uiState.update { currentState ->
                val newList = currentState.currentPlaylist.toMutableList().apply {
                    add(to, removeAt(from))
                }
                currentState.copy(currentPlaylist = newList)
            }
            val orderedIds = _uiState.value.currentPlaylist.map { it.id }
            updateMapleBgmPlaylistUseCase(playlistId, null, null, orderedIds).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(PlaylistIntent.UpdateMapleBgmPlaylistSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(PlaylistIntent.UpdateMapleBgmPlaylistFailure(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onIntent(intent: PlaylistIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is PlaylistIntent.PlayMapleBgm -> {
                fetchMapleBgmDetail(intent.bgm.id, intent.bgms)
            }

            is PlaylistIntent.FetchMapleBgmDetailSuccess -> {
                playBgm(intent.bgm, intent.bgms)
            }

            is PlaylistIntent.TogglePlayPause -> {
                togglePlayPause(intent.isPlaying)
            }

            is PlaylistIntent.SkipNext -> {
                skipToNext()
            }

            is PlaylistIntent.SkipPrevious -> {
                skipToPrevious()
            }

            is PlaylistIntent.ToggleShuffle -> {
                toggleShuffle()
            }

            is PlaylistIntent.ToggleRepeat -> {
                toggleRepeatMode(intent.repeatMode)
            }

            is PlaylistIntent.SeekTo -> {
                seekTo(intent.position)
            }

            is PlaylistIntent.ClosePlayer -> {
                stopPlayer()
            }

            is PlaylistIntent.FetchTopMapleBgms -> {
                fetchTopMapleBgms(_uiState.value.topMapleBgmsPage)
            }

            is PlaylistIntent.FetchRecentMapleBgms -> {
                fetchRecentMapleBgms(_uiState.value.recentMapleBgmsPage)
            }

            is PlaylistIntent.SearchMapleBgms -> {
                if (intent.query.isNotEmpty()) {
                    searchMapleBgms(intent.query, _uiState.value.searchedMapleBgmsPage)
                }
            }

            is PlaylistIntent.ToggleMapleBgmLikeStatus -> {
                toggleMapleBgmLikeStatus(_uiState.value.selectedBgm?.id ?: 0L, intent.status)
            }

            is PlaylistIntent.FetchMapleBgmPlaylists -> {
                fetchMapleBgmPlaylists()
            }

            is PlaylistIntent.FetchMapleBgmPlaylistDetail -> {
                fetchMapleBgmPlaylistDetail(intent.playlistId)
            }

            is PlaylistIntent.FetchMapleBgmPlaylistDetailSuccess -> {
                val firstBgm = intent.playlist.bgms.first()
                fetchMapleBgmDetail(firstBgm.id, intent.playlist.bgms)
            }

            is PlaylistIntent.CreateMapleBgmPlaylist -> {
                createMapleBgmPlaylist(
                    name = _uiState.value.newPlaylistName,
                    description = _uiState.value.newPlaylistDescription,
                    isPublic = _uiState.value.isNewPlaylistPublic
                )
            }

            is PlaylistIntent.DeleteMapleBgmPlaylist -> {
                deleteMapleBgmPlaylist(intent.playlistId)
            }

            is PlaylistIntent.AddMapleBgmToPlaylist -> {
                addMapleBgmToPlaylist(intent.playlistId, intent.bgmId)
            }

            is PlaylistIntent.RemoveMapleBgmFromPlaylist -> {
                removeMapleBgmToPlaylist(intent.playlistId, intent.bgmId)
            }

            is PlaylistIntent.RemoveMapleBgmFromPlaylistSuccess -> {
                replacePlaylist(intent.playlist.bgms)
            }

            is PlaylistIntent.UpdateMapleBgmPlaylist -> {
                updateMapleBgmToPlaylist(
                    intent.playlistId,
                    intent.from,
                    intent.to
                )
            }

            is PlaylistIntent.UpdateMapleBgmPlaylistSuccess -> {
                replacePlaylist(intent.playlist.bgms)
            }

            else -> {}
        }
    }
}