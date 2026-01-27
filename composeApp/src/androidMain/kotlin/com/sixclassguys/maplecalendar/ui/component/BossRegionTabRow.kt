package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily

@OptIn(ExperimentalMaterial3Api::class) // Material 3 신규 API 사용 시 필요
@Composable
fun BossRegionTabRow(
    selectedRegion: String,
    onRegionSelected: (String) -> Unit
) {
    val regions = listOf("그란디스", "아케인리버", "메이플 월드")
    val selectedIndex = regions.indexOf(selectedRegion)

    // 기존 TabRow 대신 SecondaryTabRow 사용
    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MapleWhite,
        contentColor = MapleBlack,
        indicator = {
            // 인디케이터 두께나 색상을 커스텀할 때 사용
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = MapleOrange, // 강조색
                height = 3.dp
            )
        },
        divider = {
            // 하단 가로선을 얇게 그리거나 없앨 수 있음
            HorizontalDivider(thickness = 1.dp, color = MapleGray)
        }
    ) {
        regions.forEachIndexed { index, region ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onRegionSelected(region) },
                text = {
                    Text(
                        text = region,
                        fontFamily = PretendardFamily,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}