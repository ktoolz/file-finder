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
package com.github.ktoolz.filefinder.model

import javaslang.collection.List
import java.io.File

/**
 * Simple wrapper for a *Bang* query, wrapping both the [Bang] and an indication saying if it's negated
 *
 * @property bang the actual [Bang] which contains both the name and behavior
 * @property negated indication if the [Bang] is negated or not. If that's the case, the opposite filter will be
 * applied on the search query
 */
data class BangQuery(val bang: Bang, val negated: Boolean = false)

/**
 * Wrapper for all information related to a search query.
 * It contains both the term that we're searching for, and also the List of BangQueries, and the List of Directories
 * to be filtered.
 *
 * @property term the actual term we're searching for
 * @property bangs a List of all the [BangQuery] to apply to that search
 * @property directories a List of all the directories to be filtered
 */
data class SearchQuery(val term: String, val bangs: List<BangQuery>, val directories: List<String>) {
    /**
     * A List of all combinations we'll use for searching through the files
     * It's used in order to allow some mistakes in the term we're searching for.
     */
    val combinations: List<List<Char>>

    init {
        val list = List.ofAll(term.toLowerCase().toCharArray())
        // We're keeping all combinations of (n choose n-1), which means 1 error basically,
        // and we prepend the actual search term (because we also want to search for the term without
        // any errors.
        combinations = list.combinations(list.size() - 1).prepend(list)
    }

    /**
     * Indicates if the file provided as a parameter matches with the directories specified in the [SearchQuery]
     * @param file the file we want to verify, searching if it's part of the directories we want to filter
     *
     * @return true if the file is part of the directories we specified
     */
    fun filterDirectories(file: File): Boolean =
            directories.forAll { file.parentFile.absolutePath?.contains("/$it") ?: false }

    /**
     * Indicates if the file provided as a parameter matches with the Bangs specified in the [SearchQuery]
     * @param file the file we want to verify, searching if it's part of the Bangs we want to filter
     *
     * @return true if the file is matching with the Bangs we specified
     */
    fun filterBangs(file: File): Boolean =
            bangs.forAll { it.negated xor it.bang.filter(file) }
}
