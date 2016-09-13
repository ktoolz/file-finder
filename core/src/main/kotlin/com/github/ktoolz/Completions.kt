package com.github.ktoolz

import com.github.ktoolz.model.PatternMatcher
import javaslang.collection.List

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
fun <T> List<T>.score(searchThis: List<T>, minScore: Int = Int.MIN_VALUE, currentScore: Int = 0): Int =
        when {
            searchThis.isEmpty -> currentScore
            currentScore == minScore -> currentScore
            else -> dropWhile { it != searchThis.head() }.let { lookupRemainder ->
                if (lookupRemainder.isEmpty) this.score(searchThis.tail(), minScore, currentScore - 1)
                else lookupRemainder.tail().score(searchThis.tail(), minScore, currentScore)
            }
        }

fun <T> List<T>.matchers(searchThis: List<T>,
                         currentMatchers: List<PatternMatcher<T>>): List<PatternMatcher<T>> =
        when {
            searchThis.isEmpty -> currentMatchers
            else ->
                dropWhile { it != searchThis.head() }.let { remainder ->
                    if (remainder.isEmpty) this.matchers(searchThis.tail(),
                                                         currentMatchers.append(PatternMatcher(searchThis.head(),
                                                                                               false,
                                                                                               0)))
                    else remainder.tail().matchers(searchThis.tail(),
                                                   currentMatchers.append(PatternMatcher(searchThis.head(),
                                                                                         true,
                                                                                         this@matchers.indexOf(
                                                                                                 searchThis.head()))))

                }
        }

fun <T> Iterable<T>.containsInOrder(iterable: Iterable<T>) = iterator().containsInOrder(iterable.iterator())
fun String.containsInOrder(s: String) = toCharArray().iterator().containsInOrder(s.toCharArray().iterator())
fun String.score(s: String,
                 minScore: Int = Int.MIN_VALUE) = List.ofAll(toCharArray()).score(List.ofAll(s.toCharArray()),
                                                                                  minScore)

fun String.matchers(s: String) = List.ofAll(toCharArray()).matchers(List.ofAll(s.toCharArray()), List.empty())


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
