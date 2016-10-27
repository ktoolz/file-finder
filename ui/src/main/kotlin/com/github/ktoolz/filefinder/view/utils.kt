package com.github.ktoolz.filefinder.view

import javafx.scene.input.KeyEvent

val KeyEvent.isNoModifier: Boolean get() = !isAltDown && !isControlDown && !isMetaDown && !isShiftDown
val KeyEvent.isOnlyControlDown: Boolean get() = !isAltDown && isControlDown && !isMetaDown && !isShiftDown
val KeyEvent.isOnlyShiftDown: Boolean get() = !isAltDown && !isControlDown && !isMetaDown && isShiftDown
val KeyEvent.isOnlyAltDown: Boolean get() = isAltDown && !isControlDown && !isMetaDown && !isShiftDown
val KeyEvent.isOnlyMetaDown: Boolean get() = !isAltDown && !isControlDown && isMetaDown && !isShiftDown
