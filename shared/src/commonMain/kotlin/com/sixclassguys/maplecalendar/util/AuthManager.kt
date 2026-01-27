package com.sixclassguys.maplecalendar.util

interface AuthManager {

    suspend fun signInWithGoogle(): String?

    suspend fun signOut()
}