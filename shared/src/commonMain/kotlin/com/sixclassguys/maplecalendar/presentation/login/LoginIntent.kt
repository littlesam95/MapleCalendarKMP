package com.sixclassguys.maplecalendar.presentation.login

import com.sixclassguys.maplecalendar.domain.model.AccountCharacter
import com.sixclassguys.maplecalendar.domain.model.Member

sealed class LoginIntent {

    data object ErrorMessageConsumed : LoginIntent() // 추가

    data class ClickGoogleLogin(val context: Any) : LoginIntent()

    data class GoogleLoginSuccess(val member: Member, val isNewMember: Boolean) : LoginIntent()

    data class GoogleLoginFailed(val message: String) : LoginIntent()

    data class ClickAppleLogin(
        val provider: String,    // "APPLE" 또는 "GOOGLE"
        val idToken: String,
        val identifier: String,
        val firstName: String? = null,
        val lastName: String? = null
    ) : LoginIntent()

    data class AppleLoginSuccess(val member: Member, val isNewMember: Boolean) : LoginIntent()

    data class AppleLoginFailed(val message: String) : LoginIntent()

    data class UpdateApiKey(val apiKey: String) : LoginIntent()

    data object ClickLogin : LoginIntent()

    data class FetchApiKeyWithEmptyCharacters(val message: String) : LoginIntent()

    data class SelectRepresentativeCharacter(val characters: Map<String, List<AccountCharacter>>) :
        LoginIntent()

    data object LoginSuccess : LoginIntent()

    data class LoginFailed(val message: String) : LoginIntent()

    data object NavigationConsumed : LoginIntent()

    data class ShowWorldSheet(val isShow: Boolean) : LoginIntent()

    data class SelectWorld(val worldName: String) : LoginIntent()

    data class SelectCharacter(val character: AccountCharacter) : LoginIntent()

    data object SubmitRepresentativeCharacter : LoginIntent()

    data class SubmitRepresentativeCharacterFailed(val message: String) : LoginIntent()

    data object SetOpenApiKey : LoginIntent()

    data class SetOpenApiKeyFailed(val message: String) : LoginIntent()

    data object InitErrorMessage : LoginIntent()
}