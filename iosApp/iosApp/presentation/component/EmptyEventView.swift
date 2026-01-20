import SwiftUI

struct EmptyEventView: View {
    
    let message: String
    var showBossImage: Bool = false
    
    var body: some View {
        
        VStack(spacing: 16) {
            Image("ic_no_data").resizable()
                .scaledToFit()
                .frame(width: 100, height: 100)
            
            Text(message).font(.system(size: 16, weight: .medium))
                .foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 40)
    }
}
