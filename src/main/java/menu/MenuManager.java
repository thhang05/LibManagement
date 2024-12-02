package menu;

import Handler.LoginHandler;
import Controller.Account.MenuController;
import Handler.RegisterHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MenuManager {
    private final LoginHandler loginHandler;
    private final RegisterHandler registerHandler;

    public MenuManager(Stage primaryStage) {
        loginHandler = new LoginHandler(primaryStage);
        registerHandler = new RegisterHandler(primaryStage);
    }

    public void showMenu(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/menu.fxml"));
            AnchorPane root = loader.load();

            MenuController controller = loader.getController();
            controller.setLoginHandler(loginHandler);
            controller.setRegisterHandler(registerHandler);
            controller.setStage(stage);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load the menu: " + e.getMessage());
        }
    }
}