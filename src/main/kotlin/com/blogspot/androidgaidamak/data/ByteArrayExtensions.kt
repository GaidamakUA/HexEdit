package com.blogspot.androidgaidamak.data

fun ByteArray.searchByByteDifference(query: ByteArray): Int {
    return this.searchByByteDifference(0, query)
}

fun ByteArray.searchByByteDifference(startingPosition: Int, query: ByteArray): Int {
    if (startingPosition >= this.size) {
        throw java.lang.IllegalArgumentException("Starting position out of bounds")
    }
    if (query.size < 2) {
        throw IllegalArgumentException("It's impossible to search if there are no difference");
    }
    outer@ for (i in startingPosition..size) {
        val diff = this[i] - query[0]
        for (j in query.indices) {
            if (i + j >= size) {
                return -1
            }
            if (this[i + j] - query[j] != diff) {
                continue@outer
            }
        }
        return i
    }
    return -1
}