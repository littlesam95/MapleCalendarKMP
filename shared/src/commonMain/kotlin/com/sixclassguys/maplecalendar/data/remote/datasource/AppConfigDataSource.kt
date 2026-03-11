package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.AppVersionResponse

interface AppConfigDataSource {

    suspend fun checkVersion(platform: String, versionCode: Int): AppVersionResponse
}