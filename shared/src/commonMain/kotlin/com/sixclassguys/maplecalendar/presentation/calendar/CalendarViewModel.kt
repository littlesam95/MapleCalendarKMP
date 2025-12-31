package com.sixclassguys.maplecalendar.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.usecase.GetMonthlyEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class CalendarViewModel(
    private val reducer: CalendarReducer,
    private val getMonthlyEventsUseCase: GetMonthlyEventsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onIntent(CalendarIntent.ChangeMonth(0))
    }

    private fun loadEventsFromServer(year: Int, month: Int, key: String) {
        viewModelScope.launch {
            getMonthlyEventsUseCase(year, month).collect { apiState ->
                // 통신 결과를 FetchEventsResult라는 Intent로 전달하여 상태에 반영
                onIntent(CalendarIntent.FetchEventsResult(key, apiState))
            }
        }
    }

    fun getLocalDateByOffset(offset: Int): LocalDate = reducer.getLocalDateByOffset(offset)

    fun generateDays(year: Int, month: Month) = reducer.generateDays(year, month)

    fun onIntent(intent: CalendarIntent) {
        // Reducer를 통해 즉시 상태 업데이트 (계산 로직)
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        // 부수 효과 처리 (네트워크 통신 등)
        when (intent) {
            is CalendarIntent.ChangeMonth -> {
                val targetDate = getLocalDateByOffset(intent.offset)
                val key = "${targetDate.year}-${targetDate.monthNumber}"

                // 해당 월 데이터가 없을 때만 서버에서 가져옴
                if (!_uiState.value.eventsMap.containsKey(key)) {
                    loadEventsFromServer(targetDate.year, targetDate.monthNumber, key)
                }
            }

            else -> {} // 다른 인텐트들은 상태 업데이트만으로 충분함
        }
    }
}