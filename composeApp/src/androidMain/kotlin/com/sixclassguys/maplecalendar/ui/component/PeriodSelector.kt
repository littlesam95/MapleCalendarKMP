package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelector(
    modifier: Modifier = Modifier,
    onPeriodSelected: (Int) -> Unit
) {
    val intervals = listOf("매일", "이틀마다", "사흘마다", "일주일마다")
    val intervalValues = listOf(1, 2, 3, 7)

    var expanded by remember { mutableStateOf(false) }
    var selectedIdx by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "날짜",
            style = TextStyle(
                color = MapleBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 드롭다운 박스
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, MapleGray, RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = intervals[selectedIdx],
                    style = TextStyle(color = MapleBlack, fontSize = 15.sp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MapleOrange
                )
            }

            // 드롭다운 메뉴 항목
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.7f) // 다이얼로그 너비에 맞춰 적절히 조절
                    .background(MapleWhite)
            ) {
                intervals.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label, color = MapleBlack) },
                        onClick = {
                            selectedIdx = index
                            onPeriodSelected(intervalValues[index])
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}