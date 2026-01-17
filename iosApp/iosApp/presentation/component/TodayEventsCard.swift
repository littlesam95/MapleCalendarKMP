import SwiftUI
import shared

struct TodayEventsCard: View {
    
    let event: MapleEvent
    
    var body: some View {
        
        VStack(alignment: .leading, spacing: 12) {
            AsyncImage(url: URL(string: event.thumbnailUrl ?? "")) { phase in
                if let image = phase.image {
                    image .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(maxWidth: UIScreen.main.bounds.width - 40)
                } else {
                    Color.gray.opacity(0.1)
                        .frame(maxWidth: .infinity)
                }
            }
            .frame(height: 160)
            .frame(maxWidth: .infinity)
            .clipped()
            .cornerRadius(12)

            // 텍스트 부분 (이미지와 동일한 수직 라인)
            VStack(alignment: .leading, spacing: 4) {
                Text(event.title) .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.mapleBlack)
                
                Text("\(event.startDate) ~ \(event.endDate)") .font(.system(size: 14))
                    .foregroundColor(.mapleGray)
            }
        }
        .padding(.bottom, 24)
    }
}
