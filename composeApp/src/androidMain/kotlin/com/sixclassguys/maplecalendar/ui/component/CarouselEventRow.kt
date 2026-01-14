package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.domain.model.MapleEvent

@Composable
fun CarouselEventRow(
    nowEvents: List<MapleEvent>,
    onNavigateToEventDetail: (Long) -> Unit
) {
    if (nowEvents.isEmpty()) return

    // 1. 실제 아이템 개수의 배수 중 중간 지점을 계산하여 시작 위치 설정
    val actualCount = nowEvents.size
    val totalCount = Int.MAX_VALUE // 가상의 무한 개수
    val startIndex = totalCount / 2 - (totalCount / 2 % actualCount)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 2. 가상의 totalCount만큼 아이템 생성
        items(
            count = totalCount,
            // 고유 키 설정 (무한 루프이므로 index 자체를 키로 써야 함)
            key = { index -> index }
        ) { index ->
            // 3. 나머지 연산으로 실제 리스트의 인덱스 추출
            val event = nowEvents[index % actualCount]

            CalendarEventCard(
                event = event,
                onClick = { onNavigateToEventDetail(it) }
            )
        }
    }
}