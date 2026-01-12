package com.sixclassguys.maplecalendar.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.DoLoginWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharacterBasicUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetOpenApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitRepresentativeCharacterUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val reducer: LoginReducer,
    private val doLoginWithApiKeyUseCase: DoLoginWithApiKeyUseCase,
    private val submitRepresentativeCharacterUseCase: SubmitRepresentativeCharacterUseCase,
    private val setOpenApiKeyUseCase: SetOpenApiKeyUseCase,
    private val getCharacterBasicUseCase: GetCharacterBasicUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initUiState()
    }

    private fun loginWithApiKey() {
        viewModelScope.launch {
            doLoginWithApiKeyUseCase(_uiState.value.nexonApiKey).collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        val ocid = apiState.data.representativeOcid
                        if (ocid == null) {
                            onIntent(LoginIntent.SelectRepresentativeCharacter(apiState.data.characters))
                        } else {
                            onIntent(LoginIntent.SetOpenApiKey)
                        }
                    }

                    is ApiState.Error -> {
                        onIntent(LoginIntent.LoginFailed(apiState.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun submitRepresentativeCharacter() {
        viewModelScope.launch {
            submitRepresentativeCharacterUseCase(
                _uiState.value.nexonApiKey,
                _uiState.value.selectedCharacter?.ocid ?: ""
            ).collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        onIntent(LoginIntent.SetOpenApiKey)
                    }

                    is ApiState.Error -> {
                        onIntent(LoginIntent.SubmitRepresentativeCharacterFailed(apiState.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun saveApiKey() {
        viewModelScope.launch {
            setOpenApiKeyUseCase(_uiState.value.nexonApiKey).collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        onIntent(LoginIntent.LoginSuccess)
                    }

                    is ApiState.Error -> {
                        onIntent(LoginIntent.SetOpenApiKeyFailed(apiState.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchAllCharacterImages() {
        viewModelScope.launch {
            // 1. 모든 월드의 캐릭터를 하나로 합침 (평탄화)
            val allCharacters = _uiState.value.characters.values.flatten()
            if (allCharacters.isEmpty()) return@launch

            // 이미 로드된 이미지는 제외하고 진행 (불필요한 중복 호출 방지)
            val charactersToFetch = allCharacters.filter {
                _uiState.value.characterImages[it.ocid] == null
            }

            // 2. 5개씩 청크(Chunk)로 나눔
            charactersToFetch.chunked(5).forEach { chunk ->
                // 3. 5개에 대한 병렬 요청 시작
                val deferredResults = chunk.map { character ->
                    async {
                        // Loading 상태가 아닌 Success/Error가 올 때까지 기다림
                        val state = getCharacterBasicUseCase(character.ocid)
                            .filter { it is ApiState.Success || it is ApiState.Error }
                            .first()

                        when (state) {
                            is ApiState.Success -> character.ocid to state.data.characterImage

                            is ApiState.Error -> {
                                character.ocid to null
                            }

                            else -> character.ocid to null
                        }
                    }
                }

                // 4. 이번 청크(5개)의 결과가 모두 나올 때까지 대기
                val chunkResults = deferredResults.awaitAll()

                // 5. [중요] 5개 결과가 나오자마자 즉시 UI 상태 업데이트
                val newBatchMap = chunkResults.associate { it.first to it.second }
                _uiState.update { currentState ->
                    currentState.copy(
                        characterImages = currentState.characterImages + newBatchMap
                    )
                }

                // 6. 넥슨 API 초당 호출 제한(Rate Limit)을 피하기 위해 1초 대기
                delay(1000L)
            }
        }
    }

    fun initUiState() {
        _uiState.update { LoginUiState() }
    }

    fun onIntent(intent: LoginIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is LoginIntent.ClickLogin -> {
                loginWithApiKey()
            }

            is LoginIntent.SelectRepresentativeCharacter -> {
                fetchAllCharacterImages()
            }

            is LoginIntent.SubmitRepresentativeCharacter -> {
                submitRepresentativeCharacter()
            }

            is LoginIntent.SetOpenApiKey -> {
                saveApiKey()
            }

            else -> {}
        }
    }
}