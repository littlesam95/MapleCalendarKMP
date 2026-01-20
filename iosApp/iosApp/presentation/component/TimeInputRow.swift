import SwiftUI
import shared

struct TimeInputRow: View {
    
    @Binding var hour: String
    @Binding var minute: String
    var onAddClick: () -> Void
    var isAddEnabled: Bool
    @FocusState var focusedField: AlarmSettingDialog.TimeField?

    var body: some View {
        
        HStack(spacing: 8) {
            timeTextField(text: $hour, limit: 23, field: .hour)
            Text("시").font(.system(size: 14, weight: .bold))
            
            timeTextField(text: $minute, limit: 59, field: .minute)
            Text("분").font(.system(size: 14, weight: .bold))
            
            Spacer()
            
            Button(action: onAddClick) {
                Text("추가")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 16).padding(.vertical, 8)
                    .background(isAddEnabled ? Color.mapleOrange : Color.gray)
                    .cornerRadius(18)
            }
            .disabled(!isAddEnabled)
        }
    }

    private func timeTextField(text: Binding<String>, limit: Int, field: AlarmSettingDialog.TimeField) -> some View {
        TextField("", text: text)
            .keyboardType(.numberPad)
            .multilineTextAlignment(.center)
            .focused($focusedField, equals: field)
            .frame(width: 50, height: 36)
            .background(Color.mapleGray.opacity(0.5))
            .cornerRadius(18)
            .onChange(of: text.wrappedValue) { newValue in
                let filtered = newValue.filter { $0.isNumber }
                if let num = Int(filtered), num > limit {
                    text.wrappedValue = String(limit)
                } else {
                    text.wrappedValue = String(filtered.prefix(2))
                }
                // 자동 포커스 이동 (시간 2글자 입력 시 분으로)
                if field == .hour && text.wrappedValue.count == 2 {
                    focusedField = .minute
                }
            }
    }
}
