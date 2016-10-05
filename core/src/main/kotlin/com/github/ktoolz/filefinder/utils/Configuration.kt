package com.github.ktoolz.filefinder.utils

import java.io.File

fun List<String>.toFiles() = this.map { File(it) }

object Configuration {
    var directories: List<File> = listOf()
        get() = if (directories.isEmpty()) listOf(File("..")) else directories
}
