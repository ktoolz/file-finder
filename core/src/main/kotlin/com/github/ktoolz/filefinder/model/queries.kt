package com.github.ktoolz.filefinder.model

import javaslang.collection.List

data class FilterQuery(val name: String, val negated: Boolean = false)

data class SearchQuery(val term: String, val contexts: List<FilterQuery>, val directories: List<String>) {
    fun filterDirectories(searchResult: SearchResult): Boolean =
            directories.forAll { searchResult.file.parentFile.absolutePath?.contains("/$it") ?: false }
}
