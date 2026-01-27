package com.sixclassguys.maplecalendar.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterIntent
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.ApiKeyGuideBottomSheet
import com.sixclassguys.maplecalendar.ui.component.CharacterStepIndicator
import com.sixclassguys.maplecalendar.ui.component.MapleCharacterFetchHeader
import com.sixclassguys.maplecalendar.utils.MapleWorld

@Composable
fun MapleCharacterFetchScreen(
    viewModel: MapleCharacterViewModel,
    onBack: () -> Unit,
    onNavigateToSubmit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showGuide by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val allWorlds = MapleWorld.entries.map { it.worldName }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.newCharacterSummeries) {
        if (uiState.newCharacterSummeries.isNotEmpty() && uiState.isFetchStarted) {
            onNavigateToSubmit()
            viewModel.onIntent(MapleCharacterIntent.LockIsFetchStarted)
        }
    }

    Scaffold(
        topBar = {
            MapleCharacterFetchHeader(onBack = onBack)
        },
        containerColor = MapleWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            CharacterStepIndicator(
                currentStep = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "대부분의 기능은\n캐릭터를 등록해야 이용하실 수 있습니다.",
                style = Typography.bodyLarge,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.clickable {
                    uriHandler.openUri("https://openapi.nexon.com/ko/")
                }
            ) {
                Row {
                    Text(
                        text = "NEXON Open API 사이트",
                        color = MapleOrange,
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        text = "에서 넥슨 아이디로 로그인하여",
                        textAlign = TextAlign.Start,
                        style = Typography.bodyLarge
                    )
                }
                Text(
                    text = "API Key를 확인하세요!",
                    textAlign = TextAlign.Start,
                    style = Typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showGuide = true }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "API Key 발급받는 방법",
                    fontFamily = PretendardFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MapleBlack
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.nexonOpenApiKey,
                onValueChange = { viewModel.onIntent(MapleCharacterIntent.UpdateApiKey(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "NEXON Open API Key를 입력하세요.",
                        style = Typography.bodyLarge
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MapleBlack,
                    unfocusedBorderColor = MapleBlack
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (uiState.nexonOpenApiKey.isNotBlank()) {
                            viewModel.onIntent(MapleCharacterIntent.SubmitApiKey(allWorlds))
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                enabled = uiState.nexonOpenApiKey.isNotBlank() && !uiState.isLoading,
                onClick = { viewModel.onIntent(MapleCharacterIntent.SubmitApiKey(allWorlds)) },
                modifier = Modifier.fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MapleBlack)
            ) {
                Text(
                    text = "Open API Key로 캐릭터 조회",
                    fontFamily = PretendardFamily,
                    color = MapleWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                        text = "캐릭터 정보를 불러오고 있습니다...",
                        color = MapleWhite,
                        style = Typography.bodyLarge
                    )
                }
            }
        }
    }

    if (showGuide) {
        ApiKeyGuideBottomSheet(onDismiss = { showGuide = false })
    }
}