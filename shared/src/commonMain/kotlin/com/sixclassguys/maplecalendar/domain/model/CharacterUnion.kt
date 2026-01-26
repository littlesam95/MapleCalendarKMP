package com.sixclassguys.maplecalendar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterUnion(
    val unionLevel: Int,
    val unionGrade: String,
    val unionArtifactLevel: Int,
    val unionArtifactExp: Long,
    val unionArtifactPoint: Int
)