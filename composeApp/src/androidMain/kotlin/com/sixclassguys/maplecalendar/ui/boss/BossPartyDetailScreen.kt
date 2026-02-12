package com.sixclassguys.maplecalendar.ui.boss

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.BossPartyAlarmContent
import com.sixclassguys.maplecalendar.ui.component.BossPartyAlarmSettingDialog
import com.sixclassguys.maplecalendar.ui.component.BossPartyAlbumContent
import com.sixclassguys.maplecalendar.ui.component.BossPartyBoardUploadDialog
import com.sixclassguys.maplecalendar.ui.component.BossPartyChatContent
import com.sixclassguys.maplecalendar.ui.component.BossPartyChatReportDialog
import com.sixclassguys.maplecalendar.ui.component.BossPartyCollapsingHeader
import com.sixclassguys.maplecalendar.ui.component.BossPartyDetailTabRow
import com.sixclassguys.maplecalendar.ui.component.BossPartyMemberContent
import com.sixclassguys.maplecalendar.ui.component.CharacterInviteDialog
import com.sixclassguys.maplecalendar.util.BossPartyTab
import org.koin.compose.getKoin

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BossPartyDetailScreen(
    viewModel: BossViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState() // 리스트형 컨텐츠를 위해 LazyListState 사용
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    // 높이 설정 (제공해주신 상수 기준 적용)
    val collapsedTopBarHeight = 48.dp
    val expandedTopBarHeight = 420.dp
    val inputBarHeight = 80.dp // 하단 입력바 예상 높이

    val configuration = LocalConfiguration.current

    // 1. 시스템 바 높이 추출 (상단 상태바 + 하단 네비게이션 바)
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val systemBarsHeight =
        systemBarsPadding.calculateTopPadding() + systemBarsPadding.calculateBottomPadding()

    // 2. 전체 화면 높이 (Dp)
    val screenHeightDp = configuration.screenHeightDp.dp

    val density = LocalDensity.current
    val collapsedHeightPx = with(density) { collapsedTopBarHeight.toPx() }
    val expandedHeightPx = with(density) { expandedTopBarHeight.toPx() }
    val maxScrollOffsetPx = expandedHeightPx - collapsedHeightPx

    var toolbarOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    // NestedScrollConnection 설정
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // 1. 내려갈 때 (Scroll Down): 헤더를 먼저 접습니다.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0) { // 위로 쓸어올릴 때 (화면은 아래로 내려감)
                    val oldOffset = toolbarOffsetHeightPx
                    val newOffset = (oldOffset + delta).coerceIn(-maxScrollOffsetPx, 0f)
                    val consumed = newOffset - oldOffset
                    toolbarOffsetHeightPx = newOffset
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            // 2. 올라올 때 (Scroll Up): 내부 리스트가 더 이상 올라갈 곳이 없을 때만 헤더를 펼칩니다.
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                if (delta > 0) { // 아래로 쓸어내릴 때 (화면은 위로 올라옴)
                    val oldOffset = toolbarOffsetHeightPx
                    val newOffset = (oldOffset + delta).coerceIn(-maxScrollOffsetPx, 0f)
                    val consumedValue = newOffset - oldOffset
                    toolbarOffsetHeightPx = newOffset
                    return Offset(0f, consumedValue)
                }
                return Offset.Zero
            }
        }
    }

    val scrollPercentage = -toolbarOffsetHeightPx / maxScrollOffsetPx

    val eventBus = getKoin().get<NotificationEventBus>()

    LaunchedEffect(Unit) {
        eventBus.kickedPartyId.collect { kickedId ->
            if (kickedId == uiState.selectedBossParty?.id) {
                viewModel.onIntent(BossIntent.FetchBossParties)
                Toast.makeText(context, "파티에서 추방되었어요.", Toast.LENGTH_SHORT).show()
                eventBus.emitKickedPartyId(null)
                onBack()
            }
        }
    }

    LaunchedEffect(Unit) {
        scrollState.scrollToItem(0)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // 앱이 포그라운드로 돌아왔을 때 실행
                    // 현재 연결 상태를 체크한 뒤 연결이 끊겨있다면 다시 연결 시도
                    viewModel.onIntent(BossIntent.ConnectBossPartyChat)
                }

                Lifecycle.Event.ON_PAUSE -> {
                    // 필요 시 백그라운드 진입 시 로직 (보통은 그대로 둠)
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            viewModel.onIntent(BossIntent.DisconnectBossPartyChat)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = MapleWhite
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            // 메인 컨텐츠 (알림, 파티원, 채팅, 게시판)
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(), // 🚀 offset 제거
                contentPadding = PaddingValues(
                    // 🚀 헤더가 확장된 높이만큼 상단 패딩을 주어 시작 지점을 맞춥니다.
                    top = with(density) { (expandedHeightPx + toolbarOffsetHeightPx).toDp() },
                    // 🚀 채팅 탭일 때만 입력바 높이만큼 하단 패딩 부여
                    bottom = if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) inputBarHeight else 16.dp
                )
            ) {
                // 탭 메뉴 (Sticky Header)
                stickyHeader {
                    BossPartyDetailTabRow(
                        selectedTab = uiState.selectedBossPartyDetailMenu,
                        onTabSelected = { menu ->
                            viewModel.onIntent(BossIntent.SelectBossPartyDetailMenu(menu))
                        }
                    ) // 알림, 파티원, 채팅, 게시판 전환 탭
                }

                // 현재 선택된 탭에 따른 컨텐츠 표시
                when (uiState.selectedBossPartyDetailMenu) {
                    BossPartyTab.ALARM -> {
                        item {
                            val availableHeight =
                                screenHeightDp - systemBarsHeight - collapsedTopBarHeight - 48.dp
                            BossPartyAlarmContent(
                                alarms = uiState.bossPartyAlarmTimes,
                                isAlarmOn = uiState.isBossPartyDetailAlarmOn,
                                snackbarHostState = snackbarHostState,
                                onToggleAlarm = {
                                    viewModel.onIntent(BossIntent.ToggleBossPartyAlarm)
                                },
                                onAddAlarm = { viewModel.onIntent(BossIntent.ShowAlarmCreateDialog) },
                                onDeleteAlarm = { viewModel.onIntent(BossIntent.DeleteBossPartyAlarm(it)) },
                                modifier = Modifier.fillMaxWidth()
                                    .height(availableHeight)
                            )
                        }
                    }

                    BossPartyTab.MEMBER -> {
                        item {
                            val availableHeight =
                                screenHeightDp - systemBarsHeight - collapsedTopBarHeight - 48.dp
                            BossPartyMemberContent(
                                isLeader = uiState.selectedBossParty?.isLeader ?: false,
                                members = uiState.selectedBossParty?.members ?: emptyList(),
                                onAddMember = { viewModel.onIntent(BossIntent.ShowCharacterInviteDialog) },
                                onTransferLeader = { characterId ->
                                    viewModel.onIntent(BossIntent.TransferBossPartyLeader(characterId))
                                },
                                onRemoveMember = { characterId ->
                                    viewModel.onIntent(BossIntent.KickBossPartyMember(characterId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(availableHeight)
                            )
                        }
                    }

                    BossPartyTab.CHAT -> {
                        item {
                            val availableHeight =
                                screenHeightDp - systemBarsHeight - collapsedTopBarHeight - 48.dp - inputBarHeight
                            BossPartyChatContent(
                                isAlarmOn = uiState.isBossPartyChatAlarmOn,
                                chats = uiState.bossPartyChats,
                                chatUiItems = uiState.bossPartyChatUiItems,
                                snackbarHostState = snackbarHostState,
                                isLastPage = uiState.isBossPartyChatLastPage,
                                isLeader = uiState.selectedBossParty?.isLeader ?: false,
                                isLoading = uiState.isLoading,
                                onLoadMore = { viewModel.onIntent(BossIntent.FetchBossPartyChatHistory) },
                                onToggleAlarm = { viewModel.onIntent(BossIntent.ToggleBossPartyChatAlarm) },
                                onHide = { bossPartyChatId ->
                                    viewModel.onIntent(
                                        BossIntent.HideBossPartyChatMessage(
                                            bossPartyChatId
                                        )
                                    )
                                },
                                onReport = { chat ->
                                    viewModel.onIntent(BossIntent.ShowBossPartyChatReportDialog(chat))
                                },
                                onDelete = { bossPartyChatId ->
                                    viewModel.onIntent(
                                        BossIntent.DeleteBossPartyChatMessage(
                                            bossPartyChatId
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .height(availableHeight)
                            )
                        }
                    }

                    BossPartyTab.ALBUM -> {
                        item {
                            val availableHeight =
                                screenHeightDp - systemBarsHeight - collapsedTopBarHeight - 48.dp
                            BossPartyAlbumContent(
                                posts = uiState.bossPartyBoards,
                                isLastPage = uiState.isBossPartyBoardLastPage,
                                isLoading = uiState.isLoading,
                                onLoadMore = {
                                    viewModel.onIntent(BossIntent.FetchBossPartyBoardHistory)
                                },
                                onSubmitBoard = {
                                    viewModel.onIntent(BossIntent.ShowBossPartyBoardDialog)
                                },
                                onLike = { postId ->
                                    viewModel.onIntent(BossIntent.LikeBossPartyBoardPost(postId))
                                },
                                onDislike = { postId ->
                                    viewModel.onIntent(BossIntent.DislikeBossPartyBoardPost(postId))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(availableHeight)
                            )
                        }
                    }
                }
            }

            // 2. 하단 고정 입력창 (CHAT 탭일 때만 노출)
            if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) {
                // Surface나 Box로 감싸고 align(Alignment.BottomCenter) 부여
                Surface(
                    modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                        .align(Alignment.BottomCenter) // 하단 고정
                        .fillMaxWidth()
                        .height(inputBarHeight),
                    color = MapleStatBackground // 와이어프레임의 어두운 배경색 유지
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = uiState.bossPartyChatMessage,
                            onValueChange = {
                                viewModel.onIntent(
                                    BossIntent.UpdateBossPartyChatMessage(
                                        it
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("메시지를 입력하세요", color = MapleGray) },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MapleWhite,
                                unfocusedContainerColor = MapleWhite,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        val isSendEnabled = uiState.bossPartyChatMessage.isNotBlank()
                        Button(
                            enabled = isSendEnabled,
                            onClick = {
                                if (isSendEnabled) viewModel.onIntent(BossIntent.SendBossPartyChatMessage)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSendEnabled) MapleOrange else MapleGray),
                            modifier = Modifier.size(50.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = if (isSendEnabled) MapleWhite else MapleBlack
                            )
                        }
                    }
                }
            }

            // 🚀 Collapsing Header 적용
            BossPartyCollapsingHeader(
                uiState = uiState,
                currentHeightPx = expandedHeightPx + toolbarOffsetHeightPx,
                scrollPercentage = scrollPercentage,
                onBack = onBack,
                onShare = { /* 공유 로직 */ },
                onDelete = { viewModel.onIntent(BossIntent.LeaveBossParty) }
            )
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
                    text = "보스 파티 정보를 불러오는 중이에요...",
                    color = MapleWhite,
                    style = Typography.bodyLarge
                )
            }
        }
    }

    if (uiState.showBossAlarmDialog) {
        BossPartyAlarmSettingDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.onIntent(BossIntent.DismissBossPartyCreateDialog) }
        )
    }

    if (uiState.showCharacterInvitationDialog) {
        CharacterInviteDialog(
            onDismiss = { viewModel.onIntent(BossIntent.DismissCharacterInviteDialog) },
            viewModel = viewModel
        )
    }

    if (uiState.showBossPartyChatReport && uiState.selectBossPartyChatToReport != null) {
        BossPartyChatReportDialog(
            chat = uiState.selectBossPartyChatToReport,
            onDismiss = { viewModel.onIntent(BossIntent.DismissBossPartyChatReportDialog) },
            onReportSubmit = { chatId, reason, reasonDetail ->
                viewModel.onIntent(BossIntent.ReportBossPartyChatMessage(chatId, reason, reasonDetail))
            }
        )
    }

    if (uiState.showBossPartyBoardDialog) {
        BossPartyBoardUploadDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.onIntent(BossIntent.DismissBossPartyBoardDialog) }
        )
    }
}