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
 * Loads a list of all files contained in all the directories given as parameters.
 *
 * @param root a list of directories from which the list of files should be extracted
 * @return a list of all Files contained in the provided directories
 */
fun loadAll(vararg root: File): List<File> =
        when {
            root.isEmpty() -> listOf()
            else -> load(root.first()) + loadAll(*root.drop(1).toTypedArray())
        }

/**
 * Loads a list of all files contained in a specific directory.
 * This list of files may not contain files coming from _dot_ directories if the configuration defined in *ExecutionContext* says so.
 *
 * @param root a directory from which the list of files should be extracted
 * @return a list of all Files contained in the provided directory
 */
fun load(root: File): List<File> =
        when {
            root.isFile -> listOf(root)
        // This line allows to load all the files from a directory if that directory isn't ignored.
        // Directory will be ignored if the configuration is set to ignore _dot_ directories, and the directory is actually a _dot_ one.
        // Reminder: _dot_ directories are the hidden ones in Linux, where the name of the directory actually starts with a `.`
            root.isDirectory && (ExecutionContext.dotdirectories || !root.absolutePath.contains("/.")) -> listOf(*root.listFiles()).flatMap(
                    ::load)
            else -> listOf()
        }
