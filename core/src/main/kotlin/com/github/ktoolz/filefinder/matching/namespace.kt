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
package com.github.ktoolz.filefinder.matching

import com.github.ktoolz.filefinder.model.MatchResult
import javaslang.collection.List
import java.util.*

/**
 * Computes a List of MatchResult elements out of a List, while searching for a Pattern.
 */
fun <T> List<T>.matchers(searchQuery: List<T>): List<MatchResult<T>> {

    /**
     * Accumulate MatchResult
     */
    fun <T> List<T>.search(searchPattern: List<T>,
                           accumulator: List<MatchResult<T>>): List<MatchResult<T>> =
            when {
                searchPattern.isEmpty -> accumulator
                else -> {
                    val lookupItem = searchPattern.head()
                    val remainder = dropWhile { it != lookupItem }
                    if (remainder.isEmpty) {
                        // not found
                        val distance = Optional.empty<Int>()
                        val matchResult = MatchResult(lookupItem, false, distance)
                        this@search.search(searchPattern.tail(), accumulator.append(matchResult))
                    } else {
                        // found
                        val distance = Optional.of(indexOf(lookupItem))
                        val matchResult = MatchResult(lookupItem, true, distance)
                        remainder.tail().search(searchPattern.tail(), accumulator.append(matchResult))
                    }
                }
            }

    fun <T> List<T>.nGramsSearch(searchQuery: List<T>): List<MatchResult<T>> {
        val ngramsMatchResults = searchQuery.combinations().map { ngram ->
            this.search(ngram, List.empty())
        }

        val bestMatch = ngramsMatchResults.sortBy { matchResultsOfNgram ->
            // count the number of matches and inverse it to sort from biggest to smallest
            -matchResultsOfNgram.filter(MatchResult<T>::match).size()
        }.head()

        // As the N-Gram can contains less elements than the search query, replace missing elements
        // by a 'not found' match result
        fun T.notFound() = MatchResult(this, false, Optional.empty())
        return searchQuery.map { searchQueryItem: T ->
            bestMatch.find { it.element == searchQueryItem }.getOrElse(searchQueryItem.notFound())
        }

    }

    return nGramsSearch(searchQuery)
}

/**
 * Extension of a String to allow Pattern Matching from another String.
 */
fun String.matchers(s: String): List<MatchResult<Char>> =
        List.ofAll(toCharArray()).matchers(List.ofAll(s.toCharArray()))
