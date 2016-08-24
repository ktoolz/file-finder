package com.github.ktoolz.model

import java.io.File

/**
 * Store a search result
 */
data class SearchResult(val score: Int, val filename: String, val file: File)