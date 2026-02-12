package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.CharacterDojangRanking
import com.sixclassguys.maplecalendar.domain.model.CharacterRanking
import com.sixclassguys.maplecalendar.domain.model.CharacterUnion
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.MapleWorld
import com.sixclassguys.maplecalendar.utils.makeCommaInt
import com.sixclassguys.maplecalendar.utils.makeCommaRank

@Composable
fun CharacterBasicCard(
    basic: CharacterBasic,
    dojangRanking: CharacterDojangRanking,
    overallRanking: CharacterRanking,
    serverRanking: CharacterRanking,
    union: CharacterUnion,
    modifier: Modifier
) {
    val worldMark = MapleWorld.getWorld(basic.worldName)?.iconRes ?: R.drawable.ic_world_scania

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, MapleOrange),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp) // 전체 여백 살짝 조정
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 캐릭터 이미지 영역
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = basic.characterImage,
                    contentDescription = "캐릭터 아바타",
                    modifier = Modifier
                        .size(240.dp) // 와이어프레임처럼 크게 강조
                        .graphicsLayer(
                            scaleX = 2.8f,
                            scaleY = 2.8f,
                            translationY = -40f // 와이어프레임 높이에 맞춰 조정
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. 캐릭터 정보 열
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 이름 / 월드아이콘 / 길드 버튼 행
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = basic.characterName,
                        style = Typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Image(
                        painter = painterResource(id = worldMark),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // 길드 표시
                    Surface(
                        border = BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = basic.characterGuildName,
                            style = Typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            color = Color.Black
                        )
                    }
                }

                Text(
                    text = "Lv. ${basic.characterLevel}  ${basic.characterExpRate}%",
                    style = Typography.bodyLarge,
                    color = Color.Black
                )

                Text(
                    text = basic.characterClass,
                    style = Typography.bodyLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 서버에서 아직 이 정보들을 보내주지 않음
                CharacterDetailRow(
                    label = "유니온",
                    value = makeCommaInt(union.unionLevel)
                )
                CharacterDetailRow(
                    label = "인기도",
                    value = makeCommaInt(overallRanking.characterPopularity)
                )
                CharacterDetailRow(
                    label = "무릉",
                    value = "${dojangRanking.dojangBestFloor}층"
                )
                CharacterDetailRow(
                    label = "종합",
                    value = makeCommaRank(overallRanking.rank)
                )
                CharacterDetailRow(
                    label = "서버",
                    value = makeCommaRank(serverRanking.rank)
                )
            }
        }
    }
}

@Composable
fun CharacterDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.width(50.dp) // 레이블 너비 고정으로 정렬 유지
        )
        Text(
            text = value,
            style = Typography.bodyMedium,
            color = Color.Black
        )
    }
}