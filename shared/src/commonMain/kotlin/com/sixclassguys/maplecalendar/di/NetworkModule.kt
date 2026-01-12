package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.remote.datasource.AuthDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.AuthDataSourceImpl
import com.sixclassguys.maplecalendar.data.remote.datasource.EventDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.EventDataSourceImpl
import com.sixclassguys.maplecalendar.data.remote.datasource.MemberDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.MemberDataSourceImpl
import com.sixclassguys.maplecalendar.data.remote.datasource.NexonOpenApiDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.NexonOpenApiDataSourceImpl
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSourceImpl
import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single(named("BackendClient")) {
        HttpClient { // 실제 엔진(OkHttp, Darwin 등)은 Ktor가 선택
            // JSON 직렬화 설정
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // 서버에서 주는 추가 필드는 무시
                })
            }

            // 응답 대기 시간 설정
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000   // 전체 요청 시간 (60초)
                connectTimeoutMillis = 10_000   // 서버 연결 대기 시간 (10초)
                socketTimeoutMillis = 60_000    // 데이터 전송 간격 대기 시간 (60초)
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
                // 본인 IP 작성하기(서버 배포가 아직 안 됨)
                // url("http://[본인 IP]:8080/api/")
                // 지원 IP
                url("http://192.168.0.14:8080/api/")
                contentType(ContentType.Application.Json)
            }
        }
    }

    single(named("NexonClient")) {
        val apiKey: String = get(named("nexonApiKey"))

        HttpClient { // 실제 엔진(OkHttp, Darwin 등)은 Ktor가 선택
            // JSON 직렬화 설정
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // 서버에서 주는 추가 필드는 무시
                })
            }

            // 응답 대기 시간 설정
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000   // 전체 요청 시간 (60초)
                connectTimeoutMillis = 10_000   // 서버 연결 대기 시간 (10초)
                socketTimeoutMillis = 60_000    // 데이터 전송 간격 대기 시간 (60초)
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
                url("https://open.api.nexon.com/maplestory/v1/")
                header("x-nxopen-api-key", apiKey)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // Auth DataSource 객체 주입
    single<AuthDataSource> { AuthDataSourceImpl(get(named("BackendClient"))) }

    // Member DataSource 객체 주입
    single<MemberDataSource> { MemberDataSourceImpl(get(named("BackendClient"))) }

    // Notification DataSource 객체 주입
    single<NotificationDataSource> { NotificationDataSourceImpl(get(named("BackendClient"))) }

    // Event DataSource 객체 주입
    single<EventDataSource> { EventDataSourceImpl(get(named("BackendClient"))) }

    // 넥슨 Open API와 통신하는 DataSource 객체 주입
    single<NexonOpenApiDataSource> { NexonOpenApiDataSourceImpl(get(named("NexonClient"))) }
}