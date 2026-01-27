package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.FcmTokenRequest
import io.ktor.client.statement.HttpResponse

interface NotificationDataSource {

    suspend fun registerToken(request: FcmTokenRequest): HttpResponse

    suspend fun unregisterToken(apiKey: String, request: FcmTokenRequest): HttpResponse
}