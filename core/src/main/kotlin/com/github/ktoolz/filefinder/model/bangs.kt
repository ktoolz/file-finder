package com.github.ktoolz.filefinder.model

import javaslang.collection.List

fun registeredBangs() = List.of(TargetBang(), SourceBang())

interface Bang {
    val name: String
    fun filter(result: SearchResult): Boolean
}

class SourceBang : Bang {
    val sourceExtensions = listOf("kt", "java", "c", "sh", "cpp", "scala", "xml", "js", "html", "css", "yml", "md")

    override val name = "src"

    override fun filter(result: SearchResult) =
            sourceExtensions.foldRight(false) { extension, keep ->
                keep || result.filename.endsWith(".$extension")
            }

}

class TargetBang : Bang {
    override val name = "target"

    override fun filter(result: SearchResult) = !result.file.canonicalPath.contains("/target/")
}
