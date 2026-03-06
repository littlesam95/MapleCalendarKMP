package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleBgmHistory
import com.sixclassguys.maplecalendar.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class SearchMapleBgmUseCase(
    private val repository: PlaylistRepository
) {

    suspend operator fun invoke(query: String, page: Int): Flow<ApiState<MapleBgmHistory>> {
        return repository.searchBgms(query, page)
    }
}