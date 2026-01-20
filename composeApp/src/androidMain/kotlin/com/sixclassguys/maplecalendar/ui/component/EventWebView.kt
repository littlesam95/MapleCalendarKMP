package com.sixclassguys.maplecalendar.ui.component

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.sixclassguys.maplecalendar.ui.calendar.COLLAPSED_TOP_BAR_HEIGHT
import com.sixclassguys.maplecalendar.utils.convertToMobileUrl

@SuppressLint("ClickableViewAccessibility", "ConfigurationScreenWidthHeight",
    "SetJavaScriptEnabled"
)
@Composable
fun EventWebView(
    url: String,
    parentScrollState: ScrollState // 💡 부모의 스크롤 상태를 반드시 받아와야 합니다.
) {
    val mobileUrl = remember(url) { convertToMobileUrl(url) }

    // 💡 1. 기기의 화면 높이 계산 (상단바 등을 제외한 가용 높이)
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp - (COLLAPSED_TOP_BAR_HEIGHT * 4)

    // 터치 방향 판단을 위한 변수
    var lastY = 0f

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }
                webViewClient = WebViewClient()

                setOnTouchListener { v, event ->
                    // 1. 부모가 더 내려갈 수 있는지 확인 (끝까지 내려갔으면 false)
                    val canParentScrollDown = parentScrollState.canScrollForward
                    // 2. 웹뷰가 최상단인지 확인
                    val isWebViewAtTop = !v.canScrollVertically(-1)

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastY = event.y
                            // 일단 MOVE를 감시하기 위해 가로채기를 막습니다.
                            v.parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val deltaY = lastY - event.y // 위로 올리면 양수(+)
                            val isSwipingUp = deltaY > 0 // 사용자가 페이지를 아래로 더 내리려는 동작

                            if (isSwipingUp && canParentScrollDown) {
                                // 💡 핵심: 사용자가 내리려고 하는데 부모가 아직 끝이 아니라면?
                                // 권한을 부모에게 넘겨서 전체 페이지가 내려가게 합니다.
                                v.parent.requestDisallowInterceptTouchEvent(false)
                            } else if (!isSwipingUp && isWebViewAtTop) {
                                // 💡 반대로 위로 올리려는데 웹뷰가 이미 맨 위라면?
                                // 권한을 부모에게 넘겨서 전체 페이지가 위로 올라가게 합니다.
                                v.parent.requestDisallowInterceptTouchEvent(false)
                            } else {
                                // 그 외(부모가 끝까지 내려갔을 때만) 웹뷰 내부 스크롤 허용
                                v.parent.requestDisallowInterceptTouchEvent(true)
                            }
                            lastY = event.y
                        }
                    }
                    false
                }
            }
        },
        update = { webView ->
            if (webView.url != mobileUrl) {
                webView.loadUrl(mobileUrl)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeightDp)
    )
}