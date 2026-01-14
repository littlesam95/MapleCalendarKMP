package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.ui.calendar.COLLAPSED_TOP_BAR_HEIGHT
import com.sixclassguys.maplecalendar.ui.calendar.IMAGE_HEIGHT

@Composable
fun EventCollapsingHeader(
    event: MapleEvent,
    currentHeightPx: Float,
    scrollPercentage: Float, // 0.0(확장) ~ 1.0(축소)
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    val density = LocalDensity.current
    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    // 💡 애니메이션 수치 계산
    // 1. Y축: 이미지 아래(216dp) -> 상단 바 중앙(약 8dp)
    val contentY = with(density) {
        lerp((IMAGE_HEIGHT + 16.dp).toPx(), 8.dp.toPx(), scrollPercentage).toDp()
    }

    // 2. 텍스트 정렬 및 크기
    val titleSize = lerp(24f, 16f, scrollPercentage).sp
    val dateSize = lerp(16f, 10f, scrollPercentage).sp
    val paddingWithTitleAndDate = lerp(8.dp, 2.dp, scrollPercentage)
    val textAlign = if (scrollPercentage > 0.8f) TextAlign.Center else TextAlign.Start
    val elevation = if (scrollPercentage > 0.95f) 4.dp else 0.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(currentHeightDp),
        color = Color.White,
        tonalElevation = if (scrollPercentage > 0.9f) 2.dp else 0.dp,
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 배경 이미지
            AsyncImage(
                model = event.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IMAGE_HEIGHT)
                    .graphicsLayer { alpha = 1f - scrollPercentage }
            )

            if (scrollPercentage < 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(COLLAPSED_TOP_BAR_HEIGHT * 1.5f) // 버튼 영역보다 조금 더 넓게
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f), // 상단은 약간 어둡게
                                    Color.Transparent // 아래로 갈수록 투명하게
                                )
                            )
                        )
                        .graphicsLayer { alpha = 1f - scrollPercentage } // 접힐수록 그라데이션 제거
                )
            }

            // 2. 상단 버튼 (뒤로가기, 공유) - 항상 고정 위치
            val iconColor by animateColorAsState(
                targetValue = if (scrollPercentage > 0.5f) Color.Black else Color.White
            )

            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(COLLAPSED_TOP_BAR_HEIGHT)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        null,
                        tint = iconColor
                    )
                }
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        null,
                        tint = iconColor
                    )
                }
            }

            // 3. 🚀 움직이는 제목 + 날짜 세트
            Column(
                modifier = Modifier.fillMaxWidth()
                    .offset(y = contentY)
                    .padding(horizontal = 16.dp), // 좌우 기본 여백
                horizontalAlignment = if (scrollPercentage > 0.8f) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    textAlign = textAlign,
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = paddingWithTitleAndDate)
                )
                Text(
                    text = "${event.startDate} ~ ${event.endDate}",
                    fontSize = dateSize,
                    color = Color.Gray,
                    textAlign = textAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// 보간법 함수 (Float용)
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}