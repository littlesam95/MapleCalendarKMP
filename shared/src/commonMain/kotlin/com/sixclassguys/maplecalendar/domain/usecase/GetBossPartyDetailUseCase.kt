package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class GetBossPartyDetailUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(bossPartyId: Long): Flow<ApiState<BossPartyDetail>> {
        return repository.getBossPartyDetail(bossPartyId)
    }
}