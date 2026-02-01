package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.utils.badgeBackground
import com.sixclassguys.maplecalendar.utils.badgeOutline
import com.sixclassguys.maplecalendar.utils.badgeText
import com.sixclassguys.maplecalendar.utils.entryBackgroundRes

@Composable
fun BossPartyCard(
    bossParty: BossParty,
    onPartyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onPartyClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MapleWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 🚀 1. 보스 대표 이미지
            AsyncImage(
                model = bossParty.boss.entryBackgroundRes,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 🚀 2. 제목 및 난이도 태그
                Text(
                    text = bossParty.title,
                    fontFamily = PretendardFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )

                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
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
                            fontFamily = PretendardFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bossParty.boss.bossName,
                        fontFamily = PretendardFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MapleBlack
                    )
                }

                // 🚀 3. 파티원 및 알람 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MapleGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (bossParty.memberCount == 1) bossParty.leaderNickname else "${bossParty.leaderNickname} 외 ${bossParty.memberCount - 1}인",
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MapleGray
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTimeFilled,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MapleOrange
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "2026년 1월 31일 토요일 19:00",
                        fontFamily = PretendardFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MapleBlack
                    )
                }
            }
        }
    }
}