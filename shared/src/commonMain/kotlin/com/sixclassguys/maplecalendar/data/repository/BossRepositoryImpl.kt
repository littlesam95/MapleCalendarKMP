package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.BossDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class BossRepositoryImpl(
    private val dataSource: BossDataSource,
    private val dataStore: AppPreferences
) : BossRepository {

    override suspend fun getBossParties(): Flow<ApiState<List<BossParty>>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getBossParties(accessToken)
            val bossParties = response.map { it.toDomain() }

            emit(ApiState.Success(bossParties))
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

    override suspend fun createBossParty(
        boss: Boss,
        bossDifficulty: BossDifficulty,
        title: String,
        description: String,
        characterId: Long
    ): Flow<ApiState<Long>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.createBossParty(
                accessToken = accessToken,
                request = BossPartyCreateRequest(
                    boss = boss,
                    difficulty = bossDifficulty,
                    title = title,
                    description = description,
                    characterId = characterId
                )
            )
            val bossPartyId = response.partyId

            emit(ApiState.Success(bossPartyId))
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

    override suspend fun getBossPartyDetail(bossPartyId: Long): Flow<ApiState<BossPartyDetail>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getBossPartyDetail(accessToken, bossPartyId)
            val bossPartyDetail = response.toDomain()

            emit(ApiState.Success(bossPartyDetail))
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

    override suspend fun connect(partyId: String, token: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        dataSource.connect(partyId, token)

        emit(ApiState.Success(Unit))
    }.catch { e ->
        emit(ApiState.Error(e.message ?: "연결 중 오류 발생"))
    }

    override fun observeMessages(): Flow<BossPartyChat> = dataSource.observeMessages()
        .filterIsInstance<Frame.Text>() // 1. 텍스트 프레임만 필터링
        .map { frame ->
            // 2. Frame에서 텍스트 추출
            val jsonString = frame.readText()

            // 3. JSON -> DTO(Response) 변환
            val response = Json.decodeFromString<BossPartyChatMessageResponse>(jsonString)

            // 4. DTO -> Domain 모델로 매핑
            response.toDomain()
        }
        .catch { e ->
            // 에러 로그 기록 및 예외 처리
            println("WebSocket Flow Error: ${e.message}")
        }

    override suspend fun sendMessage(partyId: Long, message: BossPartyChat): ApiState<Unit>{
        return try {
            dataSource.sendMessage(BossPartyChatMessageRequest(
                bossPartyId = partyId,
                characterId = message.senderId,
                content = message.content,
                messageType = message.messageType
            ))
            ApiState.Success(Unit)
        } catch (e: Exception) {
            ApiState.Error(e.message ?: "전송 실패")
        }
    }

    override suspend fun disconnect() = dataSource.disconnect()
}