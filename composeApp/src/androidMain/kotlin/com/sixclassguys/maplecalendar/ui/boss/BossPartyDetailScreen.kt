package com.sixclassguys.maplecalendar.ui.boss

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.BossPartyAlarmContent
import com.sixclassguys.maplecalendar.ui.component.BossPartyAlbumContent
import com.sixclassguys.maplecalendar.ui.component.BossPartyChatContent
import com.sixclassguys.maplecalendar.ui.component.BossPartyCollapsingHeader
import com.sixclassguys.maplecalendar.ui.component.BossPartyDetailTabRow
import com.sixclassguys.maplecalendar.ui.component.BossPartyMemberContent
import com.sixclassguys.maplecalendar.util.BossPartyTab

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BossPartyDetailScreen(
    viewModel: BossViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState() // 리스트형 컨텐츠를 위해 LazyListState 사용

    // 높이 설정 (제공해주신 상수 기준 적용)
    val COLLAPSED_TOP_BAR_HEIGHT = 48.dp
    val EXPANDED_TOP_BAR_HEIGHT = 420.dp
    val INPUT_BAR_HEIGHT = 80.dp // 🚀 하단 입력바 예상 높이

    val configuration = LocalConfiguration.current

    // 1. 시스템 바 높이 추출 (상단 상태바 + 하단 네비게이션 바)
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val systemBarsHeight =
        systemBarsPadding.calculateTopPadding() + systemBarsPadding.calculateBottomPadding()

    // 2. 전체 화면 높이 (Dp)
    val screenHeightDp = configuration.screenHeightDp.dp

    val density = LocalDensity.current
    val collapsedHeightPx = with(density) { COLLAPSED_TOP_BAR_HEIGHT.toPx() }
    val expandedHeightPx = with(density) { EXPANDED_TOP_BAR_HEIGHT.toPx() }
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

    LaunchedEffect(Unit) {
        scrollState.scrollToItem(0)
    }

    Scaffold(
        containerColor = MapleWhite
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    bottom = if (uiState.selectedBossPartyDetailMenu == BossPartyTab.CHAT) INPUT_BAR_HEIGHT else 16.dp
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
                                screenHeightDp - systemBarsHeight - COLLAPSED_TOP_BAR_HEIGHT - 48.dp
                            BossPartyAlarmContent(
                                alarms = uiState.bossPartyAlarmTimes,
                                isAlarmOn = uiState.isBossPartyDetailAlarmOn,
                                onToggleAlarm = { isEnabled ->

                                },
                                onAddAlarm = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(availableHeight)
                            )
                        }
                    }

                    BossPartyTab.MEMBER -> {
                        item {
                            val availableHeight =
                                screenHeightDp - systemBarsHeight - COLLAPSED_TOP_BAR_HEIGHT - 48.dp
                            BossPartyMemberContent(
                                isLeader = uiState.selectedBossParty?.isLeader ?: false,
                                members = uiState.selectedBossParty?.members ?: emptyList(),
                                onAddMember = {

                                },
                                onRemoveMember = { character ->

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
                                screenHeightDp - systemBarsHeight - COLLAPSED_TOP_BAR_HEIGHT - 48.dp - INPUT_BAR_HEIGHT
                            BossPartyChatContent(
                                chats = uiState.bossPartyChats,
                                isLastPage = uiState.isBossPartyChatLastPage,
                                isLoading = uiState.isLoading,
                                onLoadMore = { viewModel.onIntent(BossIntent.FetchBossPartyChatHistory) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(availableHeight)
                            )
                        }
                    }

                    BossPartyTab.ALBUM -> {
                        item {
                            val availableHeight =
                                screenHeightDp - systemBarsHeight - COLLAPSED_TOP_BAR_HEIGHT - 48.dp
                            BossPartyAlbumContent(
                                posts = uiState.bossPartyAlbums,
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
                // 🚀 핵심: Surface나 Box로 감싸고 align(Alignment.BottomCenter) 부여
                Surface(
                    modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                        .align(Alignment.BottomCenter) // 하단 고정
                        .fillMaxWidth()
                        .height(INPUT_BAR_HEIGHT),
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
                onDelete = { /* 삭제/탈퇴 로직 */ }
            )
        }
    }
}