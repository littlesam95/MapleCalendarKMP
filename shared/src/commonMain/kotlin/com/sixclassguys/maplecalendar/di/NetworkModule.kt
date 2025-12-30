package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSourceImpl
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient { // 실제 엔진(OkHttp, Darwin 등)은 Ktor가 선택
            // 1. JSON 직렬화 설정
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // 서버에서 주는 추가 필드는 무시
                })
            }

            // 2. 로깅 설정 (디버깅 시 필수)
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println("HTTP Log: $message")
                    }
                }
            }

            // 3. 기본 타임아웃 등 추가 설정 가능
        }
    }

    // Notification DataSource 객체 주입
    single<NotificationDataSource> { NotificationDataSourceImpl(get()) }
}