package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray

@Composable
fun TodayEventsCard(
    event: MapleEvent, // 혹은 사용하시는 Event DTO
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // 클릭 시 물결 효과 제거(깔끔함을 위해)
                onClick = onClick
            )
    ) {
        // 1. 이벤트 배너 이미지
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 그림자 없이 평평하게
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // 와이어프레임 비율에 맞춘 높이
        ) {
            AsyncImage(
                model = event.thumbnailUrl,
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 이미지가 꽉 차도록
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. 이벤트 제목
        Text(
            text = event.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MapleBlack
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 3. 이벤트 기간 (오늘 날짜 포함 여부와 관계없이 전체 기간 표시)
        Text(
            text = "${event.startDate} ~ ${event.endDate}",
            fontSize = 14.sp,
            color = MapleGray
        )
    }
}