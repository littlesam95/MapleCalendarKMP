package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.repository.FirebaseNotificationRepository
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterTokenUseCase
import com.sixclassguys.maplecalendar.presentation.NotificationViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// 모듈 정의: 어떤 인터페이스에 어떤 구현체를 넣을지 결정
val repositoryModule = module {
    // NotificationRepository 인터페이스 요청 시 FirebaseNotificationRepository 인스턴스를 싱글톤으로 주입
    single<NotificationRepository> { FirebaseNotificationRepository(get(), get()) }
}

val useCaseModule = module {
    // UseCase 객체 생성
    single<GetFcmTokenUseCase> { GetFcmTokenUseCase(get()) }
    single<RegisterTokenUseCase> { RegisterTokenUseCase(get()) }
}

val viewModelModule = module {
    // ViewModel (화면마다 생명주기를 관리하기 위해 factory 사용)
    factory { NotificationViewModel(get(), get()) }
}

// 공통 초기화 함수: 안드로이드와 iOS 앱 시작 시 호출됨
fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            repositoryModule,
            networkModule,
            useCaseModule,
            viewModelModule,
            sharedModule,
            platformModule
        )
    }