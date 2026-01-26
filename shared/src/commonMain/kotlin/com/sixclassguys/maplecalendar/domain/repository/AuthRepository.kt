package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.LoginInfo
import com.sixclassguys.maplecalendar.domain.model.LoginResult
import com.sixclassguys.maplecalendar.domain.model.Member
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun loginWithApiKey(apiKey: String): Flow<ApiState<LoginInfo>>

    suspend fun autoLogin(apiKey: String, fcmToken: String): Flow<ApiState<Member>>

    suspend fun loginWithGoogle(
        provider: String,
        idToken: String,
        fcmToken: String
    ): Flow<ApiState<LoginResult>>
}