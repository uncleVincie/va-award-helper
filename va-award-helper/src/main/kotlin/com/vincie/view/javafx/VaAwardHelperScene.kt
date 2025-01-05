package com.vincie.view.javafx

import com.vincie.controller.CombinedRatingService
import com.vincie.model.AwardPercentage
import com.vincie.model.Bilateral
import com.vincie.model.Rating
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*

class VaAwardHelperScene(
    private val service: CombinedRatingService
) {

    //state for the ui
    private val ratings = FXCollections.observableArrayList<Rating>()
    private val finalRating: IntegerProperty = SimpleIntegerProperty()

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

        return VBox(1.0, Label("Summary"), createSummaryTable()).also { it.alignment = Pos.CENTER}
    }

    private fun createBottom(): Region {

        return HBox(20.0, Label("Total: "), createFinalRatingLabel()).also { it.alignment = Pos.CENTER_RIGHT }
    }

    private fun createNonBilateralBox(): Region {

        return VBox(6.0, Label("Enter Non-Bilateral Award"), createPercentChoiceBox(), createAddButtonNonBilateral())
    }

    private fun createBilateralBox(): Region {
        return VBox(6.0, Label("Enter Bi-Lateral Award"), createSetOfBilateralChoiceBoxes(), createSetOfBilateralChoiceBoxes(), createAddButtonNonBilateral())
    }

    private fun createSetOfBilateralChoiceBoxes(): Region {
        return HBox(10.0, createLimbChoiceBox(), createPercentChoiceBox())
    }

    private fun createPercentChoiceBox(): Node {
        val choiceBox = ChoiceBox<AwardPercentage>()
        choiceBox.items.addAll(AwardPercentage.entries)
        return choiceBox
    }

    private fun createLimbChoiceBox(): Node {
        val choiceBox = ChoiceBox<Bilateral>()
        choiceBox.items.add(Bilateral.LEFT_ARM)
        choiceBox.items.add(Bilateral.RIGHT_ARM)
        choiceBox.items.add(Bilateral.LEFT_LEG)
        choiceBox.items.add(Bilateral.RIGHT_LEG)
        return choiceBox
    }

    private fun createAddButtonNonBilateral(): Node {
        return Button("+").also {
            it.setOnAction { evt ->
                ratings.add(Rating(Bilateral.NON_BILATERAL, AwardPercentage.TEN)) //TODO this is hard-coded, need state for the dropdowns
                finalRating.set(service.calculateFinalRating(ratings))
            }
        }
    }

    private fun createSummaryTable(): Region {
        //TODO
        val table = TableView<Rating>()
        table.items = ratings
        val bilateralColumn = TableColumn<Rating, String>("Bilateral")
        bilateralColumn.cellValueFactory = PropertyValueFactory("bilateral")
        val awardColumn = TableColumn<Rating, String>("Award")
        awardColumn.cellValueFactory = PropertyValueFactory("awardPercentage")

        table.columns.setAll(bilateralColumn,awardColumn)
        return table
    }

    private fun createFinalRatingLabel(): Node {
        return Label("").also { it.textProperty().bind(finalRating.asString())}
        //TODO style this
    }
}