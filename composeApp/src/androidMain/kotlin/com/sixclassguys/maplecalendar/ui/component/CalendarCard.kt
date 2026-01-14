package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarUiState
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.utils.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@Composable
fun CalendarCard(
    uiState: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    today: LocalDate
) {
    // 💡 1. 페이지 개수를 고정하고 중앙에서 시작합니다.
    val pageCount = 2000
    val initialPage = pageCount / 2
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }
    val scope = rememberCoroutineScope()

    // 💡 2. [핵심] 사용자의 수동 스와이프가 끝났을 때만 뷰모델 업데이트
    // isScrollInProgress를 체크하여 프로그래밍적인 이동(animateScroll)과 수동 스와이프를 구분합니다.
    LaunchedEffect(pagerState.targetPage) {
        // targetPage가 바뀌었을 때, 현재 uiState와 차이가 있다면 그때만 이벤트를 던집니다.
        val monthOffset = pagerState.targetPage - initialPage
        val startOfMonth = LocalDate(today.year, today.month, 1)
        val targetDate = startOfMonth.plusMonths(monthOffset)

        if (targetDate.year != uiState.year || targetDate.monthNumber != uiState.month.value) {
            if (pagerState.targetPage > pagerState.currentPage) {
                onNextMonth()
            } else if (pagerState.targetPage < pagerState.currentPage) {
                onPreviousMonth()
            }
        }
    }

    Card(
        modifier = Modifier.padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MapleWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            // 💡 스와이프 감도 조절 (너무 휙휙 넘어가지 않게)
            flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 월 선택기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onPreviousMonth()
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MapleOrange
                        )
                    }
                    Text(
                        text = "${uiState.year}년 ${uiState.month.value}월",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MapleOrange
                    )
                    IconButton(
                        onClick = {
                            onNextMonth()
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MapleOrange
                        )
                    }
                }

                // 요일 표시
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = if (day == "일") Color.Red else if (day == "토") Color.Blue else Color.Gray
                        )
                    }
                }

                // 날짜 그리드 (Reducer에서 생성한 42개 혹은 35개 리스트 활용)
                val chunkedDays = uiState.days.chunked(7)
                chunkedDays.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            Box(
                                modifier = Modifier.weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (date == uiState.selectedDate) MapleOrange else Color.Transparent
                                    )
                                    .clickable(enabled = date != null) { date?.let { onDateClick(it) } },
                                contentAlignment = Alignment.Center
                            ) {
                                if (date != null) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = when (date) {
                                            uiState.selectedDate -> Color.White

                                            today -> MapleOrange

                                            else -> Color.Black
                                        },
                                        fontWeight = if (date == uiState.selectedDate || date == today) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}