package com.sixclassguys.maplecalendar.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.AutoLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetTodayEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    val savedStateHandle: SavedStateHandle,
    private val reducer: HomeReducer,
    private val getApiKeyUseCase: GetApiKeyUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val getTodayEventsUseCase: GetTodayEventsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onIntent(HomeIntent.LoadApiKey)
    }

    private fun getNexonOpenApiKey() {
        viewModelScope.launch {
            getApiKeyUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(HomeIntent.LoadCharacterBasic(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.LoadApiKeyFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getCharacterBasic(apiKey: String) {
        viewModelScope.launch {
            autoLoginUseCase(apiKey).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(HomeIntent.LoadCharacterBasicSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.LoadCharacterBasicFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getTodayEvents() {
        viewModelScope.launch {
            val now = Clock.System.now()
            val seoulZone = TimeZone.of("Asia/Seoul")
            val currentLocalDateTime = now.toLocalDateTime(seoulZone)
            val today: LocalDate = currentLocalDateTime.date
            getTodayEventsUseCase(
                today.year,
                today.monthNumber,
                today.dayOfMonth
            ).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(HomeIntent.LoadEventsSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.LoadEventsFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is HomeIntent.LoadApiKey -> {
                getNexonOpenApiKey()
            }

            is HomeIntent.LoadCharacterBasic -> {
                getCharacterBasic(intent.apiKey)
            }

            is HomeIntent.LoadApiKeyFailed -> {
                getTodayEvents()
            }

            is HomeIntent.LoadCharacterBasicSuccess -> {
                getTodayEvents()
            }

            is HomeIntent.LoadCharacterBasicFailed -> {
                getTodayEvents()
            }

            else -> {}
        }
    }
}