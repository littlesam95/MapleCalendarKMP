package com.sixclassguys.maplecalendar.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.domain.usecase.GetApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetDailyBossPartySchedulesUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetEventDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMonthlyEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetTodayEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitEventAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleEventAlarmUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class CalendarViewModel(
    private val reducer: CalendarReducer,
    private val eventBus: NotificationEventBus,
    private val getApiKeyUseCase: GetApiKeyUseCase,
    private val getGlobalAlarmStatusUseCase: GetGlobalAlarmStatusUseCase,
    private val getTodayEventsUseCase: GetTodayEventsUseCase,
    private val getDailyBossPartySchedulesUseCase: GetDailyBossPartySchedulesUseCase,
    private val getMonthlyEventsUseCase: GetMonthlyEventsUseCase,
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val submitEventAlarmUseCase: SubmitEventAlarmUseCase,
    private val toggleEventAlarmUseCase: ToggleEventAlarmUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // onIntent(CalendarIntent.FetchNexonOpenApiKey
        viewModelScope.launch {
            eventBus.events.collect { eventId ->
                onIntent(CalendarIntent.SelectEvent(eventId))
            }
        }
    }

    private fun getNexonOpenApiKey() {
        viewModelScope.launch {
            getApiKeyUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(CalendarIntent.FetchNexonOpenApiKeySuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(CalendarIntent.FetchNexonOpenApiKeyFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getGlobalAlarmStatus() {
        viewModelScope.launch {
            getGlobalAlarmStatusUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(CalendarIntent.FetchGlobalAlarmStatusSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(CalendarIntent.FetchGlobalAlarmStatusFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchEventsByDay(year: Int, month: Int, day: Int, key: String) {
        viewModelScope.launch {
            getTodayEventsUseCase(year, month, day).collect { apiState ->
                onIntent(CalendarIntent.SaveEventsByDay(key, apiState))
            }
        }
    }

    private fun getTodayBossSchedules(year: Int, month: Int, day: Int, key: String) {
        viewModelScope.launch {
            getDailyBossPartySchedulesUseCase(year, month, day).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(CalendarIntent.FetchBossPartySchedulesSuccess(key, state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(CalendarIntent.FetchBossPartySchedulesFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchEventsByMonth(year: Int, month: Int, key: String) {
        viewModelScope.launch {
            getMonthlyEventsUseCase(year, month).collect { apiState ->
                onIntent(CalendarIntent.SaveEventsByMonth(key, apiState))
            }
        }
    }

    private fun fetchEvent(eventId: Long) {
        viewModelScope.launch {
            getEventDetailUseCase(eventId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(CalendarIntent.SelectEventSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(CalendarIntent.SelectEventFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun toggleEventAlarm() {
        viewModelScope.launch {
            val eventId = _uiState.value.selectedEvent?.id ?: 0L
            toggleEventAlarmUseCase(eventId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(CalendarIntent.ToggleNotificationSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(CalendarIntent.ToggleNotificationFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun submitEventAlarm(dates: List<LocalDateTime>) {
        viewModelScope.launch {
            val eventId = _uiState.value.selectedEvent?.id ?: 0L
            val isEnabled = _uiState.value.isNotificationEnabled
            val alarmTimes = dates.map { it.toString() }
            submitEventAlarmUseCase(eventId, isEnabled, alarmTimes).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(CalendarIntent.SubmitNotificationTimesSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(CalendarIntent.SubmitNotificationTimesFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun getTodayDate(): LocalDate = reducer.getTodayDate()

    fun getLocalDateByOffset(offset: Int): LocalDate = reducer.getLocalDateByOffset(offset)

    fun onIntent(intent: CalendarIntent) {
        // Reducer를 통해 즉시 상태 업데이트 (계산 로직)
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        // 부수 효과 처리 (네트워크 통신 등)
        when (intent) {
            is CalendarIntent.Refresh -> {
                onIntent(CalendarIntent.SelectDate(_uiState.value.selectedDate ?: getTodayDate()))
            }

            is CalendarIntent.FetchNexonOpenApiKey -> {
                getNexonOpenApiKey()
            }

            is CalendarIntent.FetchGlobalAlarmStatus -> {
                getGlobalAlarmStatus()
            }

            is CalendarIntent.FetchGlobalAlarmStatusSuccess -> {
                onIntent(CalendarIntent.ChangeMonth(0))
            }

            is CalendarIntent.ChangeMonth -> {
                val selectedDate = _uiState.value.selectedDate
                Napier.d("$selectedDate")
                val year = selectedDate?.year ?: getTodayDate().year
                val month = selectedDate?.monthNumber ?: getTodayDate().monthNumber
                val day = selectedDate?.dayOfMonth ?: getTodayDate().dayOfMonth
                val dayKey = "${year}-${month}-${day}"
                val targetDate = getLocalDateByOffset(intent.offset)
                val monthKey = "${targetDate.year}-${targetDate.monthNumber}"

                if (!_uiState.value.eventsMapByDay.containsKey(dayKey)) {
                    fetchEventsByDay(year, month, day, dayKey)
                }

                if (!_uiState.value.bossSchedulesMapByDay.containsKey(dayKey)) {
                    getTodayBossSchedules(year, month, day, dayKey)
                }

                // 해당 월 데이터가 없을 때만 서버에서 가져옴
//                if (!_uiState.value.eventsMapByMonth.containsKey(monthKey)) {
//                    fetchEventsByMonth(targetDate.year, targetDate.monthNumber, monthKey)
//                }
            }

            is CalendarIntent.SelectDate -> {
                val selectedDate = _uiState.value.selectedDate
                val year = selectedDate?.year ?: getTodayDate().year
                val month = selectedDate?.monthNumber ?: getTodayDate().monthNumber
                val day = selectedDate?.dayOfMonth ?: getTodayDate().dayOfMonth
                val key = "${year}-${month}-${day}"

                if (!_uiState.value.eventsMapByDay.containsKey(key)) {
                    fetchEventsByDay(year, month, day, key)
                }

                if (!_uiState.value.bossSchedulesMapByDay.containsKey(key)) {
                    getTodayBossSchedules(year, month, day, key)
                }
            }

            is CalendarIntent.SelectEvent -> {
                fetchEvent(intent.eventId)
            }

            is CalendarIntent.ToggleNotification -> {
                toggleEventAlarm()
            }

            is CalendarIntent.SubmitNotificationTimes -> {
                submitEventAlarm(intent.dates)
            }

            else -> {}
        }
    }
}