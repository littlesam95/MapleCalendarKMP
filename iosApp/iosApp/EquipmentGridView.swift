import SwiftUI

// 1. 모델
struct GameEvent: Identifiable {
    let id = UUID()
    let title: String
    let dateRange: String
    let imageURL: String
}

// 2. 개별 카드 뷰
struct EventCardView: View {
    let event: GameEvent
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Image("event") // 데이터 바인딩
                .resizable()
                .aspectRatio(16/9, contentMode: .fill)
                .frame(maxWidth: .infinity)
                .frame(height: 144) // 높이를 명시적으로 지정하면 더 안정적입니다
                .clipped()
                .cornerRadius(15)
            
            Text(event.title)
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(.black)
            
            Text(event.dateRange)
                .font(.system(size: 14))
                .foregroundColor(.gray.opacity(0.8))
        }
        .padding(.vertical, 8)
    }
}

// 3. 리스트 뷰
struct EventListView: View {
    let eventList: [GameEvent] = [
        GameEvent(title: "스페셜 선데이 메이플", dateRange: "2026.01.11 ~ 2026.01.11", imageURL: "https://lwi.nexon.com/maplestory/2026/0109_board/761538DC629D31D7.png"),
        GameEvent(title: "겨울 업데이트 기념 이벤트", dateRange: "2025.12.18 ~ 2026.02.12", imageURL: "https://lwi.nexon.com/maplestory/2026/0109_board/761538DC629D31D7.png"),
        GameEvent(title: "테라 버닝 프로젝트", dateRange: "2025.12.18 ~ 2026.01.25", imageURL: "https://lwi.nexon.com/maplestory/2026/0109_board/761538DC629D31D7.png")
    ]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("오늘 진행하는 이벤트")
                .font(.system(size: 20, weight: .bold))
            
            LazyVStack(spacing: 16) {
                ForEach(eventList) { event in
                    EventCardView(event: event)
                }
            }
        }
    }
}
