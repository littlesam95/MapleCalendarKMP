package com.sixclassguys.maplecalendar.data.repository

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.BossDataSource
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmPeriodRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyAlarmTimeRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageRequest
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyChatMessageResponse
import com.sixclassguys.maplecalendar.data.remote.dto.BossPartyCreateRequest
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoardHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.BossPartyChatHistory
import com.sixclassguys.maplecalendar.domain.model.BossPartyDetail
import com.sixclassguys.maplecalendar.domain.model.BossPartySchedule
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.handleApiError
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
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BossRepositoryImpl(
    private val dataSource: BossDataSource,
    private val dataStore: AppPreferences
) : BossRepository {

    override suspend fun getBossParties(): Flow<ApiState<List<BossParty>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.getBossParties(accessToken)
        val bossParties = response.map { it.toDomain() }

        emit(ApiState.Success(bossParties))
    }.handleApiError()

    override suspend fun createBossParty(
        boss: Boss,
        bossDifficulty: BossDifficulty,
        title: String,
        description: String,
        characterId: Long
    ): Flow<ApiState<Long>> = flow {
        emit(ApiState.Loading)

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

        emit(ApiState.Success(bossPartyId, "보스 파티 생성에 성공했어요."))
    }.handleApiError()

    override suspend fun getBossPartyDetail(bossPartyId: Long): Flow<ApiState<BossPartyDetail>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.getBossPartyDetail(accessToken, bossPartyId)
        val bossPartyDetail = response.toDomain()

        emit(ApiState.Success(bossPartyDetail))
    }.handleApiError()

    override suspend fun getBossPartySchedules(
        year: Int,
        month: Int,
        day: Int
    ): Flow<ApiState<List<BossPartySchedule>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.getBossPartySchedules(accessToken, year, month, day)
        val bossPartySchedules = response.map { it.toDomain() }

        emit(ApiState.Success(bossPartySchedules))
    }.handleApiError()

    override suspend fun getBossPartyAlarmTimes(bossPartyId: Long): Flow<ApiState<List<BossPartyAlarmTime>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.getBossPartyAlarmTimes(
            accessToken = accessToken,
            bossPartyId = bossPartyId
        )
        val alarmTimes = response.map { it.toDomain() }

        emit(ApiState.Success(alarmTimes))
    }.handleApiError()

    override suspend fun updateAlarmSetting(
        bossPartyId: Long
    ): Flow<ApiState<Boolean>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.updateAlarmSetting(
            accessToken = accessToken,
            bossPartyId = bossPartyId
        )

        emit(ApiState.Success(response))
    }.handleApiError()

    override suspend fun createBossAlarm(
        bossPartyId: Long,
        hour: Int,
        minute: Int,
        date: LocalDate,
        message: String
    ): Flow<ApiState<List<BossPartyAlarmTime>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.createBossAlarm(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            request = BossPartyAlarmTimeRequest(
                hour = hour,
                minute = minute,
                date = date,
                message = message
            )
        )
        val alarmTimes = response.map { it.toDomain() }

        emit(ApiState.Success(alarmTimes, "알람 예약에 성공했어요."))
    }.handleApiError()

    override suspend fun updateBossAlarmPeriod(
        bossPartyId: Long,
        dayOfWeek: DayOfWeek?,
        hour: Int,
        minute: Int,
        message: String,
        isImmediateApply: Boolean
    ): Flow<ApiState<List<BossPartyAlarmTime>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.updateBossAlarmPeriod(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            request = BossPartyAlarmPeriodRequest(
                dayOfWeek = dayOfWeek,
                hour = hour,
                minute = minute,
                message = message,
                isImmediateApply = isImmediateApply
            )
        )
        val alarmTimes = response.map { it.toDomain() }

        emit(ApiState.Success(alarmTimes, "알람 주기 변경에 성공했어요."))
    }.handleApiError()

    override suspend fun deleteBossAlarm(
        bossPartyId: Long,
        alarmId: Long
    ): Flow<ApiState<List<BossPartyAlarmTime>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.deleteBossAlarm(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            alarmId = alarmId
        )
        val alarmTimes = response.map { it.toDomain() }

        emit(ApiState.Success(alarmTimes, "예약된 알람을 제거했어요."))
    }.handleApiError()

    override suspend fun inviteMember(
        bossPartyId: Long,
        characterId: Long
    ): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.inviteMember(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            characterId = characterId
        )

        emit(ApiState.Success(Unit, "파티원 초대에 성공했어요."))
    }.handleApiError()

    override suspend fun acceptInvitation(bossPartyId: Long): Flow<ApiState<Long>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.acceptInvitation(
            accessToken = accessToken,
            bossPartyId = bossPartyId
        )

        emit(ApiState.Success(response, "초대 수락에 성공했어요."))
    }.handleApiError()

    override suspend fun declineInvitation(bossPartyId: Long): Flow<ApiState<List<BossParty>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.declineInvitation(
            accessToken = accessToken,
            bossPartyId = bossPartyId
        )
        val bossParties = response.map { it.toDomain() }

        emit(ApiState.Success(bossParties, "초대를 거절했어요."))
    }.handleApiError()

    override suspend fun kickMember(
        bossPartyId: Long,
        characterId: Long
    ): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.kickMember(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            characterId = characterId
        )

        emit(ApiState.Success(Unit, "파티원 추방에 성공했어요."))
    }.handleApiError()

    override suspend fun leaveParty(bossPartyId: Long): Flow<ApiState<List<BossParty>>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.leaveParty(
            accessToken = accessToken,
            bossPartyId = bossPartyId
        )
        val bossParties = response.map { it.toDomain() }

        emit(ApiState.Success(bossParties, "파티를 나갔어요."))
    }.handleApiError()

    override suspend fun transferLeader(
        bossPartyId: Long,
        targetCharacterId: Long
    ): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.transferLeader(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            targetCharacterId = targetCharacterId
        )

        emit(ApiState.Success(Unit, "파티장 양도에 성공했어요."))
    }.handleApiError()

    override suspend fun getChatMessage(bossPartyId: Long, page: Int): Flow<ApiState<BossPartyChatHistory>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.getChatMessages(accessToken, bossPartyId, page)
        val messages = response.content.map { it.toDomain() }

        emit(ApiState.Success(
            BossPartyChatHistory(
                messages = messages,
                isLastPage = response.last
            )
        ))
    }.handleApiError()

    override suspend fun connect(partyId: String): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.connect(partyId, accessToken)

        emit(ApiState.Success(Unit))
    }.catch { e ->
        emit(ApiState.Error(e.message ?: "채팅방 연결 중 오류가 발생했어요."))
    }

    override suspend fun updateChatAlarmSetting(bossPartyId: Long): Flow<ApiState<Boolean>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.updateChatAlarmSetting(
            accessToken = accessToken,
            bossPartyId = bossPartyId
        )

        emit(ApiState.Success(response))
    }.handleApiError()

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
                emit(ApiState.Error(e.message ?: "메시지 수신에 오류가 있어요."))
            }
    }

    override suspend fun sendMessage(partyId: Long, message: BossPartyChat): ApiState<Unit> {
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
            Napier.d(e.message ?: "메시지 전송 실패")
            ApiState.Error(e.message ?: "메시지 전송에 실패했어요.")
        }
    }

    override suspend fun hideMessage(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.hideMessage(accessToken, bossPartyId, chatId)

        emit(ApiState.Success(Unit, "메시지를 가렸어요."))
    }.handleApiError()

    override suspend fun deleteMessage(bossPartyId: Long, chatId: Long): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        dataSource.deleteMessage(accessToken, bossPartyId, chatId)

        emit(ApiState.Success(Unit, "메시지를 삭제했어요."))
    }.handleApiError()

    override suspend fun disconnect() = dataSource.disconnect()

    override suspend fun getBoardPosts(
        bossPartyId: Long,
        page: Int
    ): Flow<ApiState<BossPartyBoardHistory>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.getBoardPosts(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            page = page
        )
        val boards = response.content.map { it.toDomain() }

        emit(ApiState.Success(
            BossPartyBoardHistory(
                boards = boards,
                isLastPage = response.last
            )
        ))
    }.handleApiError()

    override suspend fun createBoardPost(
        bossPartyId: Long,
        content: String, // 게시글 텍스트 내용
        images: List<ByteArray> // 이미지 데이터들
    ): Flow<ApiState<BossPartyBoard>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val contentJson = Json.encodeToString(mapOf("content" to content))
        val response = dataSource.createBoardPost(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            contentJson = contentJson,
            imageFiles = images
        )
        val board = response.toDomain()

        emit(ApiState.Success(board, "게시글 작성에 성공했어요."))
    }.handleApiError()

    override suspend fun toggleBoardLike(
        bossPartyId: Long,
        boardId: Long,
        likeType: String
    ): Flow<ApiState<BossPartyBoard>> = flow {
        emit(ApiState.Loading)

        val accessToken = dataStore.accessToken.first()
        val response = dataSource.toggleBoardLike(
            accessToken = accessToken,
            bossPartyId = bossPartyId,
            boardId = boardId,
            likeType = likeType,
        )
        val board = response.toDomain()

        emit(ApiState.Success(board))
    }.handleApiError()
}