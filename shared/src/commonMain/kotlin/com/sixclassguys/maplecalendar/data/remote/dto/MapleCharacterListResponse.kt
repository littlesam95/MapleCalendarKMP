package com.sixclassguys.maplecalendar.data.remote.dto

import com.sixclassguys.maplecalendar.domain.model.CharacterSummary
import kotlinx.serialization.Serializable

@Serializable
data class MapleCharacterListResponse(
    val groupedCharacters: Map<String, List<CharacterSummaryResponse>>
) {

    fun toDomain(allWorldNames: List<String>): Map<String, Map<String, List<CharacterSummary>>> {
        // 결과 구조: Map<그룹명, Map<월드명, 캐릭터리스트>>
        val result = mutableMapOf<String, MutableMap<String, MutableList<CharacterSummary>>>()

        // 1. 모든 월드 이름을 기반으로 3개 그룹 구조 미리 생성
        allWorldNames.forEach { worldName ->
            val groupTitle = when {
                worldName == "에오스" || worldName == "핼리오스" -> "에오스/핼리오스"
                worldName.contains("챌린저스") -> "챌린저스"
                else -> "일반 월드"
            }
            result.getOrPut(groupTitle) { mutableMapOf() }
                .getOrPut(worldName) { mutableListOf() }
        }

        // 2. 서버에서 받은 월드별 데이터를 그룹에 할당
        this.groupedCharacters.forEach { (worldName, characters) ->
            val groupTitle = when {
                worldName == "에오스" || worldName == "핼리오스" -> "에오스/핼리오스"
                worldName.contains("챌린저스") -> "챌린저스"
                else -> "일반 월드"
            }

            val domainList = characters.map { it.toDomain() }
            // 이미 1단계에서 초기화된 리스트에 서버 데이터를 추가
            result[groupTitle]?.get(worldName)?.addAll(domainList)
        }

        return result
    }
}