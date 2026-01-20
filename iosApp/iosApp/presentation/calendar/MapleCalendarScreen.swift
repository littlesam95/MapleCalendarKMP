import SwiftUI
import shared

struct MapleCalendarScreen: View {
    
    @StateObject var viewModel: CalendarViewModel
    var onNavigateToEventDetail: (Int64) -> Void
    
    var body: some View {
        
        ZStack {
            Color.white.ignoresSafeArea()
            
            ScrollView(.vertical) {
                VStack(alignment: .leading, spacing: 0) {
                    // 1. 헤더 타이틀
                    Text("캘린더").font(.system(size: 32, weight: .bold))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 16)
                    
                    // 2. 캘린더 카드 섹션
                    CalendarCardView(viewModel: viewModel)
                    
                    // 3. 진행 중인 이벤트 섹션
                    VStack(alignment: .leading, spacing: 16) {
                        let selectedDateText = viewModel.uiState.selectedDate != nil ?
                            "\(viewModel.uiState.selectedDate!.year)년 \(viewModel.uiState.selectedDate!.monthNumber)월 \(viewModel.uiState.selectedDate!.dayOfMonth)일" : "날짜를 선택해주세요!"
                        
                        Text("\(selectedDateText) 진행중인 이벤트")
                            .font(.system(size: 20, weight: .semibold))
                            .padding(.horizontal, 16)
                        
                        let events = viewModel.getSelectedDateEvents()
                        if events.isEmpty {
                            EmptyEventView(message: "진행중인 이벤트가 없어요.")
                        } else {
                            CarouselEventRow(events: events) { id in
                                viewModel.onIntent(intent: .SelectEvent(eventId: id))
                                onNavigateToEventDetail(id)
                            }
                        }
                    }
                    .padding(.top, 24)
                    
                    // 4. 오늘의 보스 일정 섹션
                    VStack(alignment: .leading, spacing: 16) {
                        Text("오늘의 보스 일정")
                            .font(.system(size: 20, weight: .semibold))
                            .padding(.horizontal, 16)
                        
                        // 현재 보스 데이터가 없으므로 Placeholder 처리
                        EmptyEventView(message: "진행중인 이벤트가 없어요.", showBossImage: true)
                    }
                    .padding(.top, 32)
                    .padding(.bottom, 32)
                }
            }
        }
        .onAppear {
            // 상세 페이지에서 돌아왔을 때 선택된 이벤트 상태를 초기화
            viewModel.onIntent(intent: CalendarIntent.SelectEvent(eventId: 0)) // 0 또는 null 처리를 위한 ID
        }
    }
}
