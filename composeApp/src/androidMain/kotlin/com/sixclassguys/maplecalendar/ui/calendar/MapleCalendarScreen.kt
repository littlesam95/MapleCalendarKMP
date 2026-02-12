package com.sixclassguys.maplecalendar.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarIntent
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.BossScheduleRow
import com.sixclassguys.maplecalendar.ui.component.CalendarCard
import com.sixclassguys.maplecalendar.ui.component.CarouselEventRow
import com.sixclassguys.maplecalendar.ui.component.EmptyEventScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapleCalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToEventDetail: (Long) -> Unit,
    onNavigateToBossDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message = message)
        }
    }

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
                        style = Typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    CalendarCard(
                        uiState = uiState,
                        onMonthChanged = { newOffset ->
                            viewModel.onIntent(CalendarIntent.ChangeMonth(newOffset))
                        },
                        onDateClick = { date ->
                            viewModel.onIntent(CalendarIntent.SelectDate(date))
                        },
                        today = viewModel.getTodayDate()
                    )
                }

                // 2. 진행중인 이벤트 섹션
                item {
                    val selectedDateText = uiState.selectedDate?.let {
                        "${it.year}년 ${it.monthNumber}월 ${it.dayOfMonth}일"
                    }

                    Text(
                        text = if (selectedDateText == null) "날짜를 선택해주세요!" else "$selectedDateText 진행중인 이벤트",
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    // 해당 월의 이벤트 중 선택된 날짜가 포함된 이벤트 필터링
                    val year = uiState.selectedDate?.year ?: viewModel.getTodayDate().year
                    val month = uiState.selectedDate?.monthNumber ?: viewModel.getTodayDate().monthNumber
                    val day = uiState.selectedDate?.dayOfMonth ?: viewModel.getTodayDate().dayOfMonth
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
                    val selectedDateText = uiState.selectedDate?.let {
                        "${it.year}년 ${it.monthNumber}월 ${it.dayOfMonth}일"
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (selectedDateText == null) "날짜를 선택해주세요!" else "$selectedDateText 보스 일정",
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    val year = uiState.selectedDate?.year ?: viewModel.getTodayDate().year
                    val month = uiState.selectedDate?.monthNumber ?: viewModel.getTodayDate().monthNumber
                    val day = uiState.selectedDate?.dayOfMonth ?: viewModel.getTodayDate().dayOfMonth
                    val currentKey = "${year}-${month}-${day}"
                    val nowBossSchedules = uiState.bossSchedulesMapByDay[currentKey] ?: emptyList()

                    // 보스 데이터가 비어있을 때 (이미지 포함된 뷰)
                    if (nowBossSchedules.isEmpty()) {
                        EmptyEventScreen("보스 일정이 없어요.")
                    } else {
                        LazyRow(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(nowBossSchedules) { schedule ->
                                BossScheduleRow(
                                    schedule = schedule,
                                    onNavigateToBossDetail = onNavigateToBossDetail
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}