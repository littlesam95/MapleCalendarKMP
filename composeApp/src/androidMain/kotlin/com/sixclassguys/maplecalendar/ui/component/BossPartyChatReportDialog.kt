package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sixclassguys.maplecalendar.domain.model.BossPartyChat
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.util.ReportReason

@Composable
fun BossPartyChatReportDialog(
    chat: BossPartyChat?, // 신고 대상 메시지 정보
    onDismiss: () -> Unit,
    onReportSubmit: (Long, ReportReason, String?) -> Unit
) {
    if (chat == null) return

    var expanded by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf(ReportReason.ABUSE) }
    var detailText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MapleStatBackground, // 다크한 배경색
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "REPORT",
                    style = Typography.titleMedium,
                    color = MapleStatTitle,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Surface(
                            color = MapleWhite,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 캐릭터 프로필 (이미지 URL이 있다면 AsyncImage 권장)
                                    CharacterProfileImage(imageUrl = chat.senderImage, size = 40.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = chat.senderName, // 예: 오한별
                                        style = Typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MapleBlack
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // 채팅 내용 스냅샷 영역
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(MapleGray, RoundedCornerShape(9.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = chat.content,
                                        color = MapleBlack,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 신고 사유 선택 (Dropdown)
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("신고 사유", color = MapleBlack, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .border(1.dp, MapleBlack, RoundedCornerShape(8.dp))
                                    .background(MapleWhite, RoundedCornerShape(8.dp))
                                    .clickable { expanded = true }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(selectedReason.description, color = MapleBlack)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MapleOrange)
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    containerColor = MapleWhite,
                                    onDismissRequest = { expanded = false }
                                )  {
                                    ReportReason.entries.forEach { reason ->
                                        DropdownMenuItem(
                                            text = { Text(reason.description) },
                                            onClick = {
                                                selectedReason = reason
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 신고 사유 작성 (TextField)
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("신고 사유 작성", color = MapleBlack, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            TextField(
                                value = detailText,
                                onValueChange = { detailText = it },
                                modifier = Modifier.fillMaxWidth()
                                    .height(120.dp),
                                placeholder = { Text("상세 내용을 입력해주세요.", fontSize = 13.sp) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MapleGray,
                                    unfocusedContainerColor = MapleGray,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 신고하기 버튼
                        Button(
                            onClick = { onReportSubmit(chat.id, selectedReason, detailText) },
                            modifier = Modifier.fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "신고하기",
                                color = MapleWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}