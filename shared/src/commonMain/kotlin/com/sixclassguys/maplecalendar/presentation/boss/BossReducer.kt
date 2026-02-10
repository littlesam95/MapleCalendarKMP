package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType
import com.sixclassguys.maplecalendar.util.BossPartyChatUiItem
import io.github.aakira.napier.Napier

class BossReducer {

    private fun transformToUiItems(chats: List<BossPartyChat>): List<BossPartyChatUiItem> {
        if (chats.isEmpty()) return emptyList()

        val uiItems = mutableListOf<BossPartyChatUiItem>()

        chats.forEachIndexed { index, currentChat ->
            // 1. 이전 메시지(시간상 더 미래)와 비교하여 프로필 노출 결정
            // reverseLayout이므로 index - 1 이 시간상 바로 다음 메시지입니다.
            val nextMessageInTime = chats.getOrNull(index - 1)

            val isSameUserAsNext = nextMessageInTime != null &&
                    nextMessageInTime.senderId == currentChat.senderId &&
                    nextMessageInTime.messageType == currentChat.messageType &&
                    isSameDay(nextMessageInTime.createdAt, currentChat.createdAt)

            // 2. 현재 메시지 추가 (미래 메시지가 나랑 같은 사람이면 내 프로필은 숨김)
            uiItems.add(
                BossPartyChatUiItem.Message(
                    chat = currentChat,
                    showProfile = !isSameUserAsNext && !currentChat.isMine &&
                            currentChat.messageType !in listOf(BossPartyChatMessageType.ENTER, BossPartyChatMessageType.LEAVE),
                    showTime = true
                )
            )

            // 3. 날짜 구분선 (과거 메시지와 날짜가 다르면 추가)
            val previousMessageInTime = chats.getOrNull(index + 1)
            if (previousMessageInTime == null || !isSameDay(currentChat.createdAt, previousMessageInTime.createdAt)) {
                uiItems.add(BossPartyChatUiItem.DateDivider(currentChat.createdAt))
            }
        }
        return uiItems
    }

    // 헬퍼 함수 예시 (기존 프로젝트의 날짜 라이브러리에 맞춰 구현)
    private fun isSameDay(date1: String, date2: String): Boolean {
        // String 형태의 createdAt을 비교 (예: "2024-05-20" 부분만 잘라서 비교)
        return date1.take(10) == date2.take(10)
    }

    fun reduce(currentState: BossUiState, intent: BossIntent): BossUiState = when (intent) {
        is BossIntent.FetchBossParties -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.FetchBossPartiesSuccess -> {
            currentState.copy(
                isLoading = false,
                bossParties = intent.bossParties
            )
        }

        is BossIntent.FetchBossPartiesFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.FetchCharacters -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.FetchCharactersSuccess -> {
            val characters: List<Pair<String, CharacterSummary>> = intent.characters.values // 1. 월드 그룹 Map들만 추출
                .flatMap { worldMap ->
                    // 2. 각 월드 그룹 내부의 worldName(Key)과 characters(Value) 순회
                    worldMap.flatMap { (worldName, characters) ->
                        // 3. 캐릭터 리스트를 Pair(월드 이름, 캐릭터)로 변환
                        characters.map { character -> worldName to character }
                    }
                }
                .sortedByDescending { it.second.characterLevel } // 4. 레벨(Pair의 second) 기준 역순 정렬

            currentState.copy(
                isLoading = false,
                characters = characters
            )
        }

        is BossIntent.FetchCharactersFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.SelectRegion -> {
            currentState.copy(
                selectedRegion = intent.selectedRegion,
            )
        }

        is BossIntent.SelectBoss -> {
            currentState.copy(
                selectedBoss = intent.selectedBoss,
                selectedBossDifficulty = null
            )
        }

        is BossIntent.SelectBossDifficulty -> {
            currentState.copy(
                selectedBossDifficulty = intent.selectedBossDifficulty,
                showCreateDialog = true,
                bossPartyCreateCharacter = currentState.characters.firstOrNull()?.second
            )
        }

        is BossIntent.DismissBossPartyCreateDialog -> {
            currentState.copy(
                selectedBossDifficulty = null,
                showCreateDialog = false
            )
        }

        is BossIntent.SelectBossPartyCharacter -> {
            currentState.copy(
                bossPartyCreateCharacter = intent.character
            )
        }

        is BossIntent.UpdateBossPartyTitle -> {
            currentState.copy(
                bossPartyCreateTitle = intent.title
            )
        }

        is BossIntent.UpdateBossPartyDescription -> {
            currentState.copy(
                bossPartyCreateDescription = intent.description
            )
        }

        is BossIntent.CreateBossParty -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.CreateBossPartySuccess -> {
            currentState.copy(
                isLoading = true,
                showCreateDialog = false
            )
        }

        is BossIntent.CreateBossPartyFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.FetchBossPartyDetail -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.FetchBossPartyDetailSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedBossParty = intent.bossPartyDetail,
                bossPartyAlarmTimes = intent.bossPartyDetail.alarms,
                selectedDayOfWeek = intent.bossPartyDetail.alarmDayOfWeek
            )
        }

        is BossIntent.FetchBossPartyDetailFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.ShowAlarmCreateDialog -> {
            currentState.copy(
                showBossAlarmDialog = true
            )
        }

        is BossIntent.DismissAlarmCreateDialog -> {
            currentState.copy(
                showBossAlarmDialog = false
            )
        }

        is BossIntent.UpdateAlarmTimeHour -> {
            currentState.copy(
                selectedHour = intent.hour
            )
        }

        is BossIntent.UpdateAlarmTimeMinute -> {
            currentState.copy(
                selectedMinute = intent.minute
            )
        }

        is BossIntent.UpdateAlarmMessage -> {
            currentState.copy(
                alarmMessage = intent.message
            )
        }

        is BossIntent.UpdateAlarmTimeSelectMode -> {
            currentState.copy(
                selectedAlarmDate = intent.date
            )
        }

        is BossIntent.CreateBossPartyAlarm -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.CreateBossPartyAlarmSuccess -> {
            currentState.copy(
                isLoading = false,
                bossPartyAlarmTimes = intent.bossPartyAlarmTimes,
                showBossAlarmDialog = false,
                selectedAlarmDate = null,
                selectedDayOfWeek = null,
                isImmediatelyAlarm = false,
                selectedHour = "",
                selectedMinute = "",
                alarmMessage = "",
            )
        }

        is BossIntent.CreateBossPartyAlarmFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.UpdateAlarmTimePeriodMode -> {
            currentState.copy(
                selectedDayOfWeek = intent.dayOfWeek
            )
        }

        is BossIntent.UpdateThisWeekPeriodMode -> {
            currentState.copy(
                isImmediatelyAlarm = intent.isImmediatelyAlarm
            )
        }

        is BossIntent.UpdateBossPartyAlarmPeriod -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.UpdateBossPartyAlarmPeriodSuccess -> {
            currentState.copy(
                isLoading = false,
                bossPartyAlarmTimes = intent.bossPartyAlarmTimes,
                showBossAlarmDialog = false,
                selectedAlarmDate = null,
                selectedDayOfWeek = null,
                isImmediatelyAlarm = false,
                selectedHour = "",
                selectedMinute = "",
                alarmMessage = "",
            )
        }

        is BossIntent.UpdateBossPartyAlarmPeriodFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.DeleteBossPartyAlarm -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.DeleteBossPartyAlarmSuccess -> {
            currentState.copy(
                isLoading = false,
                bossPartyAlarmTimes = intent.bossPartyAlarmTimes
            )
        }

        is BossIntent.DeleteBossPartyAlarmFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.ToggleBossPartyAlarm -> {
            currentState
        }

        is BossIntent.ConnectBossPartyChat -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.ReceiveRealTimeChat -> {
            val newMessage = intent.bossPartyChat

            // 💡 1. 기존 리스트에서 새 메시지 ID와 같은 녀석을 완전히 필터링
            val filteredList = currentState.bossPartyChats.filterNot { it.id == newMessage.id }

            // 💡 2. 새 메시지를 맨 앞에 추가 (순서 보장)
            val updatedList = listOf(newMessage) + filteredList
            Napier.d("BossReducer - ReceiveRealTimeChat: $updatedList")

            currentState.copy(
                isLoading = false,
                bossPartyChats = updatedList,
                bossPartyChatUiItems = transformToUiItems(updatedList),
            )
        }

        is BossIntent.ConnectBossPartyChatFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
       }

        is BossIntent.UpdateBossPartyChatMessage -> {
            currentState.copy(
                bossPartyChatMessage = intent.bossPartyChatMessage
            )
        }

        is BossIntent.SendBossPartyChatMessage -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.SendBossPartyChatMessageSuccess -> {
            currentState.copy(
                isLoading = false,
                bossPartyChatMessage = ""
            )
        }

        is BossIntent.SendBossPartyChatMessageFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.FetchBossPartyChatHistory -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.FetchBossPartyChatHistorySuccess -> {
            val history = intent.bossPartyChatHistory

            // 💡 핵심: 기존 데이터와 새 데이터를 합친 후, ID를 기준으로 중복 제거
            // distinctBy는 먼저 나타나는 요소를 유지하므로,
            // 새로운 데이터(history)를 앞에 두거나 리스트를 합친 후 정렬/필터링합니다.
            val combinedChats = (currentState.bossPartyChats + history.messages)
                .distinctBy { it.id } // ID가 중복되면 뒤에 오는 데이터는 무시함
                .sortedByDescending { it.id } // ID 내림차순 정렬 (최신이 위로)

            currentState.copy(
                isLoading = false,
                bossPartyChats = combinedChats,
                bossPartyChatUiItems = transformToUiItems(combinedChats),
                isBossPartyChatLastPage = history.isLastPage,
                bossPartyChatPage = currentState.bossPartyChatPage + 1
            )
        }

        is BossIntent.FetchBossPartyChatHistoryFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.DeleteBossPartyChatMessage -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.DeleteBossPartyChatMessageSuccess -> {
            val newBossChats = currentState.bossPartyChats.map { chat ->
                if (chat.id == intent.bossPartyChatId) {
                    chat.copy(
                        content = "이 메시지는 삭제되었어요.",
                        isDeleted = true
                    )
                } else {
                    chat
                }
            }
            currentState.copy(
                isLoading = false,
                bossPartyChats = newBossChats,
                bossPartyChatUiItems = transformToUiItems(newBossChats)
            )
        }

        is BossIntent.DeleteBossPartyChatMessageFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.DisconnectBossPartyChat -> {
            currentState
        }

        is BossIntent.SelectBossPartyDetailMenu -> {
            currentState.copy(
                selectedBossPartyDetailMenu = intent.selectedBossPartyDetailMenu
            )
        }
    }
}