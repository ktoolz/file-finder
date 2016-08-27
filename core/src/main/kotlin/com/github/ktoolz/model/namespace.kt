package com.github.ktoolz.model

import javaslang.collection.List
import java.io.File

/**
 * Store a search result
 */
data class SearchResult(val score: Int, val file: File) {
    val filename: String by lazy { file.name }
}

data class SearchQuery(val term: String, val contexts: List<String>, val directories: List<String>)