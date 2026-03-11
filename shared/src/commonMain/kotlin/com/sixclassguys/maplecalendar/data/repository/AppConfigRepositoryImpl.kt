package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.remote.datasource.AppConfigDataSource
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.AppVersion
import com.sixclassguys.maplecalendar.domain.repository.AppConfigRepository
import com.sixclassguys.maplecalendar.getPlatform
import com.sixclassguys.maplecalendar.util.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AppConfigRepositoryImpl(
    private val dataSource: AppConfigDataSource
) : AppConfigRepository {

    override suspend fun checkVersion(versionCode: Int): Flow<ApiState<AppVersion>> = flow {
        emit(ApiState.Loading)

        try {
            val response = dataSource.checkVersion(getPlatform().name, versionCode)
            val event = response.toDomain()

            emit(ApiState.Success(event, "알람을 예약했어요."))
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
}