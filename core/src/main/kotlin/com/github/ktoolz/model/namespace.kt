package com.github.ktoolz.model

import com.github.ktoolz.matchers
import javaslang.collection.List
import java.io.File

/**
 * Store a search result
 */
data class SearchResult(val matchers: List<PatternMatcher<Char>>, val file: File) {
    val filename: String by lazy { file.name }
    val score: Int = matchers.foldLeft(0) {
        found, pattern ->
        if (pattern.found) found + 1 else found - 1
    }
}

data class FilterQuery(val name: String, val negated: Boolean = false)

data class SearchQuery(val term: String, val contexts: List<FilterQuery>, val directories: List<String>) {
    fun filter(searchResult: SearchResult): Boolean {
        val directoriesCheck = directories.forAll { searchResult.file.parentFile.absolutePath?.contains("/$it") ?: false }

        if(contexts.isEmpty.not() && contexts.head().name == "src") {
            if(searchResult.file.name.endsWith(".kt")) {
                return !contexts.head().negated
            } else return contexts.head().negated
        }

        return directoriesCheck
    }
}

class PatternMatcher<out T>(val matcher: T, val found: Boolean, val distance: Int)

fun Iterable<File>.search(searchQuery: SearchQuery) =
        map { file ->
            val lowerCaseName = file.name.toLowerCase()
            SearchResult(lowerCaseName.matchers(searchQuery.term.toLowerCase()), file)
        }
                .apply {
                    this.forEach {
                        kotlin.io.println(it.file.toString())
                        it.matchers.forEach { kotlin.io.println(it.matcher + " > " + it.found + " > " + it.distance) }
                        kotlin.io.println("-----------")
                    }
                }
                .filter { searchQuery.filter(it) }
                .sortedBy { javaslang.Tuple3(-it.score, it.filename.length, it.filename) }