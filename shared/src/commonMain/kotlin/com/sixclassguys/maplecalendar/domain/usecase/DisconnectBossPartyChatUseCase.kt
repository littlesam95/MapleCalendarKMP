package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.repository.BossRepository

class DisconnectBossPartyChatUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke() {
        repository.disconnect()
    }
}