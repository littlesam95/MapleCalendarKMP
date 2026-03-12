package com.sixclassguys.maplecalendar.data.remote.datasource

import com.sixclassguys.maplecalendar.data.remote.dto.EventResponse
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.handleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class EventDataSourceImpl(
    private val httpClient: HttpClient
) : EventDataSource {

    override suspend fun fetchEventDetail(accessToken: String, eventId: Long): EventResponse?  {
        return try {
            httpClient.get("events/$eventId") {
                header("Authorization", "Bearer $accessToken")
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun fetchTodayEvents(
        year: Int,
        month: Int,
        day: Int,
        accessToken: String
    ): List<EventResponse> {
        return try {
            httpClient.get("events/today") {
                header("Authorization", "Bearer $accessToken")
                parameter("year", year)
                parameter("month", month)
                parameter("day", day)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun fetchDailyEvents(year: Int, month: Int, day: Int): List<EventResponse> {
        return try {
            httpClient.get("events/daily") {
                parameter("year", year)
                parameter("month", month)
                parameter("day", day)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }

    override suspend fun fetchMonthlyEvents(year: Int, month: Int, accessToken: String): List<EventResponse> {
        return try {
            httpClient.get("events") {
                parameter("year", year)
                parameter("month", month)
            }.handleResponse()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(0, "$e: 인터넷 연결을 확인해주세요.")
        }
    }
}