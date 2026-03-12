package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.BitmapImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.MapleWorld
import com.sixclassguys.maplecalendar.utils.badgeBackground
import com.sixclassguys.maplecalendar.utils.badgeOutline
import com.sixclassguys.maplecalendar.utils.badgeText
import com.sixclassguys.maplecalendar.utils.getTopVisiblePixel
import com.sixclassguys.maplecalendar.utils.iconRes

@Composable
fun BossPartyCreateDialog(
    viewModel: BossViewModel, // ViewModel 직접 주입
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current // 💡 현재 다이얼로그의 Native View
    val dummyFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val clearFocusAll = {
        // Compose 레벨에서 포커스 해제
        focusManager.clearFocus(force = true)
        // 가짜 타겟으로 포커스 강제 이동 (TextField에서 포커스 뺏기)
        dummyFocusRequester.requestFocus()
        // 네이티브 뷰 레벨에서 포커스 해제 (이게 핵심)
        view.clearFocus()
        // 키보드 숨기기
        keyboardController?.hide()
    }

    // 선택된 난이도가 없으면 다이얼로그를 띄우지 않음
    val difficulty = uiState.selectedBossDifficulty ?: return

    Dialog(onDismissRequest = onDismiss) {
        val isEnabled = uiState.bossPartyCreateCharacter != null && uiState.bossPartyCreateTitle.isNotBlank() && uiState.bossPartyCreateDescription.isNotBlank()
        Box(
            modifier = Modifier.size(0.dp)
                .focusRequester(dummyFocusRequester)
                .focusable()
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MapleStatBackground)
                .padding(20.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { clearFocusAll() },
        ) {
            Text(
                text = "BOSS PARTY",
                color = MapleStatTitle,
                style = Typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MapleWhite)
            ) {
                // 1. 보스 정보 섹션
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = uiState.selectedBoss.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = uiState.selectedBoss.bossName,
                            style = Typography.bodyLarge,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // 난이도 배지
                        Surface(
                            color = difficulty.badgeBackground,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(2.dp, difficulty.badgeOutline), // 🚀 테두리 적용
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = difficulty.displayName,
                                color = difficulty.badgeText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 14.sp,
                                fontFamily = PretendardFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp), color = MapleGray)

                // 2. 캐릭터 선택 및 확장 리스트 섹션
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedChar = uiState.bossPartyCreateCharacter ?: uiState.characters.firstOrNull()?.second
                    val worldName = uiState.characters.firstOrNull()?.first ?: "스카니아"
                    val worldMark = MapleWorld.getWorld(worldName)?.iconRes ?: R.drawable.ic_world_scania

                    // 메인 선택 영역
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                clearFocusAll()
                                isExpanded = !isExpanded
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CharacterProfileImage(
                            imageUrl = selectedChar?.characterImage,
                            size = 56.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedChar?.characterName ?: "캐릭터 선택",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    painter = painterResource(id = worldMark),
                                    contentDescription = "월드 이름",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "Lv. ${selectedChar?.characterLevel ?: 0} ${selectedChar?.characterClass ?: ""}",
                                fontSize = 13.sp,
                                color = MapleGray
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MapleOrange,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // 🚀 와이어프레임 스타일의 확장 리스트 (애니메이션 효과)
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp)
                                .border(1.dp, MapleBlack, RoundedCornerShape(8.dp)) // 리스트 외곽선
                                .background(MapleWhite)
                                .heightIn(max = 200.dp) // 최대 높이 제한
                        ) {
                            LazyColumn {
                                items(
                                    items = uiState.characters,
                                    key = { it.second.id }, // 각 캐릭터의 고유 ID (버벅임 방지에 필수)
                                    contentType = { "CHARACTER_ITEM" }
                                ) { character ->
                                    CharacterListItem(
                                        character = character,
                                        isSelected = character.second.id == selectedChar?.id
                                    ) {
                                        viewModel.onIntent(BossIntent.SelectBossPartyCharacter(character.second))
                                        isExpanded = false
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MapleGray)

                // 1. 보스방 제목 입력 필드
                BasicTextField(
                    value = uiState.bossPartyCreateTitle,
                    onValueChange = { viewModel.onIntent(BossIntent.UpdateBossPartyTitle(it)) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next // 키보드 버튼을 '다음'으로 설정
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // 다음 필드로 포커스 이동
                            descriptionFocusRequester.requestFocus()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        if (uiState.bossPartyCreateTitle.isEmpty()) {
                            Text(
                                text = "보스방 제목을 입력하세요",
                                color = MapleGray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                BasicTextField(
                    value = uiState.bossPartyCreateDescription,
                    onValueChange = { viewModel.onIntent(BossIntent.UpdateBossPartyDescription(it)) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .focusRequester(descriptionFocusRequester),
                    decorationBox = { innerTextField ->
                        if (uiState.bossPartyCreateDescription.isEmpty()) {
                            Text("소개글을 입력하세요", color = MapleGray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MapleOrange, // 혹은 MapleOrange 계열
                        fontSize = 13.sp,
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }

                // 4. 생성 버튼 (ViewModel의 생성 로직 실행)
                Button(
                    enabled = isEnabled,
                    onClick = {
                        if (isEnabled) {
                            viewModel.onIntent(BossIntent.CreateBossParty)
                            onDismiss() // 생성 후 닫기
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isEnabled) MapleOrange else MapleGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "보스 파티 생성",
                        color = if (isEnabled) MapleWhite else MapleBlack,
                        style = Typography.bodyLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CharacterProfileImage(
    imageUrl: String?,
    size: Dp
) {
    // URL별로 계산된 오프셋을 저장하는 캐시 (리스트 스크롤 시 재계산 방지)
    var yOffset by remember(imageUrl) { mutableStateOf(0f) }
    val scale = 2.5f

    Box(
        modifier = Modifier.size(size)
            .clip(CircleShape)
            .border(1.dp, MapleBlack, CircleShape)
            .background(MapleWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .allowHardware(false)
                .diskCachePolicy(CachePolicy.ENABLED) // 디스크 캐시 활성화
                .memoryCachePolicy(CachePolicy.ENABLED) // 메모리 캐시 활성화
                .build(),
            contentDescription = null,
            onSuccess = { state ->
                if (yOffset == 0f) {
                    // 1. Coil 3의 Image 객체를 가져옴
                    val coilImage = state.result.image

                    // 2. Android 플랫폼의 BitmapImage로 캐스팅하여 비트맵 추출
                    val bitmap = if (coilImage is BitmapImage) {
                        coilImage.bitmap
                    } else {
                        // 만약 BitmapImage가 아니라면 Drawable을 통해 변환 (최후의 수단)
                        null
                    }

                    bitmap?.let {
                        // 기존 픽셀 분석 로직 실행
                        val topPixel = getTopVisiblePixel(it)
                        yOffset = -topPixel + (it.height * 0.12f)
                    }
                }
            },
            modifier = Modifier.size(160.dp)
                .graphicsLayer {
                    translationY = yOffset
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0.5f, 0.2f)
                    // 레이어 캐싱 전략 추가
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                },
            contentScale = ContentScale.None
        )
    }
}

@Composable
fun CharacterListItem(
    character: Pair<String, CharacterSummary>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val worldName = character.first
    val worldMark = MapleWorld.getWorld(worldName)?.iconRes ?: R.drawable.ic_world_scania

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 리스트용 작은 프로필
        CharacterProfileImage(
            imageUrl = character.second.characterImage,
            size = 40.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = character.second.characterName,
                    fontFamily = PretendardFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = worldMark),
                    contentDescription = "월드 이름",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = "Lv. ${character.second.characterLevel} ${character.second.characterClass}",
                fontFamily = PretendardFamily,
                fontSize = 11.sp,
                color = MapleGray
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = MapleOrange,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}