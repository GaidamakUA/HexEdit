package com.blogspot.androidgaidamak.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class ByteArrayExtensionsKtTest {

    @Test
    fun simple_failed_queryTooShort() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val query: ByteArray = byteArrayOf(0x00)
        val exception = assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
            byteArray.searchByByteDifference(query)
        }
        assertEquals("It's impossible to search if there are no difference", exception.message)
    }

    @Test
    fun simple_foundOnFirstPosition() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val query: ByteArray = byteArrayOf(0x00, 0x01, 0x02)
        val position = byteArray.searchByByteDifference(query)
        assertEquals(0, position)
    }

    @Test
    fun simple_foundOnSecondPosition() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x03)
        val query: ByteArray = byteArrayOf(0x01, 0x03)
        val position = byteArray.searchByByteDifference(query)
        assertEquals(1, position)
    }

    @Test
    fun simple_queryNotPresentInArray() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x03)
        val query: ByteArray = byteArrayOf(0x01, 0x04)
        val position = byteArray.searchByByteDifference(query)
        assertEquals(-1, position)
    }

    @Test
    fun startingPosition_failed_biggerThenLength() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x01, 0x03)
        val query: ByteArray = byteArrayOf(0x01, 0x04)
        val exception = assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) {
            byteArray.searchByByteDifference(3, query)
        }
        assertEquals("Starting position out of bounds", exception.message)
    }

    @Test
    fun startingPosition_foundSecondEntry() {
        val byteArray: ByteArray = byteArrayOf(0x00, 0x03, 0x00, 0x03)
        val query: ByteArray = byteArrayOf(0x01, 0x04)
        val position = byteArray.searchByByteDifference(1, query)
        assertEquals(2, position)
    }
}