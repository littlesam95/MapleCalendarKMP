package com.sixclassguys.maplecalendar

import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.data.remote.datasource.AlarmDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.AuthDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.EventDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.MemberDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.NexonOpenApiDataSource
import com.sixclassguys.maplecalendar.data.remote.datasource.NotificationDataSource
import com.sixclassguys.maplecalendar.domain.repository.AlarmRepository
import com.sixclassguys.maplecalendar.domain.repository.AuthRepository
import com.sixclassguys.maplecalendar.domain.repository.CharacterRepository
import com.sixclassguys.maplecalendar.domain.repository.EventRepository
import com.sixclassguys.maplecalendar.domain.repository.MemberRepository
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import com.sixclassguys.maplecalendar.domain.usecase.AutoLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.AppleLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DoLoginWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharacterBasicUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetEventDetailUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetMonthlyEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetSavedFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetTodayEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.LogoutUseCase
import com.sixclassguys.maplecalendar.domain.usecase.RegisterTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetCharacterOcidUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetOpenApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitEventAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitRepresentativeCharacterUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleEventAlarmUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleGlobalAlarmStatusUseCase
import com.sixclassguys.maplecalendar.domain.usecase.UnregisterTokenUseCase
import com.sixclassguys.maplecalendar.presentation.calendar.CalendarReducer
import com.sixclassguys.maplecalendar.presentation.home.HomeReducer
import com.sixclassguys.maplecalendar.presentation.login.LoginReducer
import com.sixclassguys.maplecalendar.presentation.notification.NotificationReducer
import com.sixclassguys.maplecalendar.presentation.setting.SettingReducer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KMPHelper : KoinComponent {

    // --- 1. UseCases (도메인 로직) ---
    val autoLoginUseCase: AutoLoginUseCase by inject()
    val appleLoginUseCase: AppleLoginUseCase by inject()
    val doLoginWithApiKeyUseCase: DoLoginWithApiKeyUseCase by inject()
    val submitRepresentativeCharacterUseCase: SubmitRepresentativeCharacterUseCase by inject()
    val getApiKeyUseCase: GetApiKeyUseCase by inject()
    val getSavedFcmTokenUseCase: GetSavedFcmTokenUseCase by inject()
    val setCharacterOcidUseCase: SetCharacterOcidUseCase by inject()
    val setOpenApiKeyUseCase: SetOpenApiKeyUseCase by inject()
    val getFcmTokenUseCase: GetFcmTokenUseCase by inject()
    val getGlobalAlarmStatusUseCase: GetGlobalAlarmStatusUseCase by inject()
    val registerTokenUseCase: RegisterTokenUseCase by inject()
    val getEventDetailUseCase: GetEventDetailUseCase by inject()
    val getTodayEventsUseCase: GetTodayEventsUseCase by inject()
    val getMonthlyEventsUseCase: GetMonthlyEventsUseCase by inject()
    val getCharacterBasicUseCase: GetCharacterBasicUseCase by inject()
    val submitEventAlarmUseCase: SubmitEventAlarmUseCase by inject()
    val toggleEventAlarmUseCase: ToggleEventAlarmUseCase by inject()
    val toggleGlobalAlarmStatusUseCase: ToggleGlobalAlarmStatusUseCase by inject()
    val unregisterTokenUseCase: UnregisterTokenUseCase by inject()
    val logoutUseCase: LogoutUseCase by inject()

    // --- 2. Reducers (MVI 상태 관리) ---
    val homeReducer: HomeReducer by inject()
    val loginReducer: LoginReducer by inject()
    val settingReducer: SettingReducer by inject()
    val calendarReducer: CalendarReducer by inject()
    val notificationReducer: NotificationReducer by inject()

    // --- 3. Repositories (데이터 중계) ---
    val authRepository: AuthRepository by inject()
    val memberRepository: MemberRepository by inject()
    val characterRepository: CharacterRepository by inject()
    val notificationRepository: NotificationRepository by inject()
    val eventRepository: EventRepository by inject()
    val alarmRepository: AlarmRepository by inject()

    // --- 4. DataSources (네트워크/로컬 데이터) ---
    val alarmDataSource: AlarmDataSource by inject()
    val authDataSource: AuthDataSource by inject()
    val memberDataSource: MemberDataSource by inject()
    val notificationDataSource: NotificationDataSource by inject()
    val eventDataSource: EventDataSource by inject()
    val nexonOpenApiDataSource: NexonOpenApiDataSource by inject()

    // --- 5. Utilities & Shared ---
    val notificationEventBus: NotificationEventBus by inject()
    val appPreferences: AppPreferences by inject()

    companion object {

        val shared = KMPHelper()
    }
}

fun getKMPHelper(): KMPHelper = KMPHelper.shared
