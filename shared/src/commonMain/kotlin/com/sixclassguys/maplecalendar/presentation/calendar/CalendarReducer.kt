package com.sixclassguys.maplecalendar.presentation.calendar

import com.sixclassguys.maplecalendar.domain.model.ApiState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.todayIn

class CalendarReducer {

    private fun getDaysInMonth(year: Int, month: Month): Int {
        return when (month) {
            Month.FEBRUARY -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
    }

    fun getTodayDate(): LocalDate {
        return Clock.System.todayIn(TimeZone.currentSystemDefault())
    }

    fun getLocalDateByOffset(offset: Int): LocalDate {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        var targetMonth = today.monthNumber + offset
        var targetYear = today.year
        while (targetMonth > 12) { targetMonth -= 12; targetYear++ }
        while (targetMonth < 1) { targetMonth += 12; targetYear-- }
        return LocalDate(targetYear, targetMonth, 1)
    }

    fun generateDays(year: Int, month: Month): List<LocalDate?> {
        val days = mutableListOf<LocalDate?>()
        val firstDayOfMonth = LocalDate(year, month, 1)
        val daysInMonth = getDaysInMonth(year, month)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.isoDayNumber
        val paddingDays = if (firstDayOfWeek == 7) 0 else firstDayOfWeek

        repeat(paddingDays) { days.add(null) }
        for (day in 1..daysInMonth) { days.add(LocalDate(year, month, day)) }
        while (days.size % 7 != 0) { days.add(null) }
        return days
    }

    fun reduce(currentState: CalendarUiState, intent: CalendarIntent): CalendarUiState {
        return when (intent) {
            is CalendarIntent.Refresh -> {
                currentState.copy(
                    isLoading = true,
                    isRefreshing = true
                )
            }

            is CalendarIntent.FetchNexonOpenApiKey -> {
                currentState.copy(
                    isLoading = true
                )
            }

            is CalendarIntent.FetchNexonOpenApiKeySuccess -> {
                currentState.copy(
                    isLoading = false,
                    nexonApiKey = intent.key
                )
            }

            is CalendarIntent.FetchNexonOpenApiKeyFailed -> {
                currentState.copy(
                    isLoading = false,
                    errorMessage = intent.message
                )
            }

            is CalendarIntent.FetchGlobalAlarmStatus -> {
                currentState.copy(
                    isLoading = true
                )
            }

            is CalendarIntent.FetchGlobalAlarmStatusSuccess -> {
                currentState.copy(
                    isLoading = false,
                    isGlobalAlarmEnabled = intent.isEnabled,
                    selectedDate = getTodayDate()
                )
            }

            is CalendarIntent.FetchGlobalAlarmStatusFailed -> {
                currentState.copy(
                    isLoading = false,
                    errorMessage = intent.message
                )
            }

            is CalendarIntent.ChangeMonth -> {
                val targetDate = getLocalDateByOffset(intent.offset)
                currentState.copy(
                    isLoading = true, // 달이 바뀌면 로딩 표시
                    year = targetDate.year,
                    month = targetDate.month,
                    days = generateDays(targetDate.year, targetDate.month),
                    selectedDate = currentState.selectedDate
                )
            }

            is CalendarIntent.SaveEventsByDay -> {
                when (val result = intent.apiState) {
                    is ApiState.Success -> currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        eventsMapByDay = currentState.eventsMapByDay + (intent.key to result.data)
                    )

                    is ApiState.Error -> currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.message
                    )

                    is ApiState.Loading -> currentState.copy(isLoading = true)

                    else -> currentState
                }
            }

            is CalendarIntent.SaveEventsByMonth -> {
                when (val result = intent.apiState) {
                    is ApiState.Success -> currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        eventsMapByMonth = currentState.eventsMapByMonth + (intent.key to result.data)
                    )

                    is ApiState.Error -> currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.message
                    )

                    is ApiState.Loading -> currentState.copy(isLoading = true)

                    else -> currentState
                }
            }

            is CalendarIntent.SelectDate -> currentState.copy(
                isRefreshing = false,
                selectedDate = intent.date,
                showBottomSheet = true
            )

            is CalendarIntent.DismissBottomSheet -> currentState.copy(
                isRefreshing = false,
                selectedDate = null,
                showBottomSheet = false
            )

            is CalendarIntent.ClearSelectedEvent -> {
                currentState.copy(
                    selectedEvent = null,
                    isNotificationEnabled = false,
                    scheduledNotifications = emptyList()
                )
            }

            is CalendarIntent.SelectEvent -> {
                currentState.copy(
                    isLoading = true
                )
            }

            is CalendarIntent.SelectEventSuccess -> {
                val isNotificationEnabled = intent.event?.isRegistered ?: false
                val scheduledNotifications = intent.event?.notificationTimes ?: emptyList()
                currentState.copy(
                    isLoading = false,
                    selectedEvent = intent.event,
                    isNotificationEnabled = isNotificationEnabled,
                    scheduledNotifications = scheduledNotifications
                )
            }

            is CalendarIntent.SelectEventFailed -> {
                currentState.copy(
                    isLoading = false,
                    errorMessage = intent.message
                )
            }

            is CalendarIntent.ToggleNotification -> {
                currentState.copy(
                    isLoading = true
                )
            }

            is CalendarIntent.ToggleNotificationSuccess -> {
                val isNotificationEnabled = intent.event.isRegistered
                val scheduledNotifications = intent.event.notificationTimes
                currentState.copy(
                    isLoading = false,
                    selectedEvent = intent.event,
                    isNotificationEnabled = isNotificationEnabled,
                    scheduledNotifications = scheduledNotifications
                )
            }

            is CalendarIntent.ToggleNotificationFailed -> {
                currentState.copy(
                    isLoading = false,
                    errorMessage = intent.message
                )
            }

            is CalendarIntent.ShowAlarmDialog -> {
                currentState.copy(
                    showAlarmDialog = intent.show,
                )
            }

            is CalendarIntent.SubmitNotificationTimes -> {
                currentState.copy(
                    isLoading = true
                )
            }

            is CalendarIntent.SubmitNotificationTimesSuccess -> {
                val event = intent.event
                val isNotificationEnabled = intent.event.isRegistered
                val scheduledNotifications = intent.event.notificationTimes
                currentState.copy(
                    isLoading = false,
                    selectedEvent = event,
                    isNotificationEnabled = isNotificationEnabled,
                    scheduledNotifications = scheduledNotifications
                )
            }

            is CalendarIntent.SubmitNotificationTimesFailed -> {
                currentState.copy(
                    isLoading = false,
                    errorMessage = intent.message
                )
            }
        }
    }
}