package com.github.ktoolz

import javaslang.collection.List
import javaslang.collection.Stream
import java.io.File

/**
 * Search if this iterator contains the given one in order
 */
fun <T> Iterator<T>.containsInOrder(searchThis: Iterator<T>): Boolean {

    tailrec fun findNext(toFind: T): Boolean =
            when {
                !hasNext() -> false
                next() == toFind -> true
                else -> findNext(toFind)
            }

    searchThis.forEach {
        if (!findNext(it)) return false
    }

    return true
}

/**
 * Compute a similarity score between this List and the given one.
 *
 *
 * @return 0 if this List contains the given iterator in the same order. Minus this result by 1 for each missing
 * element in the given operator
 */
fun <T> List<T>.score(searchThis: List<T>, minScore: Int = Int.MIN_VALUE): Int {

    fun List<T>.score(currentScore: Int, searchThis: List<T>): Int =
            when {
                searchThis.isEmpty -> currentScore
                currentScore == minScore -> minScore
                else -> dropWhile { it != searchThis.head() }.let { lookupRemainder ->
                    if (lookupRemainder.isEmpty) this.score(currentScore - 1, searchThis.tail())
                    else lookupRemainder.tail().score(currentScore, searchThis.tail())
                }
            }


    return when {
        searchThis.isEmpty -> 0
        else -> this.score(0, searchThis)
    }
}

fun <T> Iterable<T>.containsInOrder(iterable: Iterable<T>) = iterator().containsInOrder(iterable.iterator())
fun String.containsInOrder(s: String) = toCharArray().iterator().containsInOrder(s.toCharArray().iterator())
fun String.score(s: String,
                 minScore: Int = Int.MIN_VALUE) = List.ofAll(toCharArray()).score(List.ofAll(s.toCharArray()),
                                                                                  minScore)

// TODO move to dedicated project
fun traverseFile(f: File): Stream<File> = when {
    f.isFile -> Stream.of(f)
    f.isDirectory -> Stream.of(*f.listFiles()).flatMap { file -> traverseFile(file) }
    else -> Stream.empty()
}


// TODO move to dedicated project
fun time(block: () -> Any): Pair<Any, Long> {
    val now = System.currentTimeMillis()
    val result = block()
    val elapsed = System.currentTimeMillis() - now
    return result to elapsed
}

// TODO move to dedicated project
fun banner(block: () -> Any) {
    println("---------------------------------------")
    println(block())
    println("---------------------------------------")
}
