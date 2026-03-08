import SwiftUI
import shared

struct LoginScreen2: View {
    
    @ObservedObject var viewModel: LoginViewModel
    var onNavigateToCharacterSelection: () -> Void
        var onNavigateToHome: () -> Void
    
    @State private var showAlert = false
    @State private var alertMessage = ""

    var body: some View {
        
        NavigationStack {
            let normalizedApiKey = viewModel.uiState.nexonApiKey
                .trimmingCharacters(in: .whitespacesAndNewlines)

            VStack(alignment: .leading, spacing: 0) {
                // 1. 상단 타이틀
                Text("로그인").font(.system(size: 32, weight: .bold))
                    .foregroundColor(.mapleBlack)
                    .padding(.top, 8)
                
                Spacer().frame(height: 64)
                
                // 2. 설명 텍스트
                VStack(alignment: .leading, spacing: 16) {
                    Text("대부분의 기능은\n로그인을 해야 이용하실 수 있습니다.").font(.system(size: 16))
                        .foregroundColor(.mapleGray)
                        .lineSpacing(8)
                    
                    Text("NEXON Open API 사이트에서 넥슨 아이디로 로그인하여\nAPI Key를 확인하세요!").font(.system(size: 16))
                        .foregroundColor(.mapleGray)
                        .lineSpacing(4)
                }
                
                Spacer().frame(height: 40)
                
                // 3. API Key 입력 필드 (OutlinedTextField 대응)
                ZStack(alignment: .leading) {
                    if viewModel.uiState.nexonApiKey.isEmpty {
                        Text("NEXON Open API Key를 입력하세요.").foregroundColor(.mapleGray)
                            .padding(.horizontal, 16)
                    }
                    
                    TextField("", text: Binding(
                        get: { viewModel.uiState.nexonApiKey },
                        set: { viewModel.onIntent(intent: LoginIntent.UpdateApiKey(apiKey: $0)) }
                    )).padding(.horizontal, 16)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .submitLabel(.done)
                    .onSubmit {
                        if !viewModel.uiState.isLoading && !normalizedApiKey.isEmpty {
                            viewModel.onIntent(intent: LoginIntent.ClickLogin())
                        }
                    }
                }
                .frame(height: 60)
                .background(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(viewModel.uiState.nexonApiKey.isEmpty ? Color.mapleGray : Color.mapleBlack, lineWidth: 1)
                )
                
                // 에러 메시지 표시
                if let errorMessage = viewModel.uiState.errorMessage {
                    Text(errorMessage).font(.system(size: 12))
                        .foregroundColor(.mapleOrange)
                        .padding(.top, 8)
                        .padding(.leading, 4)
                }
                
                Spacer().frame(height: 24)
                
                // 4. 로그인 버튼
                Button(action: {
                    viewModel.onIntent(intent: LoginIntent.ClickLogin())
                }) {
                    HStack {
                        if viewModel.uiState.isLoading {
                            ProgressView().progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Open API Key로 로그인").font(.system(size: 24, weight: .bold))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 64)
                    .background(viewModel.uiState.isLoading || normalizedApiKey.isEmpty ? Color.mapleGray : Color.mapleBlack)
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }
                .disabled(viewModel.uiState.isLoading || normalizedApiKey.isEmpty)
                
                Spacer()
            }
        }
        .onAppear {
            viewModel.onIntent(intent: LoginIntent.NavigationConsumed())
            viewModel.initState()
        }
        .onChange(of: viewModel.uiState.isLoginSuccess) { oldValue, newValue in
            if newValue {
                onNavigateToHome()
            }
        }
        .onChange(of: viewModel.uiState.navigateToSelection) { oldValue, newValue in
            if newValue {
                onNavigateToCharacterSelection()
                viewModel.onIntent(intent: LoginIntent.NavigationConsumed())
            }
        }
        .padding(.horizontal, 16)
        .background(Color.mapleWhite.ignoresSafeArea())
        
        // 로그인 성공 시 홈으로 이동
        .onChange(of: viewModel.uiState.isLoginSuccess) { oldValue, newValue in
            if newValue {
                onNavigateToHome()
            }
        }
        
        // 캐릭터 선택 화면으로 이동
        .onChange(of: viewModel.uiState.navigateToSelection) { oldValue, newValue in
            if newValue {
                onNavigateToCharacterSelection()
                // 이동 후 신호 소모 (Kotlin의 NavigationConsumed 인텐트)
                viewModel.onIntent(intent: LoginIntent.NavigationConsumed())
            }
        }
    }
}
