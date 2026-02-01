package com.sixclassguys.maplecalendar.presentation.boss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.ConnectBossChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.CreateBossPartyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DisconnectBossPartyChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartiesUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartyChatHistoryUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartyDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharactersUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ObserveBossChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SendBossChatUseCase
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BossViewModel(
    private val reducer: BossReducer,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getBossPartiesUseCase: GetBossPartiesUseCase,
    private val createBossPartyUseCase: CreateBossPartyUseCase,
    private val getBossPartyDetailUseCase: GetBossPartyDetailUseCase,
    private val getBossPartyChatHistoryUseCase: GetBossPartyChatHistoryUseCase,
    private val connectBossChatUseCase: ConnectBossChatUseCase,
    private val observeBossChatUseCase: ObserveBossChatUseCase,
    private val sendBossChatUseCase: SendBossChatUseCase,
    private val disconnectBossPartyChatUseCase: DisconnectBossPartyChatUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BossUiState>(BossUiState())
    val uiState = _uiState.asStateFlow()

    private fun getBossParties() {
        viewModelScope.launch {
            getBossPartiesUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.FetchBossPartiesSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.FetchBossPartiesFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }
    
    private fun createBossParty(
        boss: Boss,
        bossDifficulty: BossDifficulty,
        title: String,
        description: String,
        characterId: Long
    ) {
        viewModelScope.launch {
            createBossPartyUseCase(boss, bossDifficulty, title, description, characterId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.CreateBossPartySuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.CreateBossPartyFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getBossPartyDetail(bossPartyId: Long) {
        viewModelScope.launch {
            getBossPartyDetailUseCase(bossPartyId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.FetchBossPartyDetailSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.FetchBossPartyDetailFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getBossPartyChatHistory() {
        viewModelScope.launch {
            getBossPartyChatHistoryUseCase(
                bossPartyId = _uiState.value.selectedBossParty?.id ?: 0L,
                page = _uiState.value.bossPartyChatPage
            ).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.FetchBossPartyChatHistorySuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.FetchBossPartyChatHistoryFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun connectToChat() {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: return
        viewModelScope.launch {
            // 1. 먼저 연결을 시도합니다.
            connectBossChatUseCase(bossPartyId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        // 2. 연결 성공 시, 메시지 관찰(구독) 시작
                        Napier.d("연결 성공")
                        observeRealTimeMessages()
                    }

                    is ApiState.Error -> {
                        Napier.d("연결 실패: ${state.message}")
                        onIntent(BossIntent.ConnectBossPartyChatFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun observeRealTimeMessages() {
        viewModelScope.launch {
            observeBossChatUseCase().collect { state ->
                if (state is ApiState.Success) {
                    onIntent(BossIntent.ReceiveRealTimeChat(state.data))
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: return
        Napier.d("메시지 내용: $content")
        viewModelScope.launch {
            when (val result = sendBossChatUseCase(bossPartyId, content)) {
                is ApiState.Success -> {
                    onIntent(BossIntent.SendBossPartyChatMessageSuccess)
                }

                is ApiState.Error -> {
                    onIntent(BossIntent.SendBossPartyChatMessageFailed(result.message))
                }

                else -> {}
            }
        }
    }

    private fun disconnectToChat() {
        viewModelScope.launch {
            disconnectBossPartyChatUseCase()
        }
    }

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
            is BossIntent.FetchBossParties -> {
                getBossParties()
            }
            
            is BossIntent.CreateBossParty -> {
                createBossParty(
                    boss = _uiState.value.selectedBoss,
                    bossDifficulty = _uiState.value.selectedBossDifficulty ?: BossDifficulty.NORMAL,
                    title = _uiState.value.bossPartyCreateTitle,
                    description = _uiState.value.bossPartyCreateDescription,
                    characterId = _uiState.value.bossPartyCreateCharacter?.id ?: 0L
                )
            }

            is BossIntent.CreateBossPartySuccess -> {
                getBossPartyDetail(intent.bossPartyId)
            }

            is BossIntent.FetchBossPartyDetail -> {
                getBossPartyDetail(intent.bossPartyId)
            }

            is BossIntent.FetchBossPartyDetailSuccess -> {
                connectToChat()
            }

            is BossIntent.FetchBossPartyChatHistory -> {
                getBossPartyChatHistory()
            }

            is BossIntent.SendBossPartyChatMessage -> {
                sendMessage(_uiState.value.bossPartyChatMessage)
            }

            is BossIntent.FetchCharacters -> {
                getSavedCharacters(intent.allWorldNames)
            }

            is BossIntent.DisconnectBossPartyChat -> {
                disconnectToChat()
            }

            else -> {}
        }
    }
}