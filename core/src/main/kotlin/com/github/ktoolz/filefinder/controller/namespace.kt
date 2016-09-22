/*
 * File-Finder - KToolZ
 *
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.ktoolz.filefinder.controller

import com.github.ktoolz.filefinder.matching.matchers
import com.github.ktoolz.filefinder.model.SearchQuery
import com.github.ktoolz.filefinder.model.SearchResult
import javaslang.Tuple3
import java.io.File

/**
 * Loads a list of Files from a particular directory
 */
fun load(root: File): List<File> =
        when {
            root.isFile -> listOf(root)
            root.isDirectory -> listOf(*root.listFiles()).flatMap { load(it) }
            else -> listOf()
        }

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
