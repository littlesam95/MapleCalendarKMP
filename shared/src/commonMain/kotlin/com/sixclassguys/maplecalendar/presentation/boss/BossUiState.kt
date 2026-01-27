package com.sixclassguys.maplecalendar.presentation.boss

import com.sixclassguys.maplecalendar.util.Boss

data class BossUiState(
    val isLoading: Boolean = false,
    val selectedRegion: String = "그란디스",
    val selectedBoss: Boss = Boss.SEREN,
    val errorMessage: String? = null
)