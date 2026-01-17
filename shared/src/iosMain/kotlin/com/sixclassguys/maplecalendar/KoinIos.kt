package com.sixclassguys.maplecalendar

import com.sixclassguys.maplecalendar.di.initKoin

fun initKoinIos() {
    initKoin(listOf(iosNetworkModule)) {
        // iOS는 androidContext 같은 설정이 필요 없음
    }
}