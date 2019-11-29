package com.blogspot.androidgaidamak

import at.HexLib.library.HexLib
import com.blogspot.androidgaidamak.data.search
import com.blogspot.androidgaidamak.data.searchByByteDifference
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.embed.swing.SwingNode
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.util.Callback
import javafx.util.StringConverter
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
    lateinit var inputTypeChoiceBox: ChoiceBox<String>

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
            byteColumn.onEditCommit = EventHandler { event ->
                readByte(event.newValue.substring(2))?.let {
                    val oldByte: Byte = readByte(event.oldValue.substring(2))!!
                    hexLib.textEditor.map(it, hexLib.textEditor.mappingTable[oldByte.toInt()]!!)
                    hexLib.textEditor.unmap(oldByte)
                }
            }
            byteColumn.cellFactory = TextFieldTableCell.forTableColumn()

            charColumn.cellValueFactory = Callback { stringValue -> SimpleStringProperty(stringValue.value) }
            charColumn.onEditCommit = EventHandler { event ->
                val targetByte = hexLib.textEditor.mappingTable.indexOf(event.oldValue)
                hexLib.textEditor.map(targetByte.toByte(), event.newValue)
            }
            charColumn.cellFactory = TextFieldTableCell.forTableColumn()
            mappingTableView.items = FilteredList(hexLib.textEditor.mappingTable, Predicate { string -> string != null })
        }
        borderPane.center = swingNode
        syncUi()

        searchListView.items = searchByteList
        searchListView.cellFactory = TextFieldListCell.forListView(object : StringConverter<Byte>() {
            override fun toString(item: Byte?): String = item?.let { getHexRepresentation(it) } ?: "null"

            override fun fromString(string: String?): Byte = string?.let { readByte(it.substring(2)) } ?: 0

        })
//        searchListView.cellFactory = Callback {
////            object : ListCell<Byte>() {
////                override fun updateItem(item: Byte?, empty: Boolean) {
////                    super.updateItem(item, empty)
////                    text = item?.let { getHexRepresentation(it) }
////                }
////            }
////        }
        searchListView.isEditable = true
        searchListView.onEditCommit = EventHandler { event -> searchByteList[event.index] = event.newValue }

        inputTypeChoiceBox.items = FXCollections.observableArrayList("Hex", "Dec")
        inputTypeChoiceBox.value = "Hex"
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
        var byteKey: Byte = readByte(byteField) ?: return
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
    }

    fun undo() {
        println("Undo")
    }

    fun redo() {
        println("Redo")
    }

    private fun readByte(textField: TextField): Byte? {
        try {
            val radix = if (inputTypeChoiceBox.value == "Dec") 10 else 16
            val intValue = textField.text.toInt(radix)
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

    private fun readByte(text: String): Byte? {
        try {
            val radix = if (inputTypeChoiceBox.value == "Dec") 10 else 16
            val intValue = text.toInt(radix)
            if (intValue > 255 || intValue < 0) {
                statusLabel.text = "Not a byte"
                return null
            }
            return intValue.toByte()
        } catch (e: java.lang.NumberFormatException) {
            statusLabel.text = "Not a byte"
            println(e)
            return null
        }
    }

    fun addSearchByte() {
        val byteValue: Byte = readByte(byteSearchTextField) ?: return
        searchByteList.add(byteValue)
    }

    fun searchByByteDiff() {
        val position = hexLib.byteContent.searchByByteDifference(hexLib.cursorPosition, searchByteList.toByteArray())
        statusLabel.text = "position by dif: $position"
        hexLib.setCursorPostion(position)
    }

    fun searchExact() {
        val position = hexLib.byteContent.search(hexLib.cursorPosition, searchByteList.toByteArray())
        statusLabel.text = "position exact: $position"
        hexLib.setCursorPostion(position)
    }

    fun clearSearch() {
        searchByteList.clear()
    }

    fun clearMapping() {
        hexLib.textEditor.clearMap()
    }
}
