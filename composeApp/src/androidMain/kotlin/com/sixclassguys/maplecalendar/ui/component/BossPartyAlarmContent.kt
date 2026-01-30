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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography

@Composable
fun BossPartyAlarmContent(
    alarms: List<BossPartyAlarmTime>,
    isAlarmOn: Boolean,
    onToggleAlarm: (Boolean) -> Unit,
    onAddAlarm: () -> Unit,
    modifier: Modifier
) {
    // 배경 컨테이너 (어두운 회색)
    Column(
        modifier = modifier.fillMaxWidth() // fillMaxSize 대신 fillMaxWidth 사용
            .background(MapleStatBackground, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(16.dp)
    ) {
        // 상단 헤더 영역
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ALARM",
                color = MapleStatTitle,
                style = Typography.titleMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isAlarmOn,
                    onCheckedChange = onToggleAlarm,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MapleOrange
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onAddAlarm) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }
        }

        // 🚀 수정 포인트: LazyColumn을 삭제하고 Column + forEach 사용
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            if (alarms.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(), // 부모 높이만큼 채움
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "예약된 알람이 없어요.",
                            style = Typography.bodySmall,
                            color = MapleGray
                        )
                    }
                }
            } else {
                items(alarms){ alarm ->
                    BossPartyDetailAlarmItem(
                        date = alarm.date,
                        time = alarm.time,
                        description = alarm.message,
                        onDelete = { /* 삭제 로직 */ }
                    )
                }
            }
        }
    }
}

// 알림 아이템 컴포넌트
@Composable
fun BossPartyDetailAlarmItem(
    date: String,          // "2026년 1월 31일 토요일"
    time: String,          // "19:00"
    description: String,   // "5분 내로 안 오면 추방함"
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(16.dp), // 조금 더 둥근 모서리
        colors = CardDefaults.cardColors(containerColor = MapleWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 시계 아이콘
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MapleOrange,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                // 날짜와 시간
                Text(
                    text = "$date $time",
                    fontFamily = PretendardFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 상세 설명
                Text(
                    text = description,
                    fontFamily = PretendardFamily,
                    fontSize = 13.sp,
                    color = Color.Black // 와이어프레임상 검은색
                )
            }

            // 닫기 버튼
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "삭제",
                    tint = Color.Black
                )
            }
        }
    }
}