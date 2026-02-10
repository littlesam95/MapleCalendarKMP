package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek

class UpdateBossPartyPeriodUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        bossPartyId: Long,
        dayOfWeek: DayOfWeek?,
        hour: Int,
        minute: Int,
        message: String,
        isImmediateApply: Boolean
    ): Flow<ApiState<List<BossPartyAlarmTime>>> {
        return repository.updateBossAlarmPeriod(
            bossPartyId,
            dayOfWeek,
            hour,
            minute,
            message,
            isImmediateApply
        )
    }
}