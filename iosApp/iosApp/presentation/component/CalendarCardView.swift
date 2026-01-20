import SwiftUI
import shared

struct CalendarCardView: View {
    @ObservedObject var viewModel: CalendarViewModel
    @State private var currentPage: Int = 0
    private let daysOfWeek = ["일", "월", "화", "수", "목", "금", "토"]

    var body: some View {
        // 1. 전체 높이를 결정하는 계산된 프로퍼티
        let rowCount = CGFloat(ceil(Double(viewModel.uiState.days.count) / 7.0))
        // 💡 줄 수에 따른 컨테이너 전체 높이 (헤더 + 요일 + 그리드 + 패딩)
        let containerHeight: CGFloat = 120 + (rowCount * 35) + ((rowCount - 1) * 10) + 40

        VStack {
            ZStack {
                // 2. 고정된 배경 (높이 애니메이션 적용)
                RoundedRectangle(cornerRadius: 24)
                    .fill(Color.white)
                    .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
                
                // 3. TabView는 배경의 크기를 그대로 따름
                TabView(selection: $currentPage) {
                    ForEach(-100...100, id: \.self) { offset in
                        // 내부 요소들을 수직 중앙에 배치
                        VStack(spacing: 20) {
                            monthHeaderView(offset: offset)
                            
                            VStack(spacing: 16) {
                                daysOfWeekHeader
                                calendarGridView
                            }
                        }
                        .padding(20)
                        .tag(offset)
                    }
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            }
            // 💡 컨테이너 자체의 높이를 여기서 조절합니다.
            .frame(height: containerHeight)
            .animation(.spring(response: 0.4, dampingFraction: 0.8), value: containerHeight)
        }
        .padding(.horizontal, 16)
        .onChange(of: currentPage) { oldValue, newValue in
            viewModel.onIntent(intent: CalendarIntent.ChangeMonth(offset: Int32(newValue)))
        }
    }

    // --- 나머지 컴포넌트(Header, Days, Grid)는 동일 ---
    
    @ViewBuilder
    private func monthHeaderView(offset: Int) -> some View {
        HStack {
            Button(action: { withAnimation { currentPage -= 1 } }) {
                Image(systemName: "chevron.left").foregroundColor(.orange)
                    .font(.system(size: 20, weight: .bold))
            }
            Spacer()
            Text("\(String(viewModel.uiState.year))년 \(viewModel.uiState.month.ordinal + 1)월").font(.system(size: 20, weight: .bold))
                .foregroundColor(.orange)
            Spacer()
            Button(action: { withAnimation { currentPage += 1 } }) {
                Image(systemName: "chevron.right").foregroundColor(.orange)
                    .font(.system(size: 20, weight: .bold))
            }
        }
    }

    private var daysOfWeekHeader: some View {
        HStack {
            ForEach(daysOfWeek, id: \.self) { day in
                Text(day).frame(maxWidth: .infinity)
                    .font(.system(size: 14))
                    .foregroundColor(dayColor(for: day))
            }
        }
    }

    private var calendarGridView: some View {
        let days = viewModel.uiState.days
        return LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 7), spacing: 10) {
            ForEach(0..<days.count, id: \.self) { index in
                if let date = days[index] as? Kotlinx_datetimeLocalDate {
                    DayCell(
                        date: date,
                        isSelected: viewModel.isDateSelected(date),
                        isToday: viewModel.isToday(date),
                        onTap: { viewModel.onIntent(intent: CalendarIntent.SelectDate(date: date)) }
                    )
                } else {
                    Spacer().frame(width: 35, height: 35)
                }
            }
        }
    }

    private func dayColor(for day: String) -> Color {
        if day == "일" { return .red }
        if day == "토" { return .blue }
        return .gray
    }
}

// 💡 날짜 셀을 별도 뷰로 정의 (핵심 해결책)
struct DayCell: View {
    let date: Kotlinx_datetimeLocalDate
    let isSelected: Bool
    let isToday: Bool
    let onTap: () -> Void
    
    var body: some View {
        Text("\(date.dayOfMonth)").font(.system(size: 16, weight: isSelected || isToday ? .bold : .regular))
            .frame(width: 35, height: 35)
            .background(isSelected ? Color.orange : Color.clear)
            .foregroundColor(isSelected ? .white : (isToday ? .orange : .black))
            .clipShape(Circle())
            .onTapGesture { onTap() }
    }
}
