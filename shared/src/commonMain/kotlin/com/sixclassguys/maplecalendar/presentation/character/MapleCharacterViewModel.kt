package com.sixclassguys.maplecalendar.presentation.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.CheckCharacterAuthorityUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DeleteCharacterUseCase
import com.sixclassguys.maplecalendar.domain.usecase.FetchCharactersWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharactersUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterCharactersUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UpdateRepresentativeCharacterUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.String

class MapleCharacterViewModel(
    private val reducer: MapleCharacterReducer,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val fetchCharactersWithApiKeyUseCase: FetchCharactersWithApiKeyUseCase,
    private val registerCharactersUseCase: RegisterCharactersUseCase,
    private val checkCharacterAuthorityUseCase: CheckCharacterAuthorityUseCase,
    private val updateRepresentativeCharacterUseCase: UpdateRepresentativeCharacterUseCase,
    private val deleteCharacterUseCase: DeleteCharacterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapleCharacterUiState>(MapleCharacterUiState())
    val uiState = _uiState.asStateFlow()

    private fun getSavedCharacters(allWorldNames: List<String>) {
        viewModelScope.launch {
            getCharactersUseCase(allWorldNames).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(MapleCharacterIntent.FetchCharactersSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(MapleCharacterIntent.FetchCharactersFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchCharactersWithApiKey(apiKey: String, allWorldNames: List<String>) {
        viewModelScope.launch {
            fetchCharactersWithApiKeyUseCase(apiKey, allWorldNames).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        Napier.d("Characters: ${state.data}")
                        onIntent(MapleCharacterIntent.SubmitApiKeySuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(MapleCharacterIntent.SubmitApiKeyFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun submitNewCharacters(allWorldNames: List<String>) {
        viewModelScope.launch {
            Napier.d("Ocids: ${_uiState.value.selectedCharacterOcids}")
            registerCharactersUseCase(_uiState.value.selectedCharacterOcids, allWorldNames).collect { state ->
                Napier.d("State: $state")
                when (state) {
                    is ApiState.Success -> {
                        onIntent(MapleCharacterIntent.SubmitNewCharactersSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(MapleCharacterIntent.SubmitNewCharactersFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onIntent(intent: MapleCharacterIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is MapleCharacterIntent.FetchCharacters -> {
                getSavedCharacters(intent.allWorldNames)
            }

            is MapleCharacterIntent.SubmitApiKey -> {
                fetchCharactersWithApiKey(_uiState.value.nexonOpenApiKey, intent.allWorldNames)
            }

            is MapleCharacterIntent.SubmitNewCharacters -> {
                submitNewCharacters(intent.allWorldNames)
            }

            else -> {}
        }
    }
}