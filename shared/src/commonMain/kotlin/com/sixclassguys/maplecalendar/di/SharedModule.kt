package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import org.koin.dsl.module

val sharedModule = module {
    // 플랫폼별 DataStore 인스턴스를 주입받아 TokenStorage 생성
    single { AppPreferences(get()) }
}