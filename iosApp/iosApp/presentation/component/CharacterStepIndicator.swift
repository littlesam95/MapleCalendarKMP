import SwiftUI

struct CharacterStepIndicator: View {
    
    var currentStep: Int = 1
    
    var body: some View {
        HStack(spacing: 0) {
            Spacer()
            
            // 1단계: NEXON ID 인증
            VStack(spacing: 8) {
                Image(systemName: "checkmark.shield").font(.system(size: 24))
                    .foregroundColor(currentStep >= 0 ? .mapleGray : .mapleGray) // 완료된 단계색
                
                Text("NEXON ID\n인증").font(.system(size: 14, weight: .medium))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.mapleGray)
            }
            .opacity(currentStep == 0 ? 1.0 : 0.5)
            
            // 연결 화살표
            Image(systemName: "chevron.right").font(.system(size: 18, weight: .bold))
                .foregroundColor(.black)
                .padding(.horizontal, 30)
            
            // 2단계: 대표 캐릭터 선택
            VStack(spacing: 8) {
                Image(systemName: "person.badge.plus").font(.system(size: 24))
                    .foregroundColor(currentStep == 1 ? .mapleOrange : .mapleGray)
                
                Text("대표 캐릭터\n선택").font(.system(size: 14, weight: .bold))
                    .multilineTextAlignment(.center)
                    .foregroundColor(currentStep == 1 ? .mapleOrange : .mapleGray)
            }
            
            Spacer()
        }
        .frame(maxWidth: .infinity)
    }
}
