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
import java.io.File

/**
 * Provides the list of all registered Bangs to be used during a search
 *
 * @return a List of all [Bang] to be used during the search process
 */
fun registeredBangs() = List.of(TargetBang(), SourceBang(), IgnoredBang())!!

// ---------------------------------------------------------------------------------------------------------------------

/**
 * Definition of a [Bang] element.
 *
 * A [Bang] is actually kind of a filter on the search process.
 */
interface Bang {
    /**
     * Name of the [Bang].
     * The name will actually be used in the search field for calling the Bang.
     * User will have to write it using the syntax: `!name`
     */
    val name: String

    /**
     * The actual behavior of the [Bang] which allows to say if a particular File matches the
     * [Bang] filter criteria
     *
     * @param result the result of the search which we want to filter - basically a File which we found
     * @return a Boolean stating if that File must be considered in the results depending of the Bang.
     */
    fun filter(result: File): Boolean
}

// ---------------------------------------------------------------------------------------------------------------------

/**
 * [Bang] allowing to filter only source files by their extensions.
 * It'll allow to keep in the results only source files. If negated, it'll only keep only non-source files. (Cap'tain obvious).
 */
class SourceBang : Bang {
    /**
     * Just a List of all the extensions we consider as "source" files. Surely not complete though, and might be extended in the future.
     */
    val sourceExtensions = listOf("kt", "java", "c", "sh", "cpp", "scala", "xml", "js", "html", "css", "yml", "md")

    /**
     * @see [Bang.name]
     */
    override val name = "src"

    /**
     * @see [Bang.filter]
     */
    override fun filter(result: File) =
            sourceExtensions.foldRight(false) { extension, keep ->
                keep || result.name.endsWith(".$extension")
            }

}

// ---------------------------------------------------------------------------------------------------------------------

/**
 * [Bang] allowing to filter only files from a target repository (usual folder used for results of builds).
 * It'll allow to keep only files coming from those target repositories, or the opposite while negated.
 */
class TargetBang : Bang {
    /**
     * @see [Bang.name]
     */
    override val name = "target"

    /**
     * @see [Bang.filter]
     */
    override fun filter(result: File) = result.canonicalPath.contains("/target/")
}

// ---------------------------------------------------------------------------------------------------------------------

/**
 * [Bang] allowing to filter only files which are considered as ignored. Ignored files are determined through their
 * extension, using the ones provided in the [ExecutionContext] configuration (ie. the configuration file of
 * the application).
 * It'll allow to keep only the ones which are considered ignored, or the opposite if it's negated.
 */
class IgnoredBang : Bang {
    /**
     * @see [Bang.name]
     */
    override val name = "ignored"

    /**
     * @see [Bang.filter]
     */
    override fun filter(result: File) = ExecutionContext.ignored.fold(false) {
        keep, extension ->
        keep || result.name.endsWith(".$extension")
    }
}
