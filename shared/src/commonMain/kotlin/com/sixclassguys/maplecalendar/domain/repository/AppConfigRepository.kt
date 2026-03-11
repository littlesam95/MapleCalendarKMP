package com.sixclassguys.maplecalendar.domain.repository

import com.sixclassguys.maplecalendar.Platform
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.AppVersion
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {

    suspend fun checkVersion(versionCode: Int): Flow<ApiState<AppVersion>>
}