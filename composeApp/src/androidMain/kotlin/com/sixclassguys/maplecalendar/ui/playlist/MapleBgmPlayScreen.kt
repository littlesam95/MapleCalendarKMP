package com.sixclassguys.maplecalendar.ui.playlist

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.RepeatMode
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistIntent
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.AddMusicDialog
import com.sixclassguys.maplecalendar.util.MapleBgmLikeStatus
import com.sixclassguys.maplecalendar.utils.formatTime
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MapleBgmPlayScreen(
    viewModel: PlaylistViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentPos by viewModel.currentPosition.collectAsStateWithLifecycle(0L)
    val duration by viewModel.duration.collectAsStateWithLifecycle(0L)

    var isTransitioning by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var isPlaylistVisible by remember { mutableStateOf(uiState.selectedPlaylist != null) }

    LaunchedEffect(uiState.successMessage) {
        val message = uiState.successMessage
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(PlaylistIntent.InitMessage)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(PlaylistIntent.InitMessage)
        }
    }

    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 500L) return@BackHandler // 0.5초 이내 연타 무시

        if (isPlaylistVisible) {
            isPlaylistVisible = false
        } else {
            lastClickTime = currentTime // 시간 기록
            viewModel.onIntent(PlaylistIntent.MinimizePlayer)
            onBack()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MapleWhite
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = isPlaylistVisible,
                    label = "PlayerArea"
                ) { targetVisible ->
                    if (targetVisible) {
                        // [압축된 헤더] 와이어프레임 상단: 앨범아트, 곡정보, 재생버튼
                        CompressedPlayerHeader(
                            bgm = uiState.selectedBgm,
                            isPlaying = uiState.isPlaying,
                            onTogglePlay = { viewModel.onIntent(PlaylistIntent.TogglePlayPause(uiState.isPlaying)) }
                        )
                    } else {
                        // [기존 확장된 화면] 기존에 작성했던 1~6번 전체 레이아웃
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .background(MapleWhite)
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = padding.calculateBottomPadding()
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 1. Top Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {
                                    val currentTime = System.currentTimeMillis()
                                    if (currentTime - lastClickTime < 500L) return@IconButton // 0.5초 이내 연타 무시

                                    if (isPlaylistVisible) {
                                        isPlaylistVisible = false
                                    } else {
                                        lastClickTime = currentTime // 시간 기록
                                        viewModel.onIntent(PlaylistIntent.MinimizePlayer)
                                        onBack()
                                    }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Close")
                                }
                                IconButton(onClick = { /* 메뉴 팝업 */ }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                }
                            }

                            Spacer(Modifier.height(32.dp))

                            // 2. 앨범 아트 (지역 아이콘)
                            Surface(
                                modifier = Modifier.size(280.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(4.dp, MapleBlack), // 와이어프레임의 굵은 테두리
                            ) {
                                AsyncImage(
                                    model = uiState.selectedBgm?.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            Spacer(Modifier.height(48.dp))

                            // 3. 곡 정보 (제목, 지역)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = uiState.selectedBgm?.title ?: "재생 중인 곡 없음",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.selectedBgm?.mapName ?: "-",
                                    fontSize = 16.sp,
                                    color = MapleGray
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            // 4. 반응 및 저장 버튼 레이아웃
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isLiked = uiState.selectedBgm?.likeStatus == MapleBgmLikeStatus.LIKE
                                    Icon(
                                        imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = null,
                                        tint = if (isLiked) MapleOrange else MapleGray,
                                        modifier = Modifier.size(20.dp)
                                            .clickable {
                                                viewModel.onIntent(
                                                    PlaylistIntent.ToggleMapleBgmLikeStatus(
                                                        MapleBgmLikeStatus.LIKE
                                                    )
                                                )
                                            }
                                    )
                                    Text(
                                        text = "${uiState.selectedBgm?.likeCount ?: 0}",
                                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                                    )

                                    val isDisliked = uiState.selectedBgm?.likeStatus == MapleBgmLikeStatus.DISLIKE
                                    Icon(
                                        imageVector = if (isDisliked) Icons.Default.ThumbDown else Icons.Outlined.ThumbDown,
                                        contentDescription = null,
                                        tint = if (isDisliked) MapleBlack else MapleGray,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                viewModel.onIntent(
                                                    PlaylistIntent.ToggleMapleBgmLikeStatus(
                                                        MapleBgmLikeStatus.DISLIKE
                                                    )
                                                )
                                            }
                                    )
                                    Text(
                                        text = "${uiState.selectedBgm?.dislikeCount ?: 0}",
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Row(
                                    modifier = Modifier.clickable {
                                        if (uiState.myPlaylists.isEmpty()) {
                                            Toast.makeText(context, "플레이리스트가 비어있어요.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.onIntent(PlaylistIntent.ShowAddMapleBgmToPlaylistDialog)
                                        }
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null)
                                    Text(" 내 플레이리스트에 저장", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(Modifier.height(32.dp))

                            // 5. 슬라이더 및 시간
                            PlayerSlider(currentPos, duration, onSeek = { viewModel.onIntent(PlaylistIntent.SeekTo(it)) })

                            Spacer(Modifier.weight(1f))

                            // 6. 메인 컨트롤러 (셔플, 이전, 재생, 다음, 반복)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 40.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = { viewModel.onIntent(PlaylistIntent.ToggleShuffle) }) {
                                    Icon(
                                        Icons.Default.Shuffle,
                                        tint = if (uiState.isShuffleEnabled) MapleOrange else MapleGray,
                                        contentDescription = ""
                                    )
                                }
                                IconButton(onClick = { viewModel.onIntent(PlaylistIntent.SkipPrevious) }) {
                                    Icon(
                                        Icons.Default.SkipPrevious, modifier = Modifier.size(36.dp),
                                        contentDescription = "",
                                        tint = MapleGray
                                    )
                                }

                                // 재생/정지 큰 버튼
                                Surface(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clickable {
                                            viewModel.onIntent(
                                                PlaylistIntent.TogglePlayPause(
                                                    uiState.isPlaying
                                                )
                                            )
                                        },
                                    shape = CircleShape,
                                    color = MapleOrange
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.padding(16.dp),
                                        tint = MapleWhite
                                    )
                                }

                                IconButton(onClick = { viewModel.onIntent(PlaylistIntent.SkipNext) }) {
                                    Icon(
                                        Icons.Default.SkipNext,
                                        modifier = Modifier.size(36.dp),
                                        contentDescription = ""
                                    )
                                }
                                IconButton(onClick = { viewModel.onIntent(PlaylistIntent.ToggleRepeat(uiState.repeatMode)) }) {
                                    Icon(
                                        imageVector = if (uiState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                        tint = if (uiState.repeatMode != RepeatMode.NONE) MapleOrange else MapleGray,
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }
                }

                if (isPlaylistVisible) {
                    PlaylistPanelExpanded(
                        currentBgm = uiState.selectedBgm,
                        playlist = uiState.currentPlaylist,
                        onToggle = { isPlaylistVisible = false },
                        onItemClick = { bgm ->
                            viewModel.onIntent(PlaylistIntent.PlayMapleBgm(bgm,uiState.currentPlaylist))
                        },
                        onMove = { from, to ->
                            if (uiState.currentPlaylist.size > 1) {
                                viewModel.onIntent(PlaylistIntent.UpdateMapleBgmPlaylist(uiState.selectedPlaylist?.id ?: 0L, from, to))
                            }
                        },
                        onRemove = { mapleBgm ->
                            if (uiState.selectedBgm?.id == mapleBgm.id) {
                                Toast.makeText(context, "현재 재생 중인 곡은 삭제할 수 없어요.", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.onIntent(PlaylistIntent.RemoveMapleBgmFromPlaylist(uiState.selectedPlaylist?.id ?: 0L, mapleBgm.id))
                            }
                        }
                    )
                }
            }

            if (!isPlaylistVisible) {
                PlaylistCollapsedBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onToggle = { isPlaylistVisible = true }
                )
            }
        }
    }

    if (uiState.showAddMapleBgmToPlaylistDialog) {
        AddMusicDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.onIntent(PlaylistIntent.DismissAddMapleBgmToPlaylistDialog) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSlider(
    currentPos: Long,
    totalDuration: Long,
    onSeek: (Long) -> Unit
) {
    val sliderValue = if (totalDuration > 0) currentPos.toFloat() / totalDuration else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = sliderValue,
            onValueChange = { newValue -> onSeek((newValue * totalDuration).toLong()) },
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                Surface(
                    modifier = Modifier.size(18.dp),
                    shape = CircleShape,
                    color = MapleOrange
                ) {}
            },
            track = { sliderState ->
                val fraction = sliderState.value
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MapleGray, CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .background(color = MapleOrange, shape = CircleShape)
                    )
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentPos), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(formatTime(totalDuration), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ColumnScope.PlaylistPanelExpanded(
    currentBgm: MapleBgm?,
    playlist: List<MapleBgm>,
    onToggle: () -> Unit,
    onItemClick: (MapleBgm) -> Unit,
    onMove: (Int, Int) -> Unit, // Reorderable 전용 타입
    onRemove: (MapleBgm) -> Unit
) {
    val listState = rememberLazyListState()
    val state = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            // suspend 함수이므로 내부적으로 scope 처리가 되거나
            // 단순히 ViewModel 함수를 호출하면 됩니다.
            onMove(from.index, to.index)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f) // 상단 헤더 빼고 남은 공간 다 차지
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Color(0xFF373E44))
    ) {
        // 상단 닫기 핸들러
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFFE9FF8A))
                Spacer(Modifier.width(8.dp))
                Text("PLAYLIST", color = Color(0xFFE9FF8A), fontWeight = FontWeight.Bold)
            }
        }

        // 흰색 리스트 카드
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("현재 재생 목록", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlist, key = { it.id }) { bgm ->
                        // 1. 상태 생성 (더 이상 rememberSwipeToDismissBoxState를 쓰지 않음)
                        val dismissState = remember {
                            SwipeToDismissBoxState(
                                initialValue = SwipeToDismissBoxValue.Settled,
                                positionalThreshold = { distance -> distance * 0.5f } // 50% 밀었을 때
                            )
                        }

                        // 2. 삭제 로직 실행 (상태가 끝에 도달했을 때 처리)
                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                onRemove(bgm)
                                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                            }
                        }

                        ReorderableItem(state, key = bgm.id) { isDragging ->
                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val isDismissing = dismissState.targetValue != SwipeToDismissBoxValue.Settled
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isDismissing) Color.Red.copy(alpha = 0.8f) else Color.Transparent),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        if (isDismissing) {
                                            Icon(Icons.Default.Delete, null, tint = Color.White, modifier = Modifier.padding(16.dp))
                                        }
                                    }
                                },
                                content = {
                                    PlaylistItemRow(
                                        bgm = bgm,
                                        isPlaying = bgm.id == currentBgm?.id,
                                        isDragging = isDragging,
                                        onClick = { onItemClick(bgm) },
                                        modifier = Modifier
                                            .animateItem(),
                                        handleModifier = Modifier.draggableHandle(
                                            onDragStarted = {
                                                // 드래그 시작 시 햅틱 피드백(진동)을 넣고 싶으면 여기에 작성
                                            },
                                            onDragStopped = {
                                                // 드래그 종료 시 서버 동기화 함수 호출
                                                // viewModel.syncOrderToServer()
                                            }
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistCollapsedBar(
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp) // 하단에 살짝 보이는 높이
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clickable { onToggle() },
        color = Color(0xFF373E44) // 패널과 동일한 진회색
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = Color(0xFFE9FF8A) // 연두색 포인트
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "PLAYLIST",
                color = Color(0xFFE9FF8A),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun PlaylistItemRow(
    bgm: MapleBgm,
    isPlaying: Boolean,
    isDragging: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    handleModifier: Modifier = Modifier
) {
    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

    Surface(
        shadowElevation = elevation,
        shape = RoundedCornerShape(8.dp),
        color = if (isDragging) MapleGray else if (isPlaying) MapleOrange.copy(alpha = 0.1f) else MapleWhite
    ) {
        Row(
            modifier = modifier.fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPlaying) MapleOrange.copy(alpha = 0.2f) else Color.Transparent)
                .clickable { onClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = bgm.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bgm.title,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) MapleOrange else Color.Black
                )
                Text(text = bgm.mapName, fontSize = 12.sp, color = Color.Gray)
            }

            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = null,
                tint = MapleGray,
                modifier = handleModifier.size(36.dp)
                    .padding(8.dp) // 여기서 드래그 감지
            )
        }
    }
}

@Composable
fun CompressedPlayerHeader(
    bgm: MapleBgm?,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 작은 앨범 아트
        Surface(
            modifier = Modifier.size(50.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            AsyncImage(
                model = bgm?.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = bgm?.title ?: "재생 중 아님",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = bgm?.mapName ?: "-",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // 우측 재생/정지 버튼
        IconButton(onClick = onTogglePlay) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MapleOrange
            )
        }
    }
}