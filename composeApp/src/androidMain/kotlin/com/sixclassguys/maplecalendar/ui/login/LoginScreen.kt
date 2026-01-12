package com.sixclassguys.maplecalendar.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.login.LoginIntent
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToCharacterSelection: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            Toast.makeText(context, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.navigateToSelection) {
        if (uiState.navigateToSelection) {
            onNavigateToCharacterSelection()

            // 이동하자마자 신호 종료, 이제 뒤로가기로 돌아와도 이 if문은 다시 실행되지 않음
            viewModel.onIntent(LoginIntent.NavigationConsumed)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MapleWhite)
            .statusBarsPadding() // 상태바 겹침 방지
            .padding(horizontal = 16.dp)
    ) {
        // 1. 상단 뒤로가기 및 타이틀 (디자인에 따라 조정 가능)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "로그인",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MapleBlack
        )

        Spacer(modifier = Modifier.height(64.dp))

        // 2. 설명 텍스트
        Text(
            text = "대부분의 기능은\n로그인을 해야 이용하실 수 있습니다.",
            fontSize = 16.sp,
            color = MapleGray,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "NEXON Open API 사이트에서 넥슨 아이디로 로그인하여\nAPI Key를 확인하세요!",
            fontSize = 16.sp,
            color = MapleGray,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 3. API Key 입력 필드
        OutlinedTextField(
            value = uiState.nexonApiKey,
            onValueChange = { viewModel.onIntent(LoginIntent.UpdateApiKey(it)) },
            placeholder = {
                Text(
                    text = "NEXON Open API Key를 입력하세요.",
                    color = MapleGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MapleBlack,
                unfocusedBorderColor = MapleGray,
                cursorColor = MapleBlack
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // 로그인 버튼의 enabled 조건과 동일하게 체크 후 실행
                    if (!uiState.isLoading && uiState.nexonApiKey.isNotBlank()) {
                        viewModel.onIntent(LoginIntent.ClickLogin)
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. 로그인 버튼
        Button(
            onClick = { viewModel.onIntent(LoginIntent.ClickLogin) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MapleBlack,
                contentColor = MapleWhite
            ),
            enabled = !uiState.isLoading && uiState.nexonApiKey.isNotBlank() // 로딩 중에는 클릭 방지
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = MapleWhite,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Open API Key로 로그인",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 에러 메시지 처리 (필요 시)
        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MapleOrange,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}