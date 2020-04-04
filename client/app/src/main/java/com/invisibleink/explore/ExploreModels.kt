package com.invisibleink.explore

import java.io.Serializable

data class SearchFilter(
    val keywords: String? = null,
    val limit: Int? = null,
    val withImage: Boolean? = null,
    val options: PrebuiltOptions? = null
) : Serializable {
    companion object {
        val EMPTY_FILTER = SearchFilter()
    }
}

enum class PrebuiltOptions {
    BEST, WORST, NEWEST;
}