package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleWhite

@Composable
fun CalendarEventCard(
    event: MapleEvent,
    onClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier.width(260.dp) // LazyRow에서 적절한 너비
            .padding(vertical = 8.dp)
            .clickable { onClick(event.id) }, // 클릭 시 상세 페이지 이동
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MapleWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 1. 이벤트 썸네일 이미지
            AsyncImage(
                model = event.thumbnailUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
                    .height(130.dp) // 이미지 높이 고정
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                // 이미지가 없을 때나 로딩 중일 때 보여줄 placeholder 설정 가능
            )

            // 2. 텍스트 영역
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                // 이벤트 제목
                Text(
                    text = event.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MapleBlack
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 이벤트 기간 표시
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MapleGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.startDate} ~ ${event.endDate}",
                        fontSize = 12.sp,
                        color = MapleGray
                    )
                }
            }
        }
    }
}