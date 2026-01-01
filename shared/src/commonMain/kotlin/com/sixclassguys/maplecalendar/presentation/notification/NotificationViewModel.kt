package com.sixclassguys.maplecalendar.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterTokenUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val reducer: NotificationReducer,
    private val dataStore: AppPreferences,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val registerTokenUseCase: RegisterTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onIntent(NotificationIntent.InitNotification)
    }

    private fun registerFCMToken(token: String) {
        viewModelScope.launch {
            val response = registerTokenUseCase(token)

            response.collect { state ->
                onIntent(NotificationIntent.RegisterFCMTokenSuccess(state))
            }
        }
    }

    private fun initNotification() {
        viewModelScope.launch {
            val isNotificationMode = dataStore.isNotificationMode.first()

            if (!isNotificationMode) {
                onIntent(NotificationIntent.ToggleNotification(isNotificationMode))
                return@launch
            }

            try {
                val token = getFcmTokenUseCase()
                if (token != null) {
                    Napier.d("성공적으로 토큰을 가져왔습니다: $token")
                    registerFCMToken(token)
                } else {
                    Napier.w("토큰이 null입니다.")
                    onIntent(NotificationIntent.RegisterFCMTokenFail(ApiState.Error("토큰을 가져올 수 없습니다.")))
                }
            } catch (e: Exception) {
                Napier.e("FCM 초기화 중 에러 발생", e)
                onIntent(NotificationIntent.RegisterFCMTokenFail(ApiState.Error("토큰을 가져올 수 없습니다.")))
            }
        }
    }

    private fun handleToggle(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setNotificationMode(enabled)
            if (enabled) onIntent(NotificationIntent.InitNotification)
        }
    }

    fun onIntent(intent: NotificationIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is NotificationIntent.InitNotification -> {
                initNotification()
            }

            is NotificationIntent.ToggleNotification -> {
                handleToggle(intent.isEnabled)
            }

            else -> {}
        }
    }
}