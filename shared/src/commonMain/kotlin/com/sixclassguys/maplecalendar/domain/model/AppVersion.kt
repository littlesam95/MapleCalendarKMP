package com.sixclassguys.maplecalendar.domain.model

data class AppVersion(
    val isUpdateRequired: Boolean,
    val isForceUpdate: Boolean,
    val latestVersionName: String,
    val updateMessage: String,
    val storeUrl: String
)