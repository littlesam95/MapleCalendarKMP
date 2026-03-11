package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.AppVersion
import kotlinx.serialization.Serializable

@Serializable
data class AppVersionResponse(
    val isUpdateRequired: Boolean,
    val isForceUpdate: Boolean,
    val latestVersionName: String?,
    val updateMessage: String?,
    val storeUrl: String?
) {
    
    fun toDomain(): AppVersion {
        return AppVersion(
            isUpdateRequired = this.isUpdateRequired,
            isForceUpdate = this.isForceUpdate,
            latestVersionName = this.latestVersionName ?: "0.1.0",
            updateMessage = this.updateMessage ?: "업데이트 메시지가 없어요.",
            storeUrl = this.storeUrl ?: ""
        )
    }
}