package com.sixclassguys.maplecalendar.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageableInfo(
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean
)