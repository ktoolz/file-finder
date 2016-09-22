/*
 * File-Finder - KToolZ
 *
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.ktoolz.filefinder.parser

import com.github.ktoolz.filefinder.model.FilterQuery
import com.github.ktoolz.filefinder.model.SearchQuery
import javaslang.collection.List

class ContextParser(val filterNames: List<String>,
                    val filterSpecialChar: Char = '!',
                    val directorySpecialChar: Char = '/') {

    /**
     * Mutable version of a SearchQuery used to build it step by step
     */
    private class MutableQuery(val term: MutableList<Char> = mutableListOf(),
                               val directories: MutableList<List<Char>> = mutableListOf(),
                               val modifiers: MutableList<FilterQuery> = mutableListOf()) {

        override fun toString(): String = "$term, $directories, $modifiers"

        fun toQuery() = SearchQuery(term.joinToString("").trim(),
                                    List.ofAll(modifiers),
                                    List.ofAll(directories.map { it.mkString("") }))
    }

    /**
     * Entry point of this class, parse a String and return a SearchQuery
     */
    fun parse(query: String) = neutral(List.ofAll(query.toCharArray()), MutableQuery()).toQuery()


    /**
     * Private extension for cleaner code
     *
     * @return true if the receiver char can act as a separator between two tokens
     */
    private fun Char.isTokenSeparator() = this == ' '

    /**
     * Neutral state of the state machine.
     *
     * - If the state machine read a space then it try to deal with it or backtrack to the 'query' state
     * - Any other char will goes to 'query' state
     *
     * If End of Stream reached, simply return the built query
     */
    private fun neutral(list: List<Char>, query: MutableQuery): MutableQuery =
            if (list.isEmpty) query
            else when (list.head()) {
                ' ' -> pendingSpace(list.tail(), query) { query(list, query) }
                filterSpecialChar -> filter(list.tail(), query) { query(list, query) }
                directorySpecialChar -> directory(list.tail(), query) { query(list, query) }
                else -> query(list, query)
            }

    /**
     * A space has been readed. Check if this space is followed by a special char
     *
     * - If it's followed by a spacial char (like '/' or '!') go to the corresponding state to deal with it
     * - If it's followed by another space, keep in the same state
     * - Any other char will trigger the backtracking
     *
     * If End of Stream reached, simply return the built query ignoring the trailing spaces
     */
    private fun pendingSpace(list: List<Char>, query: MutableQuery, backtracking: () -> MutableQuery): MutableQuery =
            if (list.isEmpty) query // do nothing as trailing space is ignored
            else when (list.head()) {
                ' ' -> pendingSpace(list.tail(), query, backtracking)
                filterSpecialChar -> filter(list.tail(), query, backtracking = backtracking)
                directorySpecialChar -> directory(list.tail(), query, backtracking)
                else -> backtracking()
            }


    /**
     * We are reading a filterDirectories ('!' char)
     *
     * # Read the filterDirectories identifier
     * # Check if it's a valid filterDirectories name
     * ## If yes, add it, then process the rest of the string as normal String (neutral state)
     * ## If no, trigger backtracking
     *
     * If End of Stream reached, the special char was the last one of the query.
     * Call backtracking to process this char normally (it's not a filterDirectories)
     */
    private fun filter(list: List<Char>,
                       query: MutableQuery,
                       negated: Boolean = false,
                       backtracking: () -> MutableQuery): MutableQuery =
            if (list.isEmpty) backtracking()
            else if (list.head() == filterSpecialChar) filter(list.tail(), query, !negated, backtracking)
            else {
                val split = list.splitAt { it.isTokenSeparator() }
                if (filterNames.contains(split._1.mkString())) {
                    query.modifiers.add(FilterQuery(split._1.mkString(), negated))
                    neutral(split._2, query)
                } else {
                    backtracking()
                }
            }


    /**
     * We are reading a filterDirectories ('/' char)
     *
     * # Read the directory filterDirectories
     * # Check if it's a valid directory filterDirectories (can contains '/')
     * ## If yes, add it, then process the rest of the string as normal String (neutral state)
     * ## If no, trigger backtracking
     *
     * If End of Stream reached, the special char was the last one of the query.
     * Call backtracking to process this char normally (it's not a filterDirectories)
     */
    private fun directory(list: List<Char>, query: MutableQuery, backtracking: () -> MutableQuery): MutableQuery =
            if (list.isEmpty) backtracking()
            else {
                val split = list.splitAt { it.isTokenSeparator() }
                val isValidDirectoryChar: (Char) -> Boolean = {
                    // TODO extract this list into a constant
                    it.isLetterOrDigit() || List.of('_', '-', directorySpecialChar).contains(it)
                }
                if (split._1.all(isValidDirectoryChar)) {
                    query.directories.add(split._1)
                    neutral(split._2, query)
                } else {
                    backtracking()
                }
            }

    /**
     * We are reading a query term (a.k.a. not a special trigger)
     *
     * This method deals with double quote and simple quote.
     * If the first char is a double/simple quote, read the rest of the String ignoring special trigger until the pair
     * is matched
     * If the pair doesn't match, backtrack to a normal reading (the double/simple quote will be part of the query)
     *
     * If End of Stream reached, simply return the built query
     */
    private fun query(list: List<Char>, query: MutableQuery): MutableQuery =
            if (list.isEmpty) query
            else {
                fun readUntil(c: Char, backtracking: () -> MutableQuery) =
                        list.tail().splitAt { it == c }.let { split ->
                            // use .let for this special purpose: https://www.youtube.com/watch?v=Wt88GMJmVk0
                            if (split._2.isEmpty) backtracking() // pair is not matched
                            else {
                                query.term.addAll(split._1)
                                neutral(split._2.tail(), query)
                            }
                        }

                val readWord = {
                    // make sure to consume at leat one char even if it's a space: https://www.youtube.com/watch?v=zI339U6GS9s
                    query.term.add(list.head())
                    val split = list.tail().splitAt { it.isTokenSeparator() }
                    query.term.addAll(split._1)
                    neutral(split._2, query)
                }

                when (list.head()) {
                    '\'' -> readUntil('\'') { readWord() }
                    '"' -> readUntil('"') { readWord() }
                    else -> {
                        readWord()
                    }
                }
            }
}
