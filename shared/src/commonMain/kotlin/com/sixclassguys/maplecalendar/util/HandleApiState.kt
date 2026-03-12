package com.sixclassguys.maplecalendar.util

import com.sixclassguys.maplecalendar.domain.model.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<ApiState<T>>.handleApiError(): Flow<ApiState<T>> = this.catch { e ->
    val errorState = when (e) {
        is ApiException -> ApiState.Error(e.message) // 서버에서 온 메시지 그대로 전달
        else -> ApiState.Error("시스템 오류가 발생했어요. 잠시 후 다시 시도해주세요.")
    }
    emit(errorState)
}