package com.sixclassguys.maplecalendar.domain.model

data class LoginInfo(
    val representativeOcid: String?,
    val characters: Map<String, List<AccountCharacter>>,
    val isGlobalAlarmEnabled: Boolean
)