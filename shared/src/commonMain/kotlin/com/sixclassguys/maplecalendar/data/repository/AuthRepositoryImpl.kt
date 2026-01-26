package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.AuthDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.AutoLoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.LoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.TokenRequest
import com.sixclassguys.maplecalendar.data.remote.dto.toDomain
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.LoginInfo
import com.sixclassguys.maplecalendar.domain.model.LoginResult
import com.sixclassguys.maplecalendar.domain.model.Member
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import com.sixclassguys.maplecalendar.getPlatform
import com.sixclassguys.maplecalendar.util.ApiException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
    private val dataStore: AppPreferences
) : AuthRepository {

    override suspend fun loginWithApiKey(apiKey: String): Flow<ApiState<LoginInfo>> = flow {
        emit(ApiState.Loading)

        try {
            val response: LoginResponse = dataSource.loginWithApiKey(apiKey)

            // 데이터 계층의 모델을 도메인 모델로 변환
            val loginResult = response.toDomain()
            dataStore.setNotificationMode(loginResult.isGlobalAlarmEnabled)
            emit(ApiState.Success(loginResult))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun autoLogin(
        apiKey: String,
        fcmToken: String
    ): Flow<ApiState<Member>> = flow {
        emit(ApiState.Loading)

        try {
            val response: AutoLoginResponse =
                dataSource.autoLogin(apiKey, TokenRequest(fcmToken, getPlatform().name))

            // 데이터 계층의 모델을 도메인 모델로 변환
            val loginResult = response.toDomain()
            dataStore.setNotificationMode(loginResult.isGlobalAlarmEnabled)
            emit(ApiState.Success(loginResult))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun loginWithGoogle(
        provider: String,
        idToken: String,
        fcmToken: String
    ): Flow<ApiState<LoginResult>> = flow {
        emit(ApiState.Loading)

        try {
            val response: AuthGoogleResponse =
                dataSource.loginWithGoogle(
                    AuthGoogleRequest(
                        provider,
                        idToken,
                        fcmToken,
                        getPlatform().name
                    )
                )

            // 데이터 계층의 모델을 도메인 모델로 변환
            val loginResult = response.toDomain()
            dataStore.setNotificationMode(loginResult.member.isGlobalAlarmEnabled)

            Napier.d("로그인 결과: $loginResult")
            emit(ApiState.Success(loginResult))
        } catch (e: Exception) {
            Napier.e("로그인 실패: ${e.message}")
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        Napier.e("로그인 실패: ${errorState.message}")
        emit(errorState)
    }
}