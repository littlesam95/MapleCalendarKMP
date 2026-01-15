import SwiftUI

struct CharacterCardView: View {
    var body: some View {
        HStack(alignment: .center, spacing: 20) {
            // 1. 캐릭터 이미지 (크기를 키우고 비율 유지)
            Image("character") // Assets의 이미지 이름 확인
                .resizable()
                .scaledToFit()
                .frame(width: 140, height: 140) // 이미지 크기를 사진과 비슷하게 확대
            
            // 2. 정보 텍스트 영역
            VStack(alignment: .leading, spacing: 8) {
                // 이름과 길드 뱃지
                HStack {
                    Text("신창섭")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.black)
                    
                    Spacer()
                    
                    // 길드 버튼 형태
                    Text("길드")
                        .font(.system(size: 16))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 4)
                        .background(
                            Capsule().stroke(Color.black, lineWidth: 1)
                        )
                        .background(Capsule().fill(Color.white))
                }
                
                // 레벨 및 경험치
                Text("Lv. 300  0.0%")
                    .font(.system(size: 16, weight: .medium))
                
                // 직업
                Text("히어로")
                    .font(.system(size: 16))
                
                // 상세 정보 리스트 (그리드 형태로 정렬)
                Group {
                    infoRow(label: "유니온", value: "10,597")
                    infoRow(label: "인기도", value: "141")
                    infoRow(label: "무릉", value: "100층")
                }
                
                VStack(alignment: .leading, spacing: 2) {
                    Text("종합 10,597위")
                    Text("서버 141위")
                }
                .font(.system(size: 14))
                .padding(.top, 4)
            }
            .foregroundColor(.black) // 전체 텍스트 검정색
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color.orange) // 이전에 만든 hex 생성자 사용
        )
        
    }
    
    // 레이블과 값을 정렬해주는 헬퍼 함수
    @ViewBuilder
    func infoRow(label: String, value: String) -> some View {
        HStack(spacing: 0) {
            // 1. 레이블 영역: 고정 너비를 주어 수치들의 시작 위치를 맞춥니다.
            Text(label)
                .font(.system(size: 16, weight: .medium))
                .frame(width: 50, alignment: .leading) // 너비는 글자 길이에 맞춰 조절하세요.

            // 2. 수치 영역
            Text(value)
                .font(.system(size: 16, weight: .medium))
            
            Spacer() // 오른쪽 끝까지 밀어내기 (필요 시)
        }
    }
}
