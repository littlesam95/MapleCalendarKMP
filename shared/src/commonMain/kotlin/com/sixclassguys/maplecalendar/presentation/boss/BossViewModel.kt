package com.sixclassguys.maplecalendar.presentation.boss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.domain.usecase.ConnectBossChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.CreateBossPartyAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.CreateBossPartyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DeleteBossPartyAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DeleteBossPartyChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DisconnectBossPartyChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartiesUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartyAlarmTimesUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartyChatHistoryUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetBossPartyDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharactersUseCase
import com.sixclassguys.maplecalendar.domain.usecase.HideBossPartyChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ObserveBossChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ReportBossPartyChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SendBossChatUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleBossPartyAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleBossPartyChatAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UpdateBossPartyPeriodUseCase
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.ReportReason
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class BossViewModel(
    private val reducer: BossReducer,
    private val eventBus: NotificationEventBus,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getBossPartiesUseCase: GetBossPartiesUseCase,
    private val createBossPartyUseCase: CreateBossPartyUseCase,
    private val getBossPartyDetailUseCase: GetBossPartyDetailUseCase,
    private val getBossPartyAlarmTimesUseCase: GetBossPartyAlarmTimesUseCase,
    private val toggleBossPartyAlarmUseCase: ToggleBossPartyAlarmUseCase,
    private val createBossPartyAlarmUseCase: CreateBossPartyAlarmUseCase,
    private val updateBossPartyPeriodUseCase: UpdateBossPartyPeriodUseCase,
    private val deleteBossPartyAlarmUseCase: DeleteBossPartyAlarmUseCase,
    private val getBossPartyChatHistoryUseCase: GetBossPartyChatHistoryUseCase,
    private val connectBossChatUseCase: ConnectBossChatUseCase,
    private val toggleBossPartyChatAlarmUseCase: ToggleBossPartyChatAlarmUseCase,
    private val observeBossChatUseCase: ObserveBossChatUseCase,
    private val sendBossChatUseCase: SendBossChatUseCase,
    private val hideBossPartyChatUseCase: HideBossPartyChatUseCase,
    private val deleteBossPartyChatUseCase: DeleteBossPartyChatUseCase,
    private val disconnectBossPartyChatUseCase: DisconnectBossPartyChatUseCase,
    private val reportBossPartyChatUseCase: ReportBossPartyChatUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BossUiState>(BossUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            eventBus.bossPartyId.collect { bossPartyId ->
                // 💡 알림이 오면 즉시 데이터 갱신
                onIntent(BossIntent.FetchBossPartyDetail(bossPartyId))
            }
        }
    }

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

    private fun getCurrentLocalDate(): LocalDate {
        val now: Instant = Clock.System.now()
        val timeZone: TimeZone = TimeZone.currentSystemDefault()
        return now.toLocalDateTime(timeZone).date
    }
    
    private fun createBossPartyAlarm() {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: 0L
        val hour = _uiState.value.selectedHour.toInt()
        val minute = _uiState.value.selectedMinute.toInt()
        val date = _uiState.value.selectedAlarmDate ?: getCurrentLocalDate()
        val message = _uiState.value.alarmMessage
        viewModelScope.launch {
            createBossPartyAlarmUseCase(
                bossPartyId = bossPartyId,
                hour = hour,
                minute = minute,
                date = date,
                message = message
            ).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.CreateBossPartyAlarmSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.CreateBossPartyAlarmFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun toggleBossPartyAlarm() {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: 0L
        viewModelScope.launch {
            toggleBossPartyAlarmUseCase(bossPartyId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.ToggleBossPartyAlarmSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.ToggleBossPartyAlarmFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }
    
    private fun updateBossPartyPeriod() {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: 0L
        val dayOfWeek = _uiState.value.selectedDayOfWeek
        val hour = _uiState.value.selectedHour.toInt()
        val minute = _uiState.value.selectedMinute.toInt()
        val message = _uiState.value.alarmMessage
        val isImmediateApply = _uiState.value.isImmediatelyAlarm
        viewModelScope.launch { 
            updateBossPartyPeriodUseCase(
                bossPartyId = bossPartyId,
                dayOfWeek = dayOfWeek,
                hour = hour,
                minute = minute,
                message = message,
                isImmediateApply = isImmediateApply
            ).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.UpdateBossPartyAlarmPeriodSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.UpdateBossPartyAlarmPeriodFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun deleteBossPartyAlarm(alarmId: Long) {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: 0L
        viewModelScope.launch {
            deleteBossPartyAlarmUseCase(bossPartyId, alarmId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.DeleteBossPartyAlarmSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.DeleteBossPartyAlarmFailed(state.message))
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

    private fun toggleBossPartyChatAlarm() {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: 0L
        viewModelScope.launch {
            toggleBossPartyChatAlarmUseCase(bossPartyId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.ToggleBossPartyChatAlarmSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.ToggleBossPartyChatAlarmFailed(state.message))
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

    private fun sendMessage(content: String) {
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

    private fun hideMessage(chatId: Long) {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: return
        viewModelScope.launch {
            hideBossPartyChatUseCase(bossPartyId, chatId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.HideBossPartyChatMessageSuccess(chatId))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.HideBossPartyChatMessageFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun deleteMessage(chatId: Long) {
        val bossPartyId = _uiState.value.selectedBossParty?.id ?: return
        viewModelScope.launch {
            deleteBossPartyChatUseCase(bossPartyId, chatId).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.DeleteBossPartyChatMessageSuccess(chatId))
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.DeleteBossPartyChatMessageFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun disconnectToChat() {
        viewModelScope.launch {
            disconnectBossPartyChatUseCase()
        }
    }

    private fun reportChat(chatId: Long, reason: ReportReason, reasonDetail: String?) {
        viewModelScope.launch {
            reportBossPartyChatUseCase(chatId, reason, reasonDetail).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(BossIntent.ReportBossPartyChatMessageSuccess)
                    }

                    is ApiState.Error -> {
                        onIntent(BossIntent.ReportBossPartyChatMessageFailed(state.message))
                    }

                    else -> {}
                }
            }
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

            is BossIntent.FetchCharacters -> {
                getSavedCharacters(intent.allWorldNames)
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
            
            is BossIntent.CreateBossPartyAlarm -> {
                createBossPartyAlarm()
            }

            is BossIntent.ToggleBossPartyAlarm -> {
                toggleBossPartyAlarm()
            }
            
            is BossIntent.UpdateBossPartyAlarmPeriod -> {
                updateBossPartyPeriod()
            }

            is BossIntent.DeleteBossPartyAlarm -> {
                deleteBossPartyAlarm(intent.alarmId)
            }

            is BossIntent.ConnectBossPartyChat -> {
                connectToChat()
            }

            is BossIntent.ToggleBossPartyChatAlarm -> {
                toggleBossPartyChatAlarm()
            }

            is BossIntent.FetchBossPartyChatHistory -> {
                getBossPartyChatHistory()
            }

            is BossIntent.SendBossPartyChatMessage -> {
                sendMessage(_uiState.value.bossPartyChatMessage)
            }

            is BossIntent.HideBossPartyChatMessage -> {
                hideMessage(intent.bossPartyChatId)
            }

            is BossIntent.DeleteBossPartyChatMessage -> {
                deleteMessage(intent.bossPartyChatId)
            }

            is BossIntent.DisconnectBossPartyChat -> {
                disconnectToChat()
            }

            is BossIntent.ReportBossPartyChatMessage -> {
                reportChat(intent.chatId, intent.reason, intent.reasonDetail)
            }

            else -> {}
        }
    }
}