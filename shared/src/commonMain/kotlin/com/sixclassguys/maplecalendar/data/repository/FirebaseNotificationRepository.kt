package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import com.sixclassguys.maplecalendar.getPlatform
import com.sixclassguys.maplecalendar.util.handleApiError
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging
import io.github.aakira.napier.Napier
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FirebaseNotificationRepository(
    private val notificationDataSource: NotificationDataSource,
    private val dataStore: AppPreferences
) : NotificationRepository {

    override suspend fun getGlobalAlarmStatus(): Flow<ApiState<Boolean>> = flow {
        emit(ApiState.Loading)

        val isNotificationMode = dataStore.isNotificationMode.first()

        emit(ApiState.Success(isNotificationMode))
    }.handleApiError()

    override suspend fun getSavedFcmToken(): Flow<ApiState<String?>> = flow {
        emit(ApiState.Loading)

        val apiKey = dataStore.lastSentToken.first()

        emit(ApiState.Success(apiKey))
    }.handleApiError()

    override suspend fun getFcmToken(): String? {
        return try {
            Firebase.messaging.getToken()
        } catch (e: Exception) {
            Napier.e("Token Error", e)
            null
        }
    }

    override fun registerToken(token: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading) // 로딩 시작 알림

        // 로컬에 저장된 마지막 토큰 확인
        val lastToken = dataStore.lastSentToken.first()

        if (lastToken != token) {
            val response = notificationDataSource.registerToken(
                FcmTokenRequest(
                    token = token,
                    platform = getPlatform().name
                )
            )

            if (response.status.isSuccess()) {
                dataStore.saveToken(token) // 성공했을 경우에만 DataStore에 저장
                emit(ApiState.Success(Unit)) // 성공 알림
            } else {
                emit(ApiState.Error("알림 토큰 등록에 실패했어요.")) // 서버 측 에러
            }
        } else {
            Napier.d("같은 토큰이 있음")
            emit(ApiState.Success(Unit)) // 토큰이 같으면 서버에 전송할 필요 없음
        }
    }.handleApiError().flowOn(Dispatchers.IO)

    override suspend fun unregisterToken(token: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading) // 로딩 시작 알림

        val accessToken = dataStore.accessToken.first()
        val response = notificationDataSource.unregisterToken(
            accessToken = accessToken,
            request = FcmTokenRequest(
                token = token,
                platform = getPlatform().name
            )
        )

        emit(ApiState.Success(response))
    }.handleApiError()
}