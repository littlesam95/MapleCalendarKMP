import Foundation
import shared

@MainActor
class HomeViewModel: ObservableObject {
    
    @Published var uiState = HomeUiState(
        isLoading: false,
        isAutoLoginFinished: false,
        nexonApiKey: nil,
        characterBasic: nil,
        isGlobalAlarmEnabled: false,
        events: [],
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
                let fcmToken = "SIMULATOR_DUMMY_TOKEN" // iOS 시뮬레이터에서는 FCM 토큰 추출 불가
                
                let flow = try await autoLoginUseCase.invoke(apiKey: apiKey, fcmToken: fcmToken)
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject> {
                            // 1. rawData를 Member 타입으로 캐스팅 (로그에서 확인된 타입)
                            if let data = success.data as? shared.Member {
                                // 1. 서버에서 내려준 성공 여부 확인 (data.characterBasic이 null인 경우 대응)
                                if let domainModel = data.characterBasic {
                                    self.onIntent(intent: HomeIntent.LoadCharacterBasicSuccess(
                                        characterBasic: domainModel,
                                        isGlobalAlarmEnabled: data.isGlobalAlarmEnabled
                                    ))
                                } else {
                                    // 2. 데이터는 왔지만 캐릭터 정보가 없는 경우 (실패 케이스)
                                    // 서버에서 온 메시지가 있다면 활용, 없으면 기본 메시지
                                    let errorMessage = "대표 캐릭터가 설정되지 않았습니다."
                                    self.onIntent(intent: HomeIntent.LoadCharacterBasicFailed(message: errorMessage))
                                }
                            }
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("❌ Critical Error: \(error)")
            }
        }
    }

    private func getTodayEvents() {
        Task {
            do {
                let apiKey = uiState.nexonApiKey ?? ""
                let flow = try await getTodayEventsUseCase.invoke(year: 2026, month: 1, day: 15, apiKey: apiKey)
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<NSArray>,
                           let events = success.data as? [MapleEvent] {
                            self.onIntent(intent: HomeIntent.LoadEventsSuccess(events: events))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Today Events Load Error: \(error)")
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
             is HomeIntent.LoadCharacterBasicSuccess,
             is HomeIntent.LoadCharacterBasicFailed:
            getTodayEvents()
            
        default:
            break
        }
    }
}
