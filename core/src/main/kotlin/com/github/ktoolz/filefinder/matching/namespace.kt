package com.github.ktoolz.filefinder.matching

import com.github.ktoolz.filefinder.model.PatternMatcher
import javaslang.collection.List

/**
 * Computes a List of PatternMatcher elements out of a List, while searching for a Pattern.
 */
fun <T> List<T>.matchers(pattern: List<T>,
                         matchers: List<PatternMatcher<T>>): List<PatternMatcher<T>> =
        when {
            pattern.isEmpty -> matchers
            else ->
                dropWhile { it != pattern.head() }.let { remainder ->
                    if (remainder.isEmpty) matchers(pattern.tail(),
                                                    matchers.append(PatternMatcher(pattern.head(), false, 0)))
                    else remainder.tail().matchers(pattern.tail(),
                                                   matchers.append(PatternMatcher(pattern.head(),
                                                                                  true,
                                                                                  indexOf(pattern.head()))))

                }
        }

/**
 * Extension of a String to allow Pattern Matching from another String.
 */
fun String.matchers(s: String): List<PatternMatcher<Char>> =
        List.ofAll(toCharArray()).matchers(List.ofAll(s.toCharArray()), List.empty())
