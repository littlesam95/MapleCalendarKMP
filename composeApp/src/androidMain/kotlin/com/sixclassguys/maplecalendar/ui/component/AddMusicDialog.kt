package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistIntent
import com.sixclassguys.maplecalendar.presentation.playlist.PlaylistViewModel
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMusicDialog(
    viewModel: PlaylistViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MapleStatBackground
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // 1. 다이얼로그 제목 (연두색)
                Text(
                    text = "ADD MUSIC",
                    color = MapleStatTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))

                // 2. 플레이리스트 선택 영역 (흰색 카드)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MapleWhite
                ) {
                    when {
                        uiState.isLoading -> {
                            Box(
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
                                        text = "BGM을 추가하는 중이에요...",
                                        color = MapleWhite,
                                        style = Typography.bodyLarge
                                    )
                                }
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "플레이리스트 선택",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MapleBlack
                                )
                                Spacer(Modifier.height(12.dp))

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = uiState.selectedPlaylistToAdd?.name ?: "선택 항목 없음",
                                        onValueChange = {},
                                        readOnly = true, // 직접 입력 방지
                                        modifier = Modifier.fillMaxWidth()
                                            .menuAnchor(), // 메뉴가 붙을 위치
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MapleOrange,
                                            unfocusedBorderColor = MapleGray,
                                            focusedTextColor = MapleBlack,
                                            unfocusedTextColor = MapleBlack
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    // 드롭다운 항목 리스트
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(MapleWhite)
                                    ) {
                                        uiState.myPlaylists.forEach { playlist ->
                                            DropdownMenuItem(
                                                text = { Text(text = playlist.name, color = MapleBlack) },
                                                onClick = {
                                                    viewModel.onIntent(PlaylistIntent.UpdatePlaylistToAddMapleBgm(playlist))
                                                    expanded = false
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                if (uiState.errorMessage != null) {
                                    Text(
                                        text = uiState.errorMessage!!,
                                        color = MapleOrange, // 혹은 MapleOrange 계열
                                        fontSize = 13.sp,
                                        fontFamily = PretendardFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                // 3. 음악 추가하기 버튼 (오렌지색)
                                Button(
                                    onClick = {
                                        val playlistId = uiState.selectedPlaylistToAdd?.id ?: 0L
                                        val bgmId = uiState.selectedBgm?.id ?: 0L
                                        viewModel.onIntent(PlaylistIntent.AddMapleBgmToPlaylist(playlistId, bgmId))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MapleOrange)
                                ) {
                                    Text(
                                        text = "음악 추가하기",
                                        color = MapleWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
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