package com.blogspot.androidgaidamak

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class JFXHexEdit : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.title = "HexEdit"
        val btn = Button()
        btn.text = "Say 'Hello World'"
        btn.onAction = EventHandler { println("Hello World!") }

        val root = StackPane()
        root.children.add(btn)
        primaryStage.scene = Scene(root, 300.0, 250.0)
        primaryStage.show()
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch(JFXHexEdit::class.java, *args)
        }
    }
}
