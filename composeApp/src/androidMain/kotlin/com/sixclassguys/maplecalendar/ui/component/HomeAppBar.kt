package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite

@Composable
fun HomeAppBar(
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        color = MapleWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
                .height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Vector 로고 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Maplendar Logo",
                modifier = Modifier
                    .height(32.dp) // 로고 높이에 맞춰 자동 조절 (비율 유지)
                    .wrapContentWidth(),
                // 만약 로고가 검은색인데 흰색으로 바꾸고 싶다면 아래 속성 추가
                // colorFilter = ColorFilter.tint(Color.White)
            )

            // 2. 알림 아이콘
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = "Notifications",
                    tint = MapleOrange,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}