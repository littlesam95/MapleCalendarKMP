import SwiftUI
import shared
import Kingfisher // 이미지 로딩을 위해 Kingfisher 사용

struct SelectRepresentativeCharacterScreen: View {
    
    @ObservedObject var viewModel: LoginViewModel
    var onNavigateToLogin: () -> Void
    
    let columns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]
    
    var body: some View {
        
        VStack(spacing: 0) {
            if viewModel.availableWorlds.isEmpty {
                VStack(spacing: 16) {
                    Spacer()
                    Image(systemName: "person.fill.questionmark").font(.system(size: 50))
                        .foregroundColor(.gray)
                    Text("선택 가능한 캐릭터가 없습니다.\n월드와 캐릭터 존재 여부를 확인해주세요.").multilineTextAlignment(.center)
                        .foregroundColor(.gray)
                    Spacer()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                // 기존 ScrollView (월드 선택기 + 그리드)
                ScrollView {
                    // 1. 상단 헤더 & 단계 인디케이터
                    VStack(alignment: .leading, spacing: 24) {
                        // 뒤로가기 버튼 추가
                        Button(action: {
                            // Compose의 navController.navigateUp()과 동일한 역할
                            // 상위 뷰에서 처리하거나 Dismiss 환경변수 사용
                            onNavigateToLogin()
                        }) {
                            Image(systemName: "chevron.left").font(.system(size: 20, weight: .bold))
                                .foregroundColor(.black)
                        }
                        .padding(.bottom, 8)
                        
                        Text("계정 선택").font(.system(size: 32, weight: .heavy))
                            .padding(.top, 16)
                        
                        // 와이어프레임의 단계 표시
                        // CharacterStepIndicator()로 추후 대체
                        HStack(spacing: 0) {
                            CharacterSelectStepItem(icon: "checkmark.shield", title: "NEXON ID 인증", isActive: false)
                            
                            Image(systemName: "chevron.right").font(.system(size: 14, weight: .bold))
                                .foregroundColor(.black)
                                .padding(.horizontal, 20)
                            
                            CharacterSelectStepItem(icon: "person.badge.plus", title: "대표 캐릭터 선택", isActive: true)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        
                        Text("계정 내에서 대표캐릭터로 등록을 원하는\n캐릭터를 선택해주세요!").font(.system(size: 15))
                            .foregroundColor(.gray)
                            .lineSpacing(4)
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 20)
                    
                    // 2. 월드 선택기 (우측 정렬)
                    HStack {
                        Spacer()
                        WorldSelector(selectedWorld: viewModel.uiState.selectedWorld) {
                            viewModel.onIntent(intent: LoginIntent.ShowWorldSheet(isShow: true))
                        }
                        .padding(.trailing, 16)
                    }
                    
                    // 3. 캐릭터 그리드 리스트
                    let currentWorldCharacters = viewModel.uiState.characters[viewModel.uiState.selectedWorld] as? [AccountCharacter] ?? []
                        
                    LazyVGrid(columns: columns, spacing: 12) {
                        ForEach(currentWorldCharacters, id: \.ocid) { character in
                            CharacterSelectCard(
                                character: character,
                                characterImage: (viewModel.uiState.characterImages[character.ocid] as? String) ?? "",
                                isSelected: viewModel.uiState.selectedCharacter?.ocid == character.ocid
                            ) {
                                viewModel.onIntent(intent: LoginIntent.SelectCharacter(character: character))
                            }
                        }
                    }
                    .padding(16)
                }
                    
                // 4. 하단 고정 버튼
                RepresentativeConfirmButton(isSelected: viewModel.uiState.selectedCharacter != nil) {
                    viewModel.onIntent(intent: LoginIntent.SubmitRepresentativeCharacter())
                }
            }
        }
        .background(Color.white.ignoresSafeArea())
        .navigationBarHidden(true)
        .sheet(isPresented: Binding(
            get: { viewModel.uiState.isWorldSheetOpen },
            set: { viewModel.onIntent(intent: LoginIntent.ShowWorldSheet(isShow: $0)) }
        )) {
            WorldSelectBottomSheet(viewModel: viewModel).presentationDetents([.medium, .large])
        }
        .onChange(of: viewModel.uiState.isLoginSuccess) { oldValue, newValue in
            if newValue {
                // ViewModel에서 성공 신호를 보내면, 상위 뷰에서 전달받은 클로저를 실행
                onNavigateToLogin()
            }
        }
    }
}
