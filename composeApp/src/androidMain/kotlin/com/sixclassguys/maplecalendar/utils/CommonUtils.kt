package com.sixclassguys.maplecalendar.utils

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toJavaLocalDateTime
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

object MapleDateFormatters {

    // Java의 DateTimeFormatter를 그대로 활용합니다.
    @RequiresApi(Build.VERSION_CODES.O)
    val notificationFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toMapleNotificationString(): String {
    // kotlinx -> java 변환 후 포맷팅
    return this.toJavaLocalDateTime().format(MapleDateFormatters.notificationFormatter)
}

fun makeCommaInt(number: Int): String {
    val comma = DecimalFormat("#,###")
    return "${comma.format(number)}"
}

fun makeCommaRank(number: Int): String {
    val comma = DecimalFormat("#,###")
    return "${comma.format(number)}위"
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

// CalendarCard.kt 하단 혹은 Utils.kt
@RequiresApi(Build.VERSION_CODES.O)
fun generateDaysForMonth(year: Int, month: Month): List<LocalDate?> {
    val days = mutableListOf<LocalDate?>()
    val firstDayOfMonth = LocalDate(year, month, 1)

    // 해당 월의 마지막 날짜 구하기
    val daysInMonth = when (month) {
        Month.FEBRUARY -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }

    // 1일이 무슨 요일인지 계산 (1: 월, ..., 7: 일)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.isoDayNumber

    // 일요일(7) 시작이 아니라면 그만큼 앞을 null로 채움 (일요일 시작 달력 기준)
    val paddingDays = if (firstDayOfWeek == 7) 0 else firstDayOfWeek

    repeat(paddingDays) { days.add(null) }
    for (day in 1..daysInMonth) {
        days.add(LocalDate(year, month, day))
    }

    // 주 단위(7일)를 맞추기 위해 뒤를 null로 채움
    while (days.size % 7 != 0) {
        days.add(null)
    }

    return days
}

// 정수리 위치만 빠르게 찾는 최적화 함수
fun getTopVisiblePixel(bitmap: Bitmap): Float {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)

    // 1. 모든 픽셀을 한 번에 배열로 가져옵니다 (getPixel보다 수십 배 빠름)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    // 2. 루프를 돌며 알파 값(투명도) 확인
    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = pixels[y * width + x]
            // 알파 값이 0보다 크면 (투명하지 않으면) 해당 행(y) 반환
            if ((pixel shr 24) and 0xFF > 0) {
                return y.toFloat()
            }
        }
    }
    return 0f
}