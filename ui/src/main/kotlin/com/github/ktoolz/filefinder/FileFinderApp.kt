package com.github.ktoolz.filefinder

import com.github.ktoolz.filefinder.view.MainView
import javafx.application.Application
import tornadofx.App
import tornadofx.importStylesheet

class FileFinderApp : App(MainView::class) {
    init {
        importStylesheet(Styles::class)
    }
}
fun main(args: Array<String>) {
    Application.launch(FileFinderApp::class.java, *args)
}
