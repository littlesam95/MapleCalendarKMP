//
//  TabMenuView.swift
//  iosApp
//
//  Created by 이상혁 on 1/6/26.
//

import SwiftUI

enum MenuTab {
    case equipment, stat, challenge, skill, record
}

struct TabMenuView: View {
    @Binding var selected: MenuTab

    let tabs: [(MenuTab, String, String)] = [
        (.equipment, "장비", "hammer"),
        (.stat, "스탯", "atom"),
        (.challenge, "도전", "trophy"),
        (.skill, "스킬", "s.circle"),
        (.record, "기록", "clock")
    ]

    var body: some View {
        HStack {
            ForEach(tabs, id: \.0) { tab in
                VStack {
                    Image(systemName: tab.2)
                    Text(tab.1)
                        .font(.caption)
                }
                .foregroundColor(selected == tab.0 ? .white : .gray)
                .frame(maxWidth: .infinity)
                .onTapGesture {
                    selected = tab.0
                }
            }
        }
        .padding(.vertical, 8)
    }
}

