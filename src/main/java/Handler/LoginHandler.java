package Handler;

import Controller.LoginController;
import database.Database;
import Entity.AccountStatus;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import menu.LibrarianMenu;
import models.Librarian;
import userInterface.SceneUtils;
import java.io.IOException;

import models.Member;

public class LoginHandler {
    private final Stage ownerStage;

    public LoginHandler(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void showLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/login.fxml"));
            AnchorPane loginPane = loader.load();

            Scene scene = new Scene(loginPane);

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.initModality(Modality.WINDOW_MODAL);
            loginStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            loginStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            LoginController loginController = loader.getController();
            loginController.setStage(loginStage);
            loginController.setLoginHandler(this);

            loginPane.requestFocus();

            loginStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Failed to load the login dialog: " + e.getMessage());
        }
    }

    public void handleLogin(String username, String password) {
        if (Database.getInstance().getAccountStatus(username).equals(AccountStatus.Admin)) {
            Librarian librarian = new Librarian(username, password);
            LibrarianMenu librarianMenu = new LibrarianMenu(ownerStage, librarian);
            librarianMenu.displayMenu();
        } else {
            Member member = new Member(username, password);
            SceneUtils.setupStage(ownerStage, member);
        }
        ownerStage.close();
    }
}