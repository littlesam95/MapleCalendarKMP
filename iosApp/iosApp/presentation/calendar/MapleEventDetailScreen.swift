import SwiftUI
import shared
import ScalingHeaderScrollView

struct MapleEventDetailScreen: View {
    @ObservedObject var viewModel: CalendarViewModel
    let onBack: () -> Void
    
    // 라이브러리가 관리할 progress 상태 (0.0 ~ 1.0)
    @State private var progress: CGFloat = 0
    
    private let collapsedHeight: CGFloat = 60
    private let expandedHeight: CGFloat = 200
    private let safeAreaTop: CGFloat = 54
    
    private var maxScrollRange: CGFloat {
        320 - (safeAreaTop + collapsedHeight)
    }

    var body: some View {
        if let event = viewModel.uiState.selectedEvent {
            GeometryReader { globalProxy in
                // 1. 라이브러리 컨테이너로 전체 감싸기
                ScalingHeaderScrollView {
                    // 🚀 [Header 섹션]
                    // 기존의 EventCollapsingHeader를 그대로 쓰되, 외부에서 계산한 percentage 대신
                    // 라이브러리가 제공하는 progress를 전달
                    EventCollapsingHeader(
                        event: event,
                        percentage: progress, // 👈 여기서 progress 연동
                        onBack: onBack
                    )
                } content: {
                    // 🚀 [Body 섹션]
                    VStack(spacing: 0) {
                        EventDetailBody(viewModel: viewModel)
                        
                        if !event.url.isEmpty {
                            EventWebView(
                                url: event.url,
                                // 💡 중요: progress가 1.0이면 부모 스크롤이 끝난 것
                                // offset을 넘겨줄 때 maxScrollRange와 곱해서 현재 위치를 알려줌
                                parentOffset: -(progress * maxScrollRange),
                                maxParentScroll: maxScrollRange
                            )
                            // 💡 고정 높이: (전체 화면 높이) - (접혔을 때의 상단바 높이)
                            .frame(height: globalProxy.size.height - (safeAreaTop + collapsedHeight))
                        }
                    }
                }
                // 2. 라이브러리 설정 (핵심)
                .height(min: safeAreaTop + collapsedHeight, max: 320) // 최소/최대 높이 지정
                .collapseProgress($progress) // progress 값 바인딩
                .allowsHeaderCollapse()
                .ignoresSafeArea() // 노치 영역까지 활용
                .toolbar(.hidden, for: .navigationBar)
                
                if viewModel.uiState.showAlarmDialog {
                    AlarmSettingDialog(
                        event: event,
                        onDismiss: {
                            // 다이얼로그 닫기 인텐트 발송
                            viewModel.onIntent(intent: CalendarIntent.ShowAlarmDialog(show: false, event: nil))
                        },
                        onSubmit: { selectedDateTimes in
                            // 선택된 알람 시간 리스트를 제출하고 닫기
                            viewModel.onIntent(intent:
                                CalendarIntent.SubmitNotificationTimes(
                                    eventId: viewModel.uiState.selectedEvent!.id,
                                    dates: selectedDateTimes.map { $0.toLocalDateTime() }
                                )
                            )
                            viewModel.onIntent(intent: CalendarIntent.ShowAlarmDialog(show: false, event: nil))
                        }
                    )
                    .transition(.opacity.combined(with: .scale(scale: 0.9))) // 자연스러운 등장 애니메이션
                    .zIndex(100) // 최상단 보장
                }
            }
        }
    }
}
