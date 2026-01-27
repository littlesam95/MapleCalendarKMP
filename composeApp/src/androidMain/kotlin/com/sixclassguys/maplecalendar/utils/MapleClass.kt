package com.sixclassguys.maplecalendar.utils

import androidx.annotation.DrawableRes
import com.sixclassguys.maplecalendar.R

enum class MapleClass(
    val jobName: String,
    val group: MapleClassGroup
) {

    // 전사
    SWORDMAN("검사", MapleClassGroup.WARRIOR),
    FIGHTER("파이터", MapleClassGroup.WARRIOR),
    CRUSADER("크루세이더", MapleClassGroup.WARRIOR),
    HERO("히어로", MapleClassGroup.WARRIOR),
    PAGE("페이지", MapleClassGroup.WARRIOR),
    WHITE_KNIGHT("나이트", MapleClassGroup.WARRIOR),
    PALADIN("팔라딘", MapleClassGroup.WARRIOR),
    SPEARMAN("스피어맨", MapleClassGroup.WARRIOR),
    BERSERKER("버서커", MapleClassGroup.WARRIOR),
    DARK_KNIGHT("다크나이트", MapleClassGroup.WARRIOR),
    DAWN_WARRIOR("소울마스터", MapleClassGroup.WARRIOR),
    ARAN("아란", MapleClassGroup.WARRIOR),
    MIKHAIL("미하일", MapleClassGroup.WARRIOR),
    DEMON_SLAYER("데몬슬레이어", MapleClassGroup.WARRIOR),
    DEMON_AVENGER("데몬어벤져", MapleClassGroup.WARRIOR),
    BLASTER("블래스터", MapleClassGroup.WARRIOR),
    ZERO("제로", MapleClassGroup.WARRIOR),
    KAISER("카이저", MapleClassGroup.WARRIOR),
    LEN("렌", MapleClassGroup.WARRIOR),
    ADELE("아델", MapleClassGroup.WARRIOR),

    // 마법사
    MAGICIAN("매지션", MapleClassGroup.MAGICIAN),
    WIZARD_FIRE_POISON("위자드(불,독)", MapleClassGroup.MAGICIAN),
    MAGE_FIRE_POISON("메이지(불,독)", MapleClassGroup.MAGICIAN),
    ARCHMAGE_FIRE_POISON("아크메이지(불,독)", MapleClassGroup.MAGICIAN),
    WIZARD_ICE_LIGHTNING("위자드(썬,콜)", MapleClassGroup.MAGICIAN),
    MAGE_ICE_LIGHTNING("메이지(썬,콜)", MapleClassGroup.MAGICIAN),
    ARCH_MAGE_ICE_LIGHTNING("아크메이지(썬,콜)", MapleClassGroup.MAGICIAN),
    CLERIC("클레릭", MapleClassGroup.MAGICIAN),
    PRIEST("프리스트", MapleClassGroup.MAGICIAN),
    BISHOP("비숍", MapleClassGroup.MAGICIAN),
    BLAZE_WIZARD("플레임위자드", MapleClassGroup.MAGICIAN),
    EVAN("에반", MapleClassGroup.MAGICIAN),
    LUMINOUS("루미너스", MapleClassGroup.MAGICIAN),
    BATTLE_MAGE("배틀메이지", MapleClassGroup.MAGICIAN),
    KINESIS("키네시스", MapleClassGroup.MAGICIAN),
    ILLIUM("일리움", MapleClassGroup.MAGICIAN),
    LARA("라라", MapleClassGroup.MAGICIAN),

    // 궁수
    ARCHER("아처", MapleClassGroup.ARCHER),
    HUNTER("헌터", MapleClassGroup.ARCHER),
    RANGER("레인저", MapleClassGroup.ARCHER),
    BOW_MASTER("보우마스터", MapleClassGroup.ARCHER),
    CROSSBOWMAN("사수", MapleClassGroup.ARCHER),
    SNIPER("저격수", MapleClassGroup.ARCHER),
    MARKSMAN("신궁", MapleClassGroup.ARCHER),
    ANCIENT_ARCHER("에인션트 아처", MapleClassGroup.ARCHER),
    CHASER("체이서", MapleClassGroup.ARCHER),
    PATH_FINDER("패스파인더", MapleClassGroup.ARCHER),
    WIND_ARCHER("윈드브레이커", MapleClassGroup.ARCHER),
    MERCEDES("메르세데스", MapleClassGroup.ARCHER),
    WILD_HUNTER("와일드헌터", MapleClassGroup.ARCHER),
    KAIN("카인", MapleClassGroup.ARCHER),

    // 도적
    ROGUE("로그", MapleClassGroup.THIEF),
    ASSASSIN("어쌔신", MapleClassGroup.THIEF),
    HERMIT("허밋", MapleClassGroup.THIEF),
    NIGHT_LORD("나이트로드", MapleClassGroup.THIEF),
    BANDIT("시프", MapleClassGroup.THIEF),
    CHIEF_BANDIT("시프마스터", MapleClassGroup.THIEF),
    SHADOWER("섀도어", MapleClassGroup.THIEF),
    BLADE_RECRUIT("세미듀어러", MapleClassGroup.THIEF),
    BLADE_ACOLYTE("듀어러", MapleClassGroup.THIEF),
    BLADE_SPECIALIST("듀얼마스터", MapleClassGroup.THIEF),
    BLADE_LORD("슬래셔", MapleClassGroup.THIEF),
    BLADE_MASTER("듀얼블레이더", MapleClassGroup.THIEF),
    NIGHT_WALKER("나이트워커", MapleClassGroup.THIEF),
    PHANTOM("팬텀", MapleClassGroup.THIEF),
    CADENA("카데나", MapleClassGroup.THIEF),
    KHALI("칼리", MapleClassGroup.THIEF),
    HOYOUNG("호영", MapleClassGroup.THIEF),

    // 해적
    PIRATE("해적", MapleClassGroup.PIRATE),
    BRAWLER("인파이터", MapleClassGroup.PIRATE),
    MARAUDER("버커니어", MapleClassGroup.PIRATE),
    BUCCANEER("바이퍼", MapleClassGroup.PIRATE),
    GUNSLINGER("건슬링거", MapleClassGroup.PIRATE),
    OUTLAW("발키리", MapleClassGroup.PIRATE),
    CORSAIR("캡틴", MapleClassGroup.PIRATE),
    DESTROYER("디스트로이어", MapleClassGroup.PIRATE),
    CANNON_SHOOTER("해적(캐논슈터)", MapleClassGroup.PIRATE),
    CANNONEER("캐논슈터", MapleClassGroup.PIRATE),
    CANNON_TROOPER("캐논블래스터", MapleClassGroup.PIRATE),
    CANNON_MASTER("캐논마스터", MapleClassGroup.PIRATE),
    THUNDER_BREAKER("스트라이커", MapleClassGroup.PIRATE),
    SHADE("은월", MapleClassGroup.PIRATE),
    MECHANIC("메카닉", MapleClassGroup.PIRATE),
    ANGELIC_BUSTER("엔젤릭버스터", MapleClassGroup.PIRATE),
    ARK("아크", MapleClassGroup.PIRATE),

    // 기타
    XENON("제논", MapleClassGroup.XENON),
    UNKNOWN("미확인", MapleClassGroup.WARRIOR); // 예외 처리용

    companion object {
        // 🚀 API에서 받은 문자열로 직업 객체를 찾는 메서드
        fun fromString(name: String): MapleClass {
            return entries.find { it.jobName == name } ?: UNKNOWN
        }
    }
}

enum class MapleClassGroup(
    val groupName: String,
    @DrawableRes val badge: Int
) {

    WARRIOR("전사", R.drawable.ic_class_badge_warrior),
    MAGICIAN("마법사", R.drawable.ic_class_badge_magician),
    ARCHER("궁수", R.drawable.ic_class_badge_archer),
    THIEF("도적", R.drawable.ic_class_badge_rogue),
    PIRATE("해적", R.drawable.ic_class_badge_pirate),
    XENON("제논", R.drawable.ic_class_badge_xenon)
}