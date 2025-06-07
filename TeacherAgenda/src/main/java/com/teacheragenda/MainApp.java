package com.teacheragenda;

import com.teacheragenda.config.LocaleManager; // Added
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.ResourceBundle; // Added
// import java.util.Objects; // Was present but not used, can be removed if Objects class is not used.

@SpringBootApplication
public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;
    private LocaleManager localeManager; // Added

    public static void main(String[] args) {
        // Ensure LocaleManager is initialized early if needed by Spring context,
        // though for this setup, it's primarily for JavaFX UI.
        // LocaleManager.getInstance(); // Optional: Initialize early if needed elsewhere
        launch(args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(MainApp.class);

        localeManager = LocaleManager.getInstance(); // Get instance
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
            "com.teacheragenda.i18n.messages", // Base name
            localeManager.getCurrentLocale()   // Get currently loaded/preferred locale
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/teacheragenda/view/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        fxmlLoader.setResources(resourceBundle); // Set the resource bundle for FXML
        rootNode = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (localeManager == null) { // Should be initialized in init()
            localeManager = LocaleManager.getInstance();
        }
        primaryStage.setTitle(localeManager.getString("app.title")); // Use localized title
        // Updated scene size to match FXML
        primaryStage.setScene(new Scene(rootNode, 1200, 800));
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        // Optional: any other cleanup, like saving preferences if not done elsewhere
        // localeManager.savePreferredLocale(localeManager.getCurrentLocale()); // Ensure prefs are saved if changed dynamically
    }
}
