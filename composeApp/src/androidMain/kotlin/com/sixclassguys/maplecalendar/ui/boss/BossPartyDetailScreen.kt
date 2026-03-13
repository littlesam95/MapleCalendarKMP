package com.sixclassguys.maplecalendar.ui.boss

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleTheme
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
import kotlinx.coroutines.launch
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
    val pullToRefreshState = rememberPullToRefreshState()
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
    var availableContentHeightPx by remember { mutableIntStateOf(0) }
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

    val scope = rememberCoroutineScope()
    val launcherToAlarm = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onIntent(BossIntent.ToggleBossPartyAlarm)
        } else {
            // 권한이 거부되었을 때
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "알림 권한을 허용하셔야 알림을 받을 수 있어요.",
                    actionLabel = "설정",
                    duration = SnackbarDuration.Long
                )

                // 사용자가 '설정' 버튼을 눌렀을 때 앱 정보 화면으로 이동
                if (result == SnackbarResult.ActionPerformed) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
    val launcherToChat = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onIntent(BossIntent.ToggleBossPartyChatAlarm)
        } else {
            // 권한이 거부되었을 때
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "알림 권한을 허용하셔야 알림을 받을 수 있어요.",
                    actionLabel = "설정",
                    duration = SnackbarDuration.Long
                )

                // 사용자가 '설정' 버튼을 눌렀을 때 앱 정보 화면으로 이동
                if (result == SnackbarResult.ActionPerformed) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        eventBus.invitedPartyId.collect { invitedId ->
            if (invitedId == uiState.selectedBossParty?.id && invitedId != null) {
                viewModel.onIntent(BossIntent.FetchBossPartyDetail(invitedId))
                eventBus.emitInvitedPartyId(null)
            }
        }
    }

    LaunchedEffect(Unit) {
        eventBus.invitedPartyId.collect { acceptedId ->
            if (acceptedId == uiState.selectedBossParty?.id) {
                viewModel.onIntent(BossIntent.FetchBossParties)
                eventBus.emitInvitedPartyId(null)
            }
        }
    }

    LaunchedEffect(Unit) {
        eventBus.kickedPartyId.collect { kickedId ->
            if (kickedId == uiState.selectedBossParty?.id) {
                viewModel.onIntent(BossIntent.FetchBossParties)
                Toast.makeText(context, "파티를 떠났어요.", Toast.LENGTH_SHORT).show()
                eventBus.emitKickedPartyId(null)
                onBack()
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        val message = uiState.successMessage
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(BossIntent.InitMessage)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if ((message != null) && !uiState.showBossAlarmDialog && !uiState.showCharacterInvitationDialog && !uiState.showBossPartyChatReport && !uiState.showBossPartyBoardDialog) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(BossIntent.InitMessage)
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
                    viewModel.onIntent(BossIntent.RefreshBossPartyChat)
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) {
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .navigationBarsPadding(), // 시스템 바 위로 띄움
                    color = MapleStatBackground
                ) {
                    // ... 기존 TextField와 Button 로직 (inputBarHeight 높이 유지)
                }
            } else {
                // 다른 탭일 때도 시스템 바만큼 공간을 확보하고 싶다면 Spacer
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        },
        containerColor = MapleTheme.colors.surface
    ) { padding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                val bossPartyId = uiState.selectedBossParty?.id ?: 0L
                viewModel.onIntent(BossIntent.RefreshBossPartyDetail(bossPartyId))
            },
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = MapleTheme.colors.primary,
                    containerColor = MapleTheme.colors.surface
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
            ) {
                // 메인 컨텐츠 (알림, 파티원, 채팅, 게시판)
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        // 헤더가 확장된 높이만큼 상단 패딩을 주어 시작 지점을 맞춤
                        top = with(density) { (expandedHeightPx + toolbarOffsetHeightPx).toDp() },
                        // 채팅 탭일 때만 입력바 높이만큼 하단 패딩 부여
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() +
                            if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) inputBarHeight else 0.dp
                    )
                ) {
                    // 탭 메뉴 (Sticky Header)
                    stickyHeader {
                        BossPartyDetailTabRow(
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                // 🌟 탭 바의 하단 위치를 기준으로 가용 높이 계산의 시작점을 잡을 수 있습니다.
                            },
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
                                val headerRealTimeHeightDp = with(density) { (expandedHeightPx + toolbarOffsetHeightPx).toDp() }
                                val dynamicMinHeight = with(density) { availableContentHeightPx.toDp() } - headerRealTimeHeightDp
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        // 🌟 dynamicMinHeight를 적용하여 헤더 아래 남는 공간에 딱 맞춤
                                        .heightIn(min = if (dynamicMinHeight > 0.dp) dynamicMinHeight else 0.dp)
                                ) {
                                    BossPartyAlarmContent(
                                        alarms = uiState.bossPartyAlarmTimes,
                                        isAlarmOn = uiState.isBossPartyDetailAlarmOn,
                                        isLeader = uiState.selectedBossParty?.isLeader ?: false,
                                        snackbarHostState = snackbarHostState,
                                        onToggleAlarm = {
                                            if (uiState.isGlobalAlarmEnabled) {
                                                if (uiState.isBossPartyDetailAlarmOn) {
                                                    // Android 13 이상 대응 (Tiramisu = 33)
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                        launcherToAlarm.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                    } else {
                                                        viewModel.onIntent(BossIntent.ToggleBossPartyAlarm)
                                                    }
                                                } else {
                                                    // OFF로 바꿀 때는 권한 요청 필요 없음
                                                    viewModel.onIntent(BossIntent.ToggleBossPartyAlarm)
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "전체 알림을 먼저 허용해주세요.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        onAddAlarm = { viewModel.onIntent(BossIntent.ShowAlarmCreateDialog) },
                                        onDeleteAlarm = {
                                            viewModel.onIntent(
                                                BossIntent.DeleteBossPartyAlarm(
                                                    it
                                                )
                                            )
                                        },
                                        modifier = Modifier.fillParentMaxHeight()
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }

                        BossPartyTab.MEMBER -> {
                            item {
                                val availableHeight =
                                    screenHeightDp - systemBarsHeight - collapsedTopBarHeight - 48.dp
                                BossPartyMemberContent(
                                    isLeader = uiState.selectedBossParty?.isLeader ?: false,
                                    members = uiState.selectedBossParty?.members ?: emptyList(),
                                    onAddMember = {
                                        val nowMember =
                                            uiState.selectedBossParty?.members?.size ?: 0
                                        val boss = uiState.selectedBossParty?.boss
                                        val bossDifficulty = uiState.selectedBossParty?.difficulty
                                        val difficultyIndex =
                                            boss?.difficulties?.indexOf(bossDifficulty) ?: 0
                                        val maxMember =
                                            uiState.selectedBossParty?.boss?.memberCounts[difficultyIndex]
                                                ?: 0
                                        if (uiState.selectedBossParty?.isLeader == false) {
                                            Toast.makeText(
                                                context,
                                                "파티장만 초대가 가능해요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (nowMember >= maxMember) {
                                            Toast.makeText(
                                                context,
                                                "입장 인원 수가 최대에요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            viewModel.onIntent(BossIntent.ShowCharacterInviteDialog)
                                        }
                                    },
                                    onTransferLeader = { characterId ->
                                        viewModel.onIntent(
                                            BossIntent.TransferBossPartyLeader(
                                                characterId
                                            )
                                        )
                                    },
                                    onRemoveMember = { characterId ->
                                        viewModel.onIntent(
                                            BossIntent.KickBossPartyMember(
                                                characterId
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillParentMaxHeight()
                                        .fillMaxWidth()
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
                                    onToggleAlarm = {
                                        if (uiState.isGlobalAlarmEnabled) {
                                            if (uiState.isBossPartyDetailAlarmOn) {
                                                // Android 13 이상 대응 (Tiramisu = 33)
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    launcherToChat.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                } else {
                                                    viewModel.onIntent(BossIntent.ToggleBossPartyChatAlarm)
                                                }
                                            } else {
                                                // OFF로 바꿀 때는 권한 요청 필요 없음
                                                viewModel.onIntent(BossIntent.ToggleBossPartyChatAlarm)
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "전체 알림을 먼저 허용해주세요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onHide = { bossPartyChatId ->
                                        viewModel.onIntent(
                                            BossIntent.HideBossPartyChatMessage(
                                                bossPartyChatId
                                            )
                                        )
                                    },
                                    onReport = { chat ->
                                        viewModel.onIntent(
                                            BossIntent.ShowBossPartyChatReportDialog(
                                                chat
                                            )
                                        )
                                    },
                                    onDelete = { bossPartyChatId ->
                                        viewModel.onIntent(
                                            BossIntent.DeleteBossPartyChatMessage(
                                                bossPartyChatId
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillParentMaxHeight()
                                        .fillMaxWidth()
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
                                        viewModel.onIntent(
                                            BossIntent.DislikeBossPartyBoardPost(
                                                postId
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillParentMaxHeight()
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) {
                    // Surface나 Box로 감싸고 align(Alignment.BottomCenter) 부여
                    Surface(
                        modifier = Modifier
                            .padding(bottom = padding.calculateBottomPadding())
                            .align(Alignment.BottomCenter) // 하단 고정
                            .fillMaxWidth()
                            .height(inputBarHeight),
                        color = MapleStatBackground // 와이어프레임의 어두운 배경색 유지
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
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
                                placeholder = { Text("메시지를 입력하세요", color = MapleTheme.colors.outline) },
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MapleTheme.colors.surface,
                                    unfocusedContainerColor = MapleTheme.colors.surface,
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
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSendEnabled) MapleTheme.colors.primary else MapleTheme.colors.outline),
                                modifier = Modifier.size(50.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = if (isSendEnabled) MapleTheme.colors.surface else MapleTheme.colors.onSurface
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
                    onLeave = { viewModel.onIntent(BossIntent.LeaveBossParty) }
                )
            }

            // 2. 하단 고정 입력창 (CHAT 탭일 때만 노출)
            Column(modifier = Modifier.fillMaxSize().padding(top = collapsedTopBarHeight + 48.dp)) {
                Box(
                    modifier = Modifier.weight(1f) // 🌟 탭바 아래부터 하단 입력바 위까지의 순수 공간
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            // 실제 기기에서 렌더링된 이 Box의 높이가 바로 우리가 원하는 컨텐츠 높이입니다.
                            availableContentHeightPx = coordinates.size.height
                        }
                )
                // 채팅 탭일 때만 입력바 공간만큼 띄워줌
                if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) {
                    Spacer(modifier = Modifier.height(inputBarHeight + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
                } else {
                    Spacer(modifier = Modifier.navigationBarsPadding().height(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding())
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MapleTheme.colors.onSurface.copy(alpha = 0.7f)) // 화면 어둡게 처리
                .pointerInput(Unit) {}, // 터치 이벤트 전파 방지 (클릭 막기)
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = MapleTheme.colors.primary,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "보스 파티 정보를 불러오는 중이에요...",
                    color = MapleTheme.colors.surface,
                    style = Typography.bodyLarge
                )
            }
        }
    }

    if (uiState.showBossAlarmDialog) {
        BossPartyAlarmSettingDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.onIntent(BossIntent.DismissAlarmCreateDialog) }
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
            viewModel = viewModel,
            chat = uiState.selectBossPartyChatToReport,
            onDismiss = { viewModel.onIntent(BossIntent.DismissBossPartyChatReportDialog) },
            onReportSubmit = { chatId, reason, reasonDetail ->
                viewModel.onIntent(
                    BossIntent.ReportBossPartyChatMessage(
                        chatId,
                        reason,
                        reasonDetail
                    )
                )
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