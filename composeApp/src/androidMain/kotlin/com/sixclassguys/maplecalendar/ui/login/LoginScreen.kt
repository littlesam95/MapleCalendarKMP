package com.sixclassguys.maplecalendar.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.domain.model.Member
import com.sixclassguys.maplecalendar.presentation.login.LoginIntent
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.LoginSuccessDialog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBackClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onAppleLoginClick: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateToHome: (Member) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isLoginSuccess) {
        val member = uiState.member
        if (uiState.isLoginSuccess && member != null) {
            onNavigateToHome(member)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (message != null) {
            viewModel.onIntent(LoginIntent.InitErrorMessage)
            Napier.d("Error Message: $message")
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        containerColor = MapleWhite,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = padding.calculateBottomPadding())
                .background(MapleWhite)
        ) {
            // 1. 상단 바 (뒤로가기 + 타이틀)
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "로그인",
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // 2. 설명 문구 섹션
            Text(
                text = "대부분의 기능은\n로그인을 해야 이용하실 수 있습니다.",
                style = Typography.bodyLarge,
                color = MapleGray,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "로그인하셔서 이벤트 및 보스 파티 알림 등\n다양한 기능을 이용해보세요!",
                style = Typography.bodyLarge,
                color = MapleGray,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. 구글 로그인 버튼 (와이어프레임 스타일)
            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier.fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MapleGray),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MapleWhite)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 구글 로고 아이콘
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign in with Google",
                        style = Typography.titleMedium,
                        color = MapleBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAppleLoginClick, // ViewModel에 의도 전달
                modifier = Modifier.fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MapleBlack) // 애플은 주로 블랙 배경 사용
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_apple_logo), // 애플 로고 리소스 필요
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign in with Apple",
                        style = Typography.titleMedium,
                        color = MapleWhite
                    )
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
                        text = "로그인 중이에요...",
                        color = MapleWhite,
                        style = Typography.bodyLarge
                    )
                }
            }
        }
    }

    if (uiState.showRegistrationDialog) {
        LoginSuccessDialog(
            userName = uiState.member?.nickname ?: "메이플스토리 용사",
            onDismiss = {
                uiState.member?.let { onNavigateToHome(it) }
            },
            onConfirm = {
                onNavigateToRegistration()
            }
        )
    }
}