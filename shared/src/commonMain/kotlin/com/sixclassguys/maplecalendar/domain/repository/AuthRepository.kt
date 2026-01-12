package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.CharacterBasic
import com.sixclassguys.maplecalendar.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun loginWithApiKey(apiKey: String): Flow<ApiState<LoginResult>>

    suspend fun autoLogin(apiKey: String): Flow<ApiState<CharacterBasic>>
}