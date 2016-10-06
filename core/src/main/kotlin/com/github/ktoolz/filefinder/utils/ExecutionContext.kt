package com.github.ktoolz.filefinder.utils

import java.io.File

fun List<String>.toFiles() = this.map { File(it) }

object ExecutionContext {
    var directories: List<File> = listOf()
}
