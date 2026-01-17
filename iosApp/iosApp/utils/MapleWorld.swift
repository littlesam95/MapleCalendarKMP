import Foundation

enum MapleWorld: Int, CaseIterable {
    case scania = 0, bera, luna, zenith, croa, union, elysium, enosis, red, aurora, arcane, nova
    
    var name: String {
        switch self {
            case .scania: return "스카니아"
            case .bera: return "베라"
            case .luna: return "루나"
            case .zenith: return "제니스"
            case .croa: return "크로아"
            case .union: return "유니온"
            case .elysium: return "엘리시움"
            case .enosis: return "이노시스"
            case .red: return "레드"
            case .aurora: return "오로라"
            case .arcane: return "아케인"
            case .nova: return "노바"
        }
    }
    
    // 이름으로 enum을 찾는 스태틱 함수
    static func from(name: String) -> MapleWorld? {
        
        return MapleWorld.allCases.first { $0.name == name }
    }
}
