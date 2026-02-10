package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class GetBossPartyAlarmTimesUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long): Flow<ApiState<List<BossPartyAlarmTime>>> {
        return repository.getBossPartyAlarmTimes(bossPartyId)
    }
}