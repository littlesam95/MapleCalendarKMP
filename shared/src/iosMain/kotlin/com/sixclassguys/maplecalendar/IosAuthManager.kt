package com.sixclassguys.maplecalendar

import com.sixclassguys.maplecalendar.util.AuthManager

class IosAuthManager(
    private val onSignIn: suspend () -> String?,
    private val onSignOut: () -> Unit
) : AuthManager {

    override suspend fun signInWithGoogle(): String? = onSignIn()

    override suspend fun signOut() = onSignOut()
}