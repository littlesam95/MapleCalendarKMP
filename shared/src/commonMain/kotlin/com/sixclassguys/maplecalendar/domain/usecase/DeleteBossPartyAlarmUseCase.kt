package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class DeleteBossPartyAlarmUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        bossPartyId: Long,
        alarmId: Long
    ): Flow<ApiState<List<BossPartyAlarmTime>>> {
        return repository.deleteBossAlarm(bossPartyId, alarmId)
    }
}