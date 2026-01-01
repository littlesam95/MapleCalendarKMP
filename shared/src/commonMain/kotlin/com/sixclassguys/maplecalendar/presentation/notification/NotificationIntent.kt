package com.sixclassguys.maplecalendar.presentation.notification

import com.sixclassguys.maplecalendar.domain.model.ApiState

sealed class NotificationIntent {

    data object InitNotification : NotificationIntent()

    data class RegisterFCMTokenSuccess(val apiState: ApiState<Unit>) : NotificationIntent()

    data class RegisterFCMTokenFail(val apiState: ApiState<Unit>) : NotificationIntent()

    data class ToggleNotification(val isEnabled: Boolean) : NotificationIntent()
}