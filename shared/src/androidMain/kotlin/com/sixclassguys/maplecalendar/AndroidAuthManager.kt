package com.sixclassguys.maplecalendar

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.sixclassguys.maplecalendar.util.AuthManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume

class AndroidAuthManager(
    private val context: Context
) : AuthManager {

    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()

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

    override suspend fun signInWithApple(context: Any?): String? = suspendCancellableCoroutine { continuation ->
        if (context == null) return@suspendCancellableCoroutine
        val activity = context as? Activity ?: run {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val provider = OAuthProvider.newBuilder("apple.com")
        provider.scopes = listOf("email", "name")

        // 한국어 설정 등 추가 옵션
        provider.addCustomParameter("locale", "ko_KR")

        auth.startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener { authResult ->
                // 💡 여기서 Firebase ID Token이 아닌 'credential.idToken'을 꺼냅니다.
                val credential = authResult.credential as? OAuthCredential
                val appleRawIdToken = credential?.idToken

                if (appleRawIdToken != null) {
                    Napier.d("Apple Raw Token 추출 성공!")
                    // 💡 결과값을 반환하여 suspend 함수를 완료합니다.
                    if (continuation.isActive) continuation.resume(appleRawIdToken)
                } else {
                    Napier.e("Apple 토큰이 비어있습니다.")
                    if (continuation.isActive) continuation.resume(null)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                if (continuation.isActive) continuation.resume(null)
            }
            .addOnCanceledListener {
                if (continuation.isActive) continuation.resume(null)
            }
    }

    override suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}