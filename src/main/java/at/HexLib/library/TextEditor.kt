package at.HexLib.library

import javafx.collections.ObservableList

interface TextEditor {
    val mappingTable: ObservableList<String?>
    fun map(byte: Byte, string: String)
    fun unmap(byte: Byte)
}