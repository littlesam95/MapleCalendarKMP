package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange

@Composable
fun CharacterStepIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step 1: 완료 상태
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nexon_authentication),
                contentDescription = null,
                tint = MapleGray,
                modifier = Modifier
                    .padding(horizontal = 24.dp) // 화살표 양옆 간격을 더 넓게 (기존 8dp -> 24dp)
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "NEXON ID\n인증",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MapleGray
            )
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 24.dp),
            tint = MapleBlack
        )

        // Step 2: 진행 중 상태 (주황색)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = null,
                tint = MapleOrange,
                modifier = Modifier
                    .padding(horizontal = 24.dp) // 화살표 양옆 간격을 더 넓게 (기존 8dp -> 24dp)
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "대표 캐릭터\n선택",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MapleOrange,
                fontWeight = FontWeight.Bold
            )
        }
    }
}