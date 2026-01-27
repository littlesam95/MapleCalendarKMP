package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.utils.iconRes

@Composable
fun BossIconItem(
    boss: Boss,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp) // 아이콘 크기에 맞춘 너비
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 클릭 시 잔물결 효과 제거 (깔끔한 UI를 위해)
            ) { onClick() }
    ) {
        // 아이콘 이미지 영역
        Box(
            modifier = Modifier.size(60.dp)
                .clip(RoundedCornerShape(12.dp)) // 둥근 사각형
                .background(if (isSelected) Color(0xFFFFF3E0) else Color(0xFFF5F5F5)) // 배경색 차이
                .border(
                    width = 3.dp,
                    color = if (isSelected) MapleOrange else Color.Transparent, // 선택 시 강조색 테두리
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = boss.iconRes),
                contentDescription = boss.bossName,
                modifier = Modifier.size(50.dp)
                    .alpha(if (isSelected) 1f else 0.6f), // 미선택 시 약간 흐리게
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 보스 이름 텍스트
        Text(
            text = boss.bossName,
            fontFamily = PretendardFamily,
            color = if (isSelected) Color.Black else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}