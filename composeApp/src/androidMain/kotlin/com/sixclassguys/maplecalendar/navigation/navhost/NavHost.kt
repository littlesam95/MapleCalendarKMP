package com.sixclassguys.maplecalendar.navigation.navhost

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sixclassguys.maplecalendar.navigation.Navigation
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarIntent
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.presentation.login.LoginIntent
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistIntent
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistViewModel
import com.sixclassguys.maplecalendar.presentation.setting.SettingViewModel
import com.sixclassguys.maplecalendar.ui.board.BoardScreen
import com.sixclassguys.maplecalendar.ui.boss.BossPartyCreateScreen
import com.sixclassguys.maplecalendar.ui.boss.BossPartyDetailScreen
import com.sixclassguys.maplecalendar.ui.boss.BossPartyListScreen
import com.sixclassguys.maplecalendar.ui.calendar.MapleCalendarScreen
import com.sixclassguys.maplecalendar.ui.calendar.MapleEventDetailScreen
import com.sixclassguys.maplecalendar.ui.character.MapleCharacterFetchScreen
import com.sixclassguys.maplecalendar.ui.character.MapleCharacterListScreen
import com.sixclassguys.maplecalendar.ui.character.MapleCharacterSubmitScreen
import com.sixclassguys.maplecalendar.ui.home.HomeScreen
import com.sixclassguys.maplecalendar.ui.login.LoginScreen
import com.sixclassguys.maplecalendar.ui.login.SelectRepresentativeCharacterScreen
import com.sixclassguys.maplecalendar.ui.playlist.PlaylistScreen
import com.sixclassguys.maplecalendar.ui.playlist.SearchMapleBgmScreen
import com.sixclassguys.maplecalendar.ui.setting.SettingScreen
import com.sixclassguys.maplecalendar.ui.splash.SplashScreen
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ContextCastToActivity")
@Composable
fun NavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    snackbarHostState: SnackbarHostState,
    homeViewModel: HomeViewModel,
    settingViewModel: SettingViewModel,
    calendarViewModel: CalendarViewModel,
    mapleCharacterViewModel: MapleCharacterViewModel,
    bossViewModel: BossViewModel,
    playlistViewModel: PlaylistViewModel
) {
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        navigation(
            startDestination = Navigation.Splash.destination,
            route = "main_flow"
        ) {
            composable(Navigation.Splash.destination) {
                SplashScreen(
                    viewModel = homeViewModel,
                    snackbarHostState = snackbarHostState,
                    onNavigateToHome = {
                        navController.navigate(Navigation.Home.destination) {
                            popUpTo(Navigation.Splash.destination) { inclusive = true }
                        }
                    }
                )
            }

            composable(Navigation.Home.destination) {
                HomeScreen(
                    viewModel = homeViewModel,
                    snackbarHostState = snackbarHostState,
                    onNavigateToLogin = {
                        navController.navigate("login_flow")
                    },
                    onNavigateToCharacterList = {
                        navController.navigate("character_flow")
                    },
                    onNavigateToEventDetail = { eventId ->
                        calendarViewModel.onIntent(CalendarIntent.FetchGlobalAlarmStatus)
                        calendarViewModel.onIntent(CalendarIntent.SelectEvent(eventId))
                        navController.navigate(Navigation.EventDetail.destination)
                    },
                    onNavigateToBossDetail = { bossPartyId ->
                        bossViewModel.onIntent(BossIntent.FetchGlobalAlarmStatus)
                        bossViewModel.onIntent(BossIntent.FetchBossPartyDetail(bossPartyId))
                        navController.navigate(Navigation.BossPartyDetail.destination)
                    }
                )
            }
        }

        composable(Navigation.Playlist.destination) {
            PlaylistScreen(
                viewModel = playlistViewModel,
                snackbarHostState = snackbarHostState,
                onNavigateToBgmPlay = {
                    playlistViewModel.onIntent(PlaylistIntent.MaximizePlayer)
                    // navController.navigate(Navigation.MapleBgmPlay.destination)
                },
                onNavigateToSearchBgm = {
                    navController.navigate(Navigation.SearchMapleBgm.destination)
                }
            )
        }

//        composable(Navigation.MapleBgmPlay.destination) {
//            MapleBgmPlayScreen(
//                viewModel = playlistViewModel,
//                snackbarHostState = snackbarHostState,
//                onBack = {
//                    if (navController.currentDestination?.route == Navigation.MapleBgmPlay.destination) {
//                        navController.popBackStack()
//                        playlistViewModel.onIntent(PlaylistIntent.MinimizePlayer)
//                    }
//                }
//            )
//        }

        composable(Navigation.SearchMapleBgm.destination) {
            SearchMapleBgmScreen(
                viewModel = playlistViewModel
            )
        }

        navigation(
            startDestination = Navigation.Calendar.destination,
            route = "calendar_flow"
        ) {
            composable(Navigation.Calendar.destination) {
                MapleCalendarScreen(
                    viewModel = calendarViewModel,
                    onNavigateToEventDetail = {
                        navController.navigate(Navigation.EventDetail.destination)
                    },
                    onNavigateToBossDetail = { bossPartyId ->
                        // BossViewModel에 해당 파티 정보를 불러오라고 시킨 뒤 이동
                        bossViewModel.onIntent(BossIntent.FetchBossPartyDetail(bossPartyId))
                        navController.navigate(Navigation.BossPartyDetail.destination)
                    }
                )
            }

            composable(Navigation.EventDetail.destination) {
                MapleEventDetailScreen(viewModel = calendarViewModel) {
                    calendarViewModel.onIntent(CalendarIntent.ClearSelectedEvent)
                    navController.navigateUp()
                }
            }
        }

        navigation(
            startDestination = Navigation.MapleCharacterList.destination,
            route = "character_flow"
        ) {
            composable(Navigation.MapleCharacterList.destination) {
                MapleCharacterListScreen(
                    viewModel = mapleCharacterViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToFetch = {
                        navController.navigate(Navigation.MapleCharacterFetch.destination)
                    }
                )
            }

            composable(Navigation.MapleCharacterFetch.destination) {
                MapleCharacterFetchScreen(
                    viewModel = mapleCharacterViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToSubmit = {
                        navController.navigate(Navigation.MapleCharacterSubmit.destination)
                    }
                )
            }

            composable(Navigation.MapleCharacterSubmit.destination) {
                MapleCharacterSubmitScreen(
                    viewModel = mapleCharacterViewModel,
                    onBack = { navController.popBackStack() },
                    onSubmitSuccess = {
                        navController.navigate(Navigation.MapleCharacterList.destination) { // 메인 화면 경로
                            popUpTo(Navigation.MapleCharacterList.destination) { // 메인 화면까지 백스택을 파버림
                                inclusive = true // 메인 화면 자체도 새로 그림 (데이터 갱신 반영)
                            }
                        }
                    }
                )
            }
        }

        navigation(
            startDestination = Navigation.BossPartyList.destination,
            route = "boss_flow"
        ) {
            composable(Navigation.BossPartyList.destination) {
                BossPartyListScreen(
                    viewModel = bossViewModel,
                    snackbarHostState = snackbarHostState,
                    onBack = { navController.popBackStack() },
                    onPartyClick = { bossPartyId ->
                        bossViewModel.onIntent(BossIntent.FetchBossPartyDetail(bossPartyId))
                        navController.navigate(Navigation.BossPartyDetail.destination)
                    },
                    onAddParty = {
                        navController.navigate(Navigation.BossPartyCreate.destination)
                    }
                )
            }
            composable(Navigation.BossPartyCreate.destination) {
                BossPartyCreateScreen(
                    viewModel = bossViewModel,
                    snackbarHostState = snackbarHostState,
                    onBack = { navController.popBackStack() },
                    onNavigateToDetail = { bossPartyId ->
                        bossViewModel.onIntent(BossIntent.FetchBossPartyDetail(bossPartyId))
                        navController.navigate(Navigation.BossPartyDetail.destination) {
                            popUpTo(Navigation.BossPartyCreate.destination) { inclusive = true }
                        }
                    }
                )
            }

            composable(Navigation.BossPartyDetail.destination) {
                BossPartyDetailScreen(
                    viewModel = bossViewModel,
                    onBack = {
                        bossViewModel.onIntent(BossIntent.DisconnectBossPartyChat)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Navigation.Board.destination) {
            BoardScreen()
        }

        composable(Navigation.Setting.destination) {
            SettingScreen(
                viewModel = settingViewModel,
                homeViewModel = homeViewModel,
                playlistViewModel = playlistViewModel,
                snackbarHostState = snackbarHostState
            ) {
                navController.navigate("login_flow")
            }
        }

        navigation(
            startDestination = Navigation.Login.destination,
            route = "login_flow" // 로그인 전체를 묶는 라우트
        ) {
            composable(Navigation.Login.destination) { backStackEntry ->
                // ViewModel을 login_flow 전체에 맞게 생성
                val loginParentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("login_flow")
                }
                val loginViewModel: LoginViewModel =
                    koinViewModel(viewModelStoreOwner = loginParentEntry)

                LoginScreen(
                    viewModel = loginViewModel,
                    onBackClick = { navController.popBackStack() },
                    onGoogleLoginClick = {
                        val activity = context.findActivity()
                        if (activity != null) {
                            loginViewModel.onIntent(LoginIntent.ClickGoogleLogin(activity))
                        } else {
                            // Activity를 찾지 못했을 때의 예외 처리 (로그 확인용)
                            println("Debug: Activity not found from context $context")
                        }
                    },
                    onAppleLoginClick = {
                        val activity = context.findActivity()
                        if (activity != null) {
                            loginViewModel.onIntent(LoginIntent.ClickAppleLoginInAndroid(activity))
                        } else {
                            // Activity를 찾지 못했을 때의 예외 처리 (로그 확인용)
                            println("Debug: Activity not found from context $context")
                        }
                    },
                    onNavigateToRegistration = {
                        // navController.navigate(Navigation.CharacterRegistration.destination)
                    },
                    onNavigateToHome = { member ->
                        // 홈 화면에 로그인 정보 전달
                        val memberJson = Json.encodeToString(member)
                        homeViewModel.savedStateHandle["login_member"] = memberJson
                        navController.navigate(Navigation.Home.destination) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            // 홈 화면이 이미 스택에 있다면 새로 만들지 않고 재사용(중복 방지)
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Navigation.SelectRepresentativeCharacter.destination) { backStackEntry ->
                val loginParentEntry = remember(backStackEntry) { navController.getBackStackEntry("login_flow") }
                val loginViewModel: LoginViewModel = koinViewModel(viewModelStoreOwner = loginParentEntry)

                SelectRepresentativeCharacterScreen(
                    viewModel = loginViewModel,
                    onNavigateToLogin = {
                        // 이 homeViewModel은 Home 화면의 ViewModel과 동일한 객체임
                        Napier.d("Login 성공 - 값을 넣는 VM ID: ${homeViewModel.hashCode()}")
                        homeViewModel.savedStateHandle["loginSuccess"] = true

                        navController.navigate(Navigation.Home.destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            // 홈 화면이 이미 스택에 있다면 새로 만들지 않고 재사용(중복 방지)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}