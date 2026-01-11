package com.sixclassguys.maplecalendar.navigation.navhost

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sixclassguys.maplecalendar.navigation.Navigation
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.ui.board.BoardScreen
import com.sixclassguys.maplecalendar.ui.calendar.MapleCalendarScreen
import com.sixclassguys.maplecalendar.ui.home.HomeScreen
import com.sixclassguys.maplecalendar.ui.login.LoginScreen
import com.sixclassguys.maplecalendar.ui.login.SelectRepresentativeCharacterScreen
import com.sixclassguys.maplecalendar.ui.playlist.PlaylistScreen
import com.sixclassguys.maplecalendar.ui.setting.SettingScreen
import com.sixclassguys.maplecalendar.ui.splash.SplashScreen
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String
) {
    val activity = LocalContext.current as ComponentActivity

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
                val homeViewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = activity)
                SplashScreen(
                    viewModel = homeViewModel,
                    onNavigateToHome = {
                        navController.navigate(Navigation.Home.destination) {
                            popUpTo(Navigation.Splash.destination) { inclusive = true }
                        }
                    }
                )
            }

            composable(Navigation.Home.destination) {
                val homeViewModel: HomeViewModel = koinViewModel(
                    viewModelStoreOwner = activity
                )

                Napier.d("Home 화면 진입 - VM ID: ${homeViewModel.hashCode()}")
                Napier.d("Home 화면 진입 - Handle ID: ${homeViewModel.savedStateHandle.hashCode()}")

                // 이제 이 homeViewModel은 "main_flow"의 핸들을 가지고 있음
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToLogin = {
                        navController.navigate("login_flow")
                    }
                )
            }
        }

        composable(Navigation.Playlist.destination) {
            PlaylistScreen()
        }

        composable(Navigation.Calendar.destination) {
            MapleCalendarScreen()
        }

        composable(Navigation.Board.destination) {
            BoardScreen()
        }

        composable(Navigation.Setting.destination) {
            SettingScreen()
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
                    onNavigateToCharacterSelection = { navController.navigate(Navigation.SelectRepresentativeCharacter.destination) },
                    onNavigateToHome = {
                        navController.navigate(Navigation.Home.destination) {
                            // 홈으로 갈 때 로그인 플로우 전체를 팝업(삭제)
                            popUpTo("login_flow") { inclusive = true }
                        }
                    }
                )
            }

            composable(Navigation.SelectRepresentativeCharacter.destination) { backStackEntry ->
                val loginParentEntry = remember(backStackEntry) { navController.getBackStackEntry("login_flow") }
                val loginViewModel: LoginViewModel = koinViewModel(viewModelStoreOwner = loginParentEntry)

                // Activity 스코프의 HomeViewModel을 가져옴
                val activity = LocalContext.current as ComponentActivity
                val homeViewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = activity)

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