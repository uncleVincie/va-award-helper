package com.vincie.view.javafx

import com.vincie.controller.CombinedRatingService
import com.vincie.model.AwardPercentage
import com.vincie.model.Bilateral
import com.vincie.model.Rating
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage

private const val DEFAULT_WIDTH = 500.0
private const val DEFAULT_HEIGHT = 800.0

class VaAwardHelperScene(
    private val service: CombinedRatingService,
    private val stage: Stage
) {

    private val fileChooser = FileChooser().also { it.title = "Save Report" }

    //state for the ui
    private val ratings = FXCollections.observableArrayList<Rating>()
    private val finalRating: IntegerProperty = SimpleIntegerProperty()
    private val currentNonBilateral: ObjectProperty<AwardPercentage> = SimpleObjectProperty()
    private val currentBilateralLimbA: ObjectProperty<Bilateral> = SimpleObjectProperty()
    private val currentBilateralAwardA: ObjectProperty<AwardPercentage> = SimpleObjectProperty()
    private val currentBilateralLimbB: ObjectProperty<Bilateral> = SimpleObjectProperty()
    private val currentBilateralAwardB: ObjectProperty<AwardPercentage> = SimpleObjectProperty()
    private var message: StringProperty = SimpleStringProperty()


    fun createScene() =
        Scene(
            createParentRegion(), DEFAULT_WIDTH, DEFAULT_HEIGHT
        )

    private fun createParentRegion() = VBox(20.0, createHeader(), createTop(), createCenter(), createBottom(), createMessageCenter()).also {
        it.stylesheets.add(this.javaClass.getResource("/css/va-scene-style.css").toExternalForm())
    }

    private fun createHeader(): Region {
        return HBox(Label("VA Award Calculator").also { it.styleClass.add("header") }).also { it.alignment = Pos.CENTER }
    }


    private fun createTop(): Region {

        return HBox(60.0, createNonBilateralBox(), createBilateralBox()).also { it.alignment = Pos.CENTER }
    }

    private fun createCenter(): Region {

        return VBox(
            1.0,
            Label("Summary"),
            createSummaryTable(),
            Label("Select row and hit Backspace to remove a rating")
        ).also { it.alignment = Pos.CENTER }
    }

    private fun createBottom(): Region {

        return HBox(
            20.0,
            createClearAllButton(),
            createSaveReportButton(),
            Label("Total: "),
            createFinalRatingLabel(),
            Label(" ")
        ).also { it.alignment = Pos.CENTER_RIGHT }
    }

    private fun createMessageCenter(): Region {
        return HBox(Label("").also {
            it.textProperty().bind(message)
            it.styleClass.add("message")
        })
    }

    private fun createNonBilateralBox(): Region {

        return VBox(
            6.0,
            Label("Enter Non-Bilateral Award"),
            createPercentChoiceBox(currentNonBilateral),
            createAddButtonNonBilateral()
        )
    }

    private fun createBilateralBox(): Region {
        return VBox(
            6.0,
            Label("Enter Bi-Lateral Award"),
            createSetOfBilateralChoiceBoxes(currentBilateralLimbA, currentBilateralAwardA),
            createSetOfBilateralChoiceBoxes(currentBilateralLimbB, currentBilateralAwardB),
            createAddButtonBilateral()
        )
    }

    private fun createSetOfBilateralChoiceBoxes(
        limb: ObjectProperty<Bilateral>,
        award: ObjectProperty<AwardPercentage>
    ): Region {
        return HBox(10.0, createLimbChoiceBox(limb), createPercentChoiceBox(award))
    }

    private fun createPercentChoiceBox(property: ObjectProperty<AwardPercentage>): Node {
        val choiceBox = ChoiceBox<AwardPercentage>()
        choiceBox.items.addAll(AwardPercentage.entries)
        choiceBox.valueProperty().bindBidirectional(property)
        return choiceBox
    }

    private fun createLimbChoiceBox(property: ObjectProperty<Bilateral>): Node {
        val choiceBox = ChoiceBox<Bilateral>()
        choiceBox.items.add(Bilateral.LEFT_ARM)
        choiceBox.items.add(Bilateral.RIGHT_ARM)
        choiceBox.items.add(Bilateral.LEFT_LEG)
        choiceBox.items.add(Bilateral.RIGHT_LEG)
        choiceBox.valueProperty().bindBidirectional(property)
        return choiceBox
    }

    private fun createAddButtonNonBilateral(): Node {
        return Button("+").also {
            it.setOnAction { evt ->
                if (currentNonBilateral.get() != null) {
                    message.set("")
                    ratings.add(Rating(Bilateral.NON_BILATERAL, currentNonBilateral.get()))
                    finalRating.set(service.calculateFinalRating(ratings)) //recalculate after non-bilateral addition, auto-unboxing works here, no need to convert to List
                } else {
                    message.set("Please choose an award value for non-bilateral entry")
                }
            }
        }
    }

    private fun createAddButtonBilateral(): Node {
        return Button("+").also {
            it.setOnAction { evt ->
                if (isValidBilateral()) {
                    ratings.add(Rating(currentBilateralLimbA.get(), currentBilateralAwardA.get()))
                    ratings.add(Rating(currentBilateralLimbB.get(), currentBilateralAwardB.get()))
                    finalRating.set(service.calculateFinalRating(ratings)) //recalculate after bilateral addition
                }
            }
        }
    }

    private fun isValidBilateral(): Boolean {
        if (currentBilateralLimbA.get() == null || currentBilateralLimbB.get() == null || currentBilateralAwardA.get() == null || currentBilateralAwardB.get() == null) {
            message.set("Please select a value for each of the 4 Bilateral boxes")
            return false
        } else if (currentBilateralLimbA.get() == Bilateral.LEFT_ARM && currentBilateralLimbB.get() != Bilateral.RIGHT_ARM) {
            message.set("RIGHT_ARM must accompany LEFT_ARM for a valid bilateral rating")
            return false
        } else if (currentBilateralLimbA.get() == Bilateral.RIGHT_ARM && currentBilateralLimbB.get() != Bilateral.LEFT_ARM) {
            message.set("LEFT_ARM must accompany RIGHT_ARM for a valid bilateral rating")
            return false
        } else if (currentBilateralLimbA.get() == Bilateral.LEFT_LEG && currentBilateralLimbB.get() != Bilateral.RIGHT_LEG) {
            message.set("RIGHT_LEG must accompany LEFT_LEG for a valid bilateral rating")
            return false
        } else if (currentBilateralLimbA.get() == Bilateral.RIGHT_LEG && currentBilateralLimbB.get() != Bilateral.LEFT_LEG) {
            message.set("LEFT_LEG must accompany RIGHT_LEG for a valid bilateral rating")
            return false
        }
        message.set("")
        return true
    }

    private fun createSaveReportButton(): Node {
        return Button("Save Report").also {
            it.setOnAction { evt ->
                val selectedFile = fileChooser.showSaveDialog(stage)
                service.saveReport(selectedFile)
            }
        }
    }

    private fun createClearAllButton(): Node {
        return Button("Clear All").also {
            it.setOnAction { evt ->
                ratings.clear()
            }
        }
    }

    private fun createSummaryTable(): Region {
        val table = TableView<Rating>()
        val columnWidth = DEFAULT_WIDTH / 2 - 1 //padded to prevent scroll bar from showing up
        table.items = ratings
        val bilateralColumn = TableColumn<Rating, String>("Bilateral")
        bilateralColumn.cellValueFactory = PropertyValueFactory("bilateral")
        bilateralColumn.prefWidth = columnWidth
        val awardColumn = TableColumn<Rating, String>("Award")
        awardColumn.cellValueFactory = PropertyValueFactory("awardPercentage")
        awardColumn.prefWidth = columnWidth

        table.columns.setAll(bilateralColumn, awardColumn)
        println(bilateralColumn.width)
        println(awardColumn.width)

        //this block gives ability to remove ratings from summary
        table.setOnKeyPressed { evt ->
            if (evt.code == KeyCode.BACK_SPACE && table.selectionModel.selectedItem != null) {
                ratings.remove(table.selectionModel.selectedItem)
                finalRating.set(service.calculateFinalRating(ratings)) //recalculate after removal
            }
        }

        return table
    }

    private fun createFinalRatingLabel(): Node {
        return Label("").also {
            it.textProperty().bind(finalRating.asString())
            it.styleClass.add("final-rating")
        }
        //TODO style this
    }
}