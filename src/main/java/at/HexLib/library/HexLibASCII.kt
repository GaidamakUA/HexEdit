package at.HexLib.library

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.KeyEvent

class HexLibASCII(hexLib: HexLib) : BasicContentPanel(hexLib), TextEditor {
    override val mappingTable: ObservableList<String?> = FXCollections.observableArrayList()

    init {
        setFontObjects()
        for (i in 0..255) {
            mappingTable.add(null)
        }
    }

    public override fun setFontObjects() {
        minWidth = HexLib.fontWidth * 16 + BasicPanel.borderTwice
        preferredSize = Dimension(minWidth, 0)
        minimumSize = Dimension(minWidth, 0)
    }

    override fun paint(g: Graphics) {
        if (hexLib.buff == null) {
            // nothing to paint resp. not yet initialized
            return
        }
        var ini = hexLib.start * 16
        var fin = ini + (hexLib.lines
                /* add 2, cause first line starts with 1 (instead of 0) and one is header line */
                + 2) * 16
        if (fin > hexLib.buff.size) {
            fin = hexLib.buff.size
        }

        var x = 0
        var y = 0
        if (paintCursorOnly) {
            /* background is not cleared ==> painting cursor is possible */
            val muCursor = cursorPosition - ini
            y = muCursor / 16
            x = muCursor - y * 16
            /* reduce CPU-load to paint only the changed cursor */
            ini = cursorPosition
            if (ini < 0) {
                return
            }
            fin = Math.min(ini + 1, hexLib.buff.size)
        } else {
            /* clear content and repaint complete container */
            super.paint(g)
        }
        g.font = HexLib.font

        for (n in Math.max(ini, 0) until fin) {
            if (checkCurPosPaintable(n)) {
                if (hasFocus() && selectionModel.isEmpty) {
                    if (hasStripes && (hexLib.start + y) % 2 == 0) {
                        g.color = stripeColors[1]
                    } else {
                        g.color = background
                    }
                    fillRect4Cursor(g, x, y, 1)
                    g.color = colorActiveCursor
                    if (hexLib.cursorBlink != null && hexLib.cursorBlink.isActive) {
                        rect(g, x, y, 1)
                    } else {
                        fillRect4Cursor(g, x, y, 1)
                    }
                } else {
                    g.color = colorActiveCursor
                    rect(g, x, y, 1)
                }
                if (hasFocus() && selectionModel.isEmpty) {
                    if (hexLib.cursorBlink != null && hexLib.cursorBlink.isActive) {
                        g.color = fontForeground
                    } else {
                        g.color = fontCursorForeground
                    }
                } else {
                    g.color = fontForeground
                }
            } else {
                if (selectionModel.isPositionWithinMarkPos(n)) {
                    g.color = colorMarkPos
                    fillRect4Mark(g, x, y, 1)
                    if (n == cursorPosition) {
                        /* mark cursor to be recognized more easily in multiple marks */
                        g.color = colorSecondCursor
                        rect(g, x, y, 1)
                    }
                }
                g.color = fontForeground
            }
            val stringRepresentation = convertByteToString(hexLib.buff[n])
            printString(g, stringRepresentation, x++, y)
            if (x == 16) {
                x = 0
                y++
            }
        }
    }

    @ExperimentalUnsignedTypes
    private fun convertByteToString(byte: Byte): String =
            mappingTable[byte.toUByte().toInt()] ?: byte.toUByte().toInt().toChar().toString()

    private fun fillRect4Cursor(g: Graphics, x: Int, y: Int, s: Int) {
        g.fillRect(HexLib.fontWidth * x + BasicPanel.border,
                HexLib.fontHeight * y, // + border + 1,
                HexLib.fontWidth * s,
                HexLib.fontHeight - 1)
    }

    private fun fillRect4Mark(g: Graphics, x: Int, y: Int, s: Int) {
        g.fillRect(HexLib.fontWidth * x + BasicPanel.border,
                HexLib.fontHeight * y, // + border + 1,
                HexLib.fontWidth * s,
                HexLib.fontHeight)
    }

    // calcular la posicion del raton
    public override fun calcCursorPos(x: Int, y: Int): Int {
        var x = x
        var y = y
        x = x / HexLib.fontWidth
        y = y / HexLib.fontHeight
        var total = x + (y + hexLib.start) * 16
        if (total > hexLib.buff.size - 1) {
            total = hexLib.buff.size - 1
        } else if (total < 0) {
            total = 0
        }
        return total
    }

    override fun keyTyped(event: KeyEvent) {
        if (cursorPosition > hexLib.buff.size) {
            return
        }
        if (!(hexLib.txtFieldContainer.isEditable && hexLib.txtFieldContainer.isEnabled)) {
            return
        }
        val char = event.keyChar
        val stringChar = char.toString()
        if (isPrintableChar(char) || mappingTable.contains(stringChar)) {
            hexLib.buff[cursorPosition] = if (mappingTable.contains(stringChar)) {
                mappingTable.indexOf(stringChar).toByte()
            } else {
                char.toByte()
            }
            hexLib.reCalcHashCode()
            if (cursorPosition != hexLib.buff.size - 1) {
                cursorPosition = cursorPosition + 1
            }
            updateCursor()
        }
    }

    private fun isPrintableChar(c: Char): Boolean {
        if (c.toInt() > 255) {
            return false
        }
        val block = Character.UnicodeBlock.of(c)
        return (!Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED
                && block != null && block !== Character.UnicodeBlock.SPECIALS)
    }

    override fun map(byte: Byte, string: String) {
        if (string.length != 1) {
            throw IllegalArgumentException("Only mapping to 1 character supported yet")
        }
        mappingTable[byte.toUByte().toInt()] = string
    }

    override fun unmap(byte: Byte) {
        mappingTable[byte.toUByte().toInt()] = null
    }

    override fun clearMap() {
        for (i in mappingTable.indices) {
            mappingTable[i] = null
        }
    }
}
