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

import com.github.ktoolz.filefinder.utils.ExecutionContext
import javaslang.collection.List

fun registeredBangs() = List.of(TargetBang(), SourceBang(), IgnoredBang())

interface Bang {
    val name: String
    fun filter(result: FileSearchResult): Boolean
}

class SourceBang : Bang {
    val sourceExtensions = listOf("kt", "java", "c", "sh", "cpp", "scala", "xml", "js", "html", "css", "yml", "md")

    override val name = "src"

    override fun filter(result: FileSearchResult) =
            sourceExtensions.foldRight(false) { extension, keep ->
                keep || result.filename.endsWith(".$extension")
            }

}

class TargetBang : Bang {
    override val name = "target"

    override fun filter(result: FileSearchResult) = result.file.canonicalPath.contains("/target/")
}

class IgnoredBang : Bang {
    override val name = "ignored"

    override fun filter(result: FileSearchResult) = ExecutionContext.ignored.fold(false) {
        keep, extension ->
        keep || result.filename.endsWith(".$extension")
    }
}
