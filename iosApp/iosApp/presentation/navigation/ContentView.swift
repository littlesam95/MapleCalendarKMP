import SwiftUI
import shared

struct ContentView: View {
    
    @State private var selectedTab = 0
    @StateObject private var homeViewModel = HomeViewModel()
    @StateObject private var loginViewModel = LoginViewModel()
    @StateObject private var settingViewModel = SettingViewModel()
    @StateObject private var calendarViewModel = CalendarViewModel()
    
    @State private var path = NavigationPath()
    @State private var showLogin = false
    @State private var showCalendar = false

    var body: some View {
        
        NavigationStack(path: $path) {
            ZStack(alignment: .bottom) {
                Color.mapleWhite.ignoresSafeArea()

                VStack(spacing: 0) {
                    Group {
                        switch selectedTab {
                        case 0: HomeScreen(viewModel: homeViewModel)
                        case 1: Text("플레이리스트 화면 준비 중").frame(maxWidth: .infinity, maxHeight: .infinity)
                        case 2: Text("모아보기 화면 준비 중").frame(maxWidth: .infinity, maxHeight: .infinity)
                        case 3: SettingScreen(viewModel: settingViewModel, homeViewModel: homeViewModel,
                            onNavigateToLogin: {
                                showLogin = true
                            }
                        )
                        default: HomeScreen(viewModel: homeViewModel)
                        }
                    }
                    Spacer(minLength: 75)
                }
                BottomTabBarView(selectedTab: $selectedTab, onCalendarClick: {
                    showCalendar = true
                })
            }
            .ignoresSafeArea(.container, edges: .bottom)
            .fullScreenCover(isPresented: $showCalendar) {
                NavigationStack {
                    MapleCalendarScreen(
                        viewModel: calendarViewModel,
                        onNavigateToEventDetail: { eventId in
                            // 상세 페이지 로직
                        }
                    )
                    .toolbar {
                        ToolbarItem(placement: .navigationBarLeading) {
                            Button("닫기") { showCalendar = false }
                        }
                    }
                }
            }
            
            .navigationDestination(isPresented: $showLogin) {
                LoginScreen(
                    viewModel: loginViewModel,
                    onNavigateToCharacterSelection: {
                        // 1. 단순히 다음 화면을 스택에 추가
                        path.append("CharacterSelection")
                    },
                    onNavigateToHome: {
                        // 2. 로그인창을 닫음
                        showLogin = false
                    }
                )
            }
            
            .navigationDestination(for: String.self) { value in
                if value == "CharacterSelection" {
                    SelectRepresentativeCharacterScreen(
                        viewModel: loginViewModel,
                        onNavigateToLogin: {
                            // 3. 성공 시 모든 스택을 비우고 홈으로 돌아감
                            path = NavigationPath()
                            showLogin = false
                        }
                    )
                }
            }
        }
        .onChange(of: homeViewModel.uiState.isNavigateToLogin) { oldValue, newValue in
            if newValue {
                showLogin = true
                homeViewModel.onIntent(intent: HomeIntent.NavigationHandled())
            }
        }
    }
}
