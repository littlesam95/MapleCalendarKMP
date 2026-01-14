package com.sixclassguys.maplecalendar.domain.model

data class LoginResult(
    val representativeOcid: String?,
    val characters: Map<String, List<AccountCharacter>>,
    val isGlobalAlarmEnabled: Boolean
)