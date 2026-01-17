import SwiftUI

struct RepresentativeConfirmButton: View {
    
    let isSelected: Bool
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            Text("캐릭터 선택").font(.system(size: 20, weight: .bold))
                .foregroundColor(isSelected ? .white : .black)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
        }
        .disabled(!isSelected)
        // 버튼 자체는 safeArea 내부에 두고, 배경만 채우고 싶다면 아래와 같이 처리
        .background(isSelected ? Color.mapleOrange : Color.mapleGray)
    }
}
