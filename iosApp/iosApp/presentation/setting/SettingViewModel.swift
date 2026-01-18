import Foundation
import shared

@MainActor
class SettingViewModel: ObservableObject {
    
    @Published var uiState = SettingUiState(
        isLoading: false,
        nexonApiKey: nil,
        fcmToken: nil,
        isGlobalAlarmEnabled: false,
        errorMessage: nil
    )
    
    private lazy var helper = KMPHelperKt.getKMPHelper()
    
    private lazy var getApiKeyUseCase = helper.getApiKeyUseCase
    private lazy var getSavedFcmTokenUseCase = helper.getSavedFcmTokenUseCase
    private lazy var getGlobalAlarmStatusUseCase = helper.getGlobalAlarmStatusUseCase
    private lazy var toggleGlobalAlarmStatusUseCase = helper.toggleGlobalAlarmStatusUseCase
    private lazy var unregisterTokenUseCase = helper.unregisterTokenUseCase
    private lazy var logoutUseCase = helper.logoutUseCase
    private lazy var reducer = helper.settingReducer
    
    init() {
        onIntent(intent: SettingIntent.FetchNexonOpenApiKey())
        onIntent(intent: SettingIntent.FetchFcmToken())
        onIntent(intent: SettingIntent.FetchGlobalAlarmStatus())
    }
    
    private func getNexonOpenApiKey() {
        Task {
            do {
                let flow = try await getApiKeyUseCase.invoke()
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<NSString>,
                           let key = success.data as String? {
                            self.onIntent(intent: SettingIntent.FetchNexonOpenApiKeySuccess(key: key))
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: SettingIntent.FetchNexonOpenApiKeyFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("API Key Load Error: \(error)")
            }
        }
    }
    
    private func getFcmToken() {
        Task {
            do {
                let flow = try await getSavedFcmTokenUseCase.invoke()
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<NSString>,
                           let token = success.data as String? {
                            self.onIntent(intent: SettingIntent.FetchFcmTokenSuccess(fcmToken: token))
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: SettingIntent.FetchFcmTokenFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Fcm Token Load Error: \(error)")
            }
        }
    }
    
    private func getGlobalAlarmStatus() {
        Task {
            do {
                let flow = try await getGlobalAlarmStatusUseCase.invoke()
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject> {
                            let isEnabled: Bool
                            if let boolNum = success.data as? NSNumber {
                                isEnabled = boolNum.boolValue
                                print("Fetched Alarm Status as NSNumber: \(isEnabled)") // 로그로 확인
                            } else {
                                isEnabled = success.data as? Bool ?? false
                                print("Fetched Alarm Status as Bool: \(isEnabled)") // 로그로 확인
                            }
                            self.onIntent(intent: SettingIntent.FetchGlobalAlarmStatusSuccess(isEnabled: isEnabled))
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: SettingIntent.FetchGlobalAlarmStatusFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Global Alarm Status Load Error: \(error)")
            }
        }
    }
    
    private func toggleGlobalAlarmStatus(apiKey: String) {
        Task {
            do {
                let flow = try await toggleGlobalAlarmStatusUseCase.invoke(apiKey: apiKey)
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject>,
                           let isEnabled = success.data as? Bool {
                            self.onIntent(intent: SettingIntent.ToggleGlobalAlarmStatusSuccess(isEnabled: isEnabled))
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: SettingIntent.ToggleGlobalAlarmStatusFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Toggle Global Alarm Status Error: \(error)")
            }
        }
    }
    
    private func unregisterFcmToken(apiKey: String, token: String) {
        Task {
            do {
                let flow = try await unregisterTokenUseCase.invoke(apiKey: apiKey, token: token)
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject> {
                            self.logout()
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: SettingIntent.LogoutFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Unregister Fcm Token Error: \(error)")
            }
        }
    }
    
    private func logout() {
        Task {
            do {
                let flow = try await logoutUseCase.invoke()
                
                try await flow.collect(collector: FlowCollectorWrapper<AnyObject> { state, completionHandler in
                    Task { @MainActor in
                        if let success = state as? ApiStateSuccess<AnyObject> {
                            self.onIntent(intent: SettingIntent.LogoutSuccess())
                        } else if let error = state as? ApiStateError {
                            self.onIntent(intent: SettingIntent.LogoutFailed(message: error.message))
                        }
                        completionHandler(nil)
                    }
                })
            } catch {
                print("Logout Error: \(error)")
            }
        }
    }
    
    func onIntent(intent: SettingIntent) {
        self.uiState = reducer.reduce(currentState: self.uiState, intent: intent)
        print("Setting Intent: \(intent), Loading: \(uiState.isLoading)")

        switch intent {
            case is SettingIntent.FetchNexonOpenApiKey:
                getNexonOpenApiKey()
            case is SettingIntent.FetchFcmToken:
                getFcmToken()
            case is SettingIntent.FetchGlobalAlarmStatus:
                getGlobalAlarmStatus()
            case is SettingIntent.ToggleGlobalAlarmStatus:
                toggleGlobalAlarmStatus(apiKey: uiState.nexonApiKey ?? "")
            case is SettingIntent.Logout :
                unregisterFcmToken(apiKey: uiState.nexonApiKey ?? "", token: uiState.fcmToken ?? "")
            default:
                break
        }
    }
}
