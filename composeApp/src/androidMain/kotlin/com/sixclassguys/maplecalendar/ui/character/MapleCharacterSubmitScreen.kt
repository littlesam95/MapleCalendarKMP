package com.sixclassguys.maplecalendar.ui.character

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterIntent
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.CharacterItemCard
import com.sixclassguys.maplecalendar.ui.component.MapleCharacterCollapsingHeader
import com.sixclassguys.maplecalendar.ui.component.SubmitConfirmButton
import com.sixclassguys.maplecalendar.ui.component.WorldFetchBottomSheet
import com.sixclassguys.maplecalendar.utils.MapleWorld

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapleCharacterSubmitScreen(
    viewModel: MapleCharacterViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. 헤더 높이 및 스크롤 상태 관리
    val density = LocalDensity.current
    val maxHeaderHeightPx = with(density) { 330.dp.toPx() } // 확장 시
    val minHeaderHeightPx = with(density) { 72.dp.toPx() } // 축소 시 (월드 선택기 포함 높이)
    val maxScrollOffsetPx = maxHeaderHeightPx - minHeaderHeightPx

    var toolbarOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx + delta
                toolbarOffsetHeightPx = newOffset.coerceIn(-maxScrollOffsetPx, 0f)
                return Offset.Zero // 리스트 스크롤은 그대로 둠
            }
        }
    }
    val scrollState = rememberLazyGridState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.isSubmitSuccess) {
        if (uiState.isSubmitSuccess) {
            viewModel.onIntent(MapleCharacterIntent.InitNewCharacters)
            viewModel.onIntent(MapleCharacterIntent.InitApiKey)
            onSubmitSuccess()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onIntent(MapleCharacterIntent.InitNewCharacters)
        }
    }

    Scaffold(
        containerColor = MapleWhite,
        bottomBar = {
            // 하단에 그림자나 구분선을 주고 싶다면 Surface로 감쌉니다.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                val isEnabled = uiState.selectedCharacterOcids.isNotEmpty()
                SubmitConfirmButton(
                    isSelected = isEnabled && !uiState.isLoading,
                    onClick = {
                        val allWorlds = MapleWorld.entries.map { it.worldName }
                        viewModel.onIntent(MapleCharacterIntent.SubmitNewCharacters(allWorlds))
                    }
                )
            }
        }
    ) { innerPadding ->
        // 3. 리스트 영역
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .nestedScroll(nestedScrollConnection)
        ) {
            val currentWorldCharacters =
                uiState.newCharacterSummeries[uiState.selectedFetchWorldGroup]?.get(uiState.selectedFetchWorld)
                    ?: emptyList()

            if (currentWorldCharacters.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 330.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_no_data),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${uiState.selectedFetchWorld} 월드에 캐릭터가 없어요.",
                        style = Typography.bodyMedium,
                        color = MapleGray
                    )
                }
            } else {
                val isScrollEnabled = remember(currentWorldCharacters, maxHeaderHeightPx) {
                    val rows = (currentWorldCharacters.size + 2) / 3
                    val itemHeight = 180.dp // 대략적인 카드 높이
                    val totalContentHeight = with(density) { (rows * itemHeight.toPx()) }
                    currentWorldCharacters.size > 6
                }
                LazyVerticalGrid(
                    state = scrollState,
                    columns = GridCells.Fixed(3),
                    userScrollEnabled = isScrollEnabled, // 🚀 여기서 스크롤 여부 결정
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 330.dp, // maxHeaderHeight와 동일하게 설정
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentWorldCharacters) { character ->
                        val isSelected = uiState.selectedCharacterOcids.contains(character.ocid)
                        CharacterItemCard(
                            character = character,
                            characterImage = uiState.characterImages[character.ocid] ?: "",
                            isSelected = isSelected,
                            onClick = {
                                val intent = if (isSelected) {
                                    MapleCharacterIntent.SelectNewCharacterCancel(character.ocid)
                                } else {
                                    MapleCharacterIntent.SelectNewCharacter(character.ocid)
                                }
                                viewModel.onIntent(intent)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MapleCharacterCollapsingHeader(
                uiState = uiState,
                currentHeightPx = maxHeaderHeightPx + toolbarOffsetHeightPx,
                maxHeaderHeightPx = maxHeaderHeightPx,
                minHeaderHeightPx = minHeaderHeightPx,
                onBack = onBack,
                onWorldClick = {
                    viewModel.onIntent(MapleCharacterIntent.ShowFetchWorldSheet(true))
                }
            )
            // 🚀 최상단에 로딩 인디케이터 배치
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
                            text = "캐릭터 정보를 불러오고 있습니다...",
                            color = MapleWhite,
                            style = Typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    if (uiState.showFetchWorldSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onIntent(MapleCharacterIntent.ShowFetchWorldSheet(false))
            },
            sheetState = sheetState,
            containerColor = MapleStatBackground
        ) {
            WorldFetchBottomSheet(
                uiState = uiState,
                onWorldClick = { world ->
                    viewModel.onIntent(MapleCharacterIntent.SelectFetchWorld(world))
                    // 💡 여기서 바로 닫기 Intent를 날려주세요.
                    viewModel.onIntent(MapleCharacterIntent.ShowFetchWorldSheet(false))
                },
                onGroupClick = { group ->
                    viewModel.onIntent(MapleCharacterIntent.SelectFetchWorldGroup(group))
                },
                onDismiss = {
                    viewModel.onIntent(MapleCharacterIntent.ShowFetchWorldSheet(false))
                }
            )
        }
    }
}