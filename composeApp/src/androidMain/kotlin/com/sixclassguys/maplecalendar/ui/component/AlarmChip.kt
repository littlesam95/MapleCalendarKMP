package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import kotlinx.datetime.LocalDateTime

@Composable
fun AlarmChip(
    alarm: LocalDateTime,
    onRemove: () -> Unit
) {
    Surface(
        color = MapleOrange,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${alarm.year}년 ${alarm.monthNumber}월 ${alarm.dayOfMonth}일 ${alarm.hour}시 ${alarm.minute}분",
                color = MapleWhite, fontSize = 11.sp
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.Close, null,
                tint = MapleWhite,
                modifier = Modifier.size(14.dp)
                    .clickable { onRemove() }
            )
        }
    }
}