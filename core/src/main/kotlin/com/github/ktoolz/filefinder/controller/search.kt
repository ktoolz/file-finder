package com.github.ktoolz.filefinder.controller

import com.github.ktoolz.filefinder.matching.matchers
import com.github.ktoolz.filefinder.model.SearchQuery
import com.github.ktoolz.filefinder.model.SearchResult
import javaslang.Tuple3
import java.io.File


/**
 * Searches in a List of File for the results of a particular Query.
 * Allows to filterDirectories the files through Pattern Matching, also applying filters on directories, and bangs! like
 * in DuckDuckGo for additional options.
 */
fun Iterable<File>.search(searchQuery: SearchQuery) =
        map { file ->
            val lowerCaseName = file.name.toLowerCase()
            SearchResult(lowerCaseName.matchers(searchQuery.term.toLowerCase()), file)
        }.filter { searchQuery.filterDirectories(it) }
                .sortedBy { Tuple3(-it.score, it.filename.length, it.filename) }
