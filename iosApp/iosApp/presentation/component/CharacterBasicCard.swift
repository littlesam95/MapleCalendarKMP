import SwiftUI
import shared

struct CharacterBasicCard: View {
    
    let basic: CharacterBasic
    
    var body: some View {
        
        VStack {
            HStack(alignment: .center, spacing: 20) {
                // 1. 캐릭터 이미지 (URL 기반 로딩)
                AsyncImage(url: URL(string: basic.characterImage)) { image in
                    image.resizable()
                        .scaledToFit()
                        .scaleEffect(2.7)
                        .offset(y: -30)
                } placeholder: {
                    ProgressView()
                }
                .frame(width: 120, height: 120)
                .clipShape(RoundedRectangle(cornerRadius: 8))
                
                // 2. 캐릭터 정보
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(basic.characterName).font(.system(size: 18, weight: .bold))
                        
                        Spacer()
                        
                        Text(basic.characterGuildName) .font(.system(size: 11))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 2)
                            .overlay(
                                Capsule().stroke(Color.gray, lineWidth: 1)
                            )
                    }
                    
                    Group {
                        Text("Lv. \(basic.characterLevel)  \(String(format: "%.1f", basic.characterExpRate))%")
                        Text(basic.characterClass)
                        DetailRow(label: "유니온", value: "10,597")
                        DetailRow(label: "인기도", value: "141")
                        DetailRow(label: "무릉", value: "100층")
                    }
                    .font(.system(size: 13))
                }
            }
            .padding(20)
        }
        .background(Color.white)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.mapleOrange, lineWidth: 1.5)
        )
        .padding(.vertical, 8)
    }
}

// 정보 한 줄 표시용 서브뷰
struct DetailRow: View {
    
    let label: String
    let value: String
    
    var body: some View {
        HStack(spacing: 8) {
            Text(label)
            Text(value).fontWeight(.medium)
        }
    }
}
