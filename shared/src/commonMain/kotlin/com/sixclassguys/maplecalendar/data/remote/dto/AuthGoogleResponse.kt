package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.LoginResult
import com.sixclassguys.maplecalendar.domain.model.Member
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthGoogleResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("email")
    val email: String,

    @SerialName("nickname")
    val nickname: String?,

    @SerialName("profileImageUrl")
    val profileImageUrl: String?,

    @SerialName("provider")
    val provider: String,

    @SerialName("isGlobalAlarmEnabled")
    val isGlobalAlarmEnabled: Boolean,

    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,

    @SerialName("isNewMember")
    val isNewMember: Boolean,

    @SerialName("characterBasic")
    val characterBasic: CharacterBasicResponse? = null,

    @SerialName("characterPopularity")
    val characterPopularity: Int? = null,

    @SerialName("characterOverallRanking")
    val characterOverallRanking: RankingResponse? = null,

    @SerialName("characterServerRanking")
    val characterServerRanking: RankingResponse? = null,

    @SerialName("characterUnionLevel")
    val characterUnionLevel: UnionResponse? = null,

    @SerialName("characterDojang")
    val characterDojang: DojangRankingResponse? = null,

    @SerialName("lastLoginAt")
    val lastLoginAt: LocalDateTime
) {

    fun toDomain(): LoginResult {
        return LoginResult(
            member = Member(
                email = this.email,
                nickname = this.nickname ?: "메이플스토리 용사",
                profileImageUrl = this.profileImageUrl ?: "",
                isGlobalAlarmEnabled = this.isGlobalAlarmEnabled,
                characterBasic = this.characterBasic?.toDomain(),
                characterPopularity = this.characterPopularity ?: 0,
                characterOverallRanking = this.characterOverallRanking?.toDomain(),
                characterServerRanking = this.characterServerRanking?.toDomain(),
                characterUnionLevel = characterUnionLevel?.toDomain(),
                characterDojang = characterDojang?.toDomain(),
            ),
            isNewMember = this.isNewMember,
            accessToken = this.accessToken,
            refreshToken = this.refreshToken
        )
    }
}