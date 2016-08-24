package com.github.ktoolz

import com.github.ktoolz.view.MainView
import tornadofx.App
import tornadofx.importStylesheet

class FileFinderApp : App(MainView::class) {
    init {
        importStylesheet(Styles::class)
    }
}