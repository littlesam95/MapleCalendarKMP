package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.domain.model.MapleEvent

@Composable
fun EventDetailHeader(event: MapleEvent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 💡 태그 영역만 남깁니다. (제목과 날짜는 위에서 내려옴)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val demoTags = listOf("선데이")
            demoTags.forEach { tag ->
                Surface(
                    color = Color(0xFF81D4FA),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}