package com.sixclassguys.maplecalendar.ui.boss

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.BossIconItem
import com.sixclassguys.maplecalendar.ui.component.BossPartyCreateDialog
import com.sixclassguys.maplecalendar.ui.component.BossRegionTabRow
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.utils.backgroundRes
import com.sixclassguys.maplecalendar.utils.selectButtonRes
import org.koin.compose.getKoin

@Composable
fun BossPartyCreateScreen(
    viewModel: BossViewModel,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val eventBus = getKoin().get<NotificationEventBus>()

    LaunchedEffect(Unit) {
        viewModel.onIntent(BossIntent.InitBossPartyCreate)
    }

    LaunchedEffect(uiState.createdPartyId) {
        val newBossPartyId = uiState.createdPartyId
        if (newBossPartyId != null) {
            onNavigateToDetail(newBossPartyId)
        }
    }

    LaunchedEffect(Unit) {
        eventBus.invitedPartyId.collect { invitedId ->
            if (invitedId != null) {
                viewModel.onIntent(BossIntent.FetchBossParties)
                eventBus.emitInvitedPartyId(null)
            }
        }
    }

    LaunchedEffect(Unit) {
        eventBus.kickedPartyId.collect { kickedId ->
            if (kickedId != null) {
                viewModel.onIntent(BossIntent.FetchBossParties)
                Toast.makeText(context, "파티를 떠났어요.", Toast.LENGTH_SHORT).show()
                eventBus.emitKickedPartyId(null)
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        val message = uiState.successMessage
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(BossIntent.InitMessage)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if ((message != null) && !uiState.showCreateDialog) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(BossIntent.InitMessage)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "보스방 만들기",
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MapleWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .background(MapleWhite) // 최하단 바닥 배경
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MapleStatBackground) // Wireframe 특유의 짙은 그레이 배경
            ) {
                // 1. 최상단 BOSS SELECT (배경색과 텍스트 강조)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MapleStatBackground) // 타이틀 영역 좀 더 어둡게
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    Text(
                        text = "BOSS SELECT",
                        color = MapleStatTitle,
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                // 2. 메인 컨텐츠 영역 (흰색 라운드 카드 스타일)
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f) // 🚀 중요: BOSS SELECT 텍스트를 제외한 남은 회색 공간을 꽉 채움
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp) // 🚀 바닥에서 살짝 띄우고 싶다면 유지, 아니면 0.dp
                        .clip(RoundedCornerShape(16.dp))
                        .background(MapleWhite)
                ) {
                    // 지역 선택 탭 (Wireframe처럼 텍스트 위주 구성)
                    BossRegionTabRow(
                        selectedRegion = uiState.selectedRegion,
                        onRegionSelected = { region ->
                            viewModel.onIntent(BossIntent.SelectRegion(region))
                        }
                    )

                    // 보스 아이콘 리스트 (배경 흰색 유지 및 하단 구분선)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
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

                    // 3. 메인 보스 카드 (가로폭을 꽉 채우고 배경이 다 보이도록 설정)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // 가로폭만 꽉 채움
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MapleWhite) // 혹시 모를 여백을 위한 배경색
                    ) {
                        Image(
                            painter = painterResource(id = uiState.selectedBoss.backgroundRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth() // 가로를 꽉 채움
                                .wrapContentHeight(), // 세로는 이미지 비율에 따라 자동 결정
                            contentScale = ContentScale.FillWidth // 🚀 가로 기준 맞춤 (배경이 다 보임)
                        )

                        // 난이도 버튼 (우측 하단 배치)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 16.dp), // 이미지 안쪽으로 여백 조절
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.selectedBoss.difficulties.forEach { difficulty ->
                                BossDifficultyButton(
                                    difficulty = difficulty,
                                    onClick = {
                                        viewModel.onIntent(BossIntent.SelectBossDifficulty(difficulty))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MapleBlack.copy(alpha = 0.7f)) // 화면 어둡게 처리
                    .pointerInput(Unit) {}, // 터치 이벤트 전파 방지 (클릭 막기)
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MapleOrange,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "보스 정보를 불러오고 있습니다...",
                        color = MapleWhite,
                        style = Typography.bodyLarge
                    )
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        BossPartyCreateDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.onIntent(BossIntent.DismissBossPartyCreateDialog) }
        )
    }
}

@Composable
fun BossDifficultyButton(
    difficulty: BossDifficulty,
    onClick: () -> Unit
) {
    // 버튼 이미지 리소스를 사용하여 텍스트 없이 배경 이미지 자체가 버튼 역할 수행
    Image(
        painter = painterResource(id = difficulty.selectButtonRes),
        contentDescription = difficulty.displayName,
        modifier = Modifier.width(140.dp) // 이미지 비율에 맞춘 너비
            .height(42.dp) // 이미지 비율에 맞춘 높이
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 이미지 버튼이므로 클릭 잔물결 효과 제거 (선택사항)
            ) { onClick() },
        contentScale = ContentScale.Fit
    )
}