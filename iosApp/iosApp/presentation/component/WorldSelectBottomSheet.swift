import SwiftUI
import shared

struct WorldSelectBottomSheet: View {
    @ObservedObject var viewModel: LoginViewModel
    
    // 허용된 월드 리스트
    let validWorldNames = [
        "스카니아", "베라", "루나", "제니스", "크로아", "유니온",
        "엘리시움", "이노시스", "레드", "오로라", "아케인", "노바",
        "에오스", "핼리오스",
        "챌린저스1", "챌린저스2", "챌린저스3", "챌린저스4"
    ]
    
    private var filteredWorlds: [String] {
        viewModel.uiState.characters.keys
            .compactMap { $0 as? String }
            .filter { worldName in
                let characters = viewModel.uiState.characters[worldName] as? [AccountCharacter] ?? []
                return !characters.isEmpty && validWorldNames.contains(worldName)
            }
            .sorted()
    }
    
    var body: some View {
        
        VStack(alignment: .leading, spacing: 0) {
            // 1. 상단 타이틀 영역
            Text("WORLD SELECT").font(.system(size: 22, weight: .bold))
                .foregroundColor(Color(red: 0.85, green: 0.9, blue: 0.3))
                .padding(.top, 30)
                .padding(.bottom, 20)
                .padding(.leading, 32) // 타이틀 위치 조정
            
            // 2. 흰색 리스트 영역 (좌우 패딩 추가)
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 0) {
                        ForEach(filteredWorlds, id: \.self) { worldName in
                            Button(action: {
                                viewModel.onIntent(intent: LoginIntent.SelectWorld(worldName: worldName))
                                viewModel.onIntent(intent: LoginIntent.ShowWorldSheet(isShow: false))
                            }) {
                                HStack(spacing: 16) {
                                    Image("ic_world_\(getWorldKey(worldName))")
                                        .resizable()
                                        .scaledToFit()
                                        .frame(width: 28, height: 28)
                                    
                                    Text(worldName)
                                        .font(.system(size: 18, weight: .medium))
                                        .foregroundColor(.black)
                                    
                                    Spacer()
                                }
                                .padding(.vertical, 16)
                                .padding(.horizontal, 24)
                            }
                            Divider().padding(.horizontal, 20)
                        }
                    }
                    .padding(.top, 10)
                }
            }
            .background(Color.white)
            .cornerRadius(30)
            .padding(.horizontal, 16)
            .padding(.bottom, 20)
        }
        .background(Color(red: 0.2, green: 0.22, blue: 0.25)) // 바텀시트 전체 배경
    }
    
    private func getWorldKey(_ name: String) -> String {
        switch name {
            case "스카니아": return "scania"
            case "베라": return "bera"
            case "루나": return "luna"
            case "제니스": return "zenith"
            case "크로아": return "croa"
            case "유니온": return "union"
            case "엘리시움": return "elysium"
            case "이노시스": return "enosis"
            case "레드": return "red"
            case "오로라": return "aurora"
            case "아케인": return "arcane"
            case "노바": return "nova"
            case "에오스": return "eos"
            case "핼리오스": return "helios"
            case "챌린저스1": return "challengers"
            case "챌린저스2": return "challengers"
            case "챌린저스3": return "challengers"
            case "챌린저스4": return "challengers"
            default: return "scania"
        }
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}
