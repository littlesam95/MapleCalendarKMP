package com.sixclassguys.maplecalendar.presentation.boss

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BossViewModel(
    private val reducer: BossReducer
) : ViewModel() {

    private val _uiState = MutableStateFlow<BossUiState>(BossUiState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: BossIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is BossIntent.SelectBoss -> {

            }
        }
    }
}