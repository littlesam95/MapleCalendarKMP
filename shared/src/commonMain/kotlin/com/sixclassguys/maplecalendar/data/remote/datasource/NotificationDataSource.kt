package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.TokenRequest
import io.ktor.client.statement.HttpResponse

interface NotificationDataSource {

    suspend fun registerToken(request: TokenRequest): HttpResponse

    suspend fun unregisterToken(apiKey: String, request: TokenRequest): HttpResponse
}