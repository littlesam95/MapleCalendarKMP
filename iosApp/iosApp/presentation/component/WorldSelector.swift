import SwiftUI

struct WorldSelector: View {
    
    let selectedWorld: String
    let onWorldClick: () -> Void

    var body: some View {
        Button(action: onWorldClick) {
            HStack(spacing: 8) {
                Image("ic_world_\(getWorldKey(selectedWorld))").resizable()
                    .frame(width: 20, height: 20)

                Text(selectedWorld.isEmpty ? "월드 선택" : selectedWorld).font(.system(size: 16, weight: .bold))
                    .foregroundColor(.black)

                Image(systemName: "triangle.fill").resizable()
                    .frame(width: 10, height: 8)
                    .rotationEffect(.degrees(180))
                    .foregroundColor(.mapleOrange)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Color.white)
            .cornerRadius(20)
            .overlay(Capsule().stroke(Color.black, lineWidth: 1.5))
        }
    }
    
    private func getWorldKey(_ worldName: String) -> String {
        switch worldName {
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
