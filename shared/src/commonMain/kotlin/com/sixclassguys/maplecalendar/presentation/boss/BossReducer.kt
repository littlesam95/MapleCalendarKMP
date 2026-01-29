package com.sixclassguys.maplecalendar.presentation.boss

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
    }
}