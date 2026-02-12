package com.sixclassguys.maplecalendar

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sixclassguys.maplecalendar.util.AuthManager
import java.lang.Exception

class AndroidAuthManager(
    private val context: Context
) : AuthManager {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun signInWithGoogle(context: Any): String? {
        val activity = context as? Activity

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("292941521076-fnd88tholisqj3rjvrc7b5f7i644sqns.apps.googleusercontent.com") // GCP에서 발급받은 WEB Client ID
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(activity!!, request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdTokenCredential.idToken // 💡 이 토큰을 서버로 보내면 됩니다!
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}