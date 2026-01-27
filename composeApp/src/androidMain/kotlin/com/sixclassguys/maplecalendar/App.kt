package com.sixclassguys.maplecalendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
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
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarIntent
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.ui.component.BottomNavigationBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity")
@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    val activity = LocalContext.current as ComponentActivity
    val homeViewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = activity)
    val calendarViewModel: CalendarViewModel = koinViewModel(viewModelStoreOwner = activity)
    val mapleCharacterViewModel: MapleCharacterViewModel = koinViewModel(viewModelStoreOwner = activity)
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

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
                if (currentRoute in screenWithBottomBar) {
                    BottomNavigationBar(
                        navController = navController,
                        onCalendarClicked = {
                            if (homeUiState.characterBasic == null) {
                                navController.navigate("login_flow")
                            } else {
                                calendarViewModel.onIntent(CalendarIntent.FetchNexonOpenApiKey)
                                calendarViewModel.onIntent(CalendarIntent.FetchGlobalAlarmStatus)
                                navController.navigate("calendar_flow")
                            }
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
                calendarViewModel = calendarViewModel,
                mapleCharacterViewModel = mapleCharacterViewModel
            )
        }
    }
}