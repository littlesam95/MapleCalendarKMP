package com.sixclassguys.maplecalendar

import com.sixclassguys.maplecalendar.util.AuthManager
import platform.AuthenticationServices.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
class IosAuthManager(
    private val onSignIn: suspend () -> String?,
    private val onSignOut: () -> Unit
) : AuthManager {

    // 💡 메모리에서 해제되지 않도록 클래스 멤버로 선언합니다.
    private var currentDelegate: ASAuthorizationControllerDelegateProtocol? = null
    private var currentPresentationProvider: ASAuthorizationControllerPresentationContextProvidingProtocol? = null

    override suspend fun signInWithGoogle(context: Any): String? = onSignIn()

    override suspend fun signInWithApple(): String? = suspendCancellableCoroutine { continuation ->
        val appleIDProvider = ASAuthorizationAppleIDProvider()
        val request = appleIDProvider.createRequest()
        request.requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)

        val controller = ASAuthorizationController(listOf(request))

        // 1. Delegate를 변수에 할당하여 유지
        val delegate = object : NSObject(), ASAuthorizationControllerDelegateProtocol {
            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithAuthorization: ASAuthorization
            ) {
                val credential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                val tokenString = credential?.identityToken?.let {
                    NSString.create(data = it, encoding = NSUTF8StringEncoding)?.toString()
                }

                // 작업 완료 후 메모리 해제
                currentDelegate = null
                currentPresentationProvider = null

                if (continuation.isActive) continuation.resume(tokenString)
            }

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithError: NSError
            ) {
                currentDelegate = null
                currentPresentationProvider = null
                if (continuation.isActive) continuation.resume(null)
            }
        }

        // 2. PresentationProvider를 변수에 할당하여 유지
        val presentationProvider = object : NSObject(), ASAuthorizationControllerPresentationContextProvidingProtocol {
            override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor {
                return UIApplication.sharedApplication.keyWindow ?: UIWindow()
            }
        }

        // 💡 클래스 프로퍼티에 참조를 저장하여 GC를 방지합니다.
        currentDelegate = delegate
        currentPresentationProvider = presentationProvider

        controller.delegate = delegate
        controller.presentationContextProvider = presentationProvider

        controller.performRequests()

        continuation.invokeOnCancellation {
            currentDelegate = null
            currentPresentationProvider = null
        }
    }

    override suspend fun signOut() = onSignOut()
}