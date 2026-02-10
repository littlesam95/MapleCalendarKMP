package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class CreateBossPartyAlarmUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        bossPartyId: Long,
        hour: Int,
        minute: Int,
        date: LocalDate,
        message: String
    ): Flow<ApiState<List<BossPartyAlarmTime>>> {
        return repository.createBossAlarm(bossPartyId, hour, minute, date, message)
    }
}