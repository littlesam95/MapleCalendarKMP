package com.sixclassguys.maplecalendar.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sixclassguys.maplecalendar.domain.model.ApiState
import com.sixclassguys.maplecalendar.domain.usecase.AppleLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.DoLoginWithApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetCharacterBasicUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GetFcmTokenUseCase
import com.sixclassguys.maplecalendar.domain.usecase.GoogleLoginUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SetOpenApiKeyUseCase
import com.sixclassguys.maplecalendar.domain.usecase.SubmitRepresentativeCharacterUseCase
import com.sixclassguys.maplecalendar.util.AuthManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authManager: AuthManager,
    private val reducer: LoginReducer,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val appleLoginUseCase: AppleLoginUseCase,
    private val doLoginWithApiKeyUseCase: DoLoginWithApiKeyUseCase,
    private val submitRepresentativeCharacterUseCase: SubmitRepresentativeCharacterUseCase,
    private val setOpenApiKeyUseCase: SetOpenApiKeyUseCase,
    private val getCharacterBasicUseCase: GetCharacterBasicUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initUiState()
    }

    private fun loginWithGoogle(context: Any) {
        viewModelScope.launch {
            val idToken = authManager.signInWithGoogle(context)

            if (idToken != null) {
                // 💡 여기서 이제 서버(Spring)에 토큰을 보내는 UseCase를 호출해야 합니다.
                // 예: authenticateWithGoogleUseCase(idToken).collect { ... }
                println("구글 토큰 획득 성공: $idToken")

                // 임시로 성공 처리하거나 다음 스텝(서버 검증)으로 넘김
                submitUserInfo("google", idToken)
            } else {
                onIntent(LoginIntent.LoginFailed("구글 로그인에 실패했습니다."))
            }
        }
    }

    private fun loginWithApple(
        intent: LoginIntent.ClickAppleLogin? = null,
        intentAndroid: LoginIntent.ClickAppleLoginInAndroid? = null
    ) {
        viewModelScope.launch {
            val context = intentAndroid?.context
            val idToken = intent?.idToken ?: authManager.signInWithApple(context)
            val platform = intent?.provider?.lowercase() ?: "apple"

            Napier.d("Apple ID 토큰: $idToken")

            if (!idToken.isNullOrBlank()) {
                // 💡 여기서 이제 서버(Spring)에 토큰을 보내는 UseCase를 호출해야 합니다.
                // 예: authenticateWithGoogleUseCase(idToken).collect { ... }
                println("애플 토큰 획득 성공: $idToken")

                // 임시로 성공 처리하거나 다음 스텝(서버 검증)으로 넘김
                submitUserInfo(platform, idToken.trim())
            } else {
                onIntent(LoginIntent.AppleLoginFailed("애플 로그인에 실패했습니다."))
            }
        }
    }

    private fun submitUserInfo(platform: String, idToken: String) {
        viewModelScope.launch {
            val fcmToken = getFcmTokenUseCase() ?: ""
            Napier.d("FCM 토큰: $fcmToken")

            when (platform) {
                "google" -> {
                    googleLoginUseCase(platform, idToken, fcmToken).collect { state ->
                        when (state) {
                            is ApiState.Success -> {
                                val member = state.data.member
                                val isNewMember = state.data.isNewMember
                                Napier.d("멤버: $member")
                                onIntent(LoginIntent.GoogleLoginSuccess(member, isNewMember))
                            }

                            is ApiState.Error -> {
                                onIntent(LoginIntent.GoogleLoginFailed("구글 로그인에 실패했습니다."))
                            }

                            else -> {}
                        }
                    }
                }

                "apple" -> {
                    appleLoginUseCase(platform, idToken, fcmToken).collect { state ->
                        when (state) {
                            is ApiState.Success -> {
                                val member = state.data.member
                                val isNewMember = state.data.isNewMember
                                Napier.d("멤버: $member")
                                onIntent(LoginIntent.AppleLoginSuccess(member, isNewMember))
                            }

                            is ApiState.Error -> {
                                onIntent(LoginIntent.AppleLoginFailed("애플 로그인에 실패했습니다."))
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun loginWithApiKey() {
        viewModelScope.launch {
            doLoginWithApiKeyUseCase(_uiState.value.nexonApiKey).collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        val characters = apiState.data.characters
                        val ocid = apiState.data.representativeOcid
                        when {
                            (ocid == null) && (characters.isEmpty()) -> {
                                onIntent(LoginIntent.SelectRepresentativeCharacter(apiState.data.characters))
                            }

                            (ocid == null) && (characters.isNotEmpty()) -> {
                                onIntent(LoginIntent.FetchApiKeyWithEmptyCharacters("캐릭터가 없어요."))
                            }

                            (ocid != null) -> {
                                onIntent(LoginIntent.SetOpenApiKey)
                            }
                        }
                    }

                    is ApiState.Error -> {
                        onIntent(LoginIntent.LoginFailed(apiState.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun submitRepresentativeCharacter() {
        viewModelScope.launch {
            submitRepresentativeCharacterUseCase(
                _uiState.value.nexonApiKey,
                _uiState.value.selectedCharacter?.ocid ?: ""
            ).collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        onIntent(LoginIntent.SetOpenApiKey)
                    }

                    is ApiState.Error -> {
                        onIntent(LoginIntent.SubmitRepresentativeCharacterFailed(apiState.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun saveApiKey() {
        viewModelScope.launch {
            setOpenApiKeyUseCase(_uiState.value.nexonApiKey).collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        onIntent(LoginIntent.LoginSuccess)
                    }

                    is ApiState.Error -> {
                        onIntent(LoginIntent.SetOpenApiKeyFailed(apiState.message))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun fetchAllCharacterImages() {
        viewModelScope.launch {
            // 1. 모든 월드의 캐릭터를 하나로 합침 (평탄화)
            val allCharacters = _uiState.value.characters.values.flatten()
            if (allCharacters.isEmpty()) return@launch

            // 이미 로드된 이미지는 제외하고 진행 (불필요한 중복 호출 방지)
            val charactersToFetch = allCharacters.filter {
                _uiState.value.characterImages[it.ocid] == null
            }

            // 2. 5개씩 청크(Chunk)로 나눔
            charactersToFetch.chunked(5).forEach { chunk ->
                // 3. 5개에 대한 병렬 요청 시작
                val deferredResults = chunk.map { character ->
                    async {
                        // Loading 상태가 아닌 Success/Error가 올 때까지 기다림
                        val state = getCharacterBasicUseCase(character.ocid)
                            .filter { it is ApiState.Success || it is ApiState.Error }
                            .first()

                        when (state) {
                            is ApiState.Success -> character.ocid to state.data.characterImage

                            is ApiState.Error -> {
                                character.ocid to null
                            }

                            else -> character.ocid to null
                        }
                    }
                }

                // 4. 이번 청크(5개)의 결과가 모두 나올 때까지 대기
                val chunkResults = deferredResults.awaitAll()

                // 5. [중요] 5개 결과가 나오자마자 즉시 UI 상태 업데이트
                val newBatchMap = chunkResults.associate { it.first to it.second }
                _uiState.update { currentState ->
                    currentState.copy(
                        characterImages = currentState.characterImages + newBatchMap
                    )
                }

                // 6. 넥슨 API 초당 호출 제한(Rate Limit)을 피하기 위해 1초 대기
                delay(1000L)
            }
        }
    }

    fun initUiState() {
        _uiState.update { LoginUiState() }
    }

    fun onIntent(intent: LoginIntent) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, intent)
        }

        when (intent) {
            is LoginIntent.ClickGoogleLogin -> {
                loginWithGoogle(intent.context)
            }

            is LoginIntent.ClickLogin -> {
                loginWithApiKey()
            }

            is LoginIntent.ClickAppleLoginInAndroid -> {
                loginWithApple(intentAndroid = intent)
            }

            is LoginIntent.ClickAppleLogin -> {
                loginWithApple(intent = intent)
            }

            is LoginIntent.SelectRepresentativeCharacter -> {
                fetchAllCharacterImages()
            }

            is LoginIntent.SubmitRepresentativeCharacter -> {
                submitRepresentativeCharacter()
            }

            is LoginIntent.SetOpenApiKey -> {
                saveApiKey()
            }

            else -> {}
        }
    }
}