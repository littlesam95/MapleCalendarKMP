package com.sixclassguys.maplecalendar.ui.boss

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.BossIconItem
import com.sixclassguys.maplecalendar.ui.component.BossRegionTabRow
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.utils.backgroundRes

@Composable
fun BossPartyCreateScreen(
    viewModel: BossViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MapleStatBackground) // 다크한 배경색
    ) {
        // 1. BOSS SELECT 타이틀 및 탭
        Text(
            text = "BOSS SELECT",
            color = MapleStatTitle,
            modifier = Modifier.padding(16.dp),
            style = Typography.titleLarge
        )

        // 지역 선택 탭
        BossRegionTabRow(
            selectedRegion = uiState.selectedRegion
        ) {
            // TODO: 보스 선택
        }

        // 2. 보스 아이콘 가로 리스트
        LazyRow(
            modifier = Modifier.fillMaxWidth()
                .background(MapleWhite)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredBosses = Boss.entries.filter { it.region == uiState.selectedRegion }
            items(filteredBosses) { boss ->
                BossIconItem(
                    boss = boss,
                    isSelected = uiState.selectedBoss == boss,
                    onClick = { viewModel.onIntent(BossIntent.SelectBoss(boss)) }
                )
            }
        }

        // 3. 선택된 보스 상세 배경 및 난이도 버튼 (와이어프레임 핵심)
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            // 보스 배경 이미지
            Image(
                painter = painterResource(uiState.selectedBoss.backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 난이도 버튼 리스트 (오른쪽 하단)
            Column(
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.selectedBoss.difficulties.forEach { difficulty ->

                }
            }
        }
    }
}