import SwiftUI
import shared

struct ContentView: View {
    @State private var selectedTab = 0
    
    var body: some View {
        ZStack(alignment: .bottom) {
            // 배경색이 필요하다면 여기에 배치 (예: 검은색 배경)
            Color.white.ignoresSafeArea()

            VStack(spacing: 0) {
                HeaderView()
                // 1. 상단 콘텐츠 영역 (스크롤 가능하게 구성하는 것이 좋습니다)
                ScrollView {
                    VStack(spacing: 20) {
                        
                        CharacterCardView()
                        
                        EventListView()
                       
                    }
                    .padding() // 콘텐츠에만 패딩 적용
                }
                
//                Spacer(minLength: 0)
                
                // 2. 바텀 탭바 영역
                
            }
            BottomTabBarView(selectedTab: $selectedTab)
        }
        // 탭바가 하단 홈 바 영역까지 꽉 채우도록 설정
        .ignoresSafeArea(.container, edges: .bottom)
    }
}

    
   
      
       

         
                
            




struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
