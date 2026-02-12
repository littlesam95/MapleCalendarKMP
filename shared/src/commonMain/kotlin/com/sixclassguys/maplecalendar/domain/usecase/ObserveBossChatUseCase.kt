package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import kotlinx.coroutines.flow.Flow

class ObserveBossChatUseCase(
    private val repository: BossRepository
) {

    operator fun invoke(): Flow<ApiState<BossPartyChat>> =
        repository.observeMessages()
}