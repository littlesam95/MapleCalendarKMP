package com.sixclassguys.maplecalendar.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sixclassguys.maplecalendar.data.local.DATA_STORE_FILE_NAME
import com.sixclassguys.maplecalendar.data.local.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule = module {
    single<DataStore<Preferences>> {
        createDataStore {
            val directory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
        }
    }
}