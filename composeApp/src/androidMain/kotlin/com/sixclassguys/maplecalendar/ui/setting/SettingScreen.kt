package com.sixclassguys.maplecalendar.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.home.HomeIntent
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.presentation.setting.SettingIntent
import com.sixclassguys.maplecalendar.presentation.setting.SettingViewModel
import com.sixclassguys.maplecalendar.theme.MapleWhite
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = koinViewModel(),
    homeViewModel: HomeViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MapleWhite)
            .padding(24.dp)
    ) {
        Text(
            text = "환경설정",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // 💡 로그인 상태에 따른 UI 분기
        if (uiState.nexonApiKey == null) {
            MapleButton(
                text = "로그인",
                onClick = onNavigateToLogin,
                containerColor = Color(0xFFF29F38)
            )
        } else {
            // 1. 알림 설정 섹션
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "이벤트 알림 수신",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = uiState.isGlobalAlarmEnabled,
                    onCheckedChange = { viewModel.onIntent(SettingIntent.ToggleGlobalAlarmStatus) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFF29F38), // 메이플 주황색
                        checkedTrackColor = Color(0xFFF29F38).copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 2. 로그아웃 버튼
            MapleButton(
                text = "로그아웃",
                onClick = {
                    viewModel.onIntent(SettingIntent.Logout)
                    homeViewModel.onIntent(HomeIntent.Logout)
                },
                containerColor = Color(0xFFFF7E7E) // 로그아웃용 붉은 계열
            )
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }
}

@Composable
fun MapleButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}