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
 * Computes a List of [MatchResult] elements out of a List, while searching for a Pattern.
 *
 * @param pattern a List of elements composing the pattern to search for
 * @param combinations a List of the combinations of [pattern] we want to use for the research (because we want to tolerate some mistakes)
 * @receiver a List of elements on which we actually want to search for matching elements
 *
 * @return a List of [MatchResult] which contains elements matching with our pattern research. They'll be used afterwards to compute the search results.
 */
fun <T> List<T>.matchers(pattern: List<T>, combinations: List<List<T>>): List<MatchResult<T>> {

    /**
     * Computes a List of [MatchResult] elements out of a List, keeping only the elements matching exactly the pattern which we're searching for.
     *
     * @param searchPattern the actual pattern we're searching for (exactly) in the provided List
     * @param acc because it's recursive, an accumulator of the results we compute during the execution
     * @receiver a List of elements on which we actually perform the research
     *
     * @return a List of [MatchResult] which contains the elements exactly matching with the pattern we provide
     */
    tailrec fun <T> List<T>.matchExactly(searchPattern: List<T>,
                                         acc: List<MatchResult<T>>): List<MatchResult<T>> =
            when {
                searchPattern.isEmpty -> acc
                else -> {
                    val lookupItem = searchPattern.head()
                    val remainder = dropWhile { it != lookupItem }
                    if (remainder.isEmpty) {
                        // not found
                        List.empty<MatchResult<T>>()
                    } else {
                        // found
                        val distance = Optional.of(indexOf(lookupItem))
                        val matchResult = MatchResult(lookupItem, true, distance)
                        remainder.tail().matchExactly(searchPattern.tail(), acc.append(matchResult))
                    }
                }
            }

    /**
     * Computes a List of [MatchResult] elements out of a List, keeping the elements matching with the pattern we're searching for, with some fault tolerance.
     * We're using combinations of the search pattern allowing the research to also accept up to 1 mistake or missing characters.
     *
     * @param searchPattern the pattern we're searching for in the provided List - this function will actually also use the combinations of that pattern.
     * @receiver a List of elements on which we actually perform the research
     *
     * @return a List of [MatchResult] which contains the elements matching with the pattern we provide, with tolerance on mistakes :)
     */
    fun List<T>.nGramsSearch(searchPattern: List<T>): List<MatchResult<T>> {

        /**
         * List of search results for all the combinations
         */
        val ngramsMatchResults = combinations.toStream()
                .map { matchExactly(it, List.empty()) }
                .filter { it.nonEmpty() }

        /**
         * The best option we can find while searching for the pattern
         */
        val bestMatchOption = ngramsMatchResults.headOption().getOrElse(List.empty())

        /**
         * Creates a [MatchResult] object for a not found element
         * @receiver a part of the search pattern which isn't found while searching the list of files
         *
         * @return a [MatchResult] element stating that this part of the pattern isn't found
         */
        fun T.notFound() = MatchResult(this, false, Optional.empty())

        /**
         * Computes the actual List of [MatchResult] by completing the found ones.
         * As the N-Gram can contains less elements than the search query, replace missing elements
         * by a 'not found' match result.
         *
         * @param searchPattern the pattern we're searching for in the provided List
         * @param matchResults the results we found during the search process - those results may be incomplete cause
         * they might contain less elements than the search query, as mentionned in the comment
         * @param acc an accumulator for the recursive calls, containing the complete List we're actually building
         *
         * @return a List containing all the [MatchResult], matching with the actual length of the search pattern we provided
         */
        fun addMissings(searchPattern: List<T>,
                        matchResults: List<MatchResult<T>>,
                        acc: List<MatchResult<T>>): List<MatchResult<T>> =
                when {
                    searchPattern.isEmpty -> acc
                    matchResults.isEmpty -> addMissings(searchPattern.tail(),
                                                        matchResults,
                                                        acc.append(searchPattern.head().notFound()))
                    matchResults.head().element == searchPattern.head() -> addMissings(searchPattern.tail(),
                                                                                       matchResults.tail(),
                                                                                       acc.append(matchResults.head()))
                    else -> addMissings(searchPattern.tail(),
                                        matchResults,
                                        acc.append(searchPattern.head().notFound()))
                }

        // Hey! Look, this is actually what's returned by [nGramsSearch] in case you didn't notice ;p
        return addMissings(searchPattern, bestMatchOption, List.empty())
    }
    return nGramsSearch(pattern)
}

/**
 * Returns the matchers of a String from another one, and its combinations.
 *
 * @param pattern the pattern we're searching for in the receiver String
 * @param combinations combinations of the pattern we will search for as well (used for fault tolerance)
 * @receiver the String on which we want to compute the matching
 *
 * @return a List of [MatchResult] elements containing all the matchers elements for that research
 */
fun String.matchers(pattern: String, combinations: List<List<Char>>): List<MatchResult<Char>> =
        List.ofAll(toCharArray()).matchers(List.ofAll(pattern.toCharArray()), combinations)
