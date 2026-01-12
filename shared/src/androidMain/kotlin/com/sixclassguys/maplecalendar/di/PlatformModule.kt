package com.sixclassguys.maplecalendar.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sixclassguys.maplecalendar.data.local.DATA_STORE_FILE_NAME
import com.sixclassguys.maplecalendar.data.local.createDataStore
import com.sixclassguys.maplecalendar.shared.BuildConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule = module {
    single<DataStore<Preferences>> {
        createDataStore {
            get<Context>().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
}

val androidNetworkModule = module {
    single(named("nexonApiKey")) { BuildConfig.NEXON_API_KEY }
}