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

import java.io.File
import java.util.*

fun List<String>.toFiles() = this.map { File(it) }

object ExecutionContext {
    val properties: Properties
    val directories: MutableSet<File>
    val ignored: Set<String>
    val dotdirectories: Boolean
    val debounce: Long

    init {
        properties = loadDotFile()
        directories = properties.getProperty("directories", "").splitParts().toFiles().toMutableSet()
        ignored = properties.getProperty("ignored","").splitParts().toSet()
        dotdirectories = properties.getProperty("dotdirectories","false").toBoolean()
        debounce = properties.getProperty("debounce","150").toLong()
    }

    fun String.splitParts() = this.split(",").toList().map(String::trim)
}

fun loadDotFile(): Properties {
    val properties = Properties()
    val homedir = System.getProperty("user.home")
    if (homedir != null) {
        val dotfile = File("$homedir/.filefinder")
        if (dotfile.exists()) {
            properties.load(dotfile.inputStream())
        }
    }
    return properties
}
