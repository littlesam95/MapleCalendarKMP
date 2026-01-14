package com.sixclassguys.maplecalendar.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.GetApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetSavedFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.LogoutUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UnregisterTokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val reducer: SettingReducer,
    private val getApiKeyUseCase: GetApiKeyUseCase,
    private val getSavedFcmTokenUseCase: GetSavedFcmTokenUseCase,
    private val getGlobalAlarmStatusUseCase: GetGlobalAlarmStatusUseCase,
    private val toggleGlobalAlarmStatusUseCase: ToggleGlobalAlarmStatusUseCase,
    private val unregisterTokenUseCase: UnregisterTokenUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onIntent(SettingIntent.FetchNexonOpenApiKey)
        onIntent(SettingIntent.FetchFcmToken)
        onIntent(SettingIntent.FetchGlobalAlarmStatus)
    }

    private fun getNexonOpenApiKey() {
        viewModelScope.launch {
            getApiKeyUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(SettingIntent.FetchNexonOpenApiKeySuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(SettingIntent.FetchNexonOpenApiKeyFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getFcmToken() {
        viewModelScope.launch {
            getSavedFcmTokenUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(SettingIntent.FetchFcmTokenSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(SettingIntent.FetchFcmTokenFailed(state.message))
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
                        onIntent(SettingIntent.FetchGlobalAlarmStatusSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(SettingIntent.FetchGlobalAlarmStatusFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun toggleGlobalAlarmStatus(apiKey: String) {
        viewModelScope.launch {
            toggleGlobalAlarmStatusUseCase(apiKey).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(SettingIntent.ToggleGlobalAlarmStatusSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(SettingIntent.ToggleGlobalAlarmStatusFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun unregisterFcmToken(apiKey: String, token: String) {
        viewModelScope.launch {
            unregisterTokenUseCase(apiKey, token).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        logout()
                    }

                    is ApiState.Error -> {
                        onIntent(SettingIntent.LogoutFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(SettingIntent.LogoutSuccess)
                    }

                    is ApiState.Error -> {
                        onIntent(SettingIntent.LogoutFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onIntent(intent: SettingIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is SettingIntent.FetchNexonOpenApiKey -> {
                getNexonOpenApiKey()
            }

            is SettingIntent.FetchFcmToken -> {
                getFcmToken()
            }

            is SettingIntent.FetchGlobalAlarmStatus -> {
                getGlobalAlarmStatus()
            }

            is SettingIntent.ToggleGlobalAlarmStatus -> {
                toggleGlobalAlarmStatus(_uiState.value.nexonApiKey ?: "")
            }

            is SettingIntent.Logout -> {
                unregisterFcmToken(_uiState.value.nexonApiKey ?: "", _uiState.value.fcmToken ?: "")
            }

            else -> {}
        }
    }
}