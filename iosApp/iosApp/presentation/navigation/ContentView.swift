import SwiftUI
import shared

struct ContentView: View {
    
    @State private var selectedTab = 0
    @StateObject private var homeViewModel = HomeViewModel()
    @StateObject private var loginViewModel = LoginViewModel()
    @StateObject private var settingViewModel = SettingViewModel()
    @StateObject private var calendarViewModel = CalendarViewModel()
    
    // 💡 모든 경로를 하나의 path로 관리
    @State private var path = NavigationPath()
    @State private var showLogin = false

    var body: some View {
        
        NavigationStack(path: $path) {
            ZStack(alignment: .bottom) {
                Color.mapleWhite.ignoresSafeArea()

                VStack(spacing: 0) {
                    Group {
                        switch selectedTab {
                        case 0: HomeScreen(viewModel: homeViewModel)
                        case 3: SettingScreen(viewModel: settingViewModel, homeViewModel: homeViewModel,
                                              onNavigateToLogin: { showLogin = true })
                        default: HomeScreen(viewModel: homeViewModel)
                        }
                    }
                    Spacer(minLength: 75)
                }

                BottomTabBarView(selectedTab: $selectedTab, onCalendarClick: {
                    path.append("Calendar")
                })
            }
            .ignoresSafeArea(.container, edges: .bottom)
            // 💡 모든 목적지를 여기서 정의
            .navigationDestination(for: String.self) { value in
                switch value {
                    case "Calendar":
                        MapleCalendarScreen(
                            viewModel: calendarViewModel,
                            onNavigateToEventDetail: { eventId in
                                calendarViewModel.onIntent(intent: CalendarIntent.SelectEvent(eventId: eventId))
                                path.append("EventDetail")
                            }
                        )
                    case "EventDetail":
                        MapleEventDetailScreen(
                            viewModel: calendarViewModel,
                            onBack: { path.removeLast() }
                        )
                        .navigationBarHidden(true) // 필수
                        .ignoresSafeArea(edges: .top) // 필수
                        .toolbar(.hidden, for: .navigationBar) // 상단 바 완전 제거
                        .zIndex(10)
                    case "CharacterSelection":
                        SelectRepresentativeCharacterScreen(
                            viewModel: loginViewModel,
                            onNavigateToLogin: {
                                path = NavigationPath()
                                showLogin = false
                            }
                        )
                    default: EmptyView()
                }
            }
            // 로그인 화면 (기존 유지)
            .navigationDestination(isPresented: $showLogin) {
                LoginScreen(
                    viewModel: loginViewModel,
                    onNavigateToCharacterSelection: { path.append("CharacterSelection") },
                    onNavigateToHome: { showLogin = false }
                )
            }
        }
    }
}
