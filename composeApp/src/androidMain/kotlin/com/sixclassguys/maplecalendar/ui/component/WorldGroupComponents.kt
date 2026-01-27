package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.utils.MapleWorld

@Composable
fun WorldGroupTabs(
    groups: List<String>,
    selectedGroup: String,
    onGroupSelected: (String) -> Unit
) {
    if (groups.isEmpty()) return
    val selectedIndex = groups.indexOf(selectedGroup).coerceAtLeast(0)

    // Material 3의 Secondary 버전 사용
    SecondaryScrollableTabRow(
        selectedTabIndex = groups.indexOf(selectedGroup).coerceAtLeast(0),
        containerColor = Color.Transparent,
        contentColor = MapleOrange, // 인디케이터 기본 컬러로 활용됨
        edgePadding = 16.dp,
        divider = {},
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = MapleOrange
            )
        }
    ) {
        groups.forEach { group ->
            val isSelected = selectedGroup == group
            Tab(
                selected = isSelected,
                onClick = { onGroupSelected(group) },
                text = {
                    Text(
                        text = group,
                        fontFamily = PretendardFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MapleBlack else MapleGray
                    )
                }
            )
        }
    }
}

@Composable
fun WorldIconRow(
    worlds: List<String>,
    selectedWorld: String,
    onWorldSelected: (String) -> Unit
) {

    LazyRow(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(worlds) { world ->
            val worldMark = MapleWorld.getWorld(world)?.iconRes ?: R.drawable.ic_world_scania

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onWorldSelected(world) }
            ) {
                // 월드 아이콘 (실제로는 world 이름에 맞는 이미지를 매핑해야 함)
                Image(
                    painter = painterResource(id = worldMark),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (selectedWorld == world) {
                    Box(
                        modifier = Modifier.width(20.dp)
                            .height(2.dp)
                            .background(MapleOrange)
                    )
                } else {
                    // 높이 유지를 위한 빈 공간
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}