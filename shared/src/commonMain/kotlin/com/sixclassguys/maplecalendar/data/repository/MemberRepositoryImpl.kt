package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.remote.datasource.MemberDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.RepresentativeOcidRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MemberRepositoryImpl(
    private val dataSource: MemberDataSource
) : MemberRepository {

    override suspend fun submitRepresentativeCharacter(
        apiKey: String,
        ocid: String
    ): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        try {
            dataSource.submitRepresentativeCharacter(
                apiKey = apiKey,
                request = RepresentativeOcidRequest(ocid = ocid)
            )

            emit(ApiState.Success(Unit))
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }
}