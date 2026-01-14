package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite

@Composable
fun TimeInputRow(
    hour: String,
    onHourChange: (String) -> Unit,
    minute: String,
    onMinuteChange: (String) -> Unit,
    onAddClick: () -> Unit,
    isAddEnabled: Boolean,
    focusRequester: FocusRequester,
    onNext: () -> Unit,
    onDone: () -> Unit// 날짜 선택 여부에 따라 제어
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // 중앙 정렬
    ) {
        // 시간 입력 필드
        HourTextField(
            value = hour,
            onValueChange = onHourChange,
            onNext = onNext
        )
        Text(
            text = " 시 ",
            color = MapleBlack,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // 분 입력 필드
        MinuteTextField(
            value = minute,
            onValueChange = onMinuteChange,
            focusRequester = focusRequester,
            onDone = onDone
        )
        Text(
            text = " 분 ",
            color = MapleBlack,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 추가 버튼 (Group 14 스타일)
        Button(
            onClick = onAddClick,
            enabled = isAddEnabled,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MapleOrange,
                disabledContainerColor = MapleGray.copy(alpha = 0.5f) // 비활성화 시 색상
            ),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            Text("추가", color = MapleWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HourTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit // 다음 포커스로 이동하기 위한 콜백
) {
    BasicTextField(
        value = value,
        onValueChange = { input ->
            val filtered = input.filter { it.isDigit() }

            if (filtered.isEmpty()) {
                onValueChange("")
            } else {
                // 2. 정수로 변환하여 범위 체크
                val num = filtered.toIntOrNull() ?: 0
                if (num > 23) {
                    onValueChange("23")
                } else {
                    // 최대 2자까지만 입력 허용
                    onValueChange(filtered.take(2))
                }
            }
        },
        modifier = Modifier
            .size(60.dp, 36.dp)
            .background(MapleGray, CircleShape),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MapleBlack
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next // 키보드 버튼을 '다음'으로 설정
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() } // 완료 시 다음 필드로 포커스 이동
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                innerTextField()
            }
        }
    )
}

@Composable
fun MinuteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester, // 포커스를 받기 위한 객체
    onDone: () -> Unit // 추가 로직 실행을 위한 콜백
) {
    BasicTextField(
        value = value,
        onValueChange = { input ->
            val filtered = input.filter { it.isDigit() }

            if (filtered.isEmpty()) {
                onValueChange("")
            } else {
                val num = filtered.toIntOrNull() ?: 0
                if (num > 59) {
                    onValueChange("59")
                } else {
                    onValueChange(filtered.take(2))
                }
            }
        },
        modifier = Modifier
            .size(60.dp, 36.dp)
            .focusRequester(focusRequester) // 포커스 요청자 등록
            .background(MapleGray, CircleShape),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MapleBlack
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done // 키보드 버튼을 '완료'로 설정
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() } // 추가 로직 실행 및 키보드 닫기
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                innerTextField()
            }
        }
    )
}