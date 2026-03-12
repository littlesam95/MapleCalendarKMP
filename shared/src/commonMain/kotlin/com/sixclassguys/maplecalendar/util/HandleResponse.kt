package com.sixclassguys.maplecalendar.util

import com.sixclassguys.maplecalendar.data.remote.dto.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.handleResponse(): T {
    return if (status.isSuccess()) {
        this.body()
    } else {
        // 서버에서 보낸 ErrorResponse를 파싱
        val errorResponse = try {
            this.body<ErrorResponse>()
        } catch (e: Exception) {
            // 서버 응답이 ErrorResponse 규격이 아닐 경우 예외 처리
            ErrorResponse(status.value, "알 수 없는 오류가 발생했어요.")
        }

        // 서버가 보내준 구체적인 메시지를 담아 throw
        throw ApiException(errorResponse.status, errorResponse.message)
    }
}