package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleRequest
import com.sixclassguys.maplecalendar.data.remote.dto.AuthGoogleResponse
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenRequest
import com.sixclassguys.maplecalendar.data.remote.dto.JwtTokenResponse
import com.sixclassguys.maplecalendar.data.remote.dto.LoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest

interface AuthDataSource {

    suspend fun loginWithApiKey(apiKey: String): LoginResponse

    suspend fun autoLogin(accessToken: String, request: FcmTokenRequest): AuthGoogleResponse

    suspend fun loginWithGoogle(request: AuthGoogleRequest): AuthGoogleResponse

    suspend fun reissueJwtToken(request: JwtTokenRequest): JwtTokenResponse
}