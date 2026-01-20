import SwiftUI
import shared

struct CalendarViewPlaceholder: View {
    
    let startDate: Date
    let endDate: Date
    @Binding var currentMonthDate: Date
    @Binding var selectedDates: Set<Date>
    var onDateClick: (Date) -> Void
    
    // 무한 페이징을 흉내내기 위한 큰 범위
    private let calendar = Calendar.current
    private let monthRange = -100...100

    var body: some View {
        
        VStack(spacing: 0) {
            TabView(selection: $currentMonthDate) {
                ForEach(monthRange, id: \.self) { offset in
                    if let monthDate = calendar.date(byAdding: .month, value: offset, to: startOfMonth(Date())) {
                        monthPage(monthDate)
                            .tag(monthDate)
                    }
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .frame(height: 300)
        }
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 2)
    }

    @ViewBuilder
    private func monthPage(_ monthDate: Date) -> some View {
        VStack(spacing: 12) {
            // 헤더: 연월 및 이동 버튼
            HStack {
                Button(action: { moveMonth(-1) }) {
                    Image(systemName: "chevron.left").foregroundColor(.mapleOrange)
                }
                Spacer()
                Text(monthDate.formatted(.dateTime.year().month()))
                    .font(.headline).foregroundColor(.mapleOrange)
                Spacer()
                Button(action: { moveMonth(1) }) {
                    Image(systemName: "chevron.right").foregroundColor(.mapleOrange)
                }
            }
            .padding(.horizontal)

            // 요일 표시
            HStack {
                ForEach(["일", "월", "화", "수", "목", "금", "토"], id: \.self) { day in
                    Text(day).font(.caption2).frame(maxWidth: .infinity)
                        .foregroundColor(day == "일" ? .red : (day == "토" ? .blue : .gray))
                }
            }

            // 날짜 그리드
            let days = generateDaysInMonth(monthDate)
            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 7), spacing: 8) {
                ForEach(days, id: \.self) { date in
                    if let date = date {
                        let isSelected = selectedDates.contains(date)
                        let isEventDay = (date >= startDate && date <= endDate)
                        
                        Text("\(calendar.component(.day, from: date))")
                            .font(.system(size: 12))
                            .frame(width: 30, height: 30)
                            .background(isSelected ? Color.mapleOrange : Color.clear)
                            .foregroundColor(isSelected ? .white : (isEventDay ? .black : .gray))
                            .clipShape(Circle())
                            .onTapGesture {
                                if isEventDay { onDateClick(date) }
                            }
                    } else {
                        Spacer()
                    }
                }
            }
        }
        .padding()
    }

    // 로직 도우미 함수들... (generateDaysInMonth 등 생략)
    private func moveMonth(_ offset: Int) {
        if let newDate = calendar.date(byAdding: .month, value: offset, to: currentMonthDate) {
            withAnimation { currentMonthDate = newDate }
        }
    }
    
    private func startOfMonth(_ date: Date) -> Date {
        calendar.date(from: calendar.dateComponents([.year, .month], from: date))!
    }
}


private extension CalendarViewPlaceholder {
    
    func generateDaysInMonth(_ date: Date) -> [Date?] {
        // 1. 해당 월의 첫 번째 날 계산
        let components = calendar.dateComponents([.year, .month], from: date)
        guard let monthFirstDate = calendar.date(from: components) else { return [] }
        
        // 2. 첫 번째 날의 요일 (1: 일요일, 2: 월요일 ... 7: 토요일)
        let firstWeekday = calendar.component(.weekday, from: monthFirstDate)
        
        // 3. 해당 월의 총 일수
        let range = calendar.range(of: .day, in: .month, for: monthFirstDate)!
        let numberOfDays = range.count
        
        var days: [Date?] = []
        
        // 4. 시작 요일 전까지 빈 칸 채우기
        for _ in 1..<firstWeekday {
            days.append(nil)
        }
        
        // 5. 실제 날짜 채우기
        for day in 0..<numberOfDays {
            if let date = calendar.date(byAdding: .day, value: day, to: monthFirstDate) {
                // 시간 정보는 제거하고 '날짜'만 남김 (비교를 위해)
                days.append(calendar.startOfDay(for: date))
            }
        }
        
        return days
    }
}
