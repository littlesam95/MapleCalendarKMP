package com.sixclassguys.maplecalendar.navigation

import androidx.annotation.DrawableRes
import com.sixclassguys.maplecalendar.R

sealed class Navigation(
    val destination: String,
    @DrawableRes val selectedIconRes: Int? = null,
    @DrawableRes val unselectedIconRes: Int? = null,
    val label: String? = null
) {

    data object Splash : Navigation("splash")

    data object Home :
        Navigation("home", R.drawable.bottomnav_home, R.drawable.bottomnav_home_disabled, "홈")

    data object Playlist :
        Navigation("playlist", R.drawable.bottomnav_playlist, R.drawable.bottomnav_playlist_disabled, "플리")

    data object Calendar : Navigation("calendar")

    data object EventDetail : Navigation("eventDetail")

    data object Board :
        Navigation("board", R.drawable.bottomnav_board, R.drawable.bottomnav_board_disabled, "모아보기")

    data object Setting :
        Navigation("setting", R.drawable.bottomnav_setting, R.drawable.bottomnav_setting_disabled, "설정")

    data object Login : Navigation("login")

    data object SelectRepresentativeCharacter : Navigation("selectRepresentativeCharacter")

    data object MapleCharacterList : Navigation("mapleCharacterList")

    data object MapleCharacterFetch : Navigation("mapleCharacterFetch")

    data object MapleCharacterSubmit : Navigation("mapleCharacterSubmit")
}