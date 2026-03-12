package com.sixclassguys.maplecalendar.ui.playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistIntent
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.EmptyEventScreen

@Composable
fun SearchMapleBgmScreen(
    viewModel: PlaylistViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current // 1. 포커스 매니저 선언
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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
        if (message != null) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(PlaylistIntent.InitMessage)
        }
    }

    LaunchedEffect(shouldFetchNextPage.value) {
        if ((uiState.searchedMapleBgms.isEmpty() || shouldFetchNextPage.value) && !uiState.isLoading && !uiState.isSearchedMapleBgmsLastPage) {
            viewModel.onIntent(PlaylistIntent.SearchMapleBgms(uiState.searchKeyword))
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.searchKeyword,
                onQueryChange = { viewModel.onIntent(PlaylistIntent.SearchMapleBgms(it)) },
                focusRequester = focusRequester
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MapleWhite
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.searchedMapleBgms.isEmpty() && uiState.searchKeyword.isBlank()) {
                EmptyEventScreen("검색 결과가 없어요.")
            } else {
                // 검색 결과가 있을 때
                Text(
                    text = "\'${uiState.searchKeyword}\'에 대한\n검색 결과에요.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.searchedMapleBgms) { bgm ->
                        BgmItem(
                            bgm = bgm,
                            onNavigateToBgmPlay = {
                                focusManager.clearFocus()
                                viewModel.onIntent(PlaylistIntent.InitSelectedPlaylist)
                                viewModel.onIntent(PlaylistIntent.PlayMapleBgm(bgm, uiState.searchedMapleBgms))
                                viewModel.onIntent(PlaylistIntent.MaximizePlayer)
                            },
                            onMoreClick = { /* 상세 메뉴 */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 와이어프레임의 깔끔한 입력창 구현
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(fontSize = 18.sp, color = MapleBlack),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "BGM의 제목이나 지역명을 입력하세요",
                            color = MapleGray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(28.dp),
                tint = MapleBlack
            )
        }
        HorizontalDivider(color = MapleBlack, thickness = 1.dp)
    }
}