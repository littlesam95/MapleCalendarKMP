package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.utils.MapleWorld

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldSelectBottomSheet(
    worlds: List<String>,
    onWorldClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MapleStatBackground, // 상단 어두운 배경색
        dragHandle = null // 핸들을 숨기거나 커스텀 가능
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // 1. 헤더 타이틀
            Text(
                text = "WORLD SELECT",
                color = MapleStatTitle, // 디자인 속 노란색/라임색
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(20.dp)
            )

            // 2. 월드 리스트 컨테이너 (흰색 라운드 박스)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MapleWhite
            ) {
                LazyColumn {
                    itemsIndexed(worlds) { index, world ->
                        WorldItem(
                            worldName = world,
                            onClick = { onWorldClick(world) }
                        )
                        // 마지막 아이템이 아니면 구분선 추가
                        if (index < worlds.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MapleGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorldItem(
    worldName: String,
    onClick: () -> Unit
) {
    val worldMark = MapleWorld.getWorld(worldName)?.iconRes ?: R.drawable.ic_world_scania

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = worldMark),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = worldName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MapleBlack
        )
    }
}