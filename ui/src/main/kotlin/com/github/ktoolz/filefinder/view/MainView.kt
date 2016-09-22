package com.github.ktoolz.filefinder.view

import com.github.ktoolz.filefinder.controller.load
import com.github.ktoolz.filefinder.controller.search
import com.github.ktoolz.filefinder.model.SearchResult
import com.github.ktoolz.filefinder.parser.ContextParser
import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.*
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.File

class MainView : View() {

    override val root: BorderPane = BorderPane()

    val MAX_ITEMS_TO_DISPLAY = 15

    lateinit var files: List<File>
    val search = SimpleStringProperty()
    val result = ObservableListWrapper<SearchResult>(mutableListOf<SearchResult>())

    val table: TableView<SearchResult> by lazy {
        @Suppress("UNCHECKED_CAST")
        (root.scene.lookup("#tableView") as TableView<SearchResult>)
    }

    val inputSearch: TextField by lazy {
        root.scene.lookup("#inputSearch") as TextField
    }


    init {
        title = messages["title"]

        search.onChange { searchText ->
            when (searchText) {
                null -> result.clear()
                else -> with(result) {
                    clear()
                    addAll(files.search(ContextParser(javaslang.collection.List.empty()).parse(searchText)).take(
                            MAX_ITEMS_TO_DISPLAY))
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
                        }
                    }
                }
            }

            center {
                tableview(result) {
                    id = "tableView"
                    column("Score", SearchResult::score).prefWidth = 50.0
                    column("File", SearchResult::filename).prefWidth = 150.0
                    column("Path", SearchResult::file).prefWidth = 500.0

                    setOnKeyPressed { event ->
                        when (event.code) {
                            UP, PAGE_UP, HOME -> if (this.selectedItem == result.first()) inputSearch.requestFocus()
                            ESCAPE -> inputSearch.requestFocus()
                            LEFT, RIGHT -> println("Display action menu!") // TODO display the menu
                        }
                    }

                    onUserSelect {
                        browse(it)
                    }
                }
            }
        }

        runAsync { files = load(File("..")) } ui { inputSearch.isDisable = false }
    }

    /**
     * Open the selected bookmark in a new browser window
     */
    private fun browse(result: SearchResult) {
        log.info { "Browsing ${result.filename}..." }
        //getDesktop().browse(result.file.parentFile.toURI())
    }
}
