package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.AuthDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.AuthAppleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthAppleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenRequest
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenResponse
import com.sixclassguys.maplecalendar.data.remote.dto.LoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import com.sixclassguys.maplecalendar.data.remote.dto.toDomain
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.LoginInfo
import com.sixclassguys.maplecalendar.domain.model.LoginResult
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import com.sixclassguys.maplecalendar.getPlatform
import com.sixclassguys.maplecalendar.util.handleApiError
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
    private val dataStore: AppPreferences
) : AuthRepository {

    override suspend fun loginWithApiKey(apiKey: String): Flow<ApiState<LoginInfo>> = flow {
        emit(ApiState.Loading)

        val response: LoginResponse = dataSource.loginWithApiKey(apiKey)

        val loginResult = response.toDomain()
        dataStore.setNotificationMode(loginResult.isGlobalAlarmEnabled)
        emit(ApiState.Success(loginResult, "로그인에 성공했어요."))
    }.handleApiError()

    override suspend fun autoLogin(fcmToken: String): Flow<ApiState<LoginResult>> = flow {
        val accessToken = try {
            dataStore.accessToken.first()
        } catch (e: Exception) {
            ""
        }

        Napier.d("Access Token: $accessToken")

        if (accessToken.isBlank()) {
            Napier.d("Access Token이 비었음 - Empty 방출 후 종료")
            emit(ApiState.Empty)
            return@flow
        }

        emit(ApiState.Loading)

        val response: AuthGoogleResponse =
            dataSource.autoLogin(accessToken, FcmTokenRequest(fcmToken, getPlatform().name))

        val loginResult = response.toDomain()
        dataStore.setNotificationMode(loginResult.member.isGlobalAlarmEnabled)
        dataStore.saveJwtTokens(loginResult.accessToken, loginResult.refreshToken)
        dataStore.saveToken(fcmToken)
        emit(ApiState.Success(loginResult, "로그인에 성공했어요."))
    }.handleApiError()

    override suspend fun loginWithGoogle(
        provider: String,
        idToken: String,
        fcmToken: String
    ): Flow<ApiState<LoginResult>> = flow {
        emit(ApiState.Loading)

        val response: AuthGoogleResponse =
            dataSource.loginWithGoogle(
                AuthGoogleRequest(
                    provider,
                    idToken,
                    fcmToken,
                    getPlatform().name
                )
            )

        val loginResult = response.toDomain()
        dataStore.setNotificationMode(loginResult.member.isGlobalAlarmEnabled)
        dataStore.saveJwtTokens(loginResult.accessToken, loginResult.refreshToken)

        Napier.d("로그인 결과: $loginResult")
        emit(ApiState.Success(loginResult, "로그인에 성공했어요."))
    }.handleApiError()

    override suspend fun loginWithApple(
        provider: String,
        idToken: String,
        fcmToken: String
    ): Flow<ApiState<LoginResult>> = flow {
        emit(ApiState.Loading)

        val normalizedProvider = provider.trim().lowercase().ifEmpty { "apple" }
        val response: AuthAppleResponse =
            dataSource.loginWithApple(
                AuthAppleRequest(
                    provider,
                    idToken,
                    fcmToken,
                    getPlatform().name

                )
            )

        val loginResult = response.toDomain()
        dataStore.setNotificationMode(loginResult.member.isGlobalAlarmEnabled)
        dataStore.saveJwtTokens(loginResult.accessToken, loginResult.refreshToken)

        Napier.d("로그인 결과: $loginResult")
        emit(ApiState.Success(loginResult, "로그인에 성공했어요."))
    }.handleApiError()

    override suspend fun reissueJwtToken(): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val refreshToken = dataStore.refreshToken.first()
        val response: JwtTokenResponse =
            dataSource.reissueJwtToken(JwtTokenRequest(refreshToken = refreshToken))

        val accessToken = response.accessToken
        val newRefreshToken = response.refreshToken
        dataStore.saveJwtTokens(accessToken, newRefreshToken)

        emit(ApiState.Success(Unit))
    }.handleApiError()
}
