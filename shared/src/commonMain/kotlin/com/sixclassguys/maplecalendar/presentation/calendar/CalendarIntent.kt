package com.sixclassguys.maplecalendar.presentation.calendar

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed class CalendarIntent {

    data object Refresh : CalendarIntent()

    data object FetchNexonOpenApiKey : CalendarIntent()

    data class FetchNexonOpenApiKeySuccess(val key: String) : CalendarIntent()

    data class FetchNexonOpenApiKeyFailed(val message: String) : CalendarIntent()

    data object FetchGlobalAlarmStatus : CalendarIntent()

    data class FetchGlobalAlarmStatusSuccess(val isEnabled: Boolean) : CalendarIntent()

    data class FetchGlobalAlarmStatusFailed(val message: String) : CalendarIntent()

    data class ChangeMonth(val offset: Int) : CalendarIntent()

    data class SelectDate(val date: LocalDate) : CalendarIntent()

    object DismissBottomSheet : CalendarIntent()

    data object ClearSelectedEvent : CalendarIntent()

    data class SelectEvent(val eventId: Long) : CalendarIntent()

    data class SelectEventSuccess(val event: MapleEvent?) : CalendarIntent()

    data class SelectEventFailed(val message: String) : CalendarIntent()

    data object ToggleNotification : CalendarIntent()

    data class ToggleNotificationSuccess(val event: MapleEvent) : CalendarIntent()

    data class ToggleNotificationFailed(val message: String) : CalendarIntent()

    data class ShowAlarmDialog(val show: Boolean, val event: MapleEvent? = null) : CalendarIntent()

    data class SubmitNotificationTimes(
        val eventId: Long,
        val dates: List<LocalDateTime>
    ) : CalendarIntent()

    data class SubmitNotificationTimesSuccess(val event: MapleEvent) : CalendarIntent()

    data class SubmitNotificationTimesFailed(val message: String) : CalendarIntent()

    // 캡슐화를 위한 class : 앱 내부 로직(ViewModel, API)에 의해 발생하는 비동기 결과
    data class SaveEventsByDay(
        val key: String,
        val apiState: ApiState<List<MapleEvent>>
    ) : CalendarIntent()

    data class SaveEventsByMonth(
        val key: String,
        val apiState: ApiState<List<MapleEvent>>
    ) : CalendarIntent()
}