package com.sixclassguys.maplecalendar.presentation.notification

import com.sixclassguys.maplecalendar.domain.model.ApiState

data class NotificationUiState(
    val isLoading: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val registrationState: ApiState<Unit> = ApiState.Idle
)