package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlarmTime
import com.sixclassguys.maplecalendar.domain.model.BossPartyAlbum
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.util.BossDifficulty
import com.sixclassguys.maplecalendar.util.BossPartyTab

data class BossUiState(
    val isLoading: Boolean = false,
    val bossParties: List<BossParty> = listOf(
        BossParty(
            id = 3L,
            title = "흉성 3인 ㄱㄱ",
            description = "ㅇㅇ",
            boss = Boss.RADIANTMALEFIC,
            difficulty = BossDifficulty.HARD,
            isPartyAlarmEnabled = false,
            isChatAlarmEnabled = false,
            createdAt = "2026-01-29",
            updatedAt = "2026-01-29"
        ),
        BossParty(
            id = 2L,
            title = "에오스 익세 3인",
            description = "에슝좍",
            boss = Boss.SEREN,
            difficulty = BossDifficulty.EXTREME,
            isPartyAlarmEnabled = false,
            isChatAlarmEnabled = false,
            createdAt = "2026-01-29",
            updatedAt = "2026-01-29"
        ),
        BossParty(
            id = 1L,
            title = "하적자 3인 가실분 9.5이상",
            description = "인게임 닉 강원기 << 귓\n" +
                    "본인 8.8임\n" +
                    "숙련자만 오셈",
            boss = Boss.THEFIRSTADVERSARY,
            difficulty = BossDifficulty.HARD,
            isPartyAlarmEnabled = false,
            isChatAlarmEnabled = false,
            createdAt = "2026-01-29",
            updatedAt = "2026-01-29"
        )
    ),
    val characters: List<Pair<String, CharacterSummary>> = emptyList(),
    val selectedRegion: String = "그란디스",
    val selectedBoss: Boss = Boss.SEREN,
    val selectedBossDifficulty: BossDifficulty? = null,
    val showCreateDialog: Boolean = false,
    val bossPartyCreateCharacter: CharacterSummary? = characters.firstOrNull()?.second,
    val bossPartyCreateTitle: String = "",
    val bossPartyCreateDescription: String = "",
    val selectedBossParty: BossParty? = BossParty(
        id = 1L,
        title = "하적자 3인 가실분 9.5이상",
        description = "인게임 닉 강원기 << 귓\n" +
                "본인 8.8임\n" +
                "숙련자만 오셈",
        boss = Boss.THEFIRSTADVERSARY,
        difficulty = BossDifficulty.HARD,
        isPartyAlarmEnabled = false,
        isChatAlarmEnabled = false,
        createdAt = "2026-01-29",
        updatedAt = "2026-01-29"
    ),
    val selectedBossPartyDetailMenu: BossPartyTab = BossPartyTab.ALARM,
    val isBossPartyDetailAlarmOn: Boolean = true,
    val bossPartyAlarmTimes: List<BossPartyAlarmTime> = listOf(
        BossPartyAlarmTime(
            date = "2026년 1월 31일 토요일",
            time = "19:00",
            message = "5분 내로 안 오면 추방함",
            isEnabled = true
        )
    ),
    val bossPartyMembers: List<Pair<String, CharacterSummary>> = emptyList(),
    val bossPartyChats: List<BossPartyChat> = emptyList(),
    val bossPartyChatMessage: String = "",
    val bossPartyAlbums: List<BossPartyAlbum> = emptyList(),
    val errorMessage: String? = null
)