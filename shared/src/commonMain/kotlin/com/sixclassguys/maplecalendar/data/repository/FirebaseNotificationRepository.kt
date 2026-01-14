package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.TokenRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import com.sixclassguys.maplecalendar.getPlatform
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

        val apiKey = dataStore.isNotificationMode.first()

        emit(ApiState.Success(apiKey))
    }

    override suspend fun getSavedFcmToken(): Flow<ApiState<String?>> = flow {
        emit(ApiState.Loading)

        val apiKey = dataStore.lastSentToken.first()

        emit(ApiState.Success(apiKey))
    }

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
            try {
                val response = notificationDataSource.registerToken(
                    TokenRequest(
                        token = token,
                        platform = getPlatform().name
                    )
                )

                if (response.status.isSuccess()) {
                    dataStore.saveToken(token) // 성공했을 경우에만 DataStore에 저장
                    emit(ApiState.Success(Unit)) // 성공 알림
                } else {
                    emit(ApiState.Error("서버 에러: ${response.status}")) // 서버 측 에러
                }
            } catch (e: Exception) {
                emit(ApiState.Error(e.message ?: "알 수 없는 에러")) // 네트워크 에러 등
            }
        } else {
            Napier.d("같은 토큰이 있음")
            emit(ApiState.Success(Unit)) // 토큰이 같으면 서버에 전송할 필요 없음
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun unregisterToken(apiKey: String, token: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading) // 로딩 시작 알림

        val response = notificationDataSource.unregisterToken(
            apiKey = apiKey,
            request = TokenRequest(
                token = token,
                platform = getPlatform().name
            )
        )

        if (response.status.isSuccess()) {
            dataStore.deleteToken()
            emit(ApiState.Success(Unit)) // 성공 알림
        } else {
            emit(ApiState.Error("서버 에러: ${response.status}")) // 서버 측 에러
        }
    }
}