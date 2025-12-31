package com.sixclassguys.maplecalendar.presentation.calendar

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import kotlinx.datetime.LocalDate

sealed class CalendarIntent {

    data class ChangeMonth(val offset: Int) : CalendarIntent()

    data class SelectDate(val date: LocalDate) : CalendarIntent()

    object DismissBottomSheet : CalendarIntent()

    // 캡슐화를 위한 internal class : 앱 내부 로직(ViewModel, API)에 의해 발생하는 비동기 결과
    internal data class FetchEventsResult(
        val key: String,
        val apiState: ApiState<List<MapleEvent>>
    ) : CalendarIntent()
}