package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.daysInMonth
import com.sixclassguys.maplecalendar.utils.minusMonths
import com.sixclassguys.maplecalendar.utils.plusMonths
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.todayIn

@Composable
fun BossPartyAlarmSettingDialog(
    viewModel: BossViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: 선택, 1: 주기
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var currentMonthDate by remember { mutableStateOf(LocalDate(today.year, today.month, 1)) }

    val minuteFocusRequester = remember { FocusRequester() }
    val messageFocusRequester = remember { FocusRequester() }
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val isFormValid = with(uiState) {
        val isTimeValid = selectedHour.isNotBlank() && selectedMinute.isNotBlank()
        val isMessageValid = alarmMessage.isNotBlank()

        if (selectedTab == 0) {
            selectedAlarmDate != null && isTimeValid && isMessageValid
        } else {
            selectedDayOfWeek != null && isTimeValid && isMessageValid
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MapleStatBackground // 이미지의 어두운 배경색
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ALARM SETTING",
                    style = Typography.titleLarge,
                    color = MapleStatTitle, // 형광빛 도는 타이틀 색상
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 흰색 메인 카드 섹션
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        TabSwitcher(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

                        Spacer(modifier = Modifier.height(20.dp))

                        if (selectedTab == 0) {
                            // [선택 모드] 캘린더
                            BossAlarmCalendar(
                                currentMonthDate = currentMonthDate, // 다이얼로그 내부 state
                                selectedDate = uiState.selectedAlarmDate,
                                onDateClick = { date ->
                                    viewModel.onIntent(BossIntent.UpdateAlarmTimeSelectMode(date))
                                },
                                onMonthChange = { offset ->
                                    currentMonthDate = if (offset > 0) currentMonthDate.plusMonths(1)
                                    else currentMonthDate.minusMonths(1)
                                }
                            )
                        } else {
                            // [주기 모드] 요일 선택 및 이번주부터 체크박스
                            DayOfWeekSelector(
                                selectedDay = uiState.selectedDayOfWeek,
                                onDaySelected = {
                                    viewModel.onIntent(BossIntent.UpdateAlarmTimePeriodMode(it))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 공통 시간 입력 UI
                        TimeInputSection(
                            hour = uiState.selectedHour,
                            onHourChange = { viewModel.onIntent(BossIntent.UpdateAlarmTimeHour(it)) },
                            minute = uiState.selectedMinute,
                            onMinuteChange = { viewModel.onIntent(BossIntent.UpdateAlarmTimeMinute(it)) },
                            // FocusRequester 전달
                            minuteFocusRequester = minuteFocusRequester,
                            onHourNext = { minuteFocusRequester.requestFocus() },
                            onMinuteNext = { messageFocusRequester.requestFocus() },
                            isPeriodMode = (selectedTab == 1),
                            isImmediate = uiState.isImmediatelyAlarm,
                            onImmediateChange = { viewModel.onIntent(BossIntent.UpdateThisWeekPeriodMode(it)) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("알람 메시지", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.alarmMessage,
                            onValueChange = { viewModel.onIntent(BossIntent.UpdateAlarmMessage(it)) },
                            placeholder = { Text("알람 메시지 입력", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth()
                                .height(56.dp)
                                .focusRequester(messageFocusRequester),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // '완료' 버튼
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    softwareKeyboardController?.hide() // 키보드 닫기
                                    if (isFormValid) {
                                        if (selectedTab == 0) {
                                            viewModel.onIntent(BossIntent.CreateBossPartyAlarm)
                                        } else if (selectedTab == 1) {
                                            viewModel.onIntent(BossIntent.UpdateBossPartyAlarmPeriod)
                                        }
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            // Material 3에서는 OutlinedTextFieldDefaults.colors()를 사용합니다.
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFE0E0E0),
                                unfocusedContainerColor = Color(0xFFE0E0E0),
                                disabledContainerColor = Color(0xFFE0E0E0),
                                // 테두리 색상을 투명하게 만들어 디자인 가이드에 맞춤
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            enabled = isFormValid,
                            onClick = {
                                if (selectedTab == 0) {
                                    viewModel.onIntent(BossIntent.CreateBossPartyAlarm)
                                } else if (selectedTab == 1) {
                                    viewModel.onIntent(BossIntent.UpdateBossPartyAlarmPeriod)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFormValid) MapleOrange else MapleGray) // Maple Orange
                        ) {
                            Text(
                                text = if (selectedTab == 0) "알람 예약" else "알람 주기 설정",
                                style = Typography.bodyMedium,
                                color = if (isFormValid) MapleWhite else MapleBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.width(144.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MapleGray)
    ) {
        listOf("선택", "주기").forEachIndexed { index, text ->
            Box(
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
                    .background(if (selectedTab == index) MapleOrange else Color.Transparent)
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = text, style = Typography.bodyLarge, color = if (selectedTab == index) MapleWhite else MapleBlack)
            }
        }
    }
}

@Composable
fun TimeInputSection(
    hour: String,
    onHourChange: (String) -> Unit,
    minute: String,
    onMinuteChange: (String) -> Unit,
    minuteFocusRequester: FocusRequester,
    onHourNext: () -> Unit,
    onMinuteNext: () -> Unit,
    isPeriodMode: Boolean,
    isImmediate: Boolean,
    onImmediateChange: (Boolean) -> Unit
) {
    Column {
        Text("시간", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 시간 입력
            TimeBox(
                value = hour,
                maxValue = 23,
                onValueChange = onHourChange,
                imeAction = ImeAction.Next,
                onAction = onHourNext
            )
            Text(" 시 ")

            // 분 입력
            TimeBox(
                value = minute,
                maxValue = 59,
                onValueChange = onMinuteChange,
                modifier = Modifier.focusRequester(minuteFocusRequester), // 여기서 포커스를 받음
                imeAction = ImeAction.Next,
                onAction = onMinuteNext
            )
            Text(" 분 ")

            if (isPeriodMode) {
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onImmediateChange(!isImmediate) } // 텍스트 클릭 시에도 토글
                ) {
                    Text(
                        text = "이번주\n부터",
                        style = Typography.labelSmall,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Checkbox(
                        checked = isImmediate,
                        onCheckedChange = onImmediateChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MapleOrange, // 디자인의 주황색
                            uncheckedColor = MapleGray,
                            checkmarkColor = MapleWhite
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TimeBox(
    value: String,
    maxValue: Int, // 23 또는 59 전달
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onAction: () -> Unit = {}
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            // 1. 숫자만 허용 (빈 문자열은 허용하여 삭제 가능하게 함)
            if (newValue.isEmpty()) {
                onValueChange("")
                return@BasicTextField
            }

            val numericValue = newValue.filter { it.isDigit() }
            if (numericValue.isEmpty()) return@BasicTextField

            // 2. 정수로 변환하여 범위 체크
            val intValue = numericValue.toIntOrNull() ?: 0

            // 3. 조건부 업데이트: 최대값보다 크면 최대값으로 고정
            val finalValue = if (intValue > maxValue) {
                maxValue.toString()
            } else {
                // 앞의 불필요한 0 제거 (예: "05" -> "5", 단 "0"은 유지)
                intValue.toString()
            }

            // 4. 최대 2자리까지만 입력 허용 (선택 사항)
            if (finalValue.length <= 2) {
                onValueChange(finalValue)
            }
        },
        modifier = modifier
            .size(width = 60.dp, height = 36.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(18.dp)),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction // 키보드 우하단 버튼 설정
        ),
        keyboardActions = KeyboardActions(
            onNext = { onAction() },
            onDone = { onAction() }
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text("00", color = Color.Gray, fontSize = 16.sp) // Placeholder 효과
                }
                innerTextField()
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BossAlarmCalendar(
    currentMonthDate: LocalDate, // ViewModel에서 관리하는 현재 표시 달
    selectedDate: LocalDate?,    // uiState.selectedAlarmDate
    onDateClick: (LocalDate) -> Unit,
    onMonthChange: (Int) -> Unit
) {
    val initialPage = 500
    val pagerState = rememberPagerState(initialPage = initialPage) { 1000 }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    // 외부에서 월 변경 시 페이저 동기화
    LaunchedEffect(currentMonthDate) {
        val startOfMonth = LocalDate(today.year, today.month, 1)
        val targetPage = initialPage + (
                (currentMonthDate.year - startOfMonth.year) * 12 +
                        (currentMonthDate.monthNumber - startOfMonth.monthNumber)
                )
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // 조금 더 둥글게 디자인 조정
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 다이얼로그 안이므로 그림자 제거 또는 낮게
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            userScrollEnabled = true // 스와이프 지원
        ) { page ->
            val monthOffset = page - initialPage
            val dateForPage = remember(monthOffset) {
                LocalDate(today.year, today.month, 1).plusMonths(monthOffset)
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // 1. 헤더 영역 (연/월 표시 및 이동 버튼)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onMonthChange(-1) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = Color.Gray)
                    }
                    Text(
                        text = "${dateForPage.year}년 ${dateForPage.monthNumber}월",
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = { onMonthChange(1) }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. 요일 행
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = Typography.bodySmall,
                            color = when(index) {
                                0 -> Color.Red
                                6 -> Color.Blue
                                else -> Color.Gray
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. 날짜 그리드
                val daysInMonth = dateForPage.daysInMonth()
                val firstDayOfWeek = dateForPage.dayOfWeek.isoDayNumber % 7 // 0(일) ~ 6(토)
                val totalCells = daysInMonth + firstDayOfWeek

                for (row in 0 until (totalCells + 6) / 7) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOfWeek + 1

                            if (day in 1..daysInMonth) {
                                val date = LocalDate(dateForPage.year, dateForPage.month, day)
                                val isSelected = (date == selectedDate)
                                val isPast = date < today // 오늘 이전 날짜 여부

                                Box(
                                    modifier = Modifier.weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFF59E0B) else Color.Transparent)
                                        .clickable(enabled = !isPast) { onDateClick(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = Typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> Color.White
                                            isPast -> Color.LightGray
                                            col == 0 -> Color.Red
                                            col == 6 -> Color.Blue
                                            else -> Color.Black
                                        }
                                    )
                                }
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayOfWeekSelector(
    selectedDay: DayOfWeek?,
    onDaySelected: (DayOfWeek?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // 표시용 텍스트 변환
    val displayText = when (selectedDay) {
        DayOfWeek.MONDAY -> "매주 월요일"
        DayOfWeek.TUESDAY -> "매주 화요일"
        DayOfWeek.WEDNESDAY -> "매주 수요일"
        DayOfWeek.THURSDAY -> "매주 목요일"
        DayOfWeek.FRIDAY -> "매주 금요일"
        DayOfWeek.SATURDAY -> "매주 토요일"
        DayOfWeek.SUNDAY -> "매주 일요일"
        null -> "주기 모드 해제"
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // 드롭다운 트리거 버튼
        Surface(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE0E0E0), // 타임박스와 통일감 있는 회색
            border = BorderStroke(1.dp, if(expanded) Color(0xFFF59E0B) else Color.Transparent)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayText,
                    style = Typography.bodyLarge,
                    color = if (selectedDay == null) Color.Gray else Color.Black
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.DarkGray
                )
            }
        }

        // 실제 메뉴 항목
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f) // 다이얼로그 너비에 맞춰 조정
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            // 1. 해제 옵션
            DropdownMenuItem(
                text = { Text("주기 모드 해제", color = Color.Red) },
                onClick = {
                    onDaySelected(null)
                    expanded = false
                }
            )

            HorizontalDivider(color = Color(0xFFEEEEEE))

            // 2. 요일별 옵션
            DayOfWeek.entries.forEach { day ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when(day) {
                                DayOfWeek.MONDAY -> "매주 월요일"
                                DayOfWeek.TUESDAY -> "매주 화요일"
                                DayOfWeek.WEDNESDAY -> "매주 수요일"
                                DayOfWeek.THURSDAY -> "매주 목요일"
                                DayOfWeek.FRIDAY -> "매주 금요일"
                                DayOfWeek.SATURDAY -> "매주 토요일"
                                DayOfWeek.SUNDAY -> "매주 일요일"
                            },
                            color = if (selectedDay == day) Color(0xFFF59E0B) else Color.Black,
                            fontWeight = if (selectedDay == day) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}