package com.sixclassguys.maplecalendar.ui.boss

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BossPartyImageDetailScreen(
    viewModel: BossViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. 권한 요청 런처 (Android 28 이하 대응)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onIntent(BossIntent.DownloadBossPartyBoardImage)
        } else {
            Toast.makeText(context, "이미지를 저장하려면 저장소 권한이 필요해요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. 줌 및 위치 상태 관리
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // 줌 상태 초기화 (다른 이미지로 바뀔 때를 대비)
    LaunchedEffect(uiState.selectedBossPartyBoardImageUrl) {
        scale = 1f
        offset = Offset.Zero
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
        if ((message != null) && !uiState.showBossAlarmDialog && !uiState.showCharacterInvitationDialog && !uiState.showBossPartyChatReport && !uiState.showBossPartyBoardDialog) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.onIntent(BossIntent.InitMessage)
        }
    }

    // 배경을 검은색으로 설정하여 이미지에 집중하게 함
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MapleBlack)
            .pointerInput(Unit) {
                // 더블 탭 시 줌 초기화 기능 (선택 사항)
                detectTapGestures(onDoubleTap = {
                    scale = 1f
                    offset = Offset.Zero
                })
            }
    ) {
        // 🚀 1. 메인 이미지 (중앙 배치)
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // 기본 중앙 배치
        ) {
            val screenWidth = constraints.maxWidth.toFloat()
            val screenHeight = constraints.maxHeight.toFloat()

            AsyncImage(
                model = uiState.selectedBossPartyBoardImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // 🚀 핵심: 줌과 팬을 한 곳에서 동시에 처리 (충돌 방지)
                        detectTransformGestures { _, pan, zoom, _ ->
                            // 1. 새로운 배율 계산
                            val newScale = (scale * zoom).coerceIn(1f, 5f)

                            // 2. 새로운 위치 계산 (줌이 1배보다 클 때만)
                            if (newScale > 1f) {
                                val newOffset = offset + pan * scale

                                // 🌟 실제 이미지의 가시 영역을 기준으로 경계 계산
                                // (ContentScale.Fit 환경에서 화면을 넘어서는 실제 크기 제한)
                                val maxOffsetX = (screenWidth * newScale - screenWidth).coerceAtLeast(0f) / 2
                                val maxOffsetY = (screenHeight * newScale - screenHeight).coerceAtLeast(0f) / 2

                                scale = newScale
                                offset = Offset(
                                    x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                    y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                                )
                            } else {
                                // 1배율 이하로 내려갈 경우 초기화
                                scale = 1f
                                offset = Offset.Zero
                            }
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentScale = ContentScale.Fit
            )
        }

        // 🚀 2. 상단 상단 바 (닫기, 다운로드 버튼)
        Row(
            modifier = Modifier.fillMaxWidth()
                .statusBarsPadding() // 시스템 상태바 영역 확보
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 닫기 버튼 (왼쪽)
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MapleBlack.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = MapleWhite
                )
            }

            // 다운로드 버튼 (오른쪽)
            IconButton(
                onClick = {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { // API 28 이하
                        val isGranted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED

                        if (isGranted) {
                            viewModel.onIntent(BossIntent.DownloadBossPartyBoardImage)
                        } else {
                            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    } else {
                        viewModel.onIntent(BossIntent.DownloadBossPartyBoardImage)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = MapleBlack.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "다운로드",
                    tint = MapleWhite
                )
            }
        }
    }
}