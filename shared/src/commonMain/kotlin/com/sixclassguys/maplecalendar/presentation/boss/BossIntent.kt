package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.util.Boss

sealed class BossIntent {

    data class SelectBoss(val selectedBoss: Boss) : BossIntent()
}