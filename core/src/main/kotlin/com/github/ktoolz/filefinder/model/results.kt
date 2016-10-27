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
import java.util.*

/**
 * Stores the actual result of a File search from all matchers computed previsouly.
 * It'll also compute a score allowing to order the results from best to worst.
 *
 * @property matchers a List of all [MatchResult] elements computed while searching through the files
 *                      (so basically just the results of the search process)
 * @property file the actual file which has been found
 */
data class FileSearchResult(val matchers: List<MatchResult<Char>>, val file: File) {
    /**
     * Name of the file linked to that search result
     */
    val filename: String by lazy { file.name }

    /**
     * A computed score allowing to order the results.
     * Basically, it'll be better when letters from the pattern are found and close together,
     * and it'll get worse when letters aren't found or when they're far from each other.
     */
    val score: Int = matchers.foldLeft(0) {
        score, pattern ->
        when {
        // pattern doesn't match, score loses 2 points
            !pattern.match -> score - 2
        // pattern distance is max 0 (meaning letters are following each other)
        // (meaning also that pattern is found btw) - score will be 2 (cause found) + 1 (cause small distance)
            pattern.distance.isPresent && pattern.distance.get() < 1 -> score + 2 + 1
        // pattern distance is more than half the word (meaning that pattern is found again) - score will be 2 (cause found) - 1 (cause big distance)
            pattern.distance.isPresent && pattern.distance.get() > filename.length / 2 -> score + 2 - 1
        // else will be when pattern matches, but distance is not in previsouly mentionned range - score will be 2
            else -> score + 2
        }
    }
}

/**
 * Wrapper for the results of matching process.
 * It'll basically just contain one element of the pattern, and an indication stating if it has been found or not,
 * and at which distance from the previous one.
 * It'll basically allow afterwards to calculate if a result is good or not.
 *
 * @property element the part of the pattern which we were searching for
 * @property match a boolean stating if that part of the pattern has been found or not
 * @property distance an [Optional] integer matching with the distance between the current element we were searching for and
 *                      the previous one
 */
data class MatchResult<out T>(val element: T, val match: Boolean, val distance: Optional<Int>)
