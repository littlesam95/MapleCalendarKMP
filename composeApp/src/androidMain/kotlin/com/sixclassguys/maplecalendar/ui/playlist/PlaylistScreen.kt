package com.sixclassguys.maplecalendar.ui.playlist

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.domain.model.MapleBgm
import com.sixclassguys.maplecalendar.domain.model.MapleBgmPlaylist
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistIntent
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.AddMyPlaylistDialog
import com.sixclassguys.maplecalendar.util.PlaylistTab
import com.sixclassguys.maplecalendar.utils.RegionCategory

@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToBgmPlay: () -> Unit,
    onNavigateToSearchBgm: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    // 1. 스크롤 끝 감지 로직 (안전한 lastOrNull 사용)
    val shouldFetchNextPage = remember {
        derivedStateOf {
            val layoutInfo = scrollState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            // 리스트가 비어있지 않을 때만 계산하여 NoSuchElementException 방지
            val lastVisibleItem = visibleItemsInfo.lastOrNull()

            if (lastVisibleItem == null) {
                false
            } else {
                // 전체 아이템 개수에서 5개 전쯤 도달했을 때 미리 로딩
                lastVisibleItem.index >= layoutInfo.totalItemsCount - 5
            }
        }
    }

    // 2. 데이터 요청 통합 관리 (최초 로딩 + 페이징)
    LaunchedEffect(uiState.selectedTab, shouldFetchNextPage.value) {
        val isCurrentTabEmpty = when (uiState.selectedTab) {
            PlaylistTab.TOP -> uiState.topMapleBgms.isEmpty()
            PlaylistTab.RECENT -> uiState.recentMapleBgms.isEmpty()
            else -> false
        }

        val isLastPage = when (uiState.selectedTab) {
            PlaylistTab.TOP -> uiState.isTopMapleBgmsLastPage
            PlaylistTab.RECENT -> uiState.isRecentMapleBgmsLastPage
            else -> true
        }

        // 데이터가 없거나 스크롤이 끝에 닿았을 때 (로딩 중이 아니고 마지막 페이지가 아닐 때만)
        if ((isCurrentTabEmpty || shouldFetchNextPage.value) && !uiState.isLoading && !isLastPage) {
            when (uiState.selectedTab) {
                PlaylistTab.TOP -> viewModel.onIntent(PlaylistIntent.FetchTopMapleBgms)
                PlaylistTab.RECENT -> viewModel.onIntent(PlaylistIntent.FetchRecentMapleBgms)
                else -> {}
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        val message = uiState.successMessage
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(PlaylistIntent.InitMessage)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if ((message != null) && !uiState.showAddMapleBgmToPlaylistDialog && !uiState.showNewPlaylistDialog) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(PlaylistIntent.InitMessage)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(PlaylistIntent.FetchMapleBgmPlaylists)
    }

    Scaffold(
        topBar = {
            PlaylistTopBar(
                onAddPlaylist = { viewModel.onIntent(PlaylistIntent.ShowNewPlaylistDialog) },
                onSearchClick = onNavigateToSearchBgm
            )
        },
        containerColor = MapleWhite
    ) { padding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {
            // 탭 메뉴 (Sticky Header)
            stickyHeader {
                PlaylistTabRow(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { tab ->
                        viewModel.onIntent(PlaylistIntent.SelectPlaylistMenu(tab))
                    }
                )
            }

            // 선택된 탭에 따른 컨텐츠
            when (uiState.selectedTab) {
                PlaylistTab.TOP -> {
                    itemsIndexed(
                        items = uiState.topMapleBgms,
                        key = { _, bgm -> "top_${bgm.id}" } // 안정적인 스크롤을 위한 Key 추가
                    ) { index, bgm ->
                        BgmItem(
                            rank = index + 1,
                            bgm = bgm,
                            onNavigateToBgmPlay = {
                                viewModel.onIntent(PlaylistIntent.InitSelectedPlaylist)
                                viewModel.onIntent(PlaylistIntent.PlayMapleBgm(bgm, uiState.topMapleBgms))
                                onNavigateToBgmPlay()
                            },
                            onMoreClick = { /* 메뉴 팝업 */ }
                        )
                    }
                }

                PlaylistTab.RECENT -> {
                    items(
                        items = uiState.recentMapleBgms,
                        key = { bgm -> "recent_${bgm.id}" }
                    ) { bgm ->
                        BgmItem(
                            bgm = bgm,
                            onNavigateToBgmPlay = {
                                viewModel.onIntent(PlaylistIntent.InitSelectedPlaylist)
                                viewModel.onIntent(PlaylistIntent.PlayMapleBgm(bgm, uiState.recentMapleBgms))
                                onNavigateToBgmPlay()
                            }
                        )
                    }
                }

                PlaylistTab.MYPLAYLIST -> {
                    items(
                        items = uiState.myPlaylists,
                        key = { it.id }
                    ) { playlist ->
                        MyPlaylistItem(
                            playlist = playlist,
                            onClick = { playlist ->
                                if (playlist.bgms.isEmpty()) {
                                    Toast.makeText(context, "플레이리스트에 음악이 없어요.", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.onIntent(PlaylistIntent.FetchMapleBgmPlaylistDetail(playlist.id))
                                    onNavigateToBgmPlay()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showNewPlaylistDialog) {
        AddMyPlaylistDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.onIntent(PlaylistIntent.DismissNewPlaylistDialog) }
        )
    }
}

@Composable
fun BgmItem(
    rank: Int? = null,
    bgm: MapleBgm,
    onNavigateToBgmPlay: (MapleBgm) -> Unit,
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            .clickable { onNavigateToBgmPlay(bgm) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 순위 표시 (인기 차트일 때만)
        rank?.let {
            val badgeColor = when(it) {
                1 -> MapleOrange
                2 -> MapleGray.copy(alpha = 0.5f)
                3 -> Color(0xFFE67E22) // 동메달 색상
                else -> Color(0xFF34495E)
            }
            Box(
                modifier = Modifier.size(32.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(it.toString(), color = MapleWhite, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
        }

        // 2. 지역 아이콘 (Thumbnail)
        Image(
            painter = painterResource(RegionCategory.fromCode(bgm.region).iconRes),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )

        Spacer(Modifier.width(12.dp))

        // 3. 곡 정보
        Column(modifier = Modifier.weight(1f)) {
            Text(bgm.title, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(bgm.mapName, color = MapleGray, fontSize = 12.sp)
        }

        // 4. 더보기 버튼
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = MapleBlack)
        }
    }
}

@Composable
fun PlaylistTabRow(
    selectedTab: PlaylistTab,
    onTabSelected: (PlaylistTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MapleWhite)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PlaylistTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Text(
                text = tab.title,
                modifier = Modifier.clickable { onTabSelected(tab) },
                color = if (isSelected) MapleBlack else MapleGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun PlaylistTopBar(
    onAddPlaylist: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "플레이리스트",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MapleBlack
            )
        }

        // 플레이리스트 추가 버튼
        Row {
            IconButton(onClick = onAddPlaylist) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Playlist",
                    modifier = Modifier.size(32.dp),
                    tint = MapleBlack
                )
            }

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(32.dp),
                    tint = MapleBlack
                )
            }
        }
    }
}

@Composable
fun MyPlaylistItem(
    playlist: MapleBgmPlaylist,
    onClick: (MapleBgmPlaylist) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(playlist) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 4구역 썸네일 박스
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MapleGray.copy(alpha = 0.1f))
        ) {
            val regions = playlist.bgms.map { it.region }.distinct().take(4)

            if (regions.isEmpty()) {
                // 곡이 없을 때 기본 아이콘
                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = MapleGray)
            } else {
                // 2x2 그리드로 지역 아이콘 배치
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.weight(1f)) {
                        RegionThumbnail(regions.getOrNull(0), Modifier.weight(1f))
                        RegionThumbnail(regions.getOrNull(1), Modifier.weight(1f))
                    }
                    Row(Modifier.weight(1f)) {
                        RegionThumbnail(regions.getOrNull(2), Modifier.weight(1f))
                        RegionThumbnail(regions.getOrNull(3), Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        // 2. 플레이리스트 정보
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MapleBlack
            )
        }

        // 3. 공개 여부 표시 (비공개일 때만 자물쇠 표시 등)
        if (!playlist.isPublic) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Private",
                modifier = Modifier.size(16.dp),
                tint = MapleGray
            )
        }
    }
}

@Composable
fun RegionThumbnail(region: String?, modifier: Modifier) {
    val category = RegionCategory.fromCode(region)

    Image(
        painter = painterResource(category.iconRes),
        contentDescription = category.displayName,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}