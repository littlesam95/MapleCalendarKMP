import SwiftUI

// 단계 인디케이터 서브뷰
struct CharacterSelectStepItem: View {
    
    let icon: String
    let title: String
    let isActive: Bool
    
    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: icon).font(.system(size: 24))
            Text(title).font(.system(size: 12, weight: .medium))
        }
        .foregroundColor(isActive ? .mapleOrange : .gray.opacity(0.5))
    }
}
