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

import com.github.ktoolz.filefinder.matching.matchers
import com.github.ktoolz.filefinder.utils.toJavaslangList
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek

class MatcherSpecs : Spek() { init {

    given("the list a,x,b,x,c,x,d,x,e") {
        val inputList = "abcde".toJavaslangList().intersperse('x')
        println(inputList)

        on("matching list a,b,c") {
            val search = "abc".toJavaslangList()
            val searchResult = inputList.matchers(search)

            it("should contains no error") {
                assertThat(searchResult.filter { !it.match }).hasSize(0)
            }
        }

        on("matching list e,b,c") {
            val search = "ebc".toJavaslangList()
            val searchResult = inputList.matchers(search)

            it("should contains one error") {
                assertThat(searchResult.filter { !it.match }).hasSize(1)
            }
        }
    }

}
}

