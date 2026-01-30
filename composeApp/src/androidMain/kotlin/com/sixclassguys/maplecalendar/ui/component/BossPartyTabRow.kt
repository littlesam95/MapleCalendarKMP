package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.util.BossPartyTab

@Composable
fun BossPartyDetailTabRow(
    selectedTab: BossPartyTab,
    onTabSelected: (BossPartyTab) -> Unit
) {
    // Material 3의 SecondaryTabRow 사용
    SecondaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MapleWhite, // 배경색
        contentColor = MapleOrange,     // 선택된 탭의 기본 색상
        indicator = {
            // Material 3 방식의 인디케이터 설정
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal),
                color = MapleOrange,
                height = 3.dp
            )
        },
        divider = {
            // 탭 하단 구분선 (필요 없으면 비워두거나 색상 지정)
            HorizontalDivider(color = Color.Transparent)
        }
    ) {
        BossPartyTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.title,
                        fontSize = 14.sp,
                        fontFamily = PretendardFamily, // 사용 중인 폰트 적용
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) MapleOrange else Color.LightGray
                    )
                }
            )
        }
    }
}