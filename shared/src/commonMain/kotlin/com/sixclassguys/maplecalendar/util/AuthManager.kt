package com.sixclassguys.maplecalendar.util

interface AuthManager {

    suspend fun signInWithGoogle(context: Any): String?

    suspend fun signInWithApple(): String?

    suspend fun signOut()
}