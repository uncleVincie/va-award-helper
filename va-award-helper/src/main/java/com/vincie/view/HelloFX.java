package com.vincie.view;

import com.vincie.controller.CombinedRatingService;
import com.vincie.model.CombinedRatingsTable;
import com.vincie.view.javafx.VaAwardHelperScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {

        CombinedRatingService service = new CombinedRatingService(new CombinedRatingsTable());
        stage.setScene(new VaAwardHelperScene(service, stage).createScene());
        stage.show();
    }

    public static void geterdone(String[] args) {
        launch();
    }

}
