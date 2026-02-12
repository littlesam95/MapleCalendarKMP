package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoardHistory
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class GetBossPartyBoardsUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        bossPartyId: Long,
        page: Int
    ): Flow<ApiState<BossPartyBoardHistory>> {
        return repository.getBoardPosts(bossPartyId, page)
    }
}