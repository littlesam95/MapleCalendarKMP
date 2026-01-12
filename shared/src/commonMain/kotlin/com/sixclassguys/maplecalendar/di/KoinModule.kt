package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.repository.AuthRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.CharacterRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.EventRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.FirebaseNotificationRepository
import com.sixclassguys.maplecalendar.data.repository.MemberRepositoryImpl
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import com.sixclassguys.maplecalendar.domain.repository.EventRepository
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import com.sixclassguys.maplecalendar.domain.usecase.AutoLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DoLoginWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharacterBasicUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMonthlyEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetTodayEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetCharacterOcidUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetOpenApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitRepresentativeCharacterUseCase
import com.sixclassguys.maplecalendar.presentation.notification.NotificationViewModel
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarReducer
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.presentation.home.HomeReducer
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.presentation.login.LoginReducer
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.presentation.notification.NotificationReducer
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// 모듈 정의: 어떤 인터페이스에 어떤 구현체를 넣을지 결정
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<MemberRepository> { MemberRepositoryImpl(get()) }
    single<CharacterRepository> { CharacterRepositoryImpl(get(), get()) }
    // NotificationRepository 인터페이스 요청 시 FirebaseNotificationRepository 인스턴스를 싱글톤으로 주입
    single<NotificationRepository> { FirebaseNotificationRepository(get(), get()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
}

val useCaseModule = module {
    // UseCase 객체 생성
    single<AutoLoginUseCase> { AutoLoginUseCase(get()) }
    single<DoLoginWithApiKeyUseCase> { DoLoginWithApiKeyUseCase(get()) }
    single<SubmitRepresentativeCharacterUseCase> { SubmitRepresentativeCharacterUseCase(get()) }
    single<GetApiKeyUseCase> { GetApiKeyUseCase(get()) }
    single<SetCharacterOcidUseCase> { SetCharacterOcidUseCase(get()) }
    single<SetOpenApiKeyUseCase> { SetOpenApiKeyUseCase(get()) }
    single<GetFcmTokenUseCase> { GetFcmTokenUseCase(get()) }
    single<RegisterTokenUseCase> { RegisterTokenUseCase(get()) }
    single<GetTodayEventsUseCase> { GetTodayEventsUseCase(get()) }
    single<GetMonthlyEventsUseCase> { GetMonthlyEventsUseCase(get()) }
    single<GetCharacterBasicUseCase> { GetCharacterBasicUseCase(get()) }
}

val viewModelModule = module {
    // ViewModel (화면마다 생명주기를 관리하기 위해 factory 사용)
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel { NotificationViewModel(get(), get(), get(), get()) }
    viewModel { CalendarViewModel(get(), get()) }

    // Reducer
    single { HomeReducer() }
    single { LoginReducer() }
    single { CalendarReducer() }
    single { NotificationReducer() }
}

// 공통 초기화 함수: 안드로이드와 iOS 앱 시작 시 호출됨
fun initKoin(additionalModules: List<Module> = emptyList(), appDeclaration: KoinAppDeclaration = {}) =
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
        modules(additionalModules)
    }