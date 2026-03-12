package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.remote.datasource.AppConfigDataSource
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.AppVersion
import com.sixclassguys.maplecalendar.domain.repository.AppConfigRepository
import com.sixclassguys.maplecalendar.getPlatform
import com.sixclassguys.maplecalendar.util.handleApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppConfigRepositoryImpl(
    private val dataSource: AppConfigDataSource
) : AppConfigRepository {

    override suspend fun checkVersion(versionCode: Int): Flow<ApiState<AppVersion>> = flow {
        emit(ApiState.Loading)

        val response = dataSource.checkVersion(getPlatform().name, versionCode)
        val appVersion = response.toDomain()

        emit(ApiState.Success(appVersion))
    }.handleApiError()
}