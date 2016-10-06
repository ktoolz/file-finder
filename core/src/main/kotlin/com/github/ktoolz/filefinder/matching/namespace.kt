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

import com.github.ktoolz.filefinder.model.PatternMatcher
import javaslang.collection.List
import java.util.*

/**
 * Computes a List of PatternMatcher elements out of a List, while searching for a Pattern.
 */
fun <T> List<T>.matchers(pattern: List<T>,
                         matchers: List<PatternMatcher<T>>): List<PatternMatcher<T>> =
        when {
            pattern.isEmpty -> matchers
            else ->
                dropWhile { it != pattern.head() }.let { remainder ->
                    if (remainder.isEmpty) this@matchers.matchers(pattern.tail(),
                                                    matchers.append(PatternMatcher(pattern.head(),
                                                                                   false,
                                                                                   Optional.empty())))
                    else remainder.tail().matchers(pattern.tail(),
                                                   matchers.append(PatternMatcher(pattern.head(),
                                                                                  true,
                                                                                  Optional.of(indexOf(pattern.head())))))

                }
        }

/**
 * Extension of a String to allow Pattern Matching from another String.
 */
fun String.matchers(s: String): List<PatternMatcher<Char>> =
        List.ofAll(toCharArray()).matchers(List.ofAll(s.toCharArray()), List.empty())
