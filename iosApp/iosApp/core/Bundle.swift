import Foundation

extension Bundle {
    var nexonApiKey: String {
        return infoDictionary?["NexonApiKey"] as? String ?? ""
    }
}
