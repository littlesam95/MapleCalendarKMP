package com.sixclassguys.maplecalendar.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterIntent
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.MapleCharacterGrid
import com.sixclassguys.maplecalendar.ui.component.MapleCharacterListHeader
import com.sixclassguys.maplecalendar.ui.component.MapleCharacterSelectTitle
import com.sixclassguys.maplecalendar.ui.component.WorldGroupTabs
import com.sixclassguys.maplecalendar.ui.component.WorldIconRow
import com.sixclassguys.maplecalendar.utils.MapleWorld

@Composable
fun MapleCharacterListScreen(
    viewModel: MapleCharacterViewModel,
    onBackClick: () -> Unit,
    onNavigateToFetch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    val allWorlds = MapleWorld.entries.map { it.worldName }

    LaunchedEffect(Unit) {
        viewModel.onIntent(MapleCharacterIntent.FetchCharacters(allWorlds))
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(MapleCharacterIntent.InitApiKey)
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(MapleCharacterIntent.InitMessage)
        }
    }

    // 1. 전체 배경을 하얗게 설정하고 Scaffold로 구조화
    Scaffold(
        topBar = {
            MapleCharacterListHeader(onBack = { onBackClick() })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MapleWhite
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.onIntent(MapleCharacterIntent.PullToRefresh(allWorlds)) },
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = MapleOrange,
                    containerColor = MapleWhite
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 2. 배경 이미지 (카드 뒤쪽 레이어)
                AsyncImage(
                    model = uiState.backgroundImageUrl ?: "",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 3. 메인 어두운 컨테이너 (MapleStatBackground)
                Surface(
                    modifier = Modifier.padding(16.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(24.dp),
                    color = MapleStatBackground
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // "CHARACTER SELECT" 타이틀 부분
                        MapleCharacterSelectTitle(onAddClick = { onNavigateToFetch() })

                        // 4. 흰색 배경의 콘텐츠 영역 (이 내부가 와이어프레임의 흰 부분)
                        Surface(
                            modifier = Modifier.fillMaxSize()
                                .padding(16.dp),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 24.dp,
                                bottomEnd = 24.dp
                            ),
                            color = MapleWhite
                        ) {
                            Column {
                                // 고정 영역: 월드 그룹 탭
                                if (uiState.characterSummeries.keys.isNotEmpty()) {
                                    WorldGroupTabs(
                                        groups = uiState.characterSummeries.keys.toList(),
                                        selectedGroup = uiState.selectedWorldGroup,
                                        onGroupSelected = { viewModel.onIntent(MapleCharacterIntent.SelectWorldGroup(it, uiState.characterSummeries[it]?.keys?.first()!!)) }
                                    )
                                }

                                // 와이어프레임의 구분선
                                HorizontalDivider(thickness = 1.dp, color = MapleGray.copy(alpha = 0.5f))

                                // 고정 영역: 세부 월드 아이콘
                                WorldIconRow(
                                    worlds = uiState.characterSummeries[uiState.selectedWorldGroup]?.keys?.toList() ?: emptyList(),
                                    selectedWorld = uiState.selectedWorld,
                                    onWorldSelected = { viewModel.onIntent(MapleCharacterIntent.SelectWorld(it)) }
                                )

                                // 5. 스크롤 영역: CharacterGrid
                                // Column 내부에서 나머지 공간을 꽉 채우도록 weight(1f) 부여
                                Box(modifier = Modifier.weight(1f)) {
                                    MapleCharacterGrid(
                                        characters = uiState.characterSummeries[uiState.selectedWorldGroup]?.get(uiState.selectedWorld) ?: emptyList()
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MapleBlack.copy(alpha = 0.7f)) // 화면 어둡게 처리
                            .pointerInput(Unit) {}, // 터치 이벤트 전파 방지 (클릭 막기)
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MapleOrange,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "캐릭터 정보를 불러오는 중이에요...",
                                color = MapleWhite,
                                style = Typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}