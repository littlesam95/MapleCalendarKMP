package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.CharacterBasicResponse

interface NexonOpenApiDataSource {

    suspend fun getCharacterBasic(ocid: String): CharacterBasicResponse
}