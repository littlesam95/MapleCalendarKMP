package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.remote.datasource.EventDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.EventDataSourceImpl
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSourceImpl
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient { // 실제 엔진(OkHttp, Darwin 등)은 Ktor가 선택
            // JSON 직렬화 설정
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // 서버에서 주는 추가 필드는 무시
                })
            }

            // 로깅 설정 (디버깅 시 필수)
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println("HTTP Log: $message")
                    }
                }
            }

            // 추가 설정
            defaultRequest {
                url("http://[본인 IP]:8080/api/") // 베이스 URL 설정
                contentType(ContentType.Application.Json)
            }
        }
    }

    // Notification DataSource 객체 주입
    single<NotificationDataSource> { NotificationDataSourceImpl(get()) }

    // Event DataSource 객체 주입
    single<EventDataSource> { EventDataSourceImpl(get()) }
}