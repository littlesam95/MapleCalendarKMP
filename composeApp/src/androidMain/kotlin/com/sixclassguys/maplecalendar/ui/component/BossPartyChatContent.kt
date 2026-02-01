package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography

@Composable
fun BossPartyChatContent(
    chats: List<BossPartyChat>,
    isLastPage: Boolean,            // 추가: 마지막 페이지 여부
    isLoading: Boolean,             // 추가: 로딩 상태 (상단 인디케이터용)
    onLoadMore: () -> Unit,         // 추가: 페이지 로드 콜백
    modifier: Modifier = Modifier
) {
    val internalScrollState = rememberLazyListState()

    // 1. 최상단 스크롤 감지 (페이징 호출)
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = internalScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = internalScrollState.layoutInfo.totalItemsCount

            // 리스트의 끝(과거 내역 방향)에 거의 다다랐을 때 로드
            !isLoading && !isLastPage && chats.isNotEmpty() &&
                    lastVisibleItem != null && lastVisibleItem >= totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LaunchedEffect(chats.size) {
        // 사용자가 이미 하단 근처에 있을 때만 자동으로 스크롤을 최하단(0번)으로 이동
        if (internalScrollState.firstVisibleItemIndex <= 1) {
            internalScrollState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
            .background(MapleStatBackground, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "CHAT",
            color = MapleStatTitle,
            style = Typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
        )

        // 흰색 채팅 영역
        LazyColumn(
            state = internalScrollState,
            reverseLayout = true, // 💡 리스트를 거꾸로 뒤집음
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            if (isLoading && !isLastPage) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: 로딩 인디케이터
                    }
                }
            }

            if (chats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(), // 부모 높이만큼 채움
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "채팅을 시작해보세요!",
                            style = Typography.bodySmall,
                            color = MapleGray
                        )
                    }
                }
            } else {
                // 🚀 이제 내부에서 items를 사용하여 개별 스크롤을 지원합니다.
                items(
                    items = chats,
                    // ID만 쓰지 말고, 메시지 유형을 접두어로 붙여서 중복 확률을 극도로 낮춤
                    key = { chat -> "${chat.messageType}_${chat.id}" }
                ) { chat ->
                    ChatBubble(chat)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ChatBubble(chat: BossPartyChat) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (chat.isMine) Arrangement.End else Arrangement.Start
    ) {
        if (!chat.isMine) {
            CharacterProfileImage(
                imageUrl = chat.senderImage,
                size = 40.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (chat.isMine) Alignment.End else Alignment.Start
        ) {
            if (!chat.isMine) {
                Text(
                    text = chat.senderName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Surface(
                color = if (chat.isMine) MapleOrange else MapleGray, // 내 메시지는 라임색 계열
                shape = RoundedCornerShape(
                    topStart = if (chat.isMine) 16.dp else 4.dp,
                    topEnd = if (chat.isMine) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Text(
                    text = chat.content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = if (chat.isMine) MapleWhite else MapleBlack
                )
            }
        }
    }
}