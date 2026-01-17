import SwiftUI

struct EmptyCharacterBasicCard: View {
    
    var action: () -> Void
    
    var body: some View {
        
        Button(action: action) {
            VStack {
                Image(systemName: "plus").font(.system(size: 40))
                    .foregroundColor(.mapleOrange)
                Text("로그인하여 캐릭터의 정보를 확인하세요!") .foregroundColor(.mapleOrange)
                    .fontWeight(.medium)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 200)
            .background(
                RoundedRectangle(cornerRadius: 16) .stroke(style: StrokeStyle(lineWidth: 2, dash: [10, 5]))
                    .foregroundColor(.mapleOrange)
            )
        }
    }
}
