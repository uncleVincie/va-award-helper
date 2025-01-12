package com.vincie.view.javafx

import com.vincie.controller.CombinedRatingService
import com.vincie.model.AwardPercentage
import com.vincie.model.Bilateral
import com.vincie.model.Rating
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
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


    fun createScene() =
        Scene(
            createParentRegion(), 500.0, 800.0
        )

    private fun createBorderPane() = BorderPane().also {
        it.top = createTop()
        it.center = createCenter()
        it.bottom = createBottom()
    }

    private fun createParentRegion() = VBox(20.0, createTop(), createCenter(), createBottom())


    private fun createTop(): Region {

        return HBox(60.0, createNonBilateralBox(), createBilateralBox()).also { it.alignment = Pos.CENTER }
    }

    private fun createCenter(): Region {

        return VBox(1.0, Label("Summary"), createSummaryTable(), Label("Select row and hit Backspace to remove a rating")).also { it.alignment = Pos.CENTER}
    }

    private fun createBottom(): Region {

        return HBox(20.0, createSaveReportButton(), Label("Total: "), createFinalRatingLabel()).also { it.alignment = Pos.CENTER_RIGHT }
    }

    private fun createNonBilateralBox(): Region {

        return VBox(6.0, Label("Enter Non-Bilateral Award"), createPercentChoiceBox(currentNonBilateral), createAddButtonNonBilateral())
    }

    private fun createBilateralBox(): Region {
        return VBox(6.0, Label("Enter Bi-Lateral Award"), createSetOfBilateralChoiceBoxes(currentBilateralLimbA, currentBilateralAwardA), createSetOfBilateralChoiceBoxes(currentBilateralLimbB, currentBilateralAwardB), createAddButtonBilateral())
    }

    private fun createSetOfBilateralChoiceBoxes(limb: ObjectProperty<Bilateral>, award: ObjectProperty<AwardPercentage>): Region {
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
                //TODO need to check for nulls here or in the service
                ratings.add(Rating(Bilateral.NON_BILATERAL, currentNonBilateral.get()))
                finalRating.set(service.calculateFinalRating(ratings)) //recalculate after non-bilateral addition, auto-unboxing works here, no need to convert to List
            }
        }
    }

    private fun createAddButtonBilateral(): Node {
        return Button("+").also {
            it.setOnAction { evt ->
                //TODO need to call a validate method to make sure the bilateral limbs match
                //TODO need to check for nulls here or in the service
                ratings.add(Rating(currentBilateralLimbA.get(), currentBilateralAwardA.get()))
                ratings.add(Rating(currentBilateralLimbB.get(), currentBilateralAwardB.get()))
                finalRating.set(service.calculateFinalRating(ratings)) //recalculate after bilateral addition
            }
        }
    }

    private fun createSaveReportButton(): Node {
        return Button("Save Report").also {
            it.setOnAction { evt ->
                val selectedFile = fileChooser.showSaveDialog(stage)
                service.saveReport(selectedFile)
            }
        }
    }

    private fun createSummaryTable(): Region {
        val table = TableView<Rating>()
        table.items = ratings
        val bilateralColumn = TableColumn<Rating, String>("Bilateral")
        bilateralColumn.cellValueFactory = PropertyValueFactory("bilateral")
        val awardColumn = TableColumn<Rating, String>("Award")
        awardColumn.cellValueFactory = PropertyValueFactory("awardPercentage")

        table.columns.setAll(bilateralColumn,awardColumn)

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
        return Label("").also { it.textProperty().bind(finalRating.asString())}
        //TODO style this
    }
}