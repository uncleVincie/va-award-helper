package com.vincie.view;

import com.vincie.controller.CombinedRatingService;
import com.vincie.model.CombinedRatingsTable;
import com.vincie.view.javafx.VaAwardHelperScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {

        CombinedRatingService service = new CombinedRatingService(new CombinedRatingsTable());
        service.printReport();
        stage.setScene(new VaAwardHelperScene(service).createScene());
        stage.show();
    }

    public static void geterdone(String[] args) {
        launch();
    }

}
