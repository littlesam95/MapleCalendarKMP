package com.sixclassguys.maplecalendar.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarIntent
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.ui.component.CalendarCard
import com.sixclassguys.maplecalendar.ui.component.CarouselEventRow
import com.sixclassguys.maplecalendar.ui.component.EmptyEventScreen
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapleCalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToEventDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 현재 몇 번째 달을 보고 있는지 추적 (ViewModel의 offset 연동용)
    var monthOffset by remember { mutableIntStateOf(0) }

    // 오늘 날짜 (선택 초기값 및 '오늘' 표시용)
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. 캘린더 카드
                item {
                    Text(
                        text = "캘린더",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    CalendarCard(
                        uiState = uiState,
                        onPreviousMonth = {
                            monthOffset--
                            viewModel.onIntent(CalendarIntent.ChangeMonth(monthOffset))
                        },
                        onNextMonth = {
                            monthOffset++
                            viewModel.onIntent(CalendarIntent.ChangeMonth(monthOffset))
                        },
                        onDateClick = { date ->
                            viewModel.onIntent(CalendarIntent.SelectDate(date))
                        },
                        today = today
                    )
                }

                // 2. 진행중인 이벤트 섹션
                item {
                    val selectedDateText = uiState.selectedDate?.let {
                        "${it.year}년 ${it.monthNumber}월 ${it.dayOfMonth}일"
                    }

                    Text(
                        text = if (selectedDateText == null) "날짜를 선택해주세요!" else "$selectedDateText 진행중인 이벤트",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(16.dp)
                    )

                    // 해당 월의 이벤트 중 선택된 날짜가 포함된 이벤트 필터링
                    val year = uiState.selectedDate?.year ?: today.year
                    val month = uiState.selectedDate?.monthNumber ?: today.monthNumber
                    val day = uiState.selectedDate?.dayOfMonth ?: today.dayOfMonth
                    val currentKey = "${year}-${month}-${day}"
                    val nowEvents = uiState.eventsMapByDay[currentKey] ?: emptyList()

                    if (nowEvents.isEmpty()) {
                        EmptyEventScreen("진행중인 이벤트가 없어요.")
                    } else {
                        CarouselEventRow(
                            nowEvents = nowEvents,
                            onNavigateToEventDetail = { eventId ->
                                val selected = nowEvents.find { it.id == eventId }
                                selected?.let {
                                    viewModel.onIntent(CalendarIntent.SelectEvent(it.id))
                                    onNavigateToEventDetail(eventId)
                                }
                            }
                        )
                    }
                }

                // 3. 오늘의 보스 일정 (현재 UIState에는 보스 데이터가 없으므로 자리만 유지)
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "오늘의 보스 일정",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(16.dp)
                    )

                    // 보스 데이터가 비어있을 때 (이미지 포함된 뷰)
                    EmptyEventScreen("보스 일정이 없어요.")
                }
            }
        }
    }
}