import SwiftUI
import shared
import Kingfisher // 이미지 로딩을 위해 Kingfisher 사용

struct CharacterSelectCard: View {
    
    let character: AccountCharacter
    let characterImage: String
    let isSelected: Bool
    let onClick: () -> Void
    
    var body: some View {
        
        Button(action: onClick) {
            VStack(spacing: 4) {
                // 이미지 영역 (확대 로직 적용)
                ZStack {
                    if characterImage.isEmpty {
                        Image("ic_profile_default").resizable()
                            .scaledToFit()
                            .frame(width: 45, height: 45)
                            .foregroundColor(.gray)
                    } else {
                        KFImage(URL(string: characterImage)).resizable()
                            .scaledToFit()
                            .scaleEffect(2.5) // 이미지 2.5배 확대
                            .offset(y: -10)    // 발 위치 조정
                    }
                }
                .frame(width: 80, height: 100)
                .clipped()

                Text(character.characterName).font(.system(size: 15, weight: .bold))
                    .foregroundColor(.black)
                    .lineLimit(1)
                
                Text("Lv.\(character.characterLevel)").font(.system(size: 12))
                    .foregroundColor(.mapleGray)
                
                Text(character.characterClass).font(.system(size: 11))
                    .foregroundColor(.mapleGray)
                    .lineLimit(1)
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 4)
            .frame(maxWidth: .infinity)
            .background(Color.white)
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? Color.mapleOrange : Color.black, lineWidth: isSelected ? 2 : 1)
            )
        }
    }
}
