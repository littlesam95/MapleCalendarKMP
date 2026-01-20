import SwiftUI
import shared

struct EventCollapsingHeader: View {
    
    let event: MapleEvent?
    let percentage: CGFloat // 0.0 ~ 1.0
    let onBack: () -> Void
    
    private let expandedHeight: CGFloat = 250
    private let collapsedHeight: CGFloat = 60
    private let safeAreaTop: CGFloat = 54
    
    var body: some View {
        
        ZStack(alignment: .top) {
            LinearGradient(
                stops: [
                    .init(color: .mapleWhite, location: 0),
                    .init(color: .mapleWhite, location: 0.65),
                    
                    // 2. 마지막 구간(에서만 색을 변화시켜 그림자 생성
                    .init(color: percentage > 0.8 ? .mapleGray : .mapleWhite, location: 1.0)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            // 이미지 영역 (상단 노치 아래 고정)
            AsyncImage(url: URL(string: event?.thumbnailUrl ?? "")) { phase in
                if let image = phase.image {
                    image.resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: UIScreen.main.bounds.width, height: expandedHeight)
                        .clipped()
                }
            }
            .opacity(Double(1 - (percentage * 2))) // 중간쯤 왔을 때 완전히 사라지게
            
            HStack {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .foregroundColor(percentage > 0.6 ? .black : .white)
                        .padding(10)
                        .background(Circle().fill(Color.black.opacity(percentage > 0.6 ? 0 : 0.3)))
                }
                Spacer()
                Button(action: {}) {
                    Image(systemName: "square.and.arrow.up")
                        .foregroundColor(percentage > 0.6 ? .black : .white)
                        .padding(10)
                        .background(Circle().fill(Color.black.opacity(percentage > 0.6 ? 0 : 0.3)))
                }
            }
            .padding(.horizontal, 16)
            .frame(height: collapsedHeight)
            .opacity(Double(1 - (percentage * 2))) // 중간쯤 왔을 때 완전히 사라지게
            .padding(.top, safeAreaTop) // 노치 아래 딱 붙임
            .zIndex(10)
            
            // 타이틀 이동 레이어
            // 이 VStack은 전체 헤더 크기만큼 확보되어 노치를 절대 넘지 않음
            VStack(alignment: .leading, spacing: 0) {
                // [A] 노치 방어 영역
                Color.clear.frame(height: safeAreaTop)
                    .shadow(color: Color.black.opacity(percentage > 0.9 ? 0.1 : 0), radius: 4, x: 100, y: 100)
                
                // [B] 실제 컨텐츠가 노는 마당 (상단바 높이 + 이미지 아래 여백)
                ZStack(alignment: percentage > 0.8 ? .top : .bottom) {
                    // 버튼 레이어 (좌우 고정)
                    HStack {
                        Button(action: onBack) {
                            Image(systemName: "chevron.left")
                                .foregroundColor(percentage > 0.6 ? .black : .white)
                                .padding(10)
                                .background(Circle().fill(Color.black.opacity(percentage > 0.6 ? 0 : 0.2)))
                        }
                        Spacer()
                        Button(action: {}) {
                            Image(systemName: "square.and.arrow.up")
                                .foregroundColor(percentage > 0.6 ? .black : .white)
                                .padding(10)
                                .background(Circle().fill(Color.black.opacity(percentage > 0.6 ? 0 : 0.2)))
                        }
                    }
                    .padding(.horizontal, 16)
                    .frame(height: collapsedHeight) // 버튼은 항상 60pt 바 안에 위치
                    .opacity(percentage > 0.8 ? Double((percentage - 0.8) * 5) : 0)
                    .zIndex(10)
                    
                    // 제목과 날짜
                    VStack(alignment: percentage > 0.8 ? .center : .leading, spacing: 2) {
                        Text(event?.title ?? "")
                            .font(.system(size: 24 - (8 * percentage), weight: .bold))
                            .lineLimit(1)
                            .padding(.bottom, percentage > 0.8 ? 0 : 4)
                        
                        Text("\(formatDateString(event?.startDate)) ~ \(formatDateString(event?.endDate))")
                            .font(.system(size: 14 - (3 * percentage)))
                            .foregroundColor(.gray)
                    }
                    .frame(maxWidth: .infinity, alignment: percentage > 0.8 ? .center : .leading)
                    .padding(.horizontal, percentage > 0.8 ? 60 : 16)
                    .padding(.bottom, percentage > 0.8 ? 16 : 0)
                    .offset(y: percentage > 0.8 ? 14 : 110)
                }
                .frame(maxHeight: .infinity)
                .padding(.bottom, percentage > 0.8 ? 0 : 8)
            }
        }
    }
    
    func formatDateString(_ date: Kotlinx_datetimeLocalDate?) -> String {
        guard let date = date else { return "" }
        return "\(date.year)-\(date.monthNumber)-\(date.dayOfMonth)"
    }
}
