package com.github.ktoolz.view

import com.github.ktoolz.controller.load
import com.github.ktoolz.matchers
import com.github.ktoolz.model.SearchResult
import com.sun.javafx.collections.ObservableListWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode.*
import javafx.scene.layout.BorderPane
import javaslang.Tuple3
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
                            UP, PAGE_UP, HOME -> if (this.selectedItem == result.first()) inputSearch.requestFocus()
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

        runAsync { files = load(File("..")) } success { println("Loaded!") } ui { inputSearch.isDisable = false }

    }

    private fun search(searchText: String): List<SearchResult> {
        val lowerCaseSearch = searchText.toLowerCase()

        // Exemple of ponderation, to be remove
        // TODO remove
        /*        val ponderation: (SearchResult) -> SearchResult = {
                    when {
                        it.filename.endsWith(".kt") -> it.copy(score = it.score + 2)
                        it.filename.endsWith(".md") -> it.copy(score = it.score + 1)
                        it.filename.endsWith(".class") -> it.copy(score = Int.MIN_VALUE)
                        else -> it
                    }
                }*/
        val toSearchResult: (File) -> SearchResult = { file ->
            val lowerCaseName = file.name.toLowerCase()
            SearchResult(lowerCaseName.matchers(lowerCaseSearch), file)
        }
        return files.map(toSearchResult)
                //                .map(ponderation)
                .apply {
                    this.forEach {
                        println(it.file.toString())
                        it.matchers.forEach { println(it.matcher + " > " + it.found + " > " + it.distance) }
                        println("-----------")
                    }
                }
                .filter { it.score >= minScore }
                .sortedBy { Tuple3(-it.score, it.filename.length, it.filename) }
                .subList(0, 15)
    }


    /**
     * Open the selected bookmark in a new browser window
     */
    private fun browse(result: SearchResult) {
        log.info { "Browsing ${result.filename}..." }
        //getDesktop().browse(result.file.parentFile.toURI())
    }
}
