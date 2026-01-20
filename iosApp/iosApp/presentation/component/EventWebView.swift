import SwiftUI
import WebKit
import shared

struct EventWebView: UIViewRepresentable {
    let url: String
    let parentOffset: CGFloat      // 현재 부모의 스크롤 위치
    let maxParentScroll: CGFloat   // 부모가 최대로 내려갔을 때의 위치값

    private var mobileUrl: String {
        if url.contains("maplestory.nexon.com") && !url.contains("https://m.") {
            return url.replacingOccurrences(of: "https://", with: "https://m.")
        }
        return url
    }

    func makeUIView(context: Context) -> WKWebView {
        
        let webView = WKWebView()
        webView.customUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1"
        
        // 1. 초기 스크롤 및 바운스 비활성화
        webView.scrollView.isScrollEnabled = false
        webView.scrollView.bounces = false
        
        // 2. 웹뷰 자체의 델리게이트 설정 (필요 시 스크롤 감지)
        webView.scrollView.delegate = context.coordinator
        
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        if uiView.url == nil, let urlObj = URL(string: mobileUrl) {
            uiView.load(URLRequest(url: urlObj))
        }

        // 💡 핵심: 부모 스크롤이 끝에 도달했는지 판단
        // 안드로이드의 canParentScrollDown 조건과 동일한 역할
        let isParentAtBottom = -parentOffset >= (maxParentScroll - 1)

        if uiView.scrollView.isScrollEnabled != isParentAtBottom {
            uiView.scrollView.isScrollEnabled = isParentAtBottom
            uiView.scrollView.bounces = isParentAtBottom
        }
    }

    // 💡 안드로이드의 OnTouchListener 역할을 대신할 Coordinator
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UIScrollViewDelegate {
        
        var parent: EventWebView

        init(_ parent: EventWebView) {
            self.parent = parent
        }

        func scrollViewDidScroll(_ scrollView: UIScrollView) {
            // 3. 웹뷰 내부에서 위로 스크롤하다가 맨 위(contentOffset.y <= 0)에 도달하면
            // 부모 스크롤이 다시 동작할 수 있도록 스크롤을 잠그는 로직을 여기에 추가할 수 있음
            if scrollView.contentOffset.y <= 0 {
                // 웹뷰가 맨 위일 때는 스크롤을 살짝 튕겨주거나 상태를 변경하여
                // SwiftUI 부모 ScrollView가 이벤트를 가져가게 유도
            }
        }
    }
}
