package com.sixclassguys.maplecalendar.util

enum class Boss(
    val bossName: String,
    val region: String, // "그란디스", "아케인리버", "메이플 월드"
    val difficulties: List<BossDifficulty>,
    val reqLevels: List<Int>,
    val bossLevels: List<Int>,
    val memberCounts: List<Int>,
    val rewards: List<List<Int>>
) {

    // 그란디스
    SEREN(
        bossName = "선택받은 세렌",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD, BossDifficulty.EXTREME),
        reqLevels = listOf(260, 260, 260),
        bossLevels = listOf(270, 275, 280),
        memberCounts = listOf(6, 6, 6),
        rewards = listOf()
    ),
    KALOS(
        bossName = "감시자 칼로스",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.EASY, BossDifficulty.NORMAL, BossDifficulty.CHAOS, BossDifficulty.EXTREME),
        reqLevels = listOf(265, 265, 265, 265),
        bossLevels = listOf(270, 280, 285, 285),
        memberCounts = listOf(6, 6, 6, 6),
        rewards = listOf()
    ),
    THEFIRSTADVERSARY(
        bossName = "최초의 대적자",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.EASY, BossDifficulty.NORMAL, BossDifficulty.HARD, BossDifficulty.EXTREME),
        reqLevels = listOf(270, 270, 270, 270),
        bossLevels = listOf(270, 280, 285, 290),
        memberCounts = listOf(3, 3, 3, 3),
        rewards = listOf()
    ),
    KALING(
        bossName = "카링",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.EASY, BossDifficulty.NORMAL, BossDifficulty.HARD, BossDifficulty.EXTREME),
        reqLevels = listOf(275, 275, 275, 275),
        bossLevels = listOf(275, 285, 285, 285),
        memberCounts = listOf(6, 6, 6, 6),
        rewards = listOf()
    ),
    RADIANTMALEFIC(
        bossName = "찬란한 흉성",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(280, 280),
        bossLevels = listOf(280, 280),
        memberCounts = listOf(3, 3),
        rewards = listOf()
    ),
    LIMBO(
        bossName = "림보",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(285, 285),
        bossLevels = listOf(285, 285),
        memberCounts = listOf(3, 3),
        rewards = listOf()
    ),
    BALDRIX(
        bossName = "발드릭스",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(290, 290),
        bossLevels = listOf(290, 290),
        memberCounts = listOf(3, 3),
        rewards = listOf()
    ),
    /*
    JUPITER(
        bossName = "유피테르",
        region = "그란디스",
        difficulties = listOf(BossDifficulty.BossDifficulty.NORMAL, BossDifficulty.CHAOS, BossDifficulty.EXTREME),
        reqLevels = listOf(295, 295),
        bossLevels = listOf(),
        memberCounts = listOf(3, 3),
        rewards = listOf()
    )
    */

    // 아케인리버
    LUCID(
        bossName = "루시드",
        region = "아케인리버",
        difficulties = listOf(BossDifficulty.EASY, BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(220, 220, 220),
        bossLevels = listOf(230, 230, 230),
        memberCounts = listOf(6, 6, 6),
        rewards = listOf()
    ),
    WILL(
        bossName = "윌",
        region = "아케인리버",
        difficulties = listOf(BossDifficulty.EASY, BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(235, 235, 235),
        bossLevels = listOf(235, 250, 250),
        memberCounts = listOf(6, 6, 6),
        rewards = listOf()
    ),
    DUSK(
        bossName = "더스크",
        region = "아케인리버",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.CHAOS),
        reqLevels = listOf(245, 245),
        bossLevels = listOf(255, 255),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),
    VERUSHILLA(
        bossName = "진 힐라",
        region = "아케인리버",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(250, 250),
        bossLevels = listOf(250, 250),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),
    DUNKEL(
        bossName = "듄켈",
        region = "아케인리버",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(255, 255),
        bossLevels = listOf(265, 265),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),
    BLACKMAGE(
        bossName = "검은 마법사",
        region = "아케인리버",
        difficulties = listOf(BossDifficulty.HARD, BossDifficulty.EXTREME),
        reqLevels = listOf(255, 255),
        bossLevels = listOf(275, 280),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),

    // 메이플 월드
    ZAKUM(
        bossName = "자쿰",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(90),
        bossLevels = listOf(180),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    MAGNUS(
        bossName = "매그너스",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.HARD),
        reqLevels = listOf(175),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    HILLA(
        bossName = "힐라",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.HARD),
        reqLevels = listOf(170),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    LOADBALROG(
        bossName = "파풀라투스",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(190),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    VONBON(
        bossName = "반반",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(180),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    PIERRE(
        bossName = "피에르",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(180),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    BLOODYQUEEN(
        bossName = "블러디 퀸",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(180),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    VELLUM(
        bossName = "벨룸",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(180),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    PINKBEAN(
        bossName = "핑크빈",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.CHAOS),
        reqLevels = listOf(170),
        bossLevels = listOf(190),
        memberCounts = listOf(6),
        rewards = listOf()
    ),
    CYGNUS(
        bossName = "시그너스",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.EASY, BossDifficulty.NORMAL),
        reqLevels = listOf(),
        bossLevels = listOf(),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),
    LOTUS(
        bossName = "스우",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD, BossDifficulty.EXTREME),
        reqLevels = listOf(190, 190, 190),
        bossLevels = listOf(210, 210, 285),
        memberCounts = listOf(6, 6, 2),
        rewards = listOf()
    ),
    DAMIEN(
        bossName = "데미안",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.HARD),
        reqLevels = listOf(190, 190),
        bossLevels = listOf(210, 210),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),
    GUARDIANANGELSLIME(
        bossName = "가디언 엔젤 슬라임",
        region = "메이플 월드",
        difficulties = listOf(BossDifficulty.NORMAL, BossDifficulty.CHAOS),
        reqLevels = listOf(210, 210),
        bossLevels = listOf(220, 250),
        memberCounts = listOf(6, 6),
        rewards = listOf()
    ),
}

enum class BossDifficulty(
    val displayName: String
) {

    EASY(displayName = "EASY"),
    NORMAL(displayName = "NORMAL"),
    CHAOS(displayName = "CHAOS",),
    HARD(displayName = "HARD",),
    EXTREME(displayName = "EXTREME")
}