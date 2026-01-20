import SwiftUI
import shared

struct AlarmSettingDialog: View {
    
    let event: MapleEvent
    var onDismiss: () -> Void
    var onSubmit: ([Date]) -> Void

    @State private var selectedTab = 0 // 0: 선택, 1: 주기
    @State private var selectedDates: Set<Date> = []
    @State private var addedAlarms: [Date] = []
    
    // 달력 상태 관리
    @State private var currentMonthDate = {
        let now = Date() // 2026년 1월 20일
        let components = Calendar.current.dateComponents([.year, .month], from: now)
        return Calendar.current.date(from: components)!
    }()
    
    // 시간 입력 상태
    @State private var hour: String = "10"
    @State private var minute: String = "30"
    @State private var selectedInterval: Int = 1
    
    @FocusState private var focusedField: TimeField?
    enum TimeField { case hour, minute }

    private let today = Calendar.current.startOfDay(for: Date())

    var body: some View {
        
        ZStack {
            // 배경 딤 처리
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture { focusedField = nil }

            VStack(spacing: 16) {
                HStack {
                    Text("ALARM SETTING")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(Color.mapleStatTitle)
                    
                    Spacer()
                    
                    // 닫기 버튼 추가
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.gray)
                            .padding(4)
                    }
                }
                .frame(maxWidth: .infinity)

                VStack(spacing: 16) {
                    // 1. 탭 메뉴
                    HStack(spacing: 0) {
                        tabButton(title: "선택", index: 0)
                        tabButton(title: "주기", index: 1)
                    }
                    .background(Color.mapleGray)
                    .clipShape(Capsule())
                    .frame(width: 144, height: 32)
                    .frame(maxWidth: .infinity, alignment: .leading)

                    // 2. 컨텐츠 영역 (달력 또는 주기 선택)
                    if selectedTab == 0 {
                        CalendarViewPlaceholder(
                            startDate: max(event.startDate.toDate(), today),
                            endDate: event.endDate.toDate(),
                            currentMonthDate: $currentMonthDate,
                            selectedDates: $selectedDates,
                            onDateClick: { date in
                                if selectedDates.contains(date) {
                                    selectedDates.remove(date)
                                } else {
                                    selectedDates.insert(date)
                                }
                            }
                        )
                    } else {
                        PeriodViewPlaceholder(selectedInterval: $selectedInterval)
                    }

                    // 3. 시간 입력 및 추가 버튼
                    TimeInputRow(
                        hour: $hour,
                        minute: $minute,
                        onAddClick: addAlarmAction,
                        isAddEnabled: isAddEnabled,
                        focusedField: _focusedField
                    )

                    // 4. 알림 시간 칩 리스트
                    VStack(alignment: .leading, spacing: 8) {
                        Text("알림 시간 추가")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.gray)
                        
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(addedAlarms, id: \.self) { alarm in
                                    AlarmChip(date: alarm) { removeAlarm(alarm) }
                                }
                            }
                        }
                        .frame(height: 40)
                    }

                    // 5. 제출 버튼
                    Button(action: { onSubmit(addedAlarms) }) {
                        Text("제출하기")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color.mapleOrange)
                            .cornerRadius(8)
                    }
                }
                .padding(16)
                .background(Color.white)
                .cornerRadius(16)
            }
            .padding(20)
            .background(Color.mapleStatBackground)
            .cornerRadius(20)
            .padding(.horizontal, 20)
        }
    }
}

private extension AlarmSettingDialog {
    
    var isAddEnabled: Bool {
        if selectedTab == 0 { return !selectedDates.isEmpty }
        return true
    }

    func tabButton(title: String, index: Int) -> some View {
        Text(title)
            .font(.system(size: 14, weight: .bold))
            .foregroundColor(selectedTab == index ? .white : .black)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(selectedTab == index ? Color.mapleOrange : Color.clear)
            .onTapGesture {
                selectedTab = index
                focusedField = nil
            }
    }

    func addAlarmAction() {
        let h = Int(hour) ?? 0
        let m = Int(minute) ?? 0
        var newAlarms = Set(addedAlarms)
        
        if selectedTab == 0 {
            for date in selectedDates {
                if let finalDate = combine(date: date, hour: h, minute: m) {
                    newAlarms.insert(finalDate)
                }
            }
            selectedDates.removeAll()
        } else {
            let start = max(event.startDate.toDate(), today)
            var current = start
            while current <= event.endDate.toDate() {
                if let finalDate = combine(date: current, hour: h, minute: m) {
                    newAlarms.insert(finalDate)
                }
                current = Calendar.current.date(byAdding: .day, value: selectedInterval, to: current) ?? current
            }
            if let endAlarm = combine(date: event.endDate.toDate(), hour: h, minute: m) {
                newAlarms.insert(endAlarm)
            }
        }
        addedAlarms = Array(newAlarms).sorted()
        focusedField = nil
    }

    func removeAlarm(_ date: Date) {
        addedAlarms.removeAll { $0 == date }
    }

    func combine(date: Date, hour: Int, minute: Int) -> Date? {
        Calendar.current.date(bySettingHour: hour, minute: minute, second: 0, of: date)
    }
}

struct AlarmChip: View {
    
    let date: Date
    let onRemove: () -> Void
    
    var body: some View {
        HStack(spacing: 4) {
            Text(formatDateTime(date))
            Button(action: onRemove) {
                Image(systemName: "xmark.circle.fill")
                    .font(.system(size: 12))
            }
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 6)
        .background(Color.mapleOrange)
        .foregroundColor(.mapleWhite)
        .clipShape(Capsule())
    }
}

struct PeriodViewPlaceholder: View {
    
    @Binding var selectedInterval: Int
    private let options = [("매일", 1), ("이틀마다", 2), ("사흘마다", 3), ("일주일마다", 7)]

    var body: some View {
        
        VStack(alignment: .leading, spacing: 8) {
            Text("날짜").font(.system(size: 14, weight: .bold))
            Menu {
                ForEach(options, id: \.1) { label, value in
                    Button(label) { selectedInterval = value }
                }
            } label: {
                HStack {
                    Text(options.first { $0.1 == selectedInterval }?.0 ?? "")
                        .foregroundColor(.black)
                    Spacer()
                    Image(systemName: "chevron.down").foregroundColor(.mapleOrange)
                }
                .padding(.horizontal, 12)
                .frame(maxWidth: .infinity).frame(height: 40)
                .background(RoundedRectangle(cornerRadius: 8).stroke(Color.mapleGray))
            }
        }
    }
}
