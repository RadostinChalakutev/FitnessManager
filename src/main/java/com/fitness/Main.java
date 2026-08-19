package com.fitness;

import com.fitness.database.DatabaseInitializer;
import com.fitness.database.SubscriptionInitializer;
import com.fitness.server.VerificationServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        DatabaseInitializer.initialize();
        SubscriptionInitializer.initialize();

        VerificationServer verificationServer =
                new VerificationServer();

        verificationServer.start();

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/fxml/main-view.fxml"
                        )
                );

        Scene scene =
                new Scene(
                        loader.load(),
                        1000,
                        650
                );

        stage.setTitle("Fitness Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}