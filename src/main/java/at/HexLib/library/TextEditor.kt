package at.HexLib.library

interface TextEditor {
    val mappingTable: Map<Byte, String>
    fun map(byte: Byte, string: String)
    fun unmap(byte: Byte)
}