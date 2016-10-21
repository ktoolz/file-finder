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

data class BangQuery(val bang: Bang, val negated: Boolean = false)

data class SearchQuery(val term: String, val bangs: List<BangQuery>, val directories: List<String>) {
    val combinations: List<List<Char>>
    init {
        val list = List.ofAll(term.toLowerCase().toCharArray())
        combinations = list.combinations(list.size() - 1).prepend(list)
    }
    fun filterDirectories(file: File): Boolean =
            directories.forAll { file.parentFile.absolutePath?.contains("/$it") ?: false }

    fun filterBangs(file: File): Boolean =
            bangs.forAll { it.negated xor it.bang.filter(file) }
}
