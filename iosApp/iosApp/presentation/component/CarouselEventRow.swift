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
        
        VStack(alignment: .leading, spacing: 8) {
            // 이미지 영역
            AsyncImage(url: URL(string: event.thumbnailUrl ?? "")) { image in
                image.resizable()
                    .aspectRatio(contentMode: .fill) // 와이어프레임처럼 꽉 차게
            } placeholder: {
                Rectangle().fill(Color.gray.opacity(0.2))
                    .overlay(ProgressView())
            }
            .frame(width: 260, height: 140) // 💡 너비를 적절히 조절하여 다음 카드가 보이게 함
            .clipped() // 프레임 밖으로 나가는 이미지 절단
            .cornerRadius(12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(event.title).font(.system(size: 16, weight: .bold))
                    .lineLimit(1)
                    .foregroundColor(.black)
                
                Text("\(event.startDate) ~ \(event.endDate)").font(.system(size: 12))
                    .foregroundColor(.gray)
            }
        }
        .frame(width: 260) // 전체 카드 너비 고정
    }
}
