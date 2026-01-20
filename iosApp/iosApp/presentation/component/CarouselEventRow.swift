import SwiftUI
import shared

struct CarouselEventRow: View {
    
    let events: [MapleEvent]
    let onNavigate: (Int64) -> Void
    
    // 무한 루프를 위해 충분히 큰 반복 횟수
    private let repeatCount = 100
    // 시작 위치를 전체 데이터의 정중앙 근처로 설정
    private var startIndex: Int { (events.count * repeatCount) / 2 }
    
    var body: some View {
        
        VStack(alignment: .leading) {
            if events.isEmpty {
                Text("진행 중인 이벤트가 없습니다.").padding(.horizontal, 16)
                    .foregroundColor(.gray)
            } else {
                ScrollViewReader { proxy in
                    ScrollView(.horizontal, showsIndicators: false) {
                        LazyHStack(spacing: 12) {
                            ForEach(0..<(events.count * repeatCount), id: \.self) { index in
                                let event = events[index % events.count]
                                
                                CalendarEventCard(event: event)
                                    .id(index) // ScrollViewReader가 인식할 ID 설정
                                    .onTapGesture { onNavigate(event.id) }
                            }
                        }
                        .padding(.horizontal, 16)
                        .scrollTargetLayout()
                    }
                    .scrollTargetBehavior(.viewAligned)
                    .onAppear {
                        // 💡 화면이 나타날 때 중앙 인덱스로 즉시 이동
                        proxy.scrollTo(startIndex, anchor: .leading)
                    }
                }
            }
        }
    }
}

struct CalendarEventCard: View {
    
    let event: MapleEvent
    
    var body: some View {
        
        VStack(alignment: .leading, spacing: 0) { // 내부 간격을 0으로 하고 패딩으로 조절
            // 1. 이미지 영역
            AsyncImage(url: URL(string: event.thumbnailUrl ?? "")) { phase in
                if let image = phase.image {
                    image.resizable()
                        .aspectRatio(contentMode: .fill)
                } else if phase.error != nil {
                    Color.gray.opacity(0.1) // 에러 시 배경
                } else {
                    Rectangle().fill(Color.gray.opacity(0.1))
                        .overlay(ProgressView())
                }
            }
            .frame(width: 260, height: 140)
            .clipped()
            // 이미지의 위쪽 모서리만 둥글게 하고 싶다면 카드 전체 cornerRadius 부여

            // 2. 텍스트 영역 (흰색 배경 섹션)
            VStack(alignment: .leading, spacing: 6) {
                Text(event.title)
                    .font(.system(size: 15, weight: .bold))
                    .lineLimit(1)
                    .foregroundColor(.black)
                
                HStack(spacing: 4) {
                    Image(systemName: "calendar") // 안드로이드 느낌을 위한 아이콘 추가
                        .font(.system(size: 10))
                    Text("\(event.startDate) ~ \(event.endDate)")
                        .font(.system(size: 11))
                }
                .foregroundColor(.gray)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 12)
            .frame(width: 260, alignment: .leading)
            .background(Color.white) // 텍스트 섹션 배경색
        }
        .background(Color.white)
        .cornerRadius(16) // 전체 카드의 둥근 모서리
        // 💡 그림자 효과를 주어 안드로이드처럼 입체감을 부여
        .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 3)
        .padding(.vertical, 10) // 그림자가 잘리지 않도록 상하 여백 추가
    }
}
