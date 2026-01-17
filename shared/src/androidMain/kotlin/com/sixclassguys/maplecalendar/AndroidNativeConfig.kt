package com.sixclassguys.maplecalendar

import com.sixclassguys.maplecalendar.shared.BuildConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

class AndroidNativeConfig : NativeConfig {
    override val nexonApiKey: String = BuildConfig.NEXON_API_KEY
}

val androidNetworkModule = module {
    single<NativeConfig> { AndroidNativeConfig() }
    single(named("nexonApiKey")) { get<NativeConfig>().nexonApiKey }
}