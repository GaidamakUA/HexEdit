package com.blogspot.androidgaidamak

import at.HexLib.library.HexLib
import javafx.embed.swing.SwingNode
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import java.io.File
import java.net.URL
import java.util.*
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("initialize $borderPane")

        val swingNode = SwingNode()
        SwingUtilities.invokeLater {
            hexLib = HexLib()
            swingNode.content = hexLib

            hexLib.textEditor.map(0x55, "Д")
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

    fun undo() {
        println("Undo")
    }

    fun redo() {
        println("Redo")
    }
}
