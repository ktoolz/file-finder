package com.github.ktoolz.model

import javaslang.collection.List
import java.io.File

/**
 * Store a search result
 */
data class SearchResult(val score: Int, val file: File) {
    val filename: String by lazy { file.name }
}

data class FilterQuery(val name: String, val negated: Boolean = false)

data class SearchQuery(val term: String, val contexts: List<FilterQuery>, val directories: List<String>)