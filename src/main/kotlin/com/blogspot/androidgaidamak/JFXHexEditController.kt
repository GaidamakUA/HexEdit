package com.blogspot.androidgaidamak

import at.HexLib.library.HexLib
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
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

    lateinit var searchListView: ListView<Byte>
    lateinit var byteSearchTextField: TextField
    private val searchByteList: ObservableList<Byte> = FXCollections.observableArrayList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("initialize $borderPane")

        val swingNode = SwingNode()
        SwingUtilities.invokeLater {
            hexLib = HexLib()
            swingNode.content = hexLib

            byteColumn.cellValueFactory = Callback { stringValue ->
                val targetByte = hexLib.textEditor.mappingTable.indexOf(stringValue.value).toByte()
                SimpleStringProperty(getHexRepresentation(targetByte))
            }
            charColumn.cellValueFactory = Callback { stringValue -> SimpleStringProperty(stringValue.value) }
            mappingTableView.items = FilteredList(hexLib.textEditor.mappingTable, Predicate { string -> string != null })
        }
        borderPane.center = swingNode
        syncUi()

        searchListView.items = searchByteList
        searchListView.cellFactory = Callback {
            object : ListCell<Byte>() {
                override fun updateItem(item: Byte?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = item?.let { getHexRepresentation(it) }
                }
            }
        }
    }

    private fun getHexRepresentation(byte: Byte): String =
            "0x" + byte.toUByte().toString(16).padStart(2, '0')

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
            var byteKey: Byte = readByte(byteField) ?: return
            var intKey = byteField.text.toInt()
            if (intKey > 255 || intKey < 0) {
                statusLabel.text = "Not a byte"


                return
            }
//            var byteKey = intKey.toByte()
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

    private fun readByte(textField: TextField): Byte? {
        try {
            var intValue = textField.text.toInt()
            textField.clear()
            if (intValue > 255 || intValue < 0) {
                statusLabel.text = "Not a byte"
                return null
            }
            return intValue.toByte()
        } catch (e: java.lang.NumberFormatException) {
            statusLabel.text = "Not a byte"
            println(e)
            textField.clear()
            return null
        }
    }

    fun addSearchByte() {
        val byteValue: Byte = readByte(byteSearchTextField) ?: return
        searchByteList.add(byteValue)
    }

    fun search() {

    }

    fun clearSearch() {
        searchByteList.clear()
    }
}
