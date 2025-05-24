package com.teacheragenda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(MainApp.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/teacheragenda/view/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        rootNode = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Teacher Agenda");
        primaryStage.setScene(new Scene(rootNode, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}
