package com.github.ktoolz.filefinder

import javafx.scene.text.FontWeight.BOLD
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
    }

    init {
        select(heading) {
            fontSize = 1.5.em
            fontWeight = BOLD
            padding = box(15.px)
        }
    }
}
