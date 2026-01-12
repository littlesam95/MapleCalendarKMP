package com.sixclassguys.maplecalendar

import android.app.Application
import com.sixclassguys.maplecalendar.di.androidNetworkModule
import com.sixclassguys.maplecalendar.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext

class MapleCalendarApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Napier로 로깅하기
        Napier.base(DebugAntilog())

        // Koin 시작
        initKoin(listOf(androidNetworkModule)) {
            androidContext(this@MapleCalendarApplication)
        }
    }
}