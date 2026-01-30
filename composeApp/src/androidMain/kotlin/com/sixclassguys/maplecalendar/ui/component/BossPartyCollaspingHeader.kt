package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.presentation.boss.BossUiState
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.badgeBackground
import com.sixclassguys.maplecalendar.utils.badgeOutline
import com.sixclassguys.maplecalendar.utils.badgeText
import com.sixclassguys.maplecalendar.utils.entryBackgroundRes

@Composable
fun BossPartyCollapsingHeader(
    uiState: BossUiState,
    currentHeightPx: Float,
    scrollPercentage: Float, // 0.0 ~ 1.0
    onBack: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val bossParty = uiState.selectedBossParty ?: return
    val density = LocalDensity.current
    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    // 💡 높이 기준 설정
    val IMAGE_HEIGHT = 260.dp
    val COLLAPSED_HEIGHT = 48.dp // 요청하신 높이
    val ICON_AREA_WIDTH = 48.dp // 버튼 영역

    // 1. 🚀 Y축 보간 (이미지 아래 -> 56dp의 수직 중앙)
    // 56dp 내에서 텍스트가 중앙에 오려면 y값이 대략 16dp~18dp면 적당합니다.
    val contentY = with(density) {
        lerp((IMAGE_HEIGHT + 16.dp).toPx(), 16.dp.toPx(), scrollPercentage).toDp()
    }
    val horizontalPadding = with(density) {
        val startPadding = 16.dp.toPx()
        val endPadding = ICON_AREA_WIDTH.toPx()

        lerp(startPadding, endPadding, scrollPercentage).toDp()
    }

    // 2. 텍스트 크기 및 아이콘 색상 보간
    val titleSize = lerp(20f, 17f, scrollPercentage).sp
    val subInfoAlpha = 1f - (scrollPercentage * 3).coerceIn(0f, 1f)
    val iconColor by animateColorAsState(
        targetValue = if (scrollPercentage > 0.5f) MapleBlack else MapleWhite
    )

    Surface(
        modifier = Modifier.fillMaxWidth()
            .height(currentHeightDp),
        color = MapleWhite,
        shadowElevation = if (scrollPercentage > 0.95f) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 배경 이미지
            AsyncImage(
                model = bossParty.boss.entryBackgroundRes,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
                    .height(IMAGE_HEIGHT)
                    .graphicsLayer { alpha = 1f - scrollPercentage }
            )

            if (scrollPercentage < 0.8f) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(80.dp) // 버튼 영역보다 넉넉하게
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MapleBlack.copy(alpha = 0.4f), // 위쪽은 어둡게
                                    Color.Transparent // 아래로 갈수록 투명하게
                                )
                            )
                        )
                        .graphicsLayer { alpha = 1f - scrollPercentage }
                )
            }

            // 2. 상단 버튼 (뒤로가기, 공유) - 고정 56dp 영역
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(COLLAPSED_HEIGHT)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = iconColor
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = iconColor
                    )
                }
            }

            // 3. 🚀 움직이는 제목 영역 (중앙 정렬 보정)
            Column(
                modifier = Modifier.fillMaxWidth()
                    .offset(y = contentY)
                    // 💡 핵심: 양쪽 아이콘(48dp)을 고려하여 패딩을 lerp로 조절
                    // 확장(16dp 패딩) -> 축소(48dp 패딩으로 늘려 수평 중앙 확보)
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = if (scrollPercentage > 0.8f) Alignment.CenterHorizontally else Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bossParty.title,
                        fontSize = titleSize,
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = if (scrollPercentage > 0.8f) TextAlign.Center else TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )

                    // 확장 시에만 제목 옆에 휴지통 배치
                    if (scrollPercentage < 0.2f) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, null, tint = MapleBlack)
                        }
                    }
                }

                // 4. 뱃지 및 보스 이름 (접히면 투명화)
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .graphicsLayer { alpha = subInfoAlpha },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = bossParty.difficulty.badgeBackground,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(2.dp, bossParty.difficulty.badgeOutline)
                        ) {
                            Text(
                                text = bossParty.difficulty.displayName,
                                color = bossParty.difficulty.badgeText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = bossParty.boss.bossName,
                            style = Typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = bossParty.description, // 예: "숙련자만 오세요. 숍 필수!"
                        fontSize = 14.sp,
                        color = MapleBlack,
                        fontFamily = PretendardFamily,
                        lineHeight = 20.sp,
                        maxLines = 5, // 너무 길어지면 두 줄까지만 표시
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}