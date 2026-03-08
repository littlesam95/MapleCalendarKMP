import Foundation
import shared

@MainActor
class HomeViewModel: ObservableObject {
    
    @Published var uiState = HomeUiState(
        isLoading: false,
        isAutoLoginFinished: false,
        isLoginSuccess: false,
        member: nil,
        nexonApiKey: nil,
        characterBasic: nil,
        characterDojangRanking: nil,
        characterOverallRanking: nil,
        characterServerRanking: nil,
        characterUnion: nil,
        isGlobalAlarmEnabled: false,
        events: [],
        bossSchedules: [],
        isNavigateToLogin: false,
        errorMessage: nil
    )

    private lazy var helper = KMPHelperKt.getKMPHelper()
        
    private lazy var getApiKeyUseCase = helper.getApiKeyUseCase
    private lazy var getTodayEventsUseCase = helper.getTodayEventsUseCase
    private lazy var autoLoginUseCase = helper.autoLoginUseCase
    private lazy var getFcmTokenUseCase = helper.getFcmTokenUseCase
    private lazy var reducer = helper.homeReducer

    init() {
        onIntent(intent: HomeIntent.LoadApiKey())
    }

    private func getNexonOpenApiKey() {
        Task {
            do {
                let flow = try await getApiKeyUseCase.invoke()
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<NSString>,
                           let key = success.data as String? {
                            self.onIntent(intent: HomeIntent.LoadCharacterBasic(apiKey: key))
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: HomeIntent.LoadApiKeyFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("API Key Load Error: \(error)")
            }
        }
    }

    private func getCharacterBasic(apiKey: String) {
        Task {
            do {
                let fcmToken = (try? await getFcmTokenUseCase.invoke())?
                    .trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
                
                let flow = try await autoLoginUseCase.invoke(fcmToken: fcmToken)
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject> {
                            if let loginResult = success.data as? LoginResult {
                                let data = loginResult.member
                                // 1. 서버에서 내려준 성공 여부 확인 (data.characterBasic이 null인 경우 대응)
                                if let domainModel = data.characterBasic {
                                    self.onIntent(intent: HomeIntent.LoadCharacterBasicSuccess(
                                        characterBasic: domainModel,
                                        characterDojangRanking: data.characterDojang,
                                        characterOverallRanking: data.characterOverallRanking,
                                        characterServerRanking: data.characterServerRanking,
                                        characterUnion: data.characterUnionLevel,
                                        isGlobalAlarmEnabled: data.isGlobalAlarmEnabled
                                    ))
                                } else {
                                    // 2. 데이터는 왔지만 캐릭터 정보가 없는 경우 (실패 케이스)
                                    // 서버에서 온 메시지가 있다면 활용, 없으면 기본 메시지
                                    let errorMessage = "대표 캐릭터가 설정되지 않았습니다."
                                    self.onIntent(intent: HomeIntent.LoadCharacterBasicFailed(message: errorMessage))
                                }
                            }
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: HomeIntent.LoadCharacterBasicFailed(message: error.message))
                        } else if state is ApiStateEmpty {
                            self.onIntent(intent: HomeIntent.EmptyAccessToken())
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("❌ Critical Error: \(error)")
                self.onIntent(intent: HomeIntent.LoadCharacterBasicFailed(message: error.localizedDescription))
            }
        }
    }

    private func getTodayEvents() {
        Task {
            do {
                let components = Calendar.current.dateComponents([.year, .month, .day], from: Date())
                guard let year = components.year,
                      let month = components.month,
                      let day = components.day else {
                    self.onIntent(intent: HomeIntent.LoadEventsFailed(message: "오늘 날짜 정보를 불러오지 못했습니다."))
                    return
                }

                let flow = try await getTodayEventsUseCase.invoke(year: Int32(year), month: Int32(month), day: Int32(day))
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<NSArray>,
                           let events = success.data as? [MapleEvent] {
                            self.onIntent(intent: HomeIntent.LoadEventsSuccess(events: events))
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: HomeIntent.LoadEventsFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Today Events Load Error: \(error)")
                self.onIntent(intent: HomeIntent.LoadEventsFailed(message: error.localizedDescription))
            }
        }
    }
    
    func onIntent(intent: HomeIntent) {
        // UI 상태 업데이트는 반드시 메인 스레드에서 수행
        Task { @MainActor in
            let newState = helper.homeReducer.reduce(currentState: self.uiState, intent: intent)
            self.uiState = newState
            
            // 디버깅용 로그: 실제 데이터가 담겼는지 확인
            if intent is HomeIntent.LoadCharacterBasicSuccess {
                print("✅ UIState Updated: Name = \(self.uiState.characterBasic?.characterName ?? "None")")
                print("✅ UIState Updated: Loading = \(self.uiState.isLoading)")
            }
        }

        // Side Effect 처리는 기존 로직 유지 (비동기 함수 호출)
        switch intent {
        case is HomeIntent.LoadApiKey:
            getNexonOpenApiKey()
            
        case let i as HomeIntent.LoadCharacterBasic:
            getCharacterBasic(apiKey: i.apiKey)
            
        case is HomeIntent.LoadApiKeyFailed,
             is HomeIntent.LoadEmptyApiKey,
             is HomeIntent.EmptyAccessToken,
             is HomeIntent.LoadCharacterBasicSuccess,
             is HomeIntent.LoadCharacterBasicFailed:
            getTodayEvents()
            
        default:
            break
        }
    }
}
