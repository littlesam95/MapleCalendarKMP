package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography

@Composable
fun LoginSuccessDialog(
    userName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = { /* 외부 터치 시 아무 작업도 하지 않음 */ },
        properties = DialogProperties(
            dismissOnBackPress = false,   // 뒤로가기 버튼으로 닫기 방지
            dismissOnClickOutside = false // 외부 터치로 닫기 방지
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MapleStatBackground,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 상단 타이틀 (형광 노란색 계열)
                Text(
                    text = "LOGIN SUCCESS",
                    style = Typography.titleMedium,
                    color = MapleStatTitle,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. 중앙 흰색 카드 섹션
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MapleWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "환영합니다. ${userName}님!\n캐릭터를 등록해보시겠어요?",
                            style = Typography.bodyLarge,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. 하단 버튼 섹션 (Row)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 나중에 할게요 (빨간색 계열)
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1.2f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MapleBlack)
                            ) {
                                Text("나중에 할게요", color = MapleWhite, style = Typography.bodySmall)
                            }

                            // 등록할게요 (주황색/황금색 계열)
                            Button(
                                onClick = onConfirm,
                                modifier = Modifier.weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MapleOrange)
                            ) {
                                Text("등록할게요", color = MapleWhite, style = Typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}