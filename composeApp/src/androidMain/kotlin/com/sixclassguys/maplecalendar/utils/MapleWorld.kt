package com.sixclassguys.maplecalendar.utils

import androidx.annotation.DrawableRes
import com.sixclassguys.maplecalendar.R

enum class MapleWorld(val worldName: String, @DrawableRes val iconRes: Int) {

    SCANIA("스카니아", R.drawable.ic_world_scania),
    BERA("베라", R.drawable.ic_world_bera),
    LUNA("루나", R.drawable.ic_world_luna),
    ZENITH("제니스", R.drawable.ic_world_zenith),
    CROA("크로아", R.drawable.ic_world_croa),
    UNION("유니온", R.drawable.ic_world_union),
    ELYSIUM("엘리시움", R.drawable.ic_world_elysium),
    ENOSIS("이노시스", R.drawable.ic_world_enosis),
    RED("레드", R.drawable.ic_world_red),
    AURORA("오로라", R.drawable.ic_world_aurora),
    ARCANE("아케인", R.drawable.ic_world_arcane),
    NOVA("노바", R.drawable.ic_world_nova),
    CHALLENGERS("챌린저스", R.drawable.ic_world_challengers),
    CHALLENGERS2("챌린저스2", R.drawable.ic_world_challengers),
    CHALLENGERS3("챌린저스3", R.drawable.ic_world_challengers),
    CHALLENGERS4("챌린저스4", R.drawable.ic_world_challengers),
    EOS("에오스", R.drawable.ic_world_eos),
    HELIOS("핼리오스", R.drawable.ic_world_helios);

    companion object {

        fun getWorld(name: String): MapleWorld? {
            return entries.find { it.worldName == name }
        }
    }
}