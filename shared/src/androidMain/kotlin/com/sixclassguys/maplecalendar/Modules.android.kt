package com.sixclassguys.maplecalendar

import com.sixclassguys.maplecalendar.util.AuthManager
import org.koin.dsl.module

val androidPermissionModule = module {

    // Android 전용 PermissionChecker 등록 (Context 주입)
    single { PermissionChecker(get()) }

    single<AuthManager> { AndroidAuthManager(get()) }
}