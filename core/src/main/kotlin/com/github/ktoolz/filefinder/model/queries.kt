package com.github.ktoolz.filefinder.model

import javaslang.collection.List

data class BangQuery(val bang: Bang, val negated: Boolean = false)

data class SearchQuery(val term: String, val bangs: List<BangQuery>, val directories: List<String>) {
    fun filterDirectories(searchResult: SearchResult): Boolean =
            directories.forAll { searchResult.file.parentFile.absolutePath?.contains("/$it") ?: false }

    fun filterBangs(searchResult: SearchResult): Boolean =
            bangs.forAll { !it.negated && it.bang.filter(searchResult) }
}
