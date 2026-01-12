import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        // Kotlin에서 만든 initKoin 함수를 호출하여 의존성 그래프를 생성합니다.
        shared.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            MapleCalendarView()
        }
    }
}
