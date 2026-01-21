import SwiftUI

enum MapleEventType: String, CaseIterable {
    
    case punchKing = "펀치킹"
    case coinShop = "코인샵"
    case sundayMaple = "썬데이메이플"
    case boss = "보스"
    case pass = "패스"
    case coordi = "코디"
    case pcRoom = "프리미엄PC방"
    case challengers = "챌린저스"
    case itemBurning = "아이템버닝"
    case hyperBurning = "하이퍼버닝"
    case sauna = "VIP사우나"
    case newName = "뉴네임옥션"
    case specialWorld = "스페셜월드"
    case attendance = "출석이벤트"
    case remaster = "리마스터"
    case etc = "기타"

    var color: Color {
        switch self {
            case .punchKing: return Color(hex: "F44336")
            case .coinShop: return Color(hex: "FF9800")
            case .sundayMaple: return Color(hex: "E91E63")
            case .boss: return Color(hex: "9C27B0")
            case .pass: return Color(hex: "00897B")
            case .coordi: return Color(hex: "BA68C8")
            case .pcRoom: return Color(hex: "2196F3")
            case .challengers: return Color(hex: "009688")
            case .itemBurning: return Color(hex: "4CAF50")
            case .hyperBurning: return Color(hex: "FF5722")
            case .sauna: return Color(hex: "795548")
            case .newName: return Color(hex: "607D8B")
            case .specialWorld: return Color(hex: "3F51B5")
            case .attendance: return Color(hex: "FFC107")
            case .remaster: return Color(hex: "00BCD4")
            case .etc: return Color(hex: "9E9E9E")
        }
    }

    static func fromString(_ name: String) -> MapleEventType {
        return MapleEventType.allCases.first { $0.rawValue == name } ?? .etc
    }
}
