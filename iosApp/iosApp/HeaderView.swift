//
//  HeaderView.swift
//  iosApp
//
//  Created by 이상혁 on 1/4/26.
//

import SwiftUI

struct HeaderView: View {
    var body: some View {
        HStack {
            Image( "Maplendar")
                .resizable()     // 크기 조절이 가능하게 설정
                .scaledToFit()   // 비율 유지
                .frame(height: 42) // 세로 길이를 42로 고정

            Spacer()

            HStack(spacing: 16) {
                Image(systemName: "bell").font(.system(size: 30))
            }
            .foregroundColor(.orange)
        }
        .padding(.top, 8)
        .padding(.horizontal, 20)
    }
}
