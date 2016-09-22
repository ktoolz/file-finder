package com.github.ktoolz.filefinder.utils

fun time(block: () -> Any): Pair<Any, Long> {
    val now = System.currentTimeMillis()
    val result = block()
    val elapsed = System.currentTimeMillis() - now
    return result to elapsed
}

fun banner(block: () -> Any) {
    println("---------------------------------------")
    println(block())
    println("---------------------------------------")
}

inline fun <T> T.use(block: (T) -> Unit): T = apply { block(this) }

inline fun <reified T> Any.castUse(clazz: Class<T>, block: T.() -> Unit) {
    if (this is T) block(this)
}
