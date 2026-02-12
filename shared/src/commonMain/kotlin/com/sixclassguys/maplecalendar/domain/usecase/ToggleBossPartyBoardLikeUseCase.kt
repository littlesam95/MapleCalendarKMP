package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class ToggleBossPartyBoardLikeUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        bossPartyId: Long,
        boardId: Long,
        likeType: String
    ): Flow<ApiState<BossPartyBoard>> {
        return repository.toggleBoardLike(bossPartyId, boardId, likeType)
    }
}