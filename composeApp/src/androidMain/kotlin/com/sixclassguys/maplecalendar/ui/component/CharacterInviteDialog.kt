package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun CharacterInviteDialog(
    onDismiss: () -> Unit,
    viewModel: BossViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allWorlds = MapleWorld.entries.map { it.worldName }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MapleStatBackground
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "CHARACTER INVITE",
                    color = MapleStatTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    when {
                        uiState.isMemberInviteLoading -> Box(
                            modifier = Modifier.fillMaxWidth()
                                .background(MapleBlack.copy(alpha = 0.7f)) // 화면 어둡게 처리
                                .pointerInput(Unit) {}, // 터치 이벤트 전파 방지 (클릭 막기)
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = MapleOrange,
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "유저를 초대하는 중이에요...",
                                    color = MapleWhite,
                                    style = Typography.bodyLarge
                                )
                            }
                        }

                        else -> {
                            Column {
                                // 닉네임 입력창
                                TextField(
                                    value = uiState.searchKeyword,
                                    onValueChange = { viewModel.onIntent(BossIntent.SearchCharacters(it, allWorlds)) },
                                    placeholder = { Text("닉네임 입력") },
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(16.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFE0E0E0),   // 포커스 되었을 때 배경
                                        unfocusedContainerColor = Color(0xFFE0E0E0), // 포커스 없을 때 배경
                                        disabledContainerColor = Color(0xFFE0E0E0),
                                        focusedIndicatorColor = Color.Transparent,    // 밑줄 제거
                                        unfocusedIndicatorColor = Color.Transparent,  // 밑줄 제거
                                        focusedTextColor = MapleBlack,
                                        unfocusedTextColor = MapleBlack
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
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

                                // 검색 결과 리스트
                                if (uiState.searchCharacters.isEmpty() || uiState.searchKeyword.isBlank()) {
                                    EmptyEventScreen("검색 결과가 없어요.")
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 500.dp)
                                    ) {
                                        items(uiState.searchCharacters) {
                                            SearchCharacterItem(
                                                character = it,
                                                onInvite = { viewModel.onIntent(BossIntent.InviteBossPartyMember(it.second.id)) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchCharacterItem(
    character: Pair<String, CharacterSummary>,
    onInvite: () -> Unit
) {
    val worldName = character.first
    val worldMark = MapleWorld.getWorld(worldName)?.iconRes ?: R.drawable.ic_world_scania

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onInvite)
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
    }
}