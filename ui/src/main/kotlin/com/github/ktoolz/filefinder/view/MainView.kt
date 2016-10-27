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
@file:Suppress("NON_EXHAUSTIVE_WHEN")

package com.github.ktoolz.filefinder.view

import com.github.ktoolz.filefinder.controller.loadAll
import com.github.ktoolz.filefinder.controller.search
import com.github.ktoolz.filefinder.model.FileSearchResult
import com.github.ktoolz.filefinder.model.registeredBangs
import com.github.ktoolz.filefinder.parser.ContextParser
import com.github.ktoolz.filefinder.utils.ExecutionContext
import com.github.ktoolz.filefinder.utils.time
import com.sun.javafx.collections.ObservableListWrapper
import javafx.application.Platform.runLater
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode.*
import javafx.scene.layout.BorderPane
import rx.Observable
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Main view of the File Finder application. Instance of a [View] object.
 */
class MainView : View() {
    override val root: BorderPane = BorderPane()

    lateinit var files: List<File>

    val searchTextProperty = SimpleStringProperty()
    val resultListProperty = ObservableListWrapper<FileSearchResult>(mutableListOf<FileSearchResult>())

    val searchTimeProperty = SimpleLongProperty(0L)
    val numberFilesProperty = SimpleLongProperty(0L)

    val searchTextObservableBuffer: Observable<String>

    val resultsTableView: TableView<FileSearchResult> by lazy {
        @Suppress("UNCHECKED_CAST")
        (root.scene.lookup("#tableView") as TableView<FileSearchResult>)
    }
    val searchInputText: TextField by lazy {
        root.scene.lookup("#searchInputText") as TextField
    }

    init {
        title = messages["title"]
        addStageIcon(Image(MainView::class.java.getResourceAsStream("/icon.png")))

        // Creating an observable from all the values which are typed in the input field
        searchTextObservableBuffer = Observable.create<String> {
            searchTextProperty.onChange { text ->
                when (text) {
                    null -> resultListProperty.clear()
                    else -> it.onNext(text)
                }
            }
        }

        // Using debounce to actually execute the research after a small delay when the latest character is typed
        searchTextObservableBuffer.debounce(ExecutionContext.debounce, TimeUnit.MILLISECONDS).subscribe {
            runLater {
                with(resultListProperty) {
                    clear()
                    val (searchResults, time) = time {
                        val searchQuery = ContextParser(registeredBangs()).parse(it)
                        files.search(searchQuery).take(ExecutionContext.items)
                    }
                    addAll(searchResults)
                    searchTimeProperty.set(time)
                }
            }
        }

        // Actually creating the view:
        with(root) {

            // First - a search bar to type our queries
            top {
                textfield(searchTextProperty) {
                    isDisable = true
                    id = "searchInputText"
                    setOnKeyPressed { event ->
                        when (event.code) {
                            DOWN -> resultsTableView.apply {
                                selectFirst()
                                requestFocus()
                            }
                            ENTER -> if (resultListProperty.isNotEmpty()) open(resultListProperty[0])
                            O -> if (event.isControlDown && resultListProperty.isNotEmpty()) open(resultListProperty[0])
                            B -> if (event.isControlDown && resultListProperty.isNotEmpty()) browse(resultListProperty[0])
                        }
                    }
                }
            }

            // Second - a table for displaying all the results
            center {
                tableview(resultListProperty) {
                    id = "tableView"

                    column("Score", FileSearchResult::score).prefWidth = 50.0
                    column("File", FileSearchResult::filename).prefWidth = 250.0
                    column("Path", FileSearchResult::file).prefWidth = 600.0

                    setOnKeyPressed { event ->
                        with(event) {
                            when (code) {
                                UP, PAGE_UP, HOME -> if (selectedItem == resultListProperty.first()) searchInputText.requestFocus()
                                ESCAPE -> searchInputText.requestFocus()
                                B -> if (isNoModifier) browse(selectedItem)
                                O -> if (isNoModifier) open(selectedItem)
                                C -> if (isOnlyControlDown) copy(selectedItem)
                            }
                        }
                    }

                    onUserSelect {
                        open(it)
                    }
                }
            }

            // Finally - a small label to indicate the number of files and time ellapsed while searching
            bottom {
                label {
                    textProperty().bind(Bindings.format("Searched %s files in %sms.",
                                                        numberFilesProperty,
                                                        searchTimeProperty))
                }
            }
        }

        runAsync { files = loadAll(*ExecutionContext.directories.toTypedArray()) } ui {
            searchInputText.isDisable = false
            numberFilesProperty.set(files.size.toLong())
        }
    }

    /**
     * Opens the parent folder of a file in the default application
     * @param result the selected FileSearchResult we'd like to browse
     */
    private fun browse(result: FileSearchResult?) {
        if (result != null) {
            runAsync { Desktop.getDesktop().open(result.file.parentFile) }
        }
    }

    /**
     * Opens the selected file in the default application
     * @param result the selected FileSearchResult we'd like to open
     */
    private fun open(result: FileSearchResult?) {
        if (result != null) {
            runAsync { Desktop.getDesktop().open(result.file) }
        }
    }

    /**
     * Copies in the clipboard the path of selected file
     * @param result the selected FileSearchResult we'd like to copy the path from
     */
    private fun copy(result: FileSearchResult?) {
        if (result != null) {
            Clipboard.getSystemClipboard().putString(result.file.canonicalPath)
        }
    }
}
