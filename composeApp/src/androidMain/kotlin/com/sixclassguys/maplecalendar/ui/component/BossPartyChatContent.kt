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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    modifier: Modifier = Modifier
) {
    val internalScrollState = rememberLazyListState()

    LaunchedEffect(key1 = chats) {
        if (chats.isNotEmpty()) {
            // scrollToItem을 사용하면 즉시 이동하지만 부모 스크롤을 건드릴 수 있으므로
            // 약간의 딜레이를 주거나, 내부 리스트의 레이아웃이 완료된 후 실행되도록 합니다.
            internalScrollState.scrollToItem(chats.size - 1)
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
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
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
                items(chats) { chat ->
                    ChatBubble(chat = chat)
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
                imageUrl = chat.characterSummary.characterImage,
                size = 40.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (chat.isMine) Alignment.End else Alignment.Start
        ) {
            if (!chat.isMine) {
                Text(
                    text = chat.characterSummary.characterName,
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