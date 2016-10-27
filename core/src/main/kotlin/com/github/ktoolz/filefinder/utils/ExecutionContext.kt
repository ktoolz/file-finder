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

/**
 * Transforms a List of String into a List of Files by creating Files from each String :)
 * @receiver a List of String, ideally containing paths cause we'll create files out of it
 *
 * @return a List of Files created from each String
 */
fun List<String>.toFiles() = this.map(::File)

/**
 * Singleton holding the application configuration.
 * Nothing fancy here, it's just reading the configuration file and loading its data :)
 */
object ExecutionContext {
    /**
     * Properties matching with the content of the configuration file
     */
    val properties: Properties
    /**
     * A Set containing all the directories in which we want to perform the search
     */
    val directories: MutableSet<File>
    /**
     * A Set containing all the extensions to be ignored while using the associate Bang
     */
    val ignored: Set<String>
    /**
     * A boolean allowing to state if dotdirectories should be considered while searching files or not
     */
    val dotdirectories: Boolean
    /**
     * The debounce value to be used while processing the search in milliseconds
     */
    val debounce: Long

    /**
     * The maximum number of items to be displayed in the search results
     */
    val items: Int

    init {
        fun String.splitParts() = this.split(",").toList().map(String::trim)
        properties = loadDotFile()
        directories = properties.getProperty("directories", "").splitParts().toFiles().toMutableSet()
        ignored = properties.getProperty("ignored", "").splitParts().toSet()
        dotdirectories = properties.getProperty("dotdirectories", "false").toBoolean()
        debounce = properties.getProperty("debounce", "150").toLong()
        items = properties.getProperty("items", "15").toInt()
    }

}

/**
 * Loads the default configuration file of the application, and returns a Properties object out of it.
 *
 * @return a Properties object containing all the values contained in the application default configuration file
 */
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
