package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.domain.model.BossPartySchedule
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.iconRes

@Composable
fun BossScheduleRow(
    schedule: BossPartySchedule,
    onNavigateToBossDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .padding(8.dp)
            .clickable { onNavigateToBossDetail(schedule.bossPartyId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            // 1. 보스 배경 이미지 (상단 라운드 처리)
            Image(
                painter = painterResource(id = schedule.boss.iconRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. 보스명 및 난이도
            Text(
                text = "${schedule.boss.bossName}(${schedule.bossDifficulty.displayName})",
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MapleBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3. 파티원 정보 (인원수 - 이름들)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                val memberNames = schedule.members.joinToString(", ") { it.characterName }
                Text(
                    text = "${schedule.members.size}인 - $memberNames",
                    style = Typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MapleGray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. 시간 정보
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTimeFilled,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = schedule.time, // 예: 21:00
                    style = Typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MapleGray
                )
            }
        }
    }
}