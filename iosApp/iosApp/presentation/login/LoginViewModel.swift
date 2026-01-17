import Foundation
import shared

@MainActor
class LoginViewModel: ObservableObject {
    
    @Published var uiState = LoginUiState(
        isLoading: false,
        nexonApiKey: "",
        isLoginSuccess: false,
        characters: [:],
        characterImages: [:],
        isWorldSheetOpen: false,
        selectedWorld: "",
        selectedCharacter: nil,
        navigateToSelection: false,
        errorMessage: nil
    )

    private lazy var helper = KMPHelperKt.getKMPHelper()
    
    private lazy var doLoginWithApiKeyUseCase = helper.doLoginWithApiKeyUseCase
    private lazy var submitRepresentativeCharacterUseCase = helper.submitRepresentativeCharacterUseCase
    private lazy var setOpenApiKeyUseCase = helper.setOpenApiKeyUseCase
    private lazy var getCharacterBasicUseCase = helper.getCharacterBasicUseCase
    private lazy var reducer = helper.loginReducer
    
    private let worldOrder: [String: Int] = [
        "스카니아": 0, "베라": 1, "루나": 2, "제니스": 3, "크로아": 4, "유니온": 5,
        "엘리시움": 6, "이노시스": 7, "레드": 8, "오로라": 9, "아케인": 10, "노바": 11,
        "챌린저스1": 12, "챌린저스2": 13, "챌린저스3": 14, "챌린저스4": 15
    ]

    // 2. 뷰에서 사용할 필터링된 월드 목록 (Computed Property)
    var availableWorlds: [String] {
        let keys = uiState.characters.keys.map { $0 as String }
        
        return keys
            .filter { worldOrder.keys.contains($0) } // 리부트 등 목록에 없는 월드 필터링
            .sorted { (lhs, rhs) -> Bool in
                let left = worldOrder[lhs] ?? 99
                let right = worldOrder[rhs] ?? 99
                return left < right
            }
    }

    init() {
        onIntent(intent: LoginIntent.NavigationConsumed()) // 초기화
    }

    // MARK: - 로직 처리 함수

    private func loginWithApiKey() {
        print("로그인 시도 중: \(uiState.nexonApiKey)")
        Task {
            do {
                let flow = try await doLoginWithApiKeyUseCase.invoke(apiKey: uiState.nexonApiKey)
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    // UI 업데이트는 메인 액터에서 수행
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject>, let data = success.data {
                            
                            if let domainResult = data as? LoginResult {
                                let ocid = domainResult.representativeOcid
                                let validWorlds = [
                                    "스카니아", "베라", "루나", "제니스", "크로아", "유니온",
                                    "엘리시움", "이노시스", "레드", "오로라", "아케인", "노바",
                                    "에오스", "핼리오스", "챌린저스1", "챌린저스2", "챌린저스3", "챌린저스4"
                                ]
                                    
                                // 캐릭터 데이터 필터링 (유효한 월드만 남기기)
                                let rawCharacters = domainResult.characters // [String: [AccountCharacter]]
                                let filteredCharacters = rawCharacters.filter { (worldName, characters) in
                                    // 월드 이름이 리스트에 있고, 해당 월드에 캐릭터가 존재하는 경우만 유지
                                    validWorlds.contains(worldName) && !characters.isEmpty
                                }
                                
                                if ocid == nil {
                                    self.onIntent(intent: LoginIntent.SelectRepresentativeCharacter(characters: filteredCharacters))
                                } else {
                                    self.onIntent(intent: LoginIntent.SetOpenApiKey())
                                }
                            }
                            else if let response = data as? shared.LoginResponse {
                                let domainResult = response.toDomain()
                                if domainResult.representativeOcid == nil {
                                    self.onIntent(intent: LoginIntent.SelectRepresentativeCharacter(characters: domainResult.characters))
                                } else {
                                    self.onIntent(intent: LoginIntent.SetOpenApiKey())
                                }
                            }
                        } else if let error = state as? ApiStateError {
                            print("로그인 실패: \(error.message)")
                            self.onIntent(intent: LoginIntent.LoginFailed(message: error.message))
                        }
                        
                        // 수집 완료 알림
                        completionHandler(nil)
                    }
                })
            } catch {
                print("네트워크 오류 발생: \(error.localizedDescription)")
                self.onIntent(intent: LoginIntent.LoginFailed(message: error.localizedDescription))
            }
        }
    }

    private func fetchAllCharacterImages() {
        Task {
            // 1. 데이터 평탄화 (모든 월드의 캐릭터 합치기)
            let allCharacters = uiState.characters.values.flatMap { $0 as? [AccountCharacter] ?? [] }
            if allCharacters.isEmpty { return }

            // 로드되지 않은 이미지만 필터링
            let toFetch = allCharacters.filter { uiState.characterImages[$0.ocid] == nil }

            // 2. 5개씩 청크 단위 처리 (Rate Limit 준수)
            for chunk in toFetch.chunked(into: 5) {
                let batchResults = await withTaskGroup(of: (String, String?).self) { group in
                    for character in chunk {
                        group.addTask {
                            do {
                                // Flow의 첫 번째 Success/Error 결과만 취득
                                let flow = try await self.getCharacterBasicUseCase.invoke(ocid: character.ocid)
                                return try await withCheckedThrowingContinuation { continuation in
                                    Task {
                                        try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                                            if let success = state as? ApiStateSuccess<CharacterBasicResponse> {
                                                continuation.resume(returning: (character.ocid, success.data?.characterImage))
                                            } else if state is ApiStateError {
                                                continuation.resume(returning: (character.ocid, nil))
                                            }
                                            completionHandler(nil)
                                        })
                                    }
                                }
                            } catch {
                                return (character.ocid, nil)
                            }
                        }
                    }

                    var results = [(String, String?)]()
                    for await result in group {
                        results.append(result)
                    }
                    return results
                }

                // 3. 이미지 업데이트 (Reducer 대신 직접 반영 또는 Batch Intent 설계 가능)
                // 여기서는 UI의 즉각적인 반영을 위해 직접 업데이트 형식을 취함
                var updatedImages = uiState.characterImages
                for (ocid, img) in batchResults {
                    updatedImages[ocid] = img
                }
                
                // 새로운 State 객체 생성하여 Publish
                updateImageState(newImages: updatedImages as! [String : String?])

                // 4. 초당 호출 제한 방지 (1초 대기)
                try? await Task.sleep(nanoseconds: 1_000_000_000)
            }
        }
    }

    private func submitRepresentativeCharacter() {
        Task {
            do {
                let ocid = uiState.selectedCharacter?.ocid ?? ""
                let flow = try await submitRepresentativeCharacterUseCase.invoke(apiKey: uiState.nexonApiKey, ocid: ocid)
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if state is ApiStateSuccess<NSObject> {
                            self.onIntent(intent: LoginIntent.SetOpenApiKey())
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: LoginIntent.SubmitRepresentativeCharacterFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                self.onIntent(intent: LoginIntent.SubmitRepresentativeCharacterFailed(message: error.localizedDescription))
            }
        }
    }

    private func saveApiKey() {
        Task {
            do {
                let flow = try await setOpenApiKeyUseCase.invoke(apiKey: uiState.nexonApiKey)
                
                for try await state in flow {
                    if state is ApiStateSuccess<AnyObject> {
                        self.onIntent(intent: LoginIntent.LoginSuccess())
                    } else if let error = state as? ApiStateError {
                        self.onIntent(intent: LoginIntent.SetOpenApiKeyFailed(message: error.message))
                    }
                }
            } catch {
                self.onIntent(intent: LoginIntent.SetOpenApiKeyFailed(message: error.localizedDescription))
            }
        }
    }
    
    private func updateImageState(newImages: [String: String?]) {
        self.uiState = LoginUiState(
            isLoading: uiState.isLoading,
            nexonApiKey: uiState.nexonApiKey,
            isLoginSuccess: uiState.isLoginSuccess,
            characters: uiState.characters,
            characterImages: newImages,
            isWorldSheetOpen: uiState.isWorldSheetOpen,
            selectedWorld: uiState.selectedWorld,
            selectedCharacter: uiState.selectedCharacter,
            navigateToSelection: uiState.navigateToSelection,
            errorMessage: uiState.errorMessage
        )
    }
    
    func initState() {
        self.uiState = LoginUiState(
            isLoading: false,
            nexonApiKey: "",
            isLoginSuccess: false,
            characters: [:],
            characterImages: [:],
            isWorldSheetOpen: false,
            selectedWorld: "",
            selectedCharacter: nil,
            navigateToSelection: false,
            errorMessage: nil
        )
    }

    func onIntent(intent: LoginIntent) {
        // 1. Reducer를 통한 상태 업데이트
        self.uiState = reducer.reduce(currentState: self.uiState, intent: intent)
        print("Login Intent: \(intent), Loading: \(uiState.isLoading)")

        // 2. Side Effect 처리
        switch intent {
            case is LoginIntent.ClickLogin:
                loginWithApiKey()
            case is LoginIntent.SelectRepresentativeCharacter:
                fetchAllCharacterImages()
            case is LoginIntent.SubmitRepresentativeCharacter:
                submitRepresentativeCharacter()
            case is LoginIntent.SetOpenApiKey:
                saveApiKey()
            default:
                break
        }
    }
}

// Helper: Array Chunking
extension Array {
    
    func chunked(into size: Int) -> [[Element]] {
        return stride(from: 0, to: count, by: size).map {
            Array(self[$0 ..< Swift.min($0 + size, count)])
        }
    }
}
