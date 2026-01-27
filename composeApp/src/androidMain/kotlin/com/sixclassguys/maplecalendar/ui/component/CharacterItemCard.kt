package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.Typography

@Composable
fun CharacterItemCard(
    character: CharacterSummary,
    characterImage: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MapleOrange else MapleBlack
    val borderStroke = if (isSelected) 2.dp else 1.dp

    Column(
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(0.7f) // 카드 비율 설정
            .clip(RoundedCornerShape(12.dp))
            .border(borderStroke, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 캐릭터 이미지 (Coil 사용 권장)

        if (characterImage == "") {
            AsyncImage(
                model = characterImage,
                contentDescription = "캐릭터 이미지",
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_character_silhouette),
                error = painterResource(R.drawable.ic_character_silhouette)
            )
        } else {
            AsyncImage(
                model = characterImage,
                contentDescription = "캐릭터 이미지",
                modifier = Modifier.fillMaxWidth()
                    .weight(1.2f) // 텍스트 영역보다 이미지 영역 비중을 더 높임
                    .graphicsLayer(
                        scaleX = 2.8f, // 1.5배 확대
                        scaleY = 2.8f,
                        translationY = -10f // 캐릭터 발 위치 조정 필요 시 사용
                    ),
                contentScale = ContentScale.Fit, // Crop보다는 Fit 상태에서 확대하는 게 위치 잡기 편합니다.
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = character.characterName,
            style = Typography.bodyLarge,
            maxLines = 1
        )
        Text(
            text = "Lv.${character.characterLevel}",
            style = Typography.bodySmall,
            color = MapleGray
        )
        Text(
            text = character.characterClass,
            style = Typography.bodySmall,
            color = MapleGray
        )
    }
}