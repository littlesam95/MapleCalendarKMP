package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.AppVersion
import com.sixclassguys.maplecalendar.domain.repository.AppConfigRepository
import kotlinx.coroutines.flow.Flow

class CheckLatestVersionUseCase(
    private val repository: AppConfigRepository
) {

    suspend operator fun invoke(versionCode: Int): Flow<ApiState<AppVersion>> {
        return repository.checkVersion(versionCode)
    }
}