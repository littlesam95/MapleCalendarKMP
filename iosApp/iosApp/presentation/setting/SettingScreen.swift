import SwiftUI
import shared

struct SettingScreen: View {
    
    @StateObject var viewModel: SettingViewModel
    @ObservedObject var homeViewModel: HomeViewModel
    var onNavigateToLogin: () -> Void
    
    var body: some View {
        
        VStack(alignment: .leading, spacing: 0) {
            // 1. 타이틀 영역
            Text("환경설정").font(.system(size: 24, weight: .bold))
                .padding(.top, 24)
                .padding(.bottom, 40)
            
            Spacer()
            
            // 2. 로그인 상태에 따른 UI 분기
            // uiState.nexonApiKey의 유무로 로그인 여부를 판단
            if viewModel.uiState.nexonApiKey == nil {
                // [와이어프레임 1] 로그아웃 상태
                MapleButton(
                    text: "로그인",
                    backgroundColor: Color(hex: "F29F38"),
                    action: onNavigateToLogin
                )
            } else {
                VStack(spacing: 40) {
                    // 알림 설정 섹션
                    HStack {
                        Text("이벤트 알림 수신").font(.system(size: 18, weight: .medium))
                            .foregroundColor(.mapleBlack)
                        
                        Spacer()
                        
                        if viewModel.uiState.isLoading {
                            ProgressView().tint(Color(hex: "F29F38"))
                        } else {
                            Toggle("", isOn: Binding(
                                get: { viewModel.uiState.isGlobalAlarmEnabled },
                                set: { _ in
                                    // 토글 시 Intent를 ViewModel로 전달
                                    viewModel.onIntent(intent: SettingIntent.ToggleGlobalAlarmStatus())
                                }
                            ))
                            .toggleStyle(SwitchToggleStyle(tint: Color(hex: "F29F38")))
                            .labelsHidden()
                        }
                    }
                    .padding(.vertical, 8)
                    
                    // 로그아웃 버튼
                    MapleButton(
                        text: "로그아웃",
                        backgroundColor: Color(hex: "FF7E7E"),
                        action: {
                            // 로그아웃 절차 시작
                            viewModel.onIntent(intent: SettingIntent.Logout())
                            // 홈 화면의 정보도 초기화하기 위해 homeViewModel에도 알림
                            homeViewModel.onIntent(intent: HomeIntent.Logout())
                        }
                    )
                }
            }
            
            Spacer().frame(minHeight: 100).layoutPriority(-1)
        }
        .onAppear {
            viewModel.onIntent(intent: SettingIntent.FetchNexonOpenApiKey())
            viewModel.onIntent(intent: SettingIntent.FetchFcmToken())
            viewModel.onIntent(intent: SettingIntent.FetchGlobalAlarmStatus())
        }
        .padding(.horizontal, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(Color.mapleWhite.ignoresSafeArea())
    }
}

// 🎨 공통 디자인이 적용된 메이플 버튼
struct MapleButton: View {
    let text: String
    let backgroundColor: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(text).font(.system(size: 18, weight: .bold))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(backgroundColor)
                .cornerRadius(16)
        }
        .buttonStyle(PlainButtonStyle()) // 버튼 클릭 시 기본 애니메이션 방지
    }
}
