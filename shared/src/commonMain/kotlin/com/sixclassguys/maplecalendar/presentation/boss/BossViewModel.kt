package com.sixclassguys.maplecalendar.presentation.boss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BossViewModel(
    private val reducer: BossReducer,
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BossUiState>(BossUiState())
    val uiState = _uiState.asStateFlow()

    private fun getSavedCharacters(allWorldNames: List<String>) {
        viewModelScope.launch {
            getCharactersUseCase(allWorldNames).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.FetchCharactersSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.FetchCharactersFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onIntent(intent: BossIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is BossIntent.FetchCharacters -> {
                getSavedCharacters(intent.allWorldNames)
            }

            else -> {}
        }
    }
}