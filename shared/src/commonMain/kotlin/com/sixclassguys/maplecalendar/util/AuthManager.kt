package com.sixclassguys.maplecalendar.util

interface AuthManager {

    suspend fun signInWithGoogle(context: Any): String?

    suspend fun signOut()
}