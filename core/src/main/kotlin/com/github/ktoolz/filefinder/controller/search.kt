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
import com.github.ktoolz.filefinder.model.FileSearchResult
import com.github.ktoolz.filefinder.model.SearchQuery
import javaslang.Tuple3
import java.io.File

/**
 * Searches in a List of File for the results of a provided SearchQuery.
 *
 * The search process will:
 * - filter the directories depending on what's defined in the SearchQuery,
 * - apply _Bangs!_ allowing to precise the scope of a research (something like DuckDuckGo bangs!)
 * - actually search in the filtered list of files using combinations of the research criteria in order to tolerate mistakes
 *
 * Results will then be sorted by:
 * - best score,
 * - length (the shorter the better),
 * - alphabetical order of the file name
 *
 * @param query a complete [SearchQuery] containing the search criteria as well as the directories and bangs filters
 * @receiver a list of File on which we want to perform a research
 *
 * @return a List of [FileSearchResult] containing the actual results of the research
 */
fun Iterable<File>.search(query: SearchQuery) =
        filter { query.filterDirectories(it) }
                .filter { query.filterBangs(it) }
                .map { file ->
                    val lowerCaseName = file.name.toLowerCase()
                    FileSearchResult(lowerCaseName.matchers(query.term.toLowerCase(), query.combinations), file)
                }
                .sortedBy { Tuple3(-it.score, it.filename.length, it.filename) }

