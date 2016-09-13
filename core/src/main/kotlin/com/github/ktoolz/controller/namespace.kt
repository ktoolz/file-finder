package com.github.ktoolz.controller

import java.io.File

fun load(root: File): List<File> =
        when {
            root.isFile -> listOf(root)
            root.isDirectory -> listOf(*root.listFiles()).flatMap { load(it) }
            else -> listOf()
        }


