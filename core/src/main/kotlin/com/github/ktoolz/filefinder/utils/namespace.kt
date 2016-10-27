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
package com.github.ktoolz.filefinder.utils

/**
 * Creates a Javaslang list out of a Kotlin List
 * @receiver a Kotlin List
 *
 * @return a Javaslang List containing all the elements of the Kotlin List
 */
fun <T> List<T>.toJavaslang() = javaslang.collection.List.ofAll(this)!!

/**
 * Creates a Javaslang list containing all the characters of a String
 * @receiver a String that we want to decompose
 *
 * @return a Javaslang containing all the characters of the provided String
 */
fun String.toJavaslangList() = toList().toJavaslang()

/**
 * Executes a particular block and returns the result as well as the time it required to compute
 * @param block the block to be executed, basically just a function producing something
 *
 * @return a [Pair] element containing both the result of the block execution, and the time ellapsed to do that
 */
fun <T> time(block: () -> T): Pair<T, Long> {
    val now = System.currentTimeMillis()
    val result = block()
    val elapsed = System.currentTimeMillis() - now
    return result to elapsed
}
