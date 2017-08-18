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
package com.github.ktoolz.filefinder

import com.github.ktoolz.filefinder.utils.ExecutionContext
import com.github.ktoolz.filefinder.utils.toFiles
import com.github.ktoolz.filefinder.view.MainView
import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

sealed class Condition : (Application.Parameters) -> Boolean {
    class HaveFlagPresent(val flag: String) : Condition() {
        override fun invoke(parameters: Application.Parameters): Boolean =
                parameters.unnamed.contains(flag)
    }

    class AnyFlagPresent(vararg val flag: String) : Condition() {
        override fun invoke(parameters: Application.Parameters): Boolean =
                flag.any { parameters.unnamed.contains(it) }

        infix fun or(other: String) = AnyFlagPresent(*flag, other)
    }

    class AllFlagPresent(vararg val flag: String) : Condition() {
        override fun invoke(parameters: Application.Parameters): Boolean =
                flag.all { parameters.unnamed.contains(it) }

        infix fun and(other: String) = AllFlagPresent(*flag, other)
    }

    class ParameterValue(val name: String, vararg val possibleValues: String) : Condition() {
        override fun invoke(parameters: Application.Parameters): Boolean =
                possibleValues.any { parameters.named[name] == it }
    }
}

infix fun Application.Parameters.contains(condition: Condition) = condition(this)
infix fun String.or(other: String) = Condition.AnyFlagPresent(this, other)
infix fun String.and(other: String) = Condition.AllFlagPresent(this, other)

/**
 * Main application.
 * Allows to start the GUI.
 */
class FileFinderApp : App(MainView::class) {
    init {
        importStylesheet(Styles::class)
    }

    private fun whenFlag(vararg flag: String, block: () -> Unit) {
        if (flag.any { parameters.unnamed.contains(it) }) block()
    }


    override fun start(stage: Stage) {

        if(parameters contains ("-x" or "--exclusive")) {
            ExecutionContext.directories.clear()
        }

        ExecutionContext.directories.addAll(parameters.unnamed.filter { !it.startsWith("-") }.toFiles())
        super.start(stage)
    }
}

/**
 * Just the main entrypoint of the application :)
 * It'll basically just run the GUI.
 */
fun main(args: Array<String>) {
    Application.launch(FileFinderApp::class.java, *args)
}
