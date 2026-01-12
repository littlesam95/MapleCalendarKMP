package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AutoLoginResponse
import com.sixclassguys.maplecalendar.data.remote.dto.LoginResponse

interface AuthDataSource {

    suspend fun loginWithApiKey(apiKey: String): LoginResponse

    suspend fun autoLogin(apiKey: String): AutoLoginResponse
}