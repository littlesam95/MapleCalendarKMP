package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.EventDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.EventResponse
import com.sixclassguys.maplecalendar.data.remote.dto.toDomain
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.MapleEvent
import com.sixclassguys.maplecalendar.domain.repository.EventRepository
import com.sixclassguys.maplecalendar.util.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class EventRepositoryImpl(
    private val dataSource: EventDataSource,
    private val dataStore: AppPreferences
) : EventRepository {

    override suspend fun getEventDetail(eventId: Long): Flow<ApiState<MapleEvent?>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.fetchEventDetail(accessToken, eventId)
            val events = response?.toDomain()

            if (events == null) {
                emit(ApiState.Error("이벤트 정보를 불러올 수 없어요."))
            } else {
                emit(ApiState.Success(events))
            }
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun getTodayEvents(year: Int, month: Int, day: Int): Flow<ApiState<List<MapleEvent>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.fetchTodayEvents(year, month, day, accessToken)
            val events = response.map { it.toDomain() }

            if (events.isEmpty()) {
                emit(ApiState.Error("이벤트 정보를 불러올 수 없어요."))
            } else {
                emit(ApiState.Success(events))
            }
        } catch (e: Exception) {
            emit(ApiState.Error(e.message ?: "인증 서버와 통신 중 오류가 발생했습니다."))
        }
    }.catch { e ->
        val errorState = when (e) {
            is ApiException -> ApiState.Error(e.message)
            else -> ApiState.Error("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
        emit(errorState)
    }

    override suspend fun getEvents(year: Int, month: Int): List<MapleEvent> {
        // DataSource에서 DTO 리스트를 가져옴
        val responses: List<EventResponse> = dataSource.fetchMonthlyEvents(year, month, "")

        // 각 DTO를 도메인 모델로 변환 (Mapping)
        return responses.map { it.toDomain() }
    }
}