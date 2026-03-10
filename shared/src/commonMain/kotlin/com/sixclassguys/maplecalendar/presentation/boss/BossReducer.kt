package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossPartyChatMessageType
import com.sixclassguys.maplecalendar.util.BossPartyChatUiItem
import com.sixclassguys.maplecalendar.util.BossPartyTab
import com.sixclassguys.maplecalendar.util.JoinStatus
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlin.String
import kotlin.collections.List

class BossReducer {

    private fun transformToUiItems(chats: List<BossPartyChat>): List<BossPartyChatUiItem> {
        if (chats.isEmpty()) return emptyList()

        val uiItems = mutableListOf<BossPartyChatUiItem>()
        // 비교해야 할 사용자 메시지 타입 정의
        val userMessageTypes = listOf(
            BossPartyChatMessageType.TEXT,
            BossPartyChatMessageType.IMAGE,
            BossPartyChatMessageType.BOTH
        )

        chats.forEachIndexed { index, currentChat ->
            // 1. 나보다 먼저 보낸 메시지(index + 1)를 가져옴
            val previousMessageInTime = chats.getOrNull(index + 1)

            // 2. 연속성 체크 조건
            // - 이전 메시지가 존재하고
            // - 보낸 사람이 같으며
            // - 둘 다 사용자 메시지 타입(TEXT, IMAGE, BOTH)이고
            // - 날짜가 같을 때
            val isSameUserAsPrevious = previousMessageInTime != null &&
                    previousMessageInTime.senderId == currentChat.senderId &&
                    currentChat.messageType in userMessageTypes &&
                    previousMessageInTime.messageType in userMessageTypes &&
                    isSameDay(previousMessageInTime.createdAt, currentChat.createdAt)

            // 3. 프로필 노출 결정
            // - 나보다 먼저 보낸 사람이 나랑 다른 사람이거나 다른 타입일 때
            // - 그리고 내 메시지가 사용자 메시지 타입일 때 프사 노출
            val shouldShowProfile = !isSameUserAsPrevious &&
                    !currentChat.isMine &&
                    currentChat.messageType in userMessageTypes

            if (shouldShowProfile) {
                Napier.d("첫 채팅: ${currentChat}")
            }

            uiItems.add(
                BossPartyChatUiItem.Message(
                    chat = currentChat,
                    showProfile = shouldShowProfile,
                    showTime = true
                )
            )

            // 4. 날짜 구분선 (이전 메시지가 없거나 날짜가 바뀌었을 때)
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
        is BossIntent.FetchGlobalAlarmStatus -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.FetchGlobalAlarmStatusSuccess -> {
            currentState.copy(
                isLoading = false,
                isGlobalAlarmEnabled = intent.isEnabled
            )
        }

        is BossIntent.FetchGlobalAlarmStatusFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.FetchBossParties -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartiesSuccess -> {
            currentState.copy(
                isLoading = false,
                bossParties = intent.bossParties.filter { it.joinStatus == JoinStatus.ACCEPTED },
                bossPartiesInvited = intent.bossParties.filter { it.joinStatus == JoinStatus.INVITED },
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartiesFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.ShowBossPartyInvitationDialog -> {
            currentState.copy(
                showBossInvitationDialog = true
            )
        }

        is BossIntent.DismissBossPartyInvitationDialog -> {
            currentState.copy(
                showBossInvitationDialog = false
            )
        }

        is BossIntent.AcceptBossPartyInvitation -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.AcceptBossPartyInvitationSuccess -> {
            currentState.copy(
                isLoading = false,
                showBossInvitationDialog = false
            )
        }

        is BossIntent.AcceptBossPartyInvitationFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.DeclineBossPartyInvitation -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.DeclineBossPartyInvitationSuccess -> {
            currentState.copy(
                isLoading = false,
                bossParties = intent.bossParties.filter { it.joinStatus == JoinStatus.ACCEPTED },
                bossPartiesInvited = intent.bossParties.filter { it.joinStatus == JoinStatus.INVITED },
            )
        }

        is BossIntent.DeclineBossPartyInvitationFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.FetchCharacters -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
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
                characters = characters,
                createdPartyId = null
            )
        }

        is BossIntent.FetchCharactersFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.InitBossPartyCreate -> {
            currentState.copy(
                isLoading = false,
                selectedRegion = "그란디스",
                selectedBoss = Boss.SEREN,
                selectedBossDifficulty = null,
                showCreateDialog = false,
                bossPartyCreateCharacter = currentState.characters.firstOrNull()?.second,
                bossPartyCreateTitle = "",
                bossPartyCreateDescription = "",
                createdPartyId = null,
            )
        }

        is BossIntent.SelectRegion -> {
            currentState.copy(
                isLoading = false,
                selectedRegion = intent.selectedRegion,
                createdPartyId = null
            )
        }

        is BossIntent.SelectBoss -> {
            currentState.copy(
                isLoading = false,
                selectedBoss = intent.selectedBoss,
                selectedBossDifficulty = null,
                createdPartyId = null
            )
        }

        is BossIntent.SelectBossDifficulty -> {
            currentState.copy(
                isLoading = false,
                selectedBossDifficulty = intent.selectedBossDifficulty,
                showCreateDialog = true,
                bossPartyCreateCharacter = currentState.characters.firstOrNull()?.second,
                bossPartyCreateTitle = "",
                bossPartyCreateDescription = "",
                createdPartyId = null
            )
        }

        is BossIntent.DismissBossPartyCreateDialog -> {
            currentState.copy(
                isLoading = false,
                selectedBossDifficulty = null,
                showCreateDialog = false,
                createdPartyId = null
            )
        }

        is BossIntent.SelectBossPartyCharacter -> {
            currentState.copy(
                isLoading = false,
                bossPartyCreateCharacter = intent.character,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateBossPartyTitle -> {
            currentState.copy(
                isLoading = false,
                bossPartyCreateTitle = intent.title,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateBossPartyDescription -> {
            currentState.copy(
                isLoading = false,
                bossPartyCreateDescription = intent.description,
                createdPartyId = null
            )
        }

        is BossIntent.CreateBossParty -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.CreateBossPartySuccess -> {
            currentState.copy(
                isLoading = true,
                showCreateDialog = false,
                createdPartyId = intent.bossPartyId,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.CreateBossPartyFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyDetail -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyDetailSuccess -> {
            currentState.copy(
                isLoading = true,
                selectedBossParty = intent.bossPartyDetail,
                selectedBossPartyDetailMenu = BossPartyTab.ALARM,
                isBossPartyDetailAlarmOn = intent.bossPartyDetail.isPartyAlarmEnabled,
                bossPartyAlarmTimes = intent.bossPartyDetail.alarms,
                selectedDayOfWeek = intent.bossPartyDetail.alarmDayOfWeek,
                isBossPartyChatAlarmOn = intent.bossPartyDetail.isChatAlarmEnabled,
                bossPartyChats = emptyList(),
                bossPartyChatUiItems = emptyList(),
                bossPartyChatPage = 0,
                isBossPartyChatLastPage = false,
                bossPartyChatMessage = "",
                bossPartyBoards = emptyList(),
                bossPartyBoardPage = 0,
                isBossPartyBoardLastPage = false,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyDetailFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.RefreshBossPartyDetail -> {
            currentState.copy(
                isLoading = true
            )
        }

        is BossIntent.RefreshBossPartyDetailSuccess -> {
            currentState.copy(
                isLoading = false,
                selectedBossParty = intent.bossPartyDetail,
                selectedBossPartyDetailMenu = BossPartyTab.ALARM,
                isBossPartyDetailAlarmOn = intent.bossPartyDetail.isPartyAlarmEnabled,
                bossPartyAlarmTimes = intent.bossPartyDetail.alarms,
                selectedDayOfWeek = intent.bossPartyDetail.alarmDayOfWeek,
                isBossPartyChatAlarmOn = intent.bossPartyDetail.isChatAlarmEnabled,
                createdPartyId = null
            )
        }

        is BossIntent.RefreshBossPartyDetailFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message
            )
        }

        is BossIntent.ShowAlarmCreateDialog -> {
            currentState.copy(
                isLoading = false,
                showBossAlarmDialog = true,
                createdPartyId = null
            )
        }

        is BossIntent.DismissAlarmCreateDialog -> {
            currentState.copy(
                isLoading = false,
                showBossAlarmDialog = false,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateAlarmTimeHour -> {
            currentState.copy(
                isLoading = false,
                selectedHour = intent.hour,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateAlarmTimeMinute -> {
            currentState.copy(
                isLoading = false,
                selectedMinute = intent.minute,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateAlarmMessage -> {
            currentState.copy(
                isLoading = false,
                alarmMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateAlarmTimeSelectMode -> {
            currentState.copy(
                isLoading = false,
                selectedAlarmDate = intent.date,
                createdPartyId = null
            )
        }

        is BossIntent.CreateBossPartyAlarm -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
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
                createdPartyId = null,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.CreateBossPartyAlarmFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateAlarmTimePeriodMode -> {
            currentState.copy(
                isLoading = false,
                selectedDayOfWeek = intent.dayOfWeek,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateThisWeekPeriodMode -> {
            currentState.copy(
                isLoading = false,
                isImmediatelyAlarm = intent.isImmediatelyAlarm,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateBossPartyAlarmPeriod -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
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
                createdPartyId = null,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.UpdateBossPartyAlarmPeriodFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.DeleteBossPartyAlarm -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.DeleteBossPartyAlarmSuccess -> {
            currentState.copy(
                isLoading = false,
                bossPartyAlarmTimes = intent.bossPartyAlarmTimes,
                createdPartyId = null,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.DeleteBossPartyAlarmFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.ToggleBossPartyAlarm -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.ToggleBossPartyAlarmSuccess -> {
            currentState.copy(
                isLoading = false,
                isBossPartyDetailAlarmOn = intent.enabled,
                createdPartyId = null
            )
        }

        is BossIntent.ToggleBossPartyAlarmFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.ShowCharacterInviteDialog -> {
            currentState.copy(
                showCharacterInvitationDialog = true
            )
        }

        is BossIntent.DismissCharacterInviteDialog -> {
            currentState.copy(
                showCharacterInvitationDialog = false,
                searchKeyword = ""
            )
        }

        is BossIntent.SearchCharacters -> {
            currentState.copy(
                isLoading = true,
                searchKeyword = intent.name,
                createdPartyId = null
            )
        }

        is BossIntent.SearchCharactersSuccess -> {
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
                searchCharacters = characters,
                createdPartyId = null
            )
        }

        is BossIntent.SearchCharactersFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.InviteBossPartyMember -> {
            currentState.copy(
                isMemberInviteLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.InviteBossPartyMemberSuccess -> {
            currentState.copy(
                isMemberInviteLoading = false,
                showCharacterInvitationDialog = false,
                searchKeyword = "",
                createdPartyId = null,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.InviteBossPartyMemberFailed -> {
            currentState.copy(
                isMemberInviteLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.KickBossPartyMember -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.KickBossPartyMemberSuccess -> {
            currentState.copy(
                isLoading = false,
                createdPartyId = null,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.KickBossPartyMemberFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.LeaveBossParty -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.LeaveBossPartySuccess -> {
            currentState.copy(
                isLoading = false,
                bossParties = intent.newBossParties.filter { it.joinStatus == JoinStatus.ACCEPTED },
                bossPartiesInvited = intent.newBossParties.filter { it.joinStatus == JoinStatus.INVITED },
                createdPartyId = null
            )
        }

        is BossIntent.LeaveBossPartyFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.TransferBossPartyLeader -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.TransferBossPartyLeaderSuccess -> {
            currentState.copy(
                isLoading = false,
                createdPartyId = null,
                successMessage = intent.successMessage
            )
        }

        is BossIntent.TransferBossPartyLeaderFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.ConnectBossPartyChat -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.ToggleBossPartyChatAlarm -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.ToggleBossPartyChatAlarmSuccess -> {
            currentState.copy(
                isLoading = false,
                isBossPartyChatAlarmOn = intent.enabled,
                createdPartyId = null
            )
        }

        is BossIntent.ToggleBossPartyChatAlarmFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.ConnectBossPartyChatSuccess -> {
            currentState.copy(
                isLoading = false,
                createdPartyId = null
            )
        }

        is BossIntent.ReceiveRealTimeChat -> {
            val newMessage = intent.bossPartyChat
            Napier.d("ReceiveRealTimeChat: ${newMessage}")

            // 💡 1. 기존 리스트에서 새 메시지 ID와 같은 녀석을 완전히 필터링
            val filteredList = currentState.bossPartyChats.filterNot { it.id == newMessage.id }

            // 💡 2. 새 메시지를 맨 앞에 추가 (순서 보장)
            val updatedList = (listOf(newMessage) + filteredList)
                .distinctBy { it.id } // ID가 중복되면 뒤에 오는 데이터는 무시함
                .sortedByDescending { it.id } // ID 내림차순 정렬 (최신이 위로)

            currentState.copy(
                isLoading = false,
                bossPartyChats = updatedList,
                bossPartyChatUiItems = transformToUiItems(updatedList),
                createdPartyId = null
            )
        }

        is BossIntent.ConnectBossPartyChatFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
       }

        is BossIntent.UpdateBossPartyChatMessage -> {
            currentState.copy(
                isLoading = false,
                bossPartyChatMessage = intent.bossPartyChatMessage,
                createdPartyId = null
            )
        }

        is BossIntent.SendBossPartyChatMessage -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.SendBossPartyChatMessageSuccess -> {
            currentState.copy(
                isLoading = false,
                bossPartyChatMessage = "",
                createdPartyId = null
            )
        }

        is BossIntent.SendBossPartyChatMessageFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.ShowBossPartyChatReportDialog -> {
            currentState.copy(
                showBossPartyChatReport = true,
                selectBossPartyChatToReport = intent.chat,
                createdPartyId = null
            )
        }

        is BossIntent.DismissBossPartyChatReportDialog -> {
            currentState.copy(
                showBossPartyChatReport = false,
                selectBossPartyChatToReport = null,
                createdPartyId = null
            )
        }

        is BossIntent.ReportBossPartyChatMessage -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.ReportBossPartyChatMessageSuccess -> {
            currentState.copy(
                isLoading = false,
                showBossPartyChatReport = false,
                createdPartyId = null
            )
        }

        is BossIntent.ReportBossPartyChatMessageFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.RefreshBossPartyChat -> {
            currentState.copy(
                isLoading = true,
                bossPartyChats = emptyList(),      // 기존 리스트 초기화
                bossPartyChatUiItems = emptyList(),
                bossPartyChatPage = 0,             // 페이지 번호 초기화
                isBossPartyChatLastPage = false,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyChatHistory -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
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

            val ew = transformToUiItems(combinedChats)
            Napier.d("Chats: $ew")

            currentState.copy(
                isLoading = false,
                bossPartyChats = combinedChats,
                bossPartyChatUiItems = transformToUiItems(combinedChats),
                isBossPartyChatLastPage = history.isLastPage,
                bossPartyChatPage = currentState.bossPartyChatPage + 1,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyChatHistoryFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.HideBossPartyChatMessage -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.HideBossPartyChatMessageSuccess -> {
            val newBossChats = currentState.bossPartyChats
            currentState.copy(
                isLoading = false,
                bossPartyChats = newBossChats,
                bossPartyChatUiItems = transformToUiItems(newBossChats),
                createdPartyId = null
            )
        }

        is BossIntent.HideBossPartyChatMessageFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.DeleteBossPartyChatMessage -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
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
                bossPartyChatUiItems = transformToUiItems(newBossChats),
                createdPartyId = null
            )
        }

        is BossIntent.DeleteBossPartyChatMessageFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.DisconnectBossPartyChat -> {
            Napier.d("연결끊겼다")
            currentState.copy(
                isLoading = false,
                createdPartyId = null
            )
        }

        is BossIntent.ShowBossPartyBoardDialog -> {
            currentState.copy(
                showBossPartyBoardDialog = true
            )
        }

        is BossIntent.DismissBossPartyBoardDialog -> {
            currentState.copy(
                showBossPartyBoardDialog = false,
                uploadImage = emptyList(),
                uploadComment = "",
            )
        }

        is BossIntent.FetchBossPartyBoardHistory -> {
            Napier.d("어")
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyBoardHistorySuccess -> {
            val history = intent.bossPartyBoardHistory
            Napier.d("History: $history")
            val combinedBoards = (currentState.bossPartyBoards + history.boards)
                .distinctBy { it.id }
                .sortedByDescending { it.id }

            currentState.copy(
                isLoading = false,
                bossPartyBoards = combinedBoards,
                isBossPartyBoardLastPage = history.isLastPage,
                bossPartyBoardPage = currentState.bossPartyBoardPage + 1,
                createdPartyId = null
            )
        }

        is BossIntent.FetchBossPartyBoardHistoryFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.UpdateBossPartyBoardImage -> {
            val newImages = mutableListOf<ByteArray>()
            if (intent.image != null) {
                newImages.add(intent.image)
            }

            currentState.copy(
                isLoading = false,
                uploadImage = newImages
            )
        }

        is BossIntent.UpdateBossPartyBoardComment -> {
            currentState.copy(
                isLoading = false,
                uploadComment = intent.comment
            )
        }

        is BossIntent.SubmitBossPartyBoard -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.SubmitBossPartyBoardSuccess -> {
            currentState.copy(
                isLoading = true,
                showBossPartyBoardDialog = false,
                bossPartyBoards = emptyList(),      // 🚀 리스트 비우기
                bossPartyBoardPage = 0,             // 🚀 페이지 초기화
                isBossPartyBoardLastPage = false,   // 마지막 페이지 여부 초기화
                uploadImage = emptyList(),        // 입력 데이터 초기화
                uploadComment = "",
                createdPartyId = null,
                uploadSuccessEvent = Clock.System.now().toEpochMilliseconds(),
                successMessage = intent.successMessage
            )
        }

        is BossIntent.SubmitBossPartyBoardFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.LikeBossPartyBoardPost -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.LikeBossPartyBoardPostSuccess -> {
            val newBoards = currentState.bossPartyBoards.map { board ->
                if (board.id == intent.bossPartyBoard.id) intent.bossPartyBoard else board
            }

            currentState.copy(
                isLoading = false,
                bossPartyBoards = newBoards,
                createdPartyId = null
            )
        }

        is BossIntent.LikeBossPartyBoardPostFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.DislikeBossPartyBoardPost -> {
            currentState.copy(
                isLoading = true,
                createdPartyId = null
            )
        }

        is BossIntent.DislikeBossPartyBoardPostSuccess -> {
            val newBoards = currentState.bossPartyBoards.map { board ->
                if (board.id == intent.bossPartyBoard.id) intent.bossPartyBoard else board
            }

            currentState.copy(
                isLoading = false,
                bossPartyBoards = newBoards,
                createdPartyId = null
            )
        }

        is BossIntent.DislikeBossPartyBoardFailed -> {
            currentState.copy(
                isLoading = false,
                errorMessage = intent.message,
                createdPartyId = null
            )
        }

        is BossIntent.SelectBossPartyDetailMenu -> {
            currentState.copy(
                isLoading = false,
                selectedBossPartyDetailMenu = intent.selectedBossPartyDetailMenu,
                createdPartyId = null
            )
        }

        is BossIntent.InitMessage -> {
            currentState.copy(
                isLoading = false,
                successMessage = null,
                errorMessage = null
            )
        }
    }
}