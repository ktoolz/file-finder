package com.github.ktoolz.view

import com.github.ktoolz.controller.load
import com.github.ktoolz.model.SearchResult
import com.github.ktoolz.score
import com.github.ktoolz.traverseFile
import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.*
import javafx.scene.layout.BorderPane
import javaslang.Tuple3
import javaslang.collection.Stream
import tornadofx.*
import java.io.File

// TODO move to dedicated project
inline fun <T> T.use(block: (T) -> Unit): T = apply { block(this) }

// TODO move to dedicated project
inline fun <reified T> Any.castUse(clazz: Class<T>, block: T.() -> Unit) {
    if (this is T) block(this)
}

class MainView : View() {

    override val root: BorderPane = BorderPane()

    val minScore = -3

    lateinit var files: Stream<File>

    val search = SimpleStringProperty()

    val result = ObservableListWrapper<SearchResult>(mutableListOf<SearchResult>())

    val table: TableView<SearchResult> by lazy {
        @Suppress("UNCHECKED_CAST")
        (root.scene.lookup("#tableView") as TableView<SearchResult>)
    }


    init {
        title = messages["title"]

        search.onChange { searchText ->
            when (searchText) {
                null -> result.clear()
                else -> with(result) {
                    clear()
                    addAll(search(searchText))
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
                        //                            DOWN -> scene.lookup("#tableView").use { lookup ->
                        //                                when (lookup) {
                        //                                    is TableView<*> -> lookup.apply {
                        //                                        selectFirst()
                        //                                        requestFocus()
                        //                                    }
                        //                                }
                        //                            }
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
                            UP, PAGE_UP, HOME -> if (this.selectedItem == result.first()) scene.lookup("#inputSearch").requestFocus()
                            ESCAPE -> scene.lookup("#inputSearch").requestFocus()
                            LEFT, RIGHT -> println("Display action menu!") // TODO display the menu
                        }
                    }

                    onUserSelect {
                        browse(it)
                    }
                }


                //add(table.apply { id... })
            }
        }

        runAsync { files = load(File("..")) } success { println("Loaded!") } ui { root.scene.lookup("#inputSearch").castUse(TextField::class.java) { this.isDisable = false } }

    }

    private fun search(searchText: String): Stream<SearchResult> {
        val lowerCaseSearch = searchText.toLowerCase()

        // Exemple of ponderation, to be remove
        // TODO remove
        val ponderation: (SearchResult) -> SearchResult = {
            when {
                it.filename.endsWith(".kt") -> it.copy(score = it.score + 2)
                it.filename.endsWith(".md") -> it.copy(score = it.score + 1)
                it.filename.endsWith(".class") -> it.copy(score = Int.MIN_VALUE)
                else -> it
            }
        }
        val toSearchResult: (File) -> SearchResult = { file ->
            val lowerCaseName = file.name.toLowerCase()
            SearchResult(lowerCaseName.score(lowerCaseSearch, minScore),
                         file.name,
                         file)
        }
        return files.map(toSearchResult)
                .map(ponderation)
                .filter { it.score >= minScore }
                .sortBy { Tuple3(-it.score, it.filename.length, it.filename) }
                .take(15)
    }


    /**
     * Open the selected bookmark in a new browser window
     */
    private fun browse(result: SearchResult) {
        log.info { "Browsing ${result.filename}..." }
        //getDesktop().browse(result.file.parentFile.toURI())
    }
}
