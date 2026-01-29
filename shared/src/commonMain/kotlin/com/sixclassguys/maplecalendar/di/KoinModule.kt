package com.sixclassguys.maplecalendar.di

import com.sixclassguys.maplecalendar.data.repository.AlarmRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.AuthRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.CharacterRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.EventRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.FirebaseNotificationRepository
import com.sixclassguys.maplecalendar.data.repository.MapleCharacterRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.MemberRepositoryImpl
import com.sixclassguys.maplecalendar.data.repository.NotificationEventBusImpl
import com.sixclassguys.maplecalendar.domain.repository.AlarmRepository
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import com.sixclassguys.maplecalendar.domain.repository.EventRepository
import com.sixclassguys.maplecalendar.domain.repository.MapleCharacterRepository
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import com.sixclassguys.maplecalendar.domain.usecase.AutoLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.CheckCharacterAuthorityUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DeleteCharacterUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DoLoginWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.FetchCharactersWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharacterBasicUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharactersUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetEventDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMonthlyEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetSavedFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetTodayEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GoogleLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.LogoutUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterCharactersUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ReissueJwtTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetCharacterOcidUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetOpenApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitEventAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitRepresentativeCharacterUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleEventAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UnregisterTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UpdateRepresentativeCharacterUseCase
import com.sixclassguys.maplecalendar.presentation.boss.BossReducer
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.presentation.notification.NotificationViewModel
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarReducer
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarViewModel
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterReducer
import com.sixclassguys.maplecalendar.presentation.character.MapleCharacterViewModel
import com.sixclassguys.maplecalendar.presentation.home.HomeReducer
import com.sixclassguys.maplecalendar.presentation.home.HomeViewModel
import com.sixclassguys.maplecalendar.presentation.login.LoginReducer
import com.sixclassguys.maplecalendar.presentation.login.LoginViewModel
import com.sixclassguys.maplecalendar.presentation.notification.NotificationReducer
import com.sixclassguys.maplecalendar.presentation.setting.SettingReducer
import com.sixclassguys.maplecalendar.presentation.setting.SettingViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// 모듈 정의: 어떤 인터페이스에 어떤 구현체를 넣을지 결정
val appModule = module {
    // 싱글톤으로 등록
    single<NotificationEventBus> { NotificationEventBusImpl() }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<MemberRepository> { MemberRepositoryImpl(get(), get()) }
    single<CharacterRepository> { CharacterRepositoryImpl(get(), get()) }
    single<MapleCharacterRepository> { MapleCharacterRepositoryImpl(get(), get()) }
    // NotificationRepository 인터페이스 요청 시 FirebaseNotificationRepository 인스턴스를 싱글톤으로 주입
    single<NotificationRepository> { FirebaseNotificationRepository(get(), get()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<AlarmRepository> { AlarmRepositoryImpl(get()) }
}

val useCaseModule = module {
    // UseCase 객체 생성
    single<GoogleLoginUseCase> { GoogleLoginUseCase(get()) }
    single<AutoLoginUseCase> { AutoLoginUseCase(get()) }
    single<ReissueJwtTokenUseCase> { ReissueJwtTokenUseCase(get()) }
    single<DoLoginWithApiKeyUseCase> { DoLoginWithApiKeyUseCase(get()) }
    single<SubmitRepresentativeCharacterUseCase> { SubmitRepresentativeCharacterUseCase(get()) }
    single<GetApiKeyUseCase> { GetApiKeyUseCase(get()) }
    single<GetSavedFcmTokenUseCase> { GetSavedFcmTokenUseCase(get()) }
    single<SetCharacterOcidUseCase> { SetCharacterOcidUseCase(get()) }
    single<SetOpenApiKeyUseCase> { SetOpenApiKeyUseCase(get()) }
    single<GetFcmTokenUseCase> { GetFcmTokenUseCase(get()) }
    single<GetGlobalAlarmStatusUseCase> { GetGlobalAlarmStatusUseCase(get()) }
    single<RegisterTokenUseCase> { RegisterTokenUseCase(get()) }
    single<GetEventDetailUseCase> { GetEventDetailUseCase(get()) }
    single<GetTodayEventsUseCase> { GetTodayEventsUseCase(get()) }
    single<GetMonthlyEventsUseCase> { GetMonthlyEventsUseCase(get()) }
    single<GetCharacterBasicUseCase> { GetCharacterBasicUseCase(get()) }
    single<SubmitEventAlarmUseCase> { SubmitEventAlarmUseCase(get()) }
    single<ToggleEventAlarmUseCase> { ToggleEventAlarmUseCase(get()) }
    single<ToggleGlobalAlarmStatusUseCase> { ToggleGlobalAlarmStatusUseCase(get()) }
    single<UnregisterTokenUseCase> { UnregisterTokenUseCase(get()) }
    single<LogoutUseCase> { LogoutUseCase(get()) }
    single<CheckCharacterAuthorityUseCase> { CheckCharacterAuthorityUseCase(get()) }
    single<DeleteCharacterUseCase> { DeleteCharacterUseCase(get()) }
    single<FetchCharactersWithApiKeyUseCase> { FetchCharactersWithApiKeyUseCase(get()) }
    single<GetCharactersUseCase> { GetCharactersUseCase(get()) }
    single<RegisterCharactersUseCase> { RegisterCharactersUseCase(get()) }
    single<UpdateRepresentativeCharacterUseCase> { UpdateRepresentativeCharacterUseCase(get()) }
}

val viewModelModule = module {
    // ViewModel (화면마다 생명주기를 관리하기 위해 factory 사용)
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SettingViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { NotificationViewModel(get(), get(), get(), get()) }
    viewModel { CalendarViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { MapleCharacterViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { BossViewModel(get(), get()) }

    // Reducer
    single { HomeReducer() }
    single { LoginReducer() }
    single { SettingReducer() }
    single { CalendarReducer() }
    single { MapleCharacterReducer() }
    single { NotificationReducer() }
    single { BossReducer() }
}

// 공통 초기화 함수: 안드로이드와 iOS 앱 시작 시 호출됨
fun initKoin(
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
        appDeclaration()
        modules(
            appModule,
            repositoryModule,
            networkModule,
            useCaseModule,
            viewModelModule,
            sharedModule,
            platformModule
        )
        modules(additionalModules)
    }