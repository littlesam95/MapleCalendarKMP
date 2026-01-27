package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.CharacterAuthorityResponse
import com.sixclassguys.maplecalendar.data.remote.dto.CharacterRegisterRequest
import com.sixclassguys.maplecalendar.data.remote.dto.MapleCharacterListResponse

interface MapleCharacterDataSource {

    suspend fun getCharacters(accessToken: String): MapleCharacterListResponse

    suspend fun fetchFromNexon(accessToken: String, apiKey: String): MapleCharacterListResponse

    suspend fun registerCharacters(
        accessToken: String,
        request: CharacterRegisterRequest
    ): MapleCharacterListResponse

    suspend fun checkAuthority(accessToken: String, ocid: String): CharacterAuthorityResponse

    suspend fun setRepresentative(accessToken: String, ocid: String)

    suspend fun deleteCharacter(accessToken: String, ocid: String)
}