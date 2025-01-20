package com.vincie.view;

import com.vincie.controller.CombinedRatingService;
import com.vincie.model.CombinedRatingsTable;
import com.vincie.view.javafx.VaAwardHelperScene;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {

        CombinedRatingService service = new CombinedRatingService(new CombinedRatingsTable());
        stage.setScene(new VaAwardHelperScene(service, stage).createScene());
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-24.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-36.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-48.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-60.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-72.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-84.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-96.png")));
        stage.getIcons().add(new Image(HelloFX.class.getResourceAsStream("/typescript-logo-108.png")));
        stage.show();
    }

    public static void geterdone(String[] args) {
        launch();
    }

}
