package com.blogspot.androidgaidamak

import at.HexLib.library.HexLib
import javafx.beans.property.SimpleStringProperty
import javafx.collections.transformation.FilteredList
import javafx.embed.swing.SwingNode
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.util.Callback
import java.io.File
import java.net.URL
import java.util.*
import java.util.function.Predicate
import javax.swing.SwingUtilities


class JFXHexEditController : Initializable {
    private var openedFile: File? = null
    private lateinit var jfxHexEdit: JFXHexEdit
    private lateinit var hexLib: HexLib
    lateinit var borderPane: BorderPane
    lateinit var editMenu: Menu
    lateinit var saveMenuItem: MenuItem
    lateinit var saveAsMenuItem: MenuItem
    lateinit var statusLabel: Label

    lateinit var byteColumn: TableColumn<String, String>
    lateinit var charColumn: TableColumn<String, String>
    lateinit var mappingTableView: TableView<String>

    lateinit var byteField: TextField
    lateinit var charField: TextField

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("initialize $borderPane")

        val swingNode = SwingNode()
        SwingUtilities.invokeLater {
            hexLib = HexLib()
            swingNode.content = hexLib

            hexLib.textEditor.mappingTable[0x55] = "Д"

            byteColumn.cellValueFactory = Callback { stringValue ->
                SimpleStringProperty("0x" + hexLib.textEditor.mappingTable.indexOf(stringValue.value).toUByte().toString(16).padStart(2, '0'))
            }
            charColumn.cellValueFactory = Callback { stringValue -> SimpleStringProperty(stringValue.value) }
            mappingTableView.items = FilteredList(hexLib.textEditor.mappingTable, Predicate { string -> string != null })
        }
        borderPane.center = swingNode
        syncUi()
    }

    fun openFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Open File"
        val selectedFile = fileChooser.showOpenDialog(jfxHexEdit.stage)
        selectedFile?.let {
            openedFile = it
            hexLib.byteContent = it.readBytes()
            statusLabel.text = "${it.name} loaded"
            syncUi()
        }
    }

    fun saveFile() {
        openedFile?.apply {
            writeBytes(hexLib.byteContent)
            statusLabel.text = "$name saved"
        }
    }

    private fun syncUi() {
        saveMenuItem.isDisable = openedFile == null
        saveAsMenuItem.isDisable = openedFile == null
        editMenu.isDisable = openedFile == null
        for (item in editMenu.items) {
            item.isDisable = openedFile == null
        }
    }

    fun setApplication(stage: JFXHexEdit) {
        this.jfxHexEdit = stage
    }

    fun cut() {
        hexLib.hexTransferHandler.cutContent2Clipboard()
    }

    fun copy() {
        hexLib.hexTransferHandler.copyContent2Clipboard()
    }

    fun paste() {
        hexLib.hexTransferHandler.pasteContentFromClipboard()
    }

    fun addMapping() {
        try {
            var intKey = byteField.text.toInt()
            if (intKey > 255 || intKey < 0) {
                statusLabel.text = "Not a byte"

                byteField.clear()
                charField.clear()
                return
            }
            var byteKey = intKey.toByte()
            var charValue = charField.text
            if (charValue.length != 1) {
                statusLabel.text = "Only 1 character is supported for now"

                byteField.clear()
                charField.clear()
                return
            }
            hexLib.textEditor.map(byteKey, charValue)

            byteField.clear()
            charField.clear()
        } catch (e: NumberFormatException) {
            statusLabel.text = "Not a byte"
        }
    }

    fun undo() {
        println("Undo")
    }

    fun redo() {
        println("Redo")
    }
}
