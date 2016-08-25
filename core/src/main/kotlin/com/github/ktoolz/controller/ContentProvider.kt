package com.github.ktoolz.controller

import javaslang.collection.Stream
import java.io.File

fun load(root: File): Stream<File> =
        when {
            root.isFile -> Stream.of(root)
            root.isDirectory -> Stream.of(*root.listFiles()).flatMap { load(it) }
            else -> Stream.empty()
        }


