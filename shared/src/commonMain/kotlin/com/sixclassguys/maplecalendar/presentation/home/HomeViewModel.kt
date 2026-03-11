package com.sixclassguys.maplecalendar.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.PermissionChecker
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.model.Member
import com.sixclassguys.maplecalendar.domain.usecase.AutoLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.CheckLatestVersionUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetDailyBossPartySchedulesUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetDailyEventsUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ReissueJwtTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.ToggleGlobalAlarmStatusUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

class HomeViewModel(
    val savedStateHandle: SavedStateHandle,
    private val reducer: HomeReducer,
    private val permissionChecker: PermissionChecker,
    private val checkLatestVersionUseCase: CheckLatestVersionUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val reissueJwtTokenUseCase: ReissueJwtTokenUseCase,
    private val toggleGlobalAlarmStatusUseCase: ToggleGlobalAlarmStatusUseCase,
    private val getDailyEventsUseCase: GetDailyEventsUseCase,
    private val getDailyBossPartySchedulesUseCase: GetDailyBossPartySchedulesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>("login_member", null)
                .collect { json ->
                    if (json != null) {
                        val member = Json.decodeFromString<Member>(json)
                        onIntent(HomeIntent.LoginSuccess(true, member))
                        savedStateHandle["login_member"] = null
                    }
                }
        }
    }

    private fun checkLatestVersion(versionCode: Int) {
        viewModelScope.launch {
            checkLatestVersionUseCase(versionCode).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(HomeIntent.CheckLatestVersionSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.CheckLatestVersionFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun autoLogin() {
        viewModelScope.launch {
            val fcmToken = getFcmTokenUseCase() ?: ""
            autoLoginUseCase(fcmToken).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        val member = state.data.member
                        onIntent(HomeIntent.AutoLoginSuccess(member))
                    }

                    is ApiState.Empty -> {
                        onIntent(HomeIntent.EmptyAccessToken)
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.ReissueJwtToken)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun reissueJwtToken() {
        viewModelScope.launch {
            reissueJwtTokenUseCase().collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(HomeIntent.AutoLogin)
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.AutoLoginFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

//    private fun getCharacterBasic(apiKey: String) {
//        viewModelScope.launch {
//            val fcmToken = getFcmTokenUseCase() ?: ""
//            autoLoginUseCase(apiKey, fcmToken).collect { state ->
//                when (state) {
//                    is ApiState.Success -> {
//                        onIntent(
//                            HomeIntent.LoadCharacterBasicSuccess(
//                                state.data.characterBasic,
//                                state.data.characterDojang,
//                                state.data.characterOverallRanking,
//                                state.data.characterServerRanking,
//                                state.data.characterUnionLevel,
//                                state.data.isGlobalAlarmEnabled
//                            )
//                        )
//                    }
//
//                    is ApiState.Error -> {
//                        onIntent(HomeIntent.LoadCharacterBasicFailed(state.message))
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    }

    private fun handleSyncNotification() {
        viewModelScope.launch {
            val isSystemGranted = permissionChecker.isNotificationGranted()
            Napier.d("알림 권한 허용: $isSystemGranted")
            val isServerSideOn = _uiState.value.isGlobalAlarmEnabled
            Napier.d("알림 수신 ON: $isServerSideOn")
            Napier.d("알림 동기화 요청 시도: 시스템권한=$isSystemGranted, 서버상태=$isServerSideOn")
            // 데이터 불일치 상태: 서버는 ON인데 시스템 권한은 OFF인 경우만 서버 통신
            if (isServerSideOn && !isSystemGranted) {
                // 서버에 OFF 상태 업데이트 요청
                onIntent(HomeIntent.ToggleGlobalAlarmStatus)
            }
        }
    }

    private fun toggleGlobalAlarmStatus() {
        viewModelScope.launch {
            toggleGlobalAlarmStatusUseCase().collect { state ->
                Napier.d("통신 상태 변경 감지: $state")
                when (state) {
                    is ApiState.Success -> {
                        Napier.d("알림 수신 여부 변경 성공")
                        onIntent(HomeIntent.ToggleGlobalAlarmStatusSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        Napier.d("알림 수신 여부 변경 실패")
                        onIntent(HomeIntent.ToggleGlobalAlarmStatusFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getTodayEvents() {
        viewModelScope.launch {
            val now = Clock.System.now()
            val seoulZone = TimeZone.of("Asia/Seoul")
            val currentLocalDateTime = now.toLocalDateTime(seoulZone)
            val today: LocalDate = currentLocalDateTime.date
            getDailyEventsUseCase(
                today.year,
                today.monthNumber,
                today.dayOfMonth
            ).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        Napier.d("이벤트 조회 성공")
                        onIntent(HomeIntent.LoadEventsSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        Napier.e("이벤트 조회 실패")
                        onIntent(HomeIntent.LoadEventsFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getTodayBossSchedules() {
        viewModelScope.launch {
            val now = Clock.System.now()
            val seoulZone = TimeZone.of("Asia/Seoul")
            val currentLocalDateTime = now.toLocalDateTime(seoulZone)
            val today: LocalDate = currentLocalDateTime.date
            getDailyBossPartySchedulesUseCase(
                today.year,
                today.monthNumber,
                today.dayOfMonth
            ).collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        onIntent(HomeIntent.FetchBossPartySchedulesSuccess(state.data))
                    }

                    is ApiState.Error -> {
                        onIntent(HomeIntent.FetchBossPartySchedulesFailed(state.message))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is HomeIntent.PullToRefresh -> {
                when (_uiState.value.member == null) {
                    true -> {
                        getTodayEvents()
                    }

                    false -> {
                        getTodayEvents()
                        getTodayBossSchedules()
                    }
                }
            }

            is HomeIntent.CheckLatestVersion -> {
                checkLatestVersion(intent.versionCode)
            }

            is HomeIntent.CheckLatestVersionSuccess -> {
                when (intent.appVersion.isUpdateRequired) {
                    true -> onIntent(HomeIntent.ShowVersionUpdateDialog)
                    false -> onIntent(HomeIntent.AutoLogin)
                }
            }

            is HomeIntent.DeclineVersionUpdate -> {
                onIntent(HomeIntent.AutoLogin)
            }

            is HomeIntent.AutoLogin -> {
                autoLogin()
            }

            is HomeIntent.ReissueJwtToken -> {
                reissueJwtToken()
            }

            is HomeIntent.AutoLoginSuccess -> {
                getTodayEvents()
                getTodayBossSchedules()
            }

            is HomeIntent.AutoLoginFailed -> {
                getTodayEvents()
            }

            is HomeIntent.LoginSuccess -> {
                getTodayEvents()
                getTodayBossSchedules()
            }

            is HomeIntent.EmptyAccessToken -> {
                getTodayEvents()
            }

            is HomeIntent.LoadEmptyApiKey -> {
                getTodayEvents()
            }

            is HomeIntent.LoadApiKeyFailed -> {
                getTodayEvents()
            }

            is HomeIntent.LoadCharacterBasicSuccess -> {
                getTodayEvents()
                handleSyncNotification()
            }

            is HomeIntent.LoadCharacterBasicFailed -> {
                getTodayEvents()
            }

            is HomeIntent.SyncNotificationWithSystem -> {
                handleSyncNotification()
            }

            is HomeIntent.ToggleGlobalAlarmStatus -> {
                toggleGlobalAlarmStatus()
            }

            else -> {}
        }
    }
}