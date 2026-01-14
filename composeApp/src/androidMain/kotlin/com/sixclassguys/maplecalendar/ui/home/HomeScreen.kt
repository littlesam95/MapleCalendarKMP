package com.sixclassguys.maplecalendar.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.home.HomeIntent
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.CharacterBasicCard
import com.sixclassguys.maplecalendar.ui.component.EmptyCharacterBasicCard
import com.sixclassguys.maplecalendar.ui.component.HomeAppBar
import com.sixclassguys.maplecalendar.ui.component.TodayEventsCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val loginSuccess by viewModel.savedStateHandle.getStateFlow("loginSuccess", false).collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            Toast.makeText(context, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()
            viewModel.onIntent(HomeIntent.LoadApiKey)
            // 처리가 끝났다면 다시 false로 돌려준다.
            viewModel.savedStateHandle["loginSuccess"] = false
        }
    }

    LaunchedEffect(uiState.isNavigateToLogin) {
        if (uiState.isNavigateToLogin) {
            onNavigateToLogin()
            viewModel.onIntent(HomeIntent.NavigationHandled)
        }
    }

    Scaffold(
        // topBar를 비워둠으로써 전체가 스크롤되도록 설정
        containerColor = MapleWhite
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MapleWhite)
                .padding(horizontal = 20.dp)
        ) {
            // 1. 상단 앱바를 리스트의 첫 번째 아이템으로 삽입
            item {
                HomeAppBar(
                    onNotificationClick = { /* 알림 이동 */ }
                )
            }

            // 2. 캐릭터 정보 섹션
            item {
                Spacer(modifier = Modifier.height(24.dp))
                when {
                    uiState.characterBasic != null -> {
                        CharacterBasicCard(basic = uiState.characterBasic!!)
                    }
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MapleBlack
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. 이벤트 리스트
            if (uiState.events.isEmpty() && !uiState.isLoading) {
                item {
                    Text(
                        text = "진행 중인 이벤트가 없습니다.",
                        color = MapleGray,
                        fontSize = 16.sp
                    )
                }
            } else {
                items(uiState.events) { event ->
                    // 와이어프레임에 최적화된 새로운 카드 컴포넌트 호출
                    TodayEventsCard(
                        event = event,
                        onClick = { uriHandler.openUri(event.url) }
                    )
                    Spacer(modifier = Modifier.height(32.dp)) // 항목 간 여백 확대
                }
            }

            // 하단 여백
            item {
                // 바텀바 높이(56dp) + 여유공간을 고려한 Spacer
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}