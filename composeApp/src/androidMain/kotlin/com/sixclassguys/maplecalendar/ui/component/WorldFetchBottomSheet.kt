package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterUiState
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.MapleWorld

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldFetchBottomSheet(
    uiState: MapleCharacterUiState,
    onWorldClick: (String) -> Unit,
    onGroupClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MapleStatBackground, // 가이드 시트와 같은 어두운 배경색
        dragHandle = null // 상단 줄 제거
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
                .fillMaxHeight(0.8f) // 화면의 80% 정도 높이
        ) {
            // 1. 상단 타이틀
            Text(
                text = "WORLD SELECT",
                color = Color(0xFFE6E600), // 노란색 포인트
                style = Typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            // 2. 내부 흰색 카드 영역
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp),
                color = MapleWhite
            ) {
                Column {
                    // 월드 그룹 탭 (SecondaryScrollableTabRow)
                    val worldGroups = uiState.newCharacterSummeries.keys.toList()
                    val selectedIndex = worldGroups.indexOf(uiState.selectedFetchWorldGroup).coerceAtLeast(0)

                    SecondaryScrollableTabRow(
                        selectedTabIndex = selectedIndex,
                        containerColor = MapleWhite,
                        contentColor = Color.LightGray,
                        edgePadding = 20.dp,
                        divider = {},
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                                color = MapleOrange
                            )
                        }
                    ) {
                        worldGroups.forEach { group ->
                            val isSelected = uiState.selectedFetchWorldGroup == group
                            Tab(
                                selected = isSelected,
                                onClick = { onGroupClick(group) },
                                text = {
                                    Text(
                                        text = group,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MapleBlack else MapleGray
                                    )
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF5F5F5))

                    // 3. 월드 리스트
                    val currentGroupWorlds = uiState.newCharacterSummeries[uiState.selectedFetchWorldGroup]?.keys?.toList() ?: emptyList()
                    // WorldFetchBottomSheet 내부 LazyColumn
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(currentGroupWorlds) { world ->
                            WorldListItem(
                                worldName = world,
                                isSelected = uiState.selectedFetchWorld == world,
                                onClick = {
                                    onWorldClick(world)
                                    onDismiss() // 클릭 즉시 시트 닫기
                                }
                            )
                            HorizontalDivider(color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorldListItem(
    worldName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val worldMark = MapleWorld.getWorld(worldName)?.iconRes ?: R.drawable.ic_world_scania

    // 💡 Surface 대신 Box나 Row에 직접 clickable을 주는 것이 더 확실합니다.
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(
                // 클릭 시 물결 효과(Ripple)가 보이도록 설정
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = worldMark),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = worldName,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MapleBlack else MapleBlack.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MapleOrange,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}