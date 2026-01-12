package com.sixclassguys.maplecalendar.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.login.LoginIntent
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.CharacterItemCard
import com.sixclassguys.maplecalendar.ui.component.CharacterStepIndicator
import com.sixclassguys.maplecalendar.ui.component.RepresentativeConfirmButton
import com.sixclassguys.maplecalendar.ui.component.WorldSelectBottomSheet
import com.sixclassguys.maplecalendar.ui.component.WorldSelector
import com.sixclassguys.maplecalendar.utils.MapleWorld

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRepresentativeCharacterScreen(
    viewModel: LoginViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    val columns = 3 // 그리드 열 개수
    val availableWorlds = remember(uiState.characters) {
        uiState.characters.keys.filter { worldName ->
            MapleWorld.getWorld(worldName) != null
        }.sortedBy { worldName ->
            MapleWorld.getWorld(worldName)?.ordinal ?: Int.MAX_VALUE
        }
    }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onNavigateToLogin()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            // Snackbar 표시 (비동기로 동작)
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // [중요] 메시지를 보여준 후 ViewModel에 알림 (중복 표시 방지)
            viewModel.onIntent(LoginIntent.ErrorMessageConsumed)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // 하단 캐릭터 선택 버튼
            val isSelected = uiState.selectedCharacter != null

            RepresentativeConfirmButton(
                isSelected = isSelected,
                onClick = { viewModel.onIntent(LoginIntent.SubmitRepresentativeCharacter) }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .background(MapleWhite)
                .padding(bottom = innerPadding.calculateBottomPadding()) // bottomBar만큼의 하단 여백 자동 확보
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. 타이틀 (3열 점유)
            item(span = { GridItemSpan(columns) }) {
                Text(
                    text = "대표 캐릭터 선택",
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                )
            }

            // 2. 단계 인디케이터 (3열 점유)
            item(span = { GridItemSpan(columns) }) {
                Spacer(modifier = Modifier.height(48.dp))
                CharacterStepIndicator() // 아까 키운 버전 적용
            }

            // 3. 안내 문구 (3열 점유)
            item(span = { GridItemSpan(columns) }) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "계정 내에서 대표캐릭터로 등록을 원하는\n캐릭터를 선택해주세요!",
                    style = TextStyle(color = MapleGray, fontSize = 15.sp, lineHeight = 22.sp)
                )
            }

            // 4. 월드 선택기 (3열 점유)
            item(span = { GridItemSpan(columns) }) {
                WorldSelector(
                    selectedWorld = uiState.selectedWorld,
                    onWorldClick = { viewModel.onIntent(LoginIntent.ShowWorldSheet(true)) }
                )
            }

            // 5. 캐릭터 리스트 (각 1열 점유)
            val currentWorldCharacters = uiState.characters[uiState.selectedWorld] ?: emptyList()
            items(currentWorldCharacters) { character ->
                CharacterItemCard(
                    character = character,
                    characterImage = uiState.characterImages[character.ocid] ?: "",
                    isSelected = uiState.selectedCharacter?.ocid == character.ocid,
                    onClick = { viewModel.onIntent(LoginIntent.SelectCharacter(character)) }
                )
            }

            // 하단 여유 공간 추가
            item(span = { GridItemSpan(columns) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (uiState.isWorldSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onIntent(LoginIntent.ShowWorldSheet(false)) },
                sheetState = sheetState,
                containerColor = MapleWhite
            ) {
                WorldSelectBottomSheet(
                    worlds = availableWorlds,
                    onWorldClick = { world ->
                        viewModel.onIntent(LoginIntent.SelectWorld(world))
                    },
                    onDismiss = {
                        viewModel.onIntent(LoginIntent.ShowWorldSheet(false))
                    }
                )
            }
        }
    }
}