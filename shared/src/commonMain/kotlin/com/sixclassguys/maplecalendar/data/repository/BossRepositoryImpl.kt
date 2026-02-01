package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.BossDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import com.sixclassguys.maplecalendar.util.ApiException
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import io.github.aakira.napier.Napier
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
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

    override suspend fun getChatMessage(bossPartyId: Long, page: Int): Flow<ApiState<BossPartyChatHistory>> = flow {
        emit(ApiState.Loading)

        try {
            val accessToken = dataStore.accessToken.first()
            val response = dataSource.getChatMessages(accessToken, bossPartyId, page)
            val messages = response.content.map { it.toDomain() }

            emit(ApiState.Success(
                BossPartyChatHistory(
                    messages = messages,
                    isLastPage = response.last
                )
            ))
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

    override suspend fun connect(partyId: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.connect(partyId, accessToken)

        emit(ApiState.Success(Unit))
    }.catch { e ->
        emit(ApiState.Error(e.message ?: "연결 중 오류 발생"))
    }

    override fun observeMessages(): Flow<ApiState<BossPartyChat>> {
        return dataSource.observeMessages()
            .filterIsInstance<Frame.Text>()
            .map { frame ->
                val domainModel = Json.decodeFromString<BossPartyChatMessageResponse>(frame.readText()).toDomain()
                // 명시적으로 타입을 지정하여 반환
                ApiState.Success(domainModel) as ApiState<BossPartyChat>
            }
            .retryWhen { _, attempt ->
                if (attempt < 3) {
                    delay(2000) // 여기서 멈췄다가
                    true        // true를 반환해서 재시도 결정
                } else {
                    false       // 3번 넘으면 포기
                }
            }
            .catch { e ->
                emit(ApiState.Error(e.message ?: "메시지 수신 오류"))
            }
    }

    override suspend fun sendMessage(partyId: Long, message: BossPartyChat): ApiState<Unit>{
        return try {
            Napier.d("Sending message: ${message.content}")
            dataSource.sendMessage(BossPartyChatMessageRequest(
                bossPartyId = partyId,
                characterId = message.senderId,
                content = message.content,
                messageType = message.messageType
            ))
            ApiState.Success(Unit)
        } catch (e: Exception) {
            Napier.d(e.message ?: "전송 실패")
            ApiState.Error(e.message ?: "전송 실패")
        }
    }

    override suspend fun disconnect() = dataSource.disconnect()
}