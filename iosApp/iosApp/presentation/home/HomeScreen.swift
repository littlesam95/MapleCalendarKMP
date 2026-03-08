import SwiftUI
import shared

struct HomeScreen: View {
    
    @StateObject var viewModel: HomeViewModel
    
    var body: some View {
        
        NavigationView {
            ScrollView {
                HStack {
                    Image("ic_logo") .resizable()
                        .renderingMode(.original)
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 28)
                    
                    Spacer()
                    
                    Button(action: { /* 알림 이동 */ }) {
                        Image(systemName: "bell.fill") .font(.system(size: 20))
                            .foregroundColor(.mapleOrange)
                            .padding(8) // 클릭 영역 확보
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 12)
                .padding(.bottom, 20)

                LazyVStack(alignment: .leading, spacing: 0) {
                    // 1. 캐릭터 요약창 영역
                    Group {
                        if let basic = viewModel.uiState.characterBasic {
                            CharacterBasicCard(basic: basic)
                        } else if viewModel.uiState.isLoading {
                            ProgressView().frame(maxWidth: .infinity).frame(height: 200)
                        } else {
                            EmptyCharacterBasicCard {
                                viewModel.onIntent(intent: HomeIntent.Login())
                            }
                        }
                        
                        Spacer().frame(height: 32)
                        
                        Text("오늘 진행하는 이벤트").font(.system(size: 24, weight: .bold))
                            .foregroundColor(.mapleBlack)
                            .padding(.bottom, 16)
                    }
                    .padding(.horizontal, 20) // 👈 공통 요소들에만 패딩 적용

                    // 2. 이벤트 리스트 영역
                    if viewModel.uiState.events.isEmpty && !viewModel.uiState.isLoading {
                        Text("진행 중인 이벤트가 없습니다.").foregroundColor(.mapleGray)
                            .padding(.horizontal, 20)
                    } else {
                        ForEach(viewModel.uiState.events, id: \.title) { event in
                            Link(destination: URL(string: event.url)!) {
                                TodayEventsCard(event: event).frame(maxWidth: .infinity)
                            }
                            .buttonStyle(PlainButtonStyle())
                            .padding(.horizontal, 20)
                        }
                    }
                    
                    Spacer().frame(height: 100)
                }
            }
            .navigationBarHidden(true) // 👈 기존 시스템 바를 완전히 숨김
            .background(Color.mapleWhite.ignoresSafeArea())
            .onAppear {
                viewModel.onIntent(intent: HomeIntent.LoadApiKey())
            }
        }
    }
}
