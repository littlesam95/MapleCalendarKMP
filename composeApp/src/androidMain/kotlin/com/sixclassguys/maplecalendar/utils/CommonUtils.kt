package com.sixclassguys.maplecalendar.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

object MapleDateFormatters {

    // Java의 DateTimeFormatter를 그대로 활용합니다.
    val notificationFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
}

fun LocalDateTime.toMapleNotificationString(): String {
    // kotlinx -> java 변환 후 포맷팅
    return this.toJavaLocalDateTime().format(MapleDateFormatters.notificationFormatter)
}

fun convertToMobileUrl(url: String): String {
    return if (url.contains("maplestory.nexon.com") && !url.contains("https://m.")) {
        url.replaceFirst("https://", "https://m.")
    } else {
        url
    }
}

fun LocalDate.daysInMonth(): Int {
    return when (monthNumber) {
        2 -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
}

fun LocalDate.plusMonths(months: Int): LocalDate {
    var newMonth = monthNumber + months
    var newYear = year
    while (newMonth > 12) { newMonth -= 12; newYear++ }
    while (newMonth < 1) { newMonth += 12; newYear-- }
    return LocalDate(newYear, newMonth, 1)
}

fun LocalDate.minusMonths(months: Int) = plusMonths(-months)