package com.sixclassguys.maplecalendar.domain.usecase

import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.repository.BossRepository
import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType

class SendBossChatUseCase(
    private val repository: BossRepository
) {

    suspend operator fun invoke(partyId: Long, content: String): ApiState<Unit> {
        if (content.isBlank()) {
            return ApiState.Error("메시지를 입력해주세요.")
        }

        // 💡 실제 전송 시에는 서버가 발신자를 토큰으로 식별하므로 
        // 클라이언트는 내용(content)과 타입만 중요합니다.
        val chatModel = BossPartyChat(
            id = 0L, // 서버에서 생성할 값이므로 임시값
            content = content,
            senderId = 0L, // 서버 토큰 식별
            senderName = "",
            senderImage = "",
            createdAt = "", // 서버 시간 기준
            isMine = true,
            messageType = BossPartyChatMessageType.TEXT,
            senderWorld = "",
            isDeleted = false
        )

        return repository.sendMessage(partyId, chatModel)
    }
}