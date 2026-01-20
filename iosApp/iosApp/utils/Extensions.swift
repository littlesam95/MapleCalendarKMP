import Foundation
import shared

extension Kotlinx_datetimeLocalDate {
    func toDate() -> Date {
        let calendar = Calendar.current
        var components = DateComponents()
        components.year = Int(self.year)
        components.month = Int(self.monthNumber)
        components.day = Int(self.dayOfMonth)
        return calendar.date(from: components) ?? Date()
    }
}

extension Date {
    func toLocalDateTime() -> Kotlinx_datetimeLocalDateTime {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute], from: self)
        
        return Kotlinx_datetimeLocalDateTime(
            year: Int32(components.year ?? 1970),
            monthNumber: Int32(components.month ?? 1),
            dayOfMonth: Int32(components.day ?? 1),
            hour: Int32(components.hour ?? 0),
            minute: Int32(components.minute ?? 0),
            second: 0,
            nanosecond: 0
        )
    }
}

func formatKMPDateTime(_ time: Kotlinx_datetimeLocalDateTime) -> String {
    return "\(time.year)년 \(time.monthNumber)월 \(time.dayOfMonth)일 \(time.hour)시 \(time.minute)분"
}

func formatDateTime(_ time: Date) -> String {
    let formatter = DateFormatter()

    formatter.dateFormat = "yyyy년 M월 d일 HH시 mm분"
    formatter.locale = Locale(identifier: "ko_KR")

    let result = formatter.string(from: time)
    return result
}
