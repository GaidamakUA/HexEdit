package com.blogspot.androidgaidamak.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ByteArrayExtensionsSearchTest {

    @Test
    fun simpleSearch() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val query: ByteArray = byteArrayOf(0x01)
        val position = byteArray.search(0, query)
        assertEquals(1, position)
    }

    @Test
    fun simpleSearch_fail() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val query: ByteArray = byteArrayOf(0x03)
        val position = byteArray.search(0, query)
        assertEquals(-1, position)
    }

    @Test
    fun simpleSearch_twoBytes() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val query: ByteArray = byteArrayOf(0x01, 0x02)
        val position = byteArray.search(0, query)
        assertEquals(1, position)
    }

    @Test
    fun simpleSearch_difDoesntWork() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val query: ByteArray = byteArrayOf(0x02, 0x03)
        val position = byteArray.search(0, query)
        assertEquals(-1, position)
    }
}