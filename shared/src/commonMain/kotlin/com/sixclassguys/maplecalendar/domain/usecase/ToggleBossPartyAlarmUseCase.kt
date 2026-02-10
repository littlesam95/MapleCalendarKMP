package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class ToggleBossPartyAlarmUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long, enabled: Boolean): Flow<ApiState<Unit>> {
        return repository.updateAlarmSetting(bossPartyId, enabled)
    }
}