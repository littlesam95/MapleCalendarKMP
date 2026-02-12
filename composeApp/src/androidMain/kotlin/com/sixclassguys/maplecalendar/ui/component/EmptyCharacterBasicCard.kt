package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.Typography

@Composable
fun EmptyCharacterBasicCard(
    onClick: () -> Unit,
    modifier: Modifier
) {
    val stroke = Stroke(
        width = 3f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
    )

    Box(
        modifier = Modifier.fillMaxWidth()
            .height(200.dp)
            .drawBehind {
                drawRoundRect(
                    color = MapleOrange,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MapleOrange,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "로그인하여 캐릭터의 정보를 확인하세요!",
                style = Typography.titleSmall,
                color = MapleOrange
            )
        }
    }
}