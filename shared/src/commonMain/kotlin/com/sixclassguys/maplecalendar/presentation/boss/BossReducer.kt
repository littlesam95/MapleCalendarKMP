package com.sixclassguys.maplecalendar.presentation.boss

class BossReducer {

    fun reduce(currentState: BossUiState, intent: BossIntent): BossUiState = when (intent) {
        is BossIntent.SelectBoss -> {
            currentState.copy(
                isLoading = false
            )
        }
    }
}