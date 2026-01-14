package com.sixclassguys.maplecalendar.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.utils.minusMonths
import com.sixclassguys.maplecalendar.utils.plusMonths
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmSettingDialog(
    event: MapleEvent,
    onDismiss: () -> Unit,
    onSubmit: (List<LocalDateTime>) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 선택, 1: 주기
    // 💡 상태 관리
    var selectedDates by remember { mutableStateOf(setOf<LocalDate>()) } // 다중 선택 날짜
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var currentMonthDate by remember { mutableStateOf(LocalDate(today.year, today.month, 1)) }

    // 최종 추가된 알림 리스트 (TreeMap 활용을 권장하셨으므로 내부적으로 정렬 유지)
    var addedAlarms by remember {
        mutableStateOf(java.util.TreeMap<LocalDateTime, Boolean>(event.notificationTimes.associateWith { true }
            .toMap()))
    }

    // 시간 선택 상태
    var hour by remember { mutableStateOf("10") }
    var minute by remember { mutableStateOf("30") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current // 💡 현재 다이얼로그의 Native View
    val dummyFocusRequester = remember { FocusRequester() }
    val minuteFocusRequester = remember { FocusRequester() }
    val clearFocusAll = {
        // Compose 레벨에서 포커스 해제
        focusManager.clearFocus(force = true)
        // 가짜 타겟으로 포커스 강제 이동 (TextField에서 포커스 뺏기)
        dummyFocusRequester.requestFocus()
        // 네이티브 뷰 레벨에서 포커스 해제 (이게 핵심)
        view.clearFocus()
        // 키보드 숨기기
        keyboardController?.hide()
    }
    val addAlarmAction = {
        if (selectedDates.isNotEmpty()) {
            val newMap = java.util.TreeMap<LocalDateTime, Boolean>(addedAlarms)
            selectedDates.forEach { date ->
                val h = hour.toIntOrNull() ?: 0
                val m = minute.toIntOrNull() ?: 0
                newMap[LocalDateTime(date.year, date.month, date.dayOfMonth, h, m)] = true
            }
            addedAlarms = newMap
            selectedDates = emptySet()
        }
        clearFocusAll()
    }

    var selectedInterval by remember { mutableIntStateOf(1) } // 기본값 '매일'
    // 💡 주기 모드용 알림 생성 로직
    val applyPeriodAlarms = {
        val h = hour.toIntOrNull() ?: 0
        val m = minute.toIntOrNull() ?: 0

        // 🚀 수정 포인트: 이벤트 시작일이 오늘보다 이전이면 '오늘'부터 계산 시작
        val calculationStart = if (event.startDate < today) today else event.startDate

        val newMap = java.util.TreeMap<LocalDateTime, Boolean>(addedAlarms)

        // 1. 계산 시작일부터 종료일까지 주기적으로 추가
        var current = calculationStart
        while (current <= event.endDate) {
            newMap[LocalDateTime(current.year, current.month, current.dayOfMonth, h, m)] = true
            current = current.plus(DatePeriod(days = selectedInterval))
        }

        // 2. 종료일 알람은 주기에 상관없이 무조건 포함 (TreeMap이 중복 자동 제거)
        val endDateTime = LocalDateTime(
            event.endDate.year, event.endDate.month, event.endDate.dayOfMonth, h, m
        )
        newMap[endDateTime] = true

        addedAlarms = newMap // 기존 리스트 초기화 후 교체
        clearFocusAll()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // 이 부분이 'Initial' 패스를 사용하여 자식보다 먼저 터치를 감지합니다.
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.type == PointerEventType.Press) {
                                clearFocusAll()
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(0.dp)
                    .focusRequester(dummyFocusRequester)
                    .focusable()
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { clearFocusAll() }, // 🚀 몸체 클릭 시에도 실행
                shape = RoundedCornerShape(16.dp),
                color = MapleStatBackground
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "ALARM SETTING",
                        style = TextStyle(
                            color = MapleStatTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MapleWhite
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { clearFocusAll() }, // 🚀 화이트 카드 클릭 시에도 실행
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 💡 탭 메뉴 (선택 / 주기)
                            Row(
                                modifier = Modifier
                                    .width(144.dp) // 전체 너비 제한 (작게 설정)
                                    .height(28.dp) // 높이도 조금 더 컴팩트하게 조절
                                    .align(Alignment.Start) // 부모 Column 내에서 왼쪽으로 배치
                                    .clip(RoundedCornerShape(20.dp)) // 전체를 캡슐 모양으로 깎음
                                    .background(MapleGray) // 기본 배경색 (연한 회색)
                            ) {
                                // 1. 선택 탭
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (selectedTab == 0) MapleOrange else Color.Transparent) // 선택 시 오렌지색
                                        .clickable { selectedTab = 0 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "선택",
                                        style = TextStyle(
                                            color = if (selectedTab == 0) MapleWhite else MapleBlack, // 선택 시 흰색, 아닐 때 검정
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                }

                                // 2. 주기 탭
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (selectedTab == 1) MapleOrange else Color.Transparent) // 선택 시 오렌지색
                                        .clickable { selectedTab = 1 },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "주기",
                                        style = TextStyle(
                                            color = if (selectedTab == 1) MapleWhite else MapleBlack, // 선택 시 흰색, 아닐 때 검정
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 💡 모드별 컨텐츠
                            if (selectedTab == 0) {
                                // 선택 모드: 실제 구현 시에는 Calendar 라이브러리나 DatePicker 배치
                                CalendarPlaceholder(
                                    startDate = if (event.startDate < today) today else event.startDate,
                                    endDate = event.endDate,
                                    currentMonthDate = currentMonthDate,
                                    selectedDates = selectedDates,
                                    onDateClick = { date ->
                                        selectedDates = if (selectedDates.contains(date)) {
                                            selectedDates - date
                                        } else {
                                            selectedDates + date
                                        }
                                    },
                                    onMonthChange = { offset ->
                                        // 월 변경 로직 (단순화된 예시)
                                        currentMonthDate = if (offset > 0) {
                                            currentMonthDate.plusMonths(1)
                                        } else {
                                            currentMonthDate.minusMonths(1)
                                        }
                                    }
                                )
                            } else {
                                // 주기 모드: 드롭다운 메뉴
                                PeriodSelector(
                                    onPeriodSelected = { selectedInterval = it }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            TimeInputRow(
                                hour = hour, onHourChange = { hour = it },
                                minute = minute, onMinuteChange = { minute = it },
                                isAddEnabled = ((selectedTab == 0) && selectedDates.isNotEmpty()) || (selectedTab == 1), // 👈 날짜 선택 여부 체크
                                onAddClick = {
                                    if (selectedTab == 0) {
                                        addAlarmAction()
                                    } else {
                                        applyPeriodAlarms()
                                    }
                                },
                                focusRequester = minuteFocusRequester,
                                onNext = { minuteFocusRequester.requestFocus() },
                                onDone = { addAlarmAction() }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 💡 알림 시간 추가 리스트 (FlowRow로 태그 형태 구현)
                            Text(
                                text = "알림 시간 추가",
                                color = MapleWhite,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp) // Chip 높이에 맞게 적절히 조절
                                    .horizontalScroll(rememberScrollState()), // 🚀 수평 스크롤 활성화
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                addedAlarms.keys.forEach { alarm ->
                                    AlarmChip(
                                        alarm = alarm,
                                        onRemove = {
                                            val newMap = java.util.TreeMap<LocalDateTime, Boolean>(
                                                addedAlarms
                                            )
                                            newMap.remove(alarm)
                                            addedAlarms = newMap
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // 💡 제출 버튼
                            Button(
                                onClick = { onSubmit(addedAlarms.keys.toList()) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MapleOrange),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("제출하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}