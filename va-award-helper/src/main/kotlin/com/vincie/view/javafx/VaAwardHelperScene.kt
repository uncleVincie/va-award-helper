package com.vincie.view.javafx

import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

class VaAwardHelperScene {

    fun createScene() =
        Scene(
            createContent(), 1280.0, 832.0
        )

    private fun createContent(): Region {
        val label = Label("Hello JavaFX")

        return StackPane(label)
    }
}