package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.theme.MapleOrange

@Composable
fun CharacterBasicCard(
    basic: CharacterBasic
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, MapleOrange),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
        ) {
            // 1. 캐릭터 이미지 (Coil 사용)
            Box(
                modifier = Modifier
                    .size(140.dp) // 카드 크기에 맞춰 조절
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = basic.characterImage,
                    contentDescription = "캐릭터 아바타",
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer(
                            scaleX = 2.7f,
                            scaleY = 2.7f,
                            // [핵심] 음수 값을 주어 캐릭터를 위로 올립니다.
                            // 캐릭터 위치에 따라 -40f ~ -80f 사이에서 조절해 보세요.
                            translationY = -60f
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(24.dp)) // 이미지와 텍스트 사이 간격 확대

            // 2. 캐릭터 정보 열
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp) // 행 사이 간격
            ) {
                // 이름과 길드 버튼 행
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // 이름은 왼쪽, 길드는 오른쪽
                ) {
                    Text(
                        text = basic.characterName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // 길드 표시 (Pill 모양)
                    Surface(
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "${basic.characterGuildName}",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 상세 정보들
                CharacterDetailRow("Lv. ${basic.characterLevel}  ${basic.characterExpRate}%")
                CharacterDetailRow(basic.characterClass)

                // 유니온, 무릉 등은 character/basic 외 별도 API가 필요하지만 일단 레이아웃 유지
                CharacterDetailRow("유니온", "10,597") // 예시 데이터
                CharacterDetailRow("인기도", "141")
                CharacterDetailRow("무릉", "100층")
                CharacterDetailRow("종합", "10,597위")
                CharacterDetailRow("서버", "141위")
            }
        }
    }
}

@Composable
fun CharacterDetailRow(label: String, value: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Black
        )
        if (value != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}