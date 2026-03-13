package com.sixclassguys.maplecalendar

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sixclassguys.maplecalendar.navigation.Navigation
import com.sixclassguys.maplecalendar.navigation.navhost.NavHost
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarIntent
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistIntent
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistViewModel
import com.sixclassguys.maplecalendar.presentation.setting.SettingViewModel
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.BottomNavigationBar
import com.sixclassguys.maplecalendar.ui.component.DraggableMiniPlayerOverlay
import com.sixclassguys.maplecalendar.ui.playlist.MapleBgmPlayScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity")
@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    val homeViewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = activity)
    val settingViewModel: SettingViewModel = koinViewModel(viewModelStoreOwner = activity)
    val calendarViewModel: CalendarViewModel = koinViewModel(viewModelStoreOwner = activity)
    val mapleCharacterViewModel: MapleCharacterViewModel = koinViewModel(viewModelStoreOwner = activity)
    val bossViewModel: BossViewModel = koinViewModel(viewModelStoreOwner = activity)
    val playlistViewModel: PlaylistViewModel = koinViewModel(viewModelStoreOwner = activity)

    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val playlistUiState by playlistViewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val screenWithBottomBar = listOf(
        Navigation.Home.destination,
        Navigation.Playlist.destination,
        Navigation.Board.destination,
        Navigation.Setting.destination
    )

    // App.kt 내부
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // 권한 거부 시 유저에게 알림창이 뜨지 않는다고 안내
            Toast.makeText(context, "알림 권한이 허용되지 않으면 백그라운드 재생 시 알림이 뜨지 않아요.", Toast.LENGTH_SHORT).show()
        }
    }

    var backPressedTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    BackHandler(enabled = currentRoute in screenWithBottomBar) {
        val currentTime = System.currentTimeMillis()

        // 2초(2000ms) 이내에 다시 눌렀는지 확인
        if (currentTime - backPressedTime < 2000) {
            // 1. 음악 정지 로직 호출 (ViewModel의 Intent 활용)
            playlistViewModel.onIntent(PlaylistIntent.ClosePlayer)

            // 2. 앱 종료
            activity.finish()
        } else {
            // 처음 눌렀을 때 토스트 메시지 출력
            backPressedTime = currentTime
            Toast.makeText(context, "뒤로가기를 한 번 더 누르면 종료돼요.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    // 3. 터치 시 포커스 해제 -> 키보드가 자동으로 내려감
                    focusManager.clearFocus()
                })
            }
            .background(MapleWhite)
    ) {
        Spacer(
            modifier = Modifier.fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(MapleOrange)
        )

        Scaffold(
            modifier = Modifier.weight(1f),
            // 시스템 바 인셋을 Scaffold가 자동으로 소비하지 않도록 0으로 설정
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                // 커스텀 Bottom Navigation Bar 적용
                AnimatedVisibility(
                    visible = currentRoute in screenWithBottomBar && playlistUiState.isPlayerMinimized,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomNavigationBar(
                        navController = navController,
                        isLoginSuccess = homeUiState.isLoginSuccess,
                        onFetchEvent = {
                            calendarViewModel.onIntent(CalendarIntent.InitCalendarInfo)
                        }
                    )
                }
            }
        ) { innerPadding ->
            val layoutDirection = LocalLayoutDirection.current
            val navHostPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection)
            )

            NavHost(
                modifier = Modifier.fillMaxSize()
                    .padding(navHostPadding)
                    .background(Color.Transparent),
                navController = navController,
                startDestination = "main_flow",
                snackbarHostState = snackbarHostState,
                homeViewModel = homeViewModel,
                settingViewModel = settingViewModel,
                calendarViewModel = calendarViewModel,
                mapleCharacterViewModel = mapleCharacterViewModel,
                bossViewModel = bossViewModel,
                playlistViewModel = playlistViewModel
            )

            // 2. 미니 플레이어 (곡이 선택되어 있고 최소화 상태일 때만)
            val selectedBgm = playlistUiState.selectedBgm
            if (selectedBgm != null && playlistUiState.isPlayerMinimized) {
                DraggableMiniPlayerOverlay(
                    bgm = selectedBgm,
                    isPlaying = playlistUiState.isPlaying,
                    onTogglePlay = { isPlaying ->
                        playlistViewModel.onIntent(PlaylistIntent.TogglePlayPause(isPlaying))
                    },
                    onClose = { playlistViewModel.onIntent(PlaylistIntent.ClosePlayer) },
                    onClick = {
                        playlistViewModel.onIntent(PlaylistIntent.MaximizePlayer)
                        // navController.navigate(Navigation.MapleBgmPlay.destination)
                    }
                )
            }

            // 3. 전체 화면 플레이어 (슬라이드 애니메이션)
            AnimatedVisibility(
                visible = !playlistUiState.isPlayerMinimized,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                MapleBgmPlayScreen(
                    viewModel = playlistViewModel,
                    onBack = {
                        // navController.popBackStack()
                        playlistViewModel.onIntent(PlaylistIntent.MinimizePlayer)
                    }
                )
            }
        }
    }
}