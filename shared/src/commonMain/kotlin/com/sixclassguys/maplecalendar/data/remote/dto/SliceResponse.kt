package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SliceResponse<T>(
    val content: List<T>,
    val pageable: PageableInfo,
    val size: Int,
    val number: Int, // 현재 페이지 번호
    val first: Boolean,
    val last: Boolean, // 마지막 페이지 여부 (중요!)
    val numberOfElements: Int,
    val empty: Boolean
)