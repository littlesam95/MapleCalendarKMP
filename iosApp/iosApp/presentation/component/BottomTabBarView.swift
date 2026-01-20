import SwiftUI

// UIKit의 createPath 로직을 SwiftUI Shape로 변환
struct TabCurveShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let height: CGFloat = 37.0
        let centerWidth = rect.width / 2
        
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: centerWidth - height * 2, y: 0))
        
        // 움푹 파인 곡선 부분
        path.addCurve(to: CGPoint(x: centerWidth, y: height),
                      control1: CGPoint(x: centerWidth - 30, y: 0),
                      control2: CGPoint(x: centerWidth - 35, y: height))
        
        path.addCurve(to: CGPoint(x: centerWidth + height * 2, y: 0),
                      control1: CGPoint(x: centerWidth + 35, y: height),
                      control2: CGPoint(x: centerWidth + 30, y: 0))
        
        path.addLine(to: CGPoint(x: rect.width, y: 0))
        path.addLine(to: CGPoint(x: rect.width, y: rect.height))
        path.addLine(to: CGPoint(x: 0, y: rect.height))
        path.closeSubpath()
        
        return path
    }
}

struct BottomTabBarView: View {
    
    @Binding var selectedTab: Int
    var onCalendarClick: () -> Void // 💡 추가
    let mainColor = Color.mapleOrange
    
    var body: some View {
        
        ZStack(alignment: .top) {
            // 1. 투명하게 뚫린 배경 쉐이프
            // Shape 자체가 파여 있으므로 .fill을 하면 파인 윗부분은 자동으로 투명해집니다.
            TabCurveShape()
                .fill(mainColor)
                .frame(height: 75) // 높이는 UI에 맞춰 조절
                .shadow(color: .black.opacity(0.15), radius: 10, x: 0, y: -5)

            // 2. 탭 버튼들
            HStack(spacing: 0) {
                tabButton(icon: "house.fill", index: 0)
                tabButton(icon: "play.rectangle.fill", index: 1)
                
                // 중앙 투명 공간 확보
                Spacer().frame(width: 80)
                
                tabButton(icon: "book.fill", index: 2)
                tabButton(icon: "line.3.horizontal", index: 3)
            }
            .padding(.top, 15)
            
            // 3. 중앙 돌출 버튼
            Button(action: { onCalendarClick() }) {
                ZStack {
                    // 배경에 흰색 원을 넣지 않아야 뒤쪽 콘텐츠가 보입니다.
                    Circle()
                        .fill(mainColor)
                        .frame(width: 65, height: 65)
                        .overlay(Circle().stroke(Color.white, lineWidth: 3)) // 테두리만 흰색
                    
                    Image(systemName: "leaf.fill")
                        .foregroundColor(.white)
                        .font(.system(size: 28))
                }
            }
            .offset(y: -35) // 곡선 깊이(height: 37)에 맞춰 적절히 배치
        }
        // 배경을 기기 하단 끝(Safe Area)까지 채우면서 투명도 유지
        .background(mainColor.ignoresSafeArea(edges: .bottom).opacity(0.001))
    }
    
    @ViewBuilder
    func tabButton(icon: String, index: Int) -> some View {
        Button(action: { selectedTab = index }) {
            Spacer()
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(selectedTab == index ? .mapleWhite : .mapleGray)
            Spacer()
        }
    }
}
