package com.sixclassguys.maplecalendar.utils

import androidx.compose.ui.graphics.Color
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.util.Boss
import com.sixclassguys.maplecalendar.theme.MapleDifficultyChaosBackground
import com.sixclassguys.maplecalendar.theme.MapleDifficultyChaosOutline
import com.sixclassguys.maplecalendar.theme.MapleDifficultyChaosText
import com.sixclassguys.maplecalendar.theme.MapleDifficultyEasyBackground
import com.sixclassguys.maplecalendar.theme.MapleDifficultyEasyOutline
import com.sixclassguys.maplecalendar.theme.MapleDifficultyEasyText
import com.sixclassguys.maplecalendar.theme.MapleDifficultyExtremeBackground
import com.sixclassguys.maplecalendar.theme.MapleDifficultyExtremeOutline
import com.sixclassguys.maplecalendar.theme.MapleDifficultyExtremeText
import com.sixclassguys.maplecalendar.theme.MapleDifficultyHardBackground
import com.sixclassguys.maplecalendar.theme.MapleDifficultyHardOutline
import com.sixclassguys.maplecalendar.theme.MapleDifficultyHardText
import com.sixclassguys.maplecalendar.theme.MapleDifficultyNormalBackground
import com.sixclassguys.maplecalendar.theme.MapleDifficultyNormalOutline
import com.sixclassguys.maplecalendar.theme.MapleDifficultyNormalText
import com.sixclassguys.maplecalendar.util.BossDifficulty

/**
 * BossDifficulty에 대응하는 UI 리소스 매퍼
 */
val BossDifficulty.badgeBackground: Color
    get() = when (this) {
        BossDifficulty.EASY -> MapleDifficultyEasyBackground
        BossDifficulty.NORMAL -> MapleDifficultyNormalBackground
        BossDifficulty.CHAOS -> MapleDifficultyChaosBackground
        BossDifficulty.HARD -> MapleDifficultyHardBackground
        BossDifficulty.EXTREME -> MapleDifficultyExtremeBackground
    }

val BossDifficulty.badgeOutline: Color
    get() = when (this) {
        BossDifficulty.EASY -> MapleDifficultyEasyOutline
        BossDifficulty.NORMAL -> MapleDifficultyNormalOutline
        BossDifficulty.CHAOS -> MapleDifficultyChaosOutline
        BossDifficulty.HARD -> MapleDifficultyHardOutline
        BossDifficulty.EXTREME -> MapleDifficultyExtremeOutline
    }

val BossDifficulty.badgeText: Color
    get() = when (this) {
        BossDifficulty.EASY -> MapleDifficultyEasyText
        BossDifficulty.NORMAL -> MapleDifficultyNormalText
        BossDifficulty.CHAOS -> MapleDifficultyChaosText
        BossDifficulty.HARD -> MapleDifficultyHardText
        BossDifficulty.EXTREME -> MapleDifficultyExtremeText
    }

val BossDifficulty.selectButtonRes: Int
    get() = when (this) {
        BossDifficulty.EASY -> R.drawable.bg_boss_difficulty_easy
        BossDifficulty.NORMAL -> R.drawable.bg_boss_difficulty_normal
        BossDifficulty.CHAOS -> R.drawable.bg_boss_difficulty_chaos
        BossDifficulty.HARD -> R.drawable.bg_boss_difficulty_hard
        BossDifficulty.EXTREME -> R.drawable.bg_boss_difficulty_extreme
    }

/**
 * Boss 타입에 대응하는 이미지 리소스 매퍼
 */
val Boss.iconRes: Int
    get() = when (this) {
        Boss.SEREN -> R.drawable.ic_boss_seren
        Boss.KALOS -> R.drawable.ic_boss_kalos
        Boss.THEFIRSTADVERSARY -> R.drawable.ic_boss_the_first_adversary
        Boss.KALING -> R.drawable.ic_boss_kaling
        Boss.RADIANTMALEFIC -> R.drawable.ic_boss_radiant_malefic
        Boss.LIMBO -> R.drawable.ic_boss_limbo
        Boss.BALDRIX -> R.drawable.ic_boss_baldrix
        Boss.LUCID -> R.drawable.ic_boss_lucid
        Boss.WILL -> R.drawable.ic_boss_will
        Boss.DUSK -> R.drawable.ic_boss_dusk
        Boss.VERUSHILLA -> R.drawable.ic_boss_verus_hilla
        Boss.DUNKEL -> R.drawable.ic_boss_dunkel
        Boss.BLACKMAGE -> R.drawable.ic_boss_black_mage
        Boss.ZAKUM -> R.drawable.ic_boss_zakum
        Boss.MAGNUS -> R.drawable.ic_boss_magnus
        Boss.HILLA -> R.drawable.ic_boss_hilla
        Boss.LOADBALROG -> R.drawable.ic_boss_papulatus
        Boss.VONBON -> R.drawable.ic_boss_von_bon
        Boss.PIERRE -> R.drawable.ic_boss_pierre
        Boss.BLOODYQUEEN -> R.drawable.ic_boss_bloody_queen
        Boss.VELLUM -> R.drawable.ic_boss_vellum
        Boss.PINKBEAN -> R.drawable.ic_boss_pink_bean
        Boss.CYGNUS -> R.drawable.ic_boss_cygnus
        Boss.LOTUS -> R.drawable.ic_boss_lotus
        Boss.DAMIEN -> R.drawable.ic_boss_damien
        Boss.GUARDIANANGELSLIME -> R.drawable.ic_boss_guardian_angel_slime
    }

val Boss.backgroundRes: Int
    get() = when (this) {
        Boss.SEREN -> R.drawable.bg_boss_seren
        Boss.KALOS -> R.drawable.bg_boss_kalos
        Boss.THEFIRSTADVERSARY -> R.drawable.bg_boss_the_first_adversary
        Boss.KALING -> R.drawable.bg_boss_kaling
        Boss.RADIANTMALEFIC -> R.drawable.bg_boss_radiant_malefic
        Boss.LIMBO -> R.drawable.bg_boss_limbo
        Boss.BALDRIX -> R.drawable.bg_boss_baldrix
        Boss.LUCID -> R.drawable.bg_boss_lucid
        Boss.WILL -> R.drawable.bg_boss_will
        Boss.DUSK -> R.drawable.bg_boss_dusk
        Boss.VERUSHILLA -> R.drawable.bg_boss_verus_hilla
        Boss.DUNKEL -> R.drawable.bg_boss_dunkel
        Boss.BLACKMAGE -> R.drawable.bg_boss_black_mage
        Boss.ZAKUM -> R.drawable.bg_boss_zakum
        Boss.MAGNUS -> R.drawable.bg_boss_magnus
        Boss.HILLA -> R.drawable.ic_boss_hilla
        Boss.LOADBALROG -> R.drawable.bg_boss_papulatus
        Boss.VONBON -> R.drawable.bg_boss_von_bon
        Boss.PIERRE -> R.drawable.bg_boss_pierre
        Boss.BLOODYQUEEN -> R.drawable.bg_boss_bloody_queen
        Boss.VELLUM -> R.drawable.bg_boss_vellum
        Boss.PINKBEAN -> R.drawable.bg_boss_pink_bean
        Boss.CYGNUS -> R.drawable.bg_boss_cygnus
        Boss.LOTUS -> R.drawable.bg_boss_lotus
        Boss.DAMIEN -> R.drawable.bg_boss_damien
        Boss.GUARDIANANGELSLIME -> R.drawable.bg_boss_guardian_angel_slime
    }