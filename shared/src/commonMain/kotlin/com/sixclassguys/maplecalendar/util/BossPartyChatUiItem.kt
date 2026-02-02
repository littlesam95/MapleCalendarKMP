package com.sixclassguys.maplecalendar.util

import com.sixclassguys.maplecalendar.domain.model.BossPartyChat

sealed class BossPartyChatUiItem {

    data class Message(
        val chat: BossPartyChat,
        val showProfile: Boolean, // 닉네임과 프로필 사진을 보여줄지 여부
        val showTime: Boolean     // 시간을 보여줄지 여부 (고도화용)
    ) : BossPartyChatUiItem()

    data class DateDivider(val date: String) : BossPartyChatUiItem()
}