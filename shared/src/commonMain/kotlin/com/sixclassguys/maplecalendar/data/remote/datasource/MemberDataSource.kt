package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.RepresentativeOcidRequest

interface MemberDataSource {

    suspend fun submitRepresentativeCharacter(
        apiKey: String,
        request: RepresentativeOcidRequest
    )

    suspend fun toggleGlobalAlarmStatus(apiKey: String): Boolean
}