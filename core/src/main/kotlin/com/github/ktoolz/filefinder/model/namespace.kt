package com.github.ktoolz.filefinder.model

import javaslang.collection.List
import java.io.File

/**
 * Store a search result
 */
data class SearchResult(val matchers: List<PatternMatcher<Char>>, val file: File) {
    val filename: String by lazy { file.name }
    val score: Int = matchers.foldLeft(0) {
        found, pattern ->
        if (pattern.match) found + 1 else found - 1
    }
}

data class FilterQuery(val name: String, val negated: Boolean = false)

data class SearchQuery(val term: String, val contexts: List<FilterQuery>, val directories: List<String>) {
    fun filterDirectories(searchResult: SearchResult): Boolean =
            directories.forAll { searchResult.file.parentFile.absolutePath?.contains("/$it") ?: false }
}

class PatternMatcher<out T>(val element: T, val match: Boolean, val distance: Int)
