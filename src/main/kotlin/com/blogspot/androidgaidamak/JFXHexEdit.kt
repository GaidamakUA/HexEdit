package com.blogspot.androidgaidamak

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File


class JFXHexEdit : Application() {

    lateinit var stage: Stage

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        primaryStage.title = "HexEdit"

        val url = File("src/main/kotlin/com/blogspot/androidgaidamak/JFXHexEditLayout.fxml").toURI().toURL()
        val loader = FXMLLoader(url)
        val root = loader.load<Parent>()
        val controller = loader.getController() as JFXHexEditController
        controller.setApplication(this)
        primaryStage.scene = Scene(root, 800.0, 600.0)
        primaryStage.show()
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch(JFXHexEdit::class.java, *args)
        }
    }
}
