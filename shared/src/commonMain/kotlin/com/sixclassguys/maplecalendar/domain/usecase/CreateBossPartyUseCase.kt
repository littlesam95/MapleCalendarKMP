package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import kotlinx.coroutines.flow.Flow

class CreateBossPartyUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(
        boss: Boss,
        bossDifficulty: BossDifficulty,
        title: String,
        description: String,
        characterId: Long
    ): Flow<ApiState<Long>> {
        return repository.createBossParty(boss, bossDifficulty, title, description, characterId)
    }
}