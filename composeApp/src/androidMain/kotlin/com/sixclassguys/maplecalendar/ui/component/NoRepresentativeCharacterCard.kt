package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography

@Composable
fun NoRepresentativeCharacterCard(
    nickname: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .height(200.dp) // 기존 캐릭터 카드와 높이를 통일하여 일관성 유지
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MapleWhite),
        border = BorderStroke(1.5.dp, MapleOrange) // 테두리로 포인트
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 실루엣 이미지 (와이어프레임 이미지 3번 참고)
            Image(
                painter = painterResource(R.drawable.ic_character_silhouette), // 실루엣 리소스 필요
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 안내 문구
            Text(
                text = "${nickname}님을 대표하는 캐릭터가 없어요.",
                style = Typography.bodyLarge,
                color = MapleOrange
            )
            Text(
                text = "대표 캐릭터를 설정해보세요!",
                style = Typography.bodyLarge,
                color = MapleOrange
            )
        }
    }
}