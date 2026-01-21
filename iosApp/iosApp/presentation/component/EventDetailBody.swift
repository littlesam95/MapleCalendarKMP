import SwiftUI
import shared

struct EventDetailBody: View {
    
    @ObservedObject var viewModel: CalendarViewModel
    
    var body: some View {
        
        VStack(spacing: 0) {
            // 1. 태그 섹션 (안드로이드 EventDetailHeader 대응)
            tagsSection
            
            Divider()
                .background(Color(white: 0.9))
                .padding(.horizontal, 16)
            
            // 2. 알림 설정 섹션 (안드로이드 NotificationSection 대응)
            notificationSection
            
            // 3. 홈페이지 섹션 구분선 (안드로이드 HorizontalDivider 대응)
            Rectangle()
                .fill(Color(white: 0.95))
                .frame(height: 8)
        }
    }
    
    private var tagsSection: some View {
        Group {
            if let eventTypes = viewModel.uiState.selectedEvent?.eventTypes, !eventTypes.isEmpty {
                // 태그가 많을 경우를 대비해 ScrollView 또는 Flow 형태를 고려하세요.
                // 여기서는 일단 ScrollView로 가로 스크롤이 가능하게 처리했습니다.
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(eventTypes, id: \.self) { type in
                            EventTypeTag(typeName: type)
                        }
                    }
                    .padding(.horizontal, 16)
                }
                .padding(.vertical, 12)
            }
        }
    }
    
    private var notificationSection: some View {
        VStack(alignment: .leading, spacing: 16) {
            // 타이틀 및 스위치 영역
            HStack {
                Text("알림 설정")
                    .font(.system(size: 18, weight: .bold))
                
                Spacer()
                
                // 알림이 켜져 있을 때만 달력 아이콘 표시
                if viewModel.uiState.isNotificationEnabled {
                    Button(action: {
                        // 알림 설정 다이얼로그 호출
                        viewModel.onIntent(intent: CalendarIntent.ShowAlarmDialog(show: true, event: nil))
                    }) {
                        Image(systemName: "calendar.badge.clock") // 안드로이드의 아이콘과 유사한 이미지
                            .foregroundColor(.orange)
                            .font(.system(size: 20))
                    }
                    .padding(.trailing, 8)
                }
                
                Toggle("", isOn: Binding(
                    get: { viewModel.uiState.isNotificationEnabled },
                    set: { _ in viewModel.onIntent(intent: CalendarIntent.ToggleNotification()) }
                ))
                .labelsHidden()
                .tint(.orange)
            }
            
            // 다음 알림 시간 영역
            VStack(alignment: .leading, spacing: 12) {
                Text("다음 알림 시간")
                    .font(.system(size: 16, weight: .semibold))
                
                if !viewModel.uiState.isNotificationEnabled || viewModel.uiState.scheduledNotifications.isEmpty {
                    Text("예약된 알림이 없어요.")
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .center)
                        .padding(.vertical, 20)
                } else {
                    // 안드로이드처럼 시계 아이콘과 함께 리스트 표시
                    ForEach(viewModel.uiState.scheduledNotifications, id: \.self) { time in
                        HStack(spacing: 8) {
                            Image(systemName: "clock.fill")
                                .foregroundColor(.orange)
                                .font(.system(size: 14))
                            
                            Text(formatKMPDateTime(time))
                                .font(.system(size: 15))
                                .foregroundColor(.primary)
                        }
                    }
                }
            }
        }
        .padding(16)
    }
}

struct EventTypeTag: View {
    
    let typeName: String
    
    var body: some View {
        
        let type = MapleEventType.fromString(typeName)
        
        Text(type.rawValue)
            .font(.system(size: 10, weight: .bold))
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .foregroundColor(type.color)
            .background(type.color.opacity(0.1))
            .cornerRadius(4)
            .overlay(
                RoundedRectangle(cornerRadius: 4)
                    .stroke(type.color.opacity(0.5), lineWidth: 1)
            )
    }
}
