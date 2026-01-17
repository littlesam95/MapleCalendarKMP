package com.sixclassguys.maplecalendar

import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSBundle

class IosNativeConfig : NativeConfig {
    override val nexonApiKey: String = NSBundle.mainBundle.objectForInfoDictionaryKey("NexonApiKey") as? String ?: ""
}

val iosNetworkModule = module {
    single<NativeConfig> { IosNativeConfig() }
    single(named("nexonApiKey")) { get<NativeConfig>().nexonApiKey }
}