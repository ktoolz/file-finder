package com.github.ktoolz.parser

import com.github.ktoolz.model.FilterQuery
import javaslang.collection.List
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek

/*
 * File Finder - KToolZ
 *
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
class ParserSpecs : Spek() { init {

    given("a search query without: filter, directory, quote") {
        val (term, contexts, directories) = ContextParser(List.empty()).parse("foo")

        on("checking term content") { it("should be equals to the query") { assertThat(term).isEqualTo("foo") } }
        on("checking contexts") { it("should be empty") { assertThat(contexts).isEmpty() } }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query with trailing spaces") {
        val (term, contexts, directories) = ContextParser(List.empty()).parse("foo              ")

        on("checking term content") {
            it("should be equals to the query without trailing spaces") {
                assertThat(term).isEqualTo("foo")
            }
        }
        on("checking contexts") { it("should be empty") { assertThat(contexts).isEmpty() } }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query ending with a exclamation mark") {
        val (term, contexts, directories) = ContextParser(List.empty()).parse("foo !")

        on("checking term content") {
            it("should be equals to the query including the exlamation mark") {
                assertThat(term).isEqualTo("foo !")
            }
        }
        on("checking contexts") { it("should be empty") { assertThat(contexts).isEmpty() } }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query ending with a slash") {
        val (term, contexts, directories) = ContextParser(List.empty()).parse("foo /")

        on("checking term content") {
            it("should be equals to the query including the slash") {
                assertThat(term).isEqualTo("foo /")
            }
        }
        on("checking contexts") { it("should be empty") { assertThat(contexts).isEmpty() } }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query with slash and exclamation mark with no space before them") {
        val (term, contexts, directories) = ContextParser(List.empty()).parse("foo/bar!goo")

        on("checking term content") {
            it("should be equals to the query including the slash and the exclamation mark") {
                assertThat(term).isEqualTo("foo/bar!goo")
            }
        }
        on("checking contexts") { it("should be empty") { assertThat(contexts).isEmpty() } }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query with a valid filter query at the beginning") {
        val (term, contexts, directories) = ContextParser(List.of("foo")).parse("!foo bar")

        on("checking term content") {
            it("should be equals to the search query without the filer query") {
                assertThat(term).isEqualTo("bar")
            }
        }
        on("checking contexts") {
            it("should contains exactly the filter query") {
                assertThat(contexts).containsExactly(FilterQuery("foo"))
            }
        }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query with a valid filter query at the beginning and another one at the end") {
        val (term, contexts, directories) = ContextParser(List.of("foo", "goo")).parse("!foo bar !goo")

        on("checking term content") {
            it("should be equals to the search query without the filer queries") {
                assertThat(term).isEqualTo("bar")
            }
        }
        on("checking contexts") {
            it("should contains exactly the filter query") {
                assertThat(contexts).containsExactly(FilterQuery("foo"), FilterQuery("goo"))
            }
        }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

    given("a search query with a valid negated filter query") {
        val (term, contexts, directories) = ContextParser(List.of("foo")).parse("!!foo bar")

        on("checking term content") {
            it("should be equals to the search query without the filer query") {
                assertThat(term).isEqualTo("bar")
            }
        }
        on("checking contexts") {
            it("should contains exactly the filter query and it should be negated") {
                assertThat(contexts).containsExactly(FilterQuery("foo", true))
            }
        }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }


    given("a search query with an invalid filter query") {
        val (term, contexts, directories) = ContextParser(List.empty()).parse("!!foo bar")

        on("checking term content") {
            it("should be equals to the input string") {
                assertThat(term).isEqualTo("!!foo bar")
            }
        }
        on("checking contexts") {
            it("should be empty") { assertThat(contexts).isEmpty() }
        }
        on("checking directories") { it("should be empty") { assertThat(directories).isEmpty() } }
    }

}
}