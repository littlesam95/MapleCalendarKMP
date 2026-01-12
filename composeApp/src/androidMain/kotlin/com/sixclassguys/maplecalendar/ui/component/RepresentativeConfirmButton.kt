package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite

@Composable
fun RepresentativeConfirmButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val buttonColor = if (isSelected) MapleOrange else MapleGray
    val textColor = if (isSelected) MapleWhite else MapleBlack

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // 시스템 네비게이션 바와 겹침 방지
            .background(buttonColor)
            .clickable(enabled = isSelected) { onClick() }
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "대표 캐릭터 선택",
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}