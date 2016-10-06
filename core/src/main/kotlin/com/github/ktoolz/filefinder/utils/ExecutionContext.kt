package com.github.ktoolz.filefinder.utils

import java.io.File

fun List<String>.toFiles() = this.map { File(it) }

object Configuration {
    var directories: List<File> = listOf()
}
