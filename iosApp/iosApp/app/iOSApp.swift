import SwiftUI
import shared
import Firebase

@main
struct iOSApp: App {
    
    // @UIApplicationDelegateAdaptor:
    // SwiftUI 앱 생명주기(App Lifecycle) 내에서 UIKit의 AppDelegate 기능을 사용할 수 있게 해주는 속성 래퍼(Property Wrapper)입니다.
    // 파이어베이스(Firebase) 초기화, 푸시 알림 처리(FCM) 등 UIKit 전용 메서드가 필요할 때 반드시 사용해야 합니다.
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    // 1. 선언만 하고 초기화는 init에서 수행
    @StateObject private var homeViewModel: HomeViewModel

    init() {
        
        
        
        // 2. Koin 초기화를 최우선으로 실행
        // KoinModuleKt.doInitKoin(additionalModules: [], appDeclaration: { _ in })
        KoinIosKt.doInitKoinIos()
        
        
        
        // 3. 초기화가 끝난 후 ViewModel 인스턴스 생성
        _homeViewModel = StateObject(wrappedValue: HomeViewModel())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
