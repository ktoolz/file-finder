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
package com.github.ktoolz.filefinder.view

import com.github.ktoolz.filefinder.controller.loadAll
import com.github.ktoolz.filefinder.controller.search
import com.github.ktoolz.filefinder.model.SearchResult
import com.github.ktoolz.filefinder.model.registeredBangs
import com.github.ktoolz.filefinder.parser.ContextParser
import com.github.ktoolz.filefinder.utils.ExecutionContext
import com.github.ktoolz.filefinder.utils.time
import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.input.KeyCode.*
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.awt.Desktop
import java.awt.Event
import java.io.File

class MainView : View() {

    val KeyEvent.isNoModifier: Boolean get() = !isAltDown && !isControlDown && !isMetaDown && !isShiftDown
    val KeyEvent.isOnlyControlDown: Boolean get() = !isAltDown && isControlDown && !isMetaDown && !isShiftDown
    val KeyEvent.isOnlyShiftDown: Boolean get() = !isAltDown && !isControlDown && !isMetaDown && isShiftDown
    val KeyEvent.isOnlyAltDown: Boolean get() = isAltDown && !isControlDown && !isMetaDown && !isShiftDown
    val KeyEvent.isOnlyMetaDown: Boolean get() = !isAltDown && !isControlDown && isMetaDown && !isShiftDown

    override val root: BorderPane = BorderPane()

    val MAX_ITEMS_TO_DISPLAY = 15

    lateinit var files: List<File>
    val search = SimpleStringProperty()
    val result = ObservableListWrapper<SearchResult>(mutableListOf<SearchResult>())

    var searchTime by property(0L)
    fun searchTimeProperty() = getProperty(MainView::searchTime)

    val table: TableView<SearchResult> by lazy {
        @Suppress("UNCHECKED_CAST")
        (root.scene.lookup("#tableView") as TableView<SearchResult>)
    }

    val inputSearch: TextField by lazy {
        root.scene.lookup("#inputSearch") as TextField
    }


    init {
        title = messages["title"]
        addStageIcon(Image(MainView::class.java.getResourceAsStream("/icon.png")))

        search.onChange { searchText ->
            when (searchText) {
                null -> result.clear()
                else -> with(result) {
                    clear()
                    val (searchResults, time) = time {
                        files.search(ContextParser(registeredBangs()).parse(searchText)).take(
                                MAX_ITEMS_TO_DISPLAY)
                    }
                    addAll(searchResults)
                    searchTime = time
                }
            }
        }

        with(root) {
            top {
                textfield(search) {
                    isDisable = true
                    id = "inputSearch"
                    setOnKeyPressed { event ->
                        when (event.code) {
                            DOWN -> table.apply {
                                selectFirst()
                                requestFocus()
                            }
                            ENTER -> if (result.isNotEmpty()) open(result[0])
                            O -> if (event.isControlDown && result.isNotEmpty()) open(result[0])
                            B -> if (event.isControlDown && result.isNotEmpty()) browse(result[0])
                        }
                    }
                }
            }

            center {
                tableview(result) {
                    id = "tableView"
                    column("Score", SearchResult::score).prefWidth = 50.0
                    column("File", SearchResult::filename).prefWidth = 250.0
                    column("Path", SearchResult::file).prefWidth = 600.0

                    setOnKeyPressed { event ->
                        with(event) {
                            when (code) {
                                UP, PAGE_UP, HOME -> if (selectedItem == result.first()) inputSearch.requestFocus()
                                ESCAPE -> inputSearch.requestFocus()
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

            bottom {
                label {
                    textProperty().bind(Bindings.format("Results computed in %sms.", searchTimeProperty()))
                }
            }
        }

        runAsync { files = loadAll(*ExecutionContext.directories.toTypedArray()) } ui { inputSearch.isDisable = false }
    }

    /**
     * Open the parent folder of a file in the default application
     */
    private fun browse(result: SearchResult?) {
        if (result != null) {
            runAsync { Desktop.getDesktop().open(result.file.parentFile) }
        }
    }

    /**
     * Open the selected file in the default application
     */
    private fun open(result: SearchResult?) {
        if (result != null) {
            runAsync { Desktop.getDesktop().open(result.file) }
        }
    }

    /**
     * Copy in the clipboard the path of selected file
     */
    private fun copy(result: SearchResult?) {
        if (result != null) {
            Clipboard.getSystemClipboard().putString(result.file.canonicalPath)
        }
    }
}
