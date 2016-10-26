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

import com.github.ktoolz.filefinder.utils.ExecutionContext
import java.io.File

/**
 * Loads a lit of Files from a results of several directories
 */
fun loadAll(vararg root: File): List<File> =
        when {
            root.isEmpty() -> listOf()
            else -> load(root.first()) + loadAll(*root.drop(1).toTypedArray())
        }

/**
 * Loads a results of Files from a particular directory
 */
fun load(root: File): List<File> =
        when {
            root.isFile -> listOf(root)
            root.isDirectory && (ExecutionContext.dotdirectories || !root.absolutePath.contains("/.")) -> listOf(*root.listFiles()).flatMap(::load)
            else -> listOf()
        }
