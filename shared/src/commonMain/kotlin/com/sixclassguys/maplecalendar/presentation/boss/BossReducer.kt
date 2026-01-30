package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossPartyAlbum
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary

class BossReducer {

    fun reduce(currentState: BossUiState, intent: BossIntent): BossUiState = when (intent) {
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
            val chats = listOf(
                BossPartyChat(
                    characterSummary = characters[0].second,
                    content = "파장님 계속 그렇게 하실거면\n" +
                            "저 나갈게요 그냥",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[1].second,
                    content = "파장님 계속 그렇게 사세요\n" +
                            "스펙사기로 인벤 박제할게요",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[2].second,
                    content = "좆까",
                    isMine = true
                ),
                BossPartyChat(
                    characterSummary = characters[0].second,
                    content = "파장님 계속 그렇게 하실거면\n" +
                            "저 나갈게요 그냥",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[1].second,
                    content = "파장님 계속 그렇게 사세요\n" +
                            "스펙사기로 인벤 박제할게요",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[2].second,
                    content = "니면상",
                    isMine = true
                ),
                BossPartyChat(
                    characterSummary = characters[0].second,
                    content = "파장님 계속 그렇게 하실거면\n" +
                            "저 나갈게요 그냥",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[1].second,
                    content = "파장님 계속 그렇게 사세요\n" +
                            "스펙사기로 인벤 박제할게요",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[0].second,
                    content = "파장님 계속 그렇게 하실거면\n" +
                            "저 나갈게요 그냥",
                    isMine = false
                ),
                BossPartyChat(
                    characterSummary = characters[1].second,
                    content = "파장님 계속 그렇게 사세요\n" +
                            "스펙사기로 인벤 박제할게요",
                    isMine = false
                )
            )
            val albums = listOf(
                BossPartyAlbum(
                    id = 1L,
                    imageUrl = "https://i.ytimg.com/vi/k2yeIH_kVGU/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLC5szSZMvHMUforEoZyI8b0TpnEdQ",
                    author = characters[0].second,
                    content = "앙 기모링 ㅋㅋ",
                    date = "2026-01-30",
                    likeCount = 0,
                    dislikeCount = 2
                ),
                BossPartyAlbum(
                    id = 2L,
                    imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS2lYWhtv1chNTZb8QFE4pzodnkGH874S8dUw&s",
                    author = characters[1].second,
                    content = "아아앙 ㅋㅋ",
                    date = "2026-01-29",
                    likeCount = 0,
                    dislikeCount = 2
                )
            )

            currentState.copy(
                isLoading = false,
                characters = characters,
                bossPartyMembers = characters,
                bossPartyChats = chats,
                bossPartyAlbums = albums
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

        is BossIntent.DismissDialog -> {
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

        is BossIntent.SelectBossPartyDetailMenu -> {
            currentState.copy(
                selectedBossPartyDetailMenu = intent.selectedBossPartyDetailMenu
            )
        }
    }
}