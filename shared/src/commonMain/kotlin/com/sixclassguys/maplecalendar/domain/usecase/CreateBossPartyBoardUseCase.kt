package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class CreateBossPartyBoardUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        bossPartyId: Long,
        content: String,
        images: List<ByteArray>
    ): Flow<ApiState<BossPartyBoard>> {
        return repository.createBoardPost(bossPartyId, content, images)
    }
}