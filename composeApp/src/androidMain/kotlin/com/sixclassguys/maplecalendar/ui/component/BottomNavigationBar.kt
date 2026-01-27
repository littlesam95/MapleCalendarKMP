package com.sixclassguys.maplecalendar.ui.component

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.navigation.Navigation
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleWhite

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    onCalendarClicked: () -> Unit
) {
    val context = LocalContext.current
    val navItems = listOf(
        Navigation.Home, Navigation.Playlist, Navigation.Board, Navigation.Setting
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val systemBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier.fillMaxWidth()
            .height(90.dp + systemBottomPadding)
            .background(Color.Transparent)
            .pointerInput(Unit) {} // 하단 터치 전파 방지
    ) {
        // 1. 배경 Canvas (와이어프레임처럼 주황색 적용)
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val fabRadius = 38.dp.toPx()
            val fabSpacing = 8.dp.toPx()
            val cutoutRadius = fabRadius + fabSpacing
            val centerX = width / 2

            // 와이어프레임의 부드러운 곡선 느낌 재현
            val curveWidth = cutoutRadius * 1.5f
            val curveHeight = cutoutRadius * 0.8f

            val basePath = Path().apply {
                moveTo(0f, 20.dp.toPx()) // 상단 시작점
                lineTo(centerX - curveWidth, 20.dp.toPx())
                cubicTo(
                    x1 = centerX - curveWidth / 2f, y1 = 20.dp.toPx(),
                    x2 = centerX - cutoutRadius, y2 = 20.dp.toPx() + curveHeight,
                    x3 = centerX, y3 = 20.dp.toPx() + curveHeight
                )
                cubicTo(
                    x1 = centerX + cutoutRadius, y1 = 20.dp.toPx() + curveHeight,
                    x2 = centerX + curveWidth / 2f, y2 = 20.dp.toPx(),
                    x3 = centerX + curveWidth, y3 = 20.dp.toPx()
                )
                lineTo(width, 20.dp.toPx())
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = basePath,
                color = MapleOrange,
                style = Fill
            )
        }

        // 2. 아이콘 영역
        Row(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = systemBottomPadding + 8.dp) // 시스템 바 위로 배치
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                if (index == 2) Spacer(modifier = Modifier.width(70.dp))

                MapleBottomNavItem(
                    item = item,
                    isSelected = currentRoute == item.destination,
                    onClick = {
                        // BGM 플레이리스트와 게시판 기능은 준비중
                        if ((item == Navigation.Playlist) || (item == Navigation.Board)) {
                            Toast.makeText(context, "준비중입니다.", Toast.LENGTH_SHORT).show()
                        } else {
//                            navController.navigate(item.destination) {
//                                popUpTo(navController.graph.startDestinationId)
//                                launchSingleTop = true
//                            }
                            if (currentRoute != item.destination) {
                                navController.navigate(item.destination) {
                                    // 💡 핵심: 현재 스택에 있는 모든 화면을 제거하고 이동합니다.
                                    // 이렇게 하면 항상 스택에는 현재 화면 '딱 하나'만 남게 됩니다.
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            }
        }

        // 3. 중앙 FAB (흰색 테두리가 있는 단풍잎)
        FloatingActionButton(
            onClick = { onCalendarClicked() },
            containerColor = MapleOrange,
            contentColor = MapleWhite,
            shape = CircleShape,
            modifier = Modifier.size(60.dp)
                .align(Alignment.TopCenter)
                // FAB 위치는 고정 (시스템 바 패딩에 영향받지 않도록)
                .offset(y = (-10).dp)
                .zIndex(1F)
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(R.drawable.bottomnav_calendar),
                contentDescription = "달력",
                tint = Color.Unspecified
            )
        }
    }
}

// 개별 네비게이션 아이템 컴포저블
@Composable
fun MapleBottomNavItem(
    item: Navigation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 아이콘 색상을 흰색/연한주황색으로 대비 조정
        Icon(
            painter = painterResource(
                id = if (isSelected) item.selectedIconRes!! else item.unselectedIconRes!!
            ),
            contentDescription = item.label,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // [핵심] 선택 표시 인디케이터 (밑줄 모양)
        Box(
            modifier = Modifier.width(20.dp)
                .height(3.dp)
                .background(
                    color = if (isSelected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}