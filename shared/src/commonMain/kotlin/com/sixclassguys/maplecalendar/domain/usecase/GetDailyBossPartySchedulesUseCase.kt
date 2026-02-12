package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartySchedule
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class GetDailyBossPartySchedulesUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(year: Int, month: Int, day: Int): Flow<ApiState<List<BossPartySchedule>>> {
        return repository.getBossPartySchedules(year, month, day)
    }
}