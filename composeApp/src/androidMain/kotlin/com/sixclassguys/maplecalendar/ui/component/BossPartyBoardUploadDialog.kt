package com.sixclassguys.maplecalendar.ui.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
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

@Composable
fun BossPartyBoardUploadDialog(
    viewModel: BossViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // 2. 선택된 URI를 ByteArray로 변환 (서버 전송 준비)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()

            if (bytes != null) {
                // 3. ViewModel의 Intent를 통해 이미지 데이터 업데이트
                viewModel.onIntent(BossIntent.UpdateBossPartyBoardImage(bytes))
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MapleStatBackground
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "IMAGE UPLOAD",
                    color = MapleStatTitle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    when {
                        uiState.isLoading -> Box(
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
                                    text = "게시글을 올리는 중이에요...",
                                    color = MapleWhite,
                                    style = Typography.bodyLarge
                                )
                            }
                        }

                        else -> Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // 1. 이미지 프리뷰 카드
                            Card(
                                modifier = Modifier.fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MapleWhite)
                            ) {
                                val firstImage = uiState.uploadImage.getOrNull(0)

                                if (firstImage != null) {
                                    AsyncImage(
                                        model = firstImage,
                                        contentDescription = "Selected Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(), // 부모 높이만큼 채움
                                        contentAlignment = Alignment.Center
                                    ) {
                                        EmptyEventScreen("이미지를 업로드해주세요!")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. 한줄평 입력창
                            TextField(
                                value = uiState.uploadComment,
                                onValueChange = { viewModel.onIntent(BossIntent.UpdateBossPartyBoardComment(it)) },
                                placeholder = { Text("한줄평", color = MapleGray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MapleWhite,
                                    unfocusedContainerColor = MapleWhite,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
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

                            // 3. 업로드 버튼
                            Button(
                                enabled = (uiState.uploadImage.isNotEmpty()),
                                onClick = { viewModel.onIntent(BossIntent.SubmitBossPartyBoard) },
                                modifier = Modifier.fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MapleOrange,
                                    disabledContainerColor = MapleGray
                                )
                            ) {
                                Text(
                                    text = "업로드",
                                    color = MapleWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}