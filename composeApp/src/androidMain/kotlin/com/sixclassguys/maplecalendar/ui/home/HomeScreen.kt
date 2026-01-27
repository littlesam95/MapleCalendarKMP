package com.sixclassguys.maplecalendar.ui.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.home.HomeIntent
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.CarouselEventRow
import com.sixclassguys.maplecalendar.ui.component.CharacterBasicCard
import com.sixclassguys.maplecalendar.ui.component.EmptyCharacterBasicCard
import com.sixclassguys.maplecalendar.ui.component.EmptyEventScreen
import com.sixclassguys.maplecalendar.ui.component.HomeAppBar
import com.sixclassguys.maplecalendar.ui.component.NoRepresentativeCharacterCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onNavigateToCharacterList: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(uiState.member) {
        val member = uiState.member
        if (member != null) {
            Toast.makeText(context, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()
            // viewModel.onIntent(HomeIntent.LoadApiKey)
        }
    }

    LaunchedEffect(uiState.isNavigateToLogin) {
        if (uiState.isNavigateToLogin) {
            onNavigateToLogin()
            viewModel.onIntent(HomeIntent.NavigationHandled)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        // topBar를 비워둠으로써 전체가 스크롤되도록 설정
        containerColor = MapleWhite
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .background(MapleWhite)
                .padding(horizontal = 20.dp)
        ) {
            // 1. 상단 앱바를 리스트의 첫 번째 아이템으로 삽입
            item {
                HomeAppBar(
                    onNotificationClick = {
                        // 알림 모아보기 기능은 준비중
                        Toast.makeText(context, "준비중입니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 2. 캐릭터 정보 섹션
            item {
                Spacer(modifier = Modifier.height(24.dp))
                val member = uiState.member
                val basic = member?.characterBasic
                val dojangRanking = member?.characterDojang
                val overallRanking = member?.characterOverallRanking
                val serverRanking = member?.characterServerRanking
                val union = member?.characterUnionLevel
                when {
                    !uiState.isLoginSuccess -> {
                        EmptyCharacterBasicCard(
                            onClick = { viewModel.onIntent(HomeIntent.Login) }
                        )
                    }

                    ((basic != null) && (dojangRanking != null) && (overallRanking != null) && (serverRanking != null) && (union != null)) -> {
                        CharacterBasicCard(
                            basic = basic,
                            dojangRanking = dojangRanking,
                            overallRanking = overallRanking,
                            serverRanking = serverRanking,
                            union = union
                        )
                    }

                    (basic == null) -> {
                        NoRepresentativeCharacterCard(
                            nickname = member?.nickname ?: "메이플스토리 용사",
                            onClick = onNavigateToCharacterList
                        )
                    }

                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MapleOrange)
                        }
                    }

                    else -> {
                        EmptyCharacterBasicCard(
                            onClick = { viewModel.onIntent(HomeIntent.Login) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // 3. 오늘 진행하는 이벤트 타이틀
            item {
                Text(
                    text = "오늘 진행하는 이벤트",
                    style = Typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MapleBlack
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.events.isEmpty() && !uiState.isLoading) {
                    EmptyEventScreen("진행중인 이벤트가 없어요.")
                } else {
                    CarouselEventRow(
                        nowEvents = uiState.events,
                        onNavigateToEventDetail = { /* url 오픈 혹은 상세 페이지 */ }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp)) // 항목 간 여백 확대
            }

            // 4. 오늘의 보스 파티 일정 (수정)
            item {
                Text(
                    text = "오늘의 보스 파티 일정",
                    style = Typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MapleBlack
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (!uiState.isLoginSuccess || uiState.bossSchedules.isEmpty()) {
                    // 로그아웃이거나 일정이 없을 때 (이미지 4번 하단 슬픈 버섯 참고)
                    EmptyEventScreen("오늘은 보스 쉬는 날~")
                } else {
                    // 보스 일정 리스트 (가로 스크롤 혹은 세로 배치)
                    // BossScheduleRow(uiState.bossSchedules)
                }
            }

            // 하단 여백
            item {
                // 바텀바 높이(56dp) + 여유공간을 고려한 Spacer
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}