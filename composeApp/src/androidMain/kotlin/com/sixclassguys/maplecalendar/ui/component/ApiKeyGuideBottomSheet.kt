package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyGuideBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val pagerState = rememberPagerState(pageCount = { 4 })

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MapleStatBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
                .fillMaxHeight(0.85f)
        ) {
            Text(
                text = "CREATE API KEY",
                color = MapleStatTitle,
                style = Typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp),
                color = MapleWhite
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 페이지 점 표시 (Indicator)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(4) { i ->
                            Box(
                                modifier = Modifier.size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (pagerState.currentPage == i) MapleOrange else MapleGray)
                            )
                        }
                    }

                    // 가로 스와이프 컨텐츠 영역
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        GuideStepPage(step = page + 1)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GuideStepPage(step: Int) {
    val title = when (step) {
        1 -> "NEXON OPEN API 시작하기"
        2 -> "애플리케이션 등록하기"
        3 -> "애플리케이션 정보 입력하기"
        else -> "발급받은 API 가져오기"
    }
    val content = when (step) {
        1 -> "NEXON Open API 홈페이지로 접속하셔서\n" +
                "시작하기 버튼을 눌러주세요.\n" +
                "로그인이 되어 있지 않다면 넥슨아이디로 로그인을\n" +
                "하셔야 합니다."
        2 -> "상단 목록의 마이 페이지 - NEXON Open API에서\n" +
                "애플리케이션 등록을 선택해주세요."
        3 -> "약관에 동의하시고 이미지와 같이\n" +
                "정보를 입력해주세요."
        else -> "애플리케이션 목록에서 생성한 애플리케이션을\n" +
                "확인하고, 첫번째 API Key 옆의 버튼을 눌러\n" +
                "Key를 복사하시고 Maplendar로 돌아오셔서\n" +
                "API Key를 입력하시면 끝!"
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "STEP $step.",
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp
        )
        Text(
            text = title,
            fontFamily = PretendardFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (step != 4) {
            // Step 1, 2, 3: 이미지 1개
            GuideImageHolder(step = step, label = "가이드 이미지")
        } else {
            // Step 4: 이미지 2개 위아래 배치
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GuideImageHolder(step = 4, label = "내 애플리케이션 목록 이미지")
                GuideImageHolder(step = 5, label = "API Key 정보 이미지")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 하단 설명 텍스트
        Text(
            text = content,
            style = Typography.bodyMedium
        )
    }
}

@Composable
fun GuideImageHolder(
    step: Int,
    label: String
) {
    val imageId = when (step) {
        1 -> R.drawable.bg_character_fetch_step_01
        2 -> R.drawable.bg_character_fetch_step_02
        3 -> R.drawable.bg_character_fetch_step_03
        4 -> R.drawable.bg_character_fetch_step_04
        else -> R.drawable.bg_character_fetch_step_04_2
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = label,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}