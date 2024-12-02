package Controller.Account;

import Handler.LoginHandler;
import Handler.RegisterHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {
    private LoginHandler loginHandler;
    private RegisterHandler registerHandler;
    private Stage stage;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button exitButton;

    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    public void setRegisterHandler(RegisterHandler registerHandler) {
        this.registerHandler = registerHandler;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> {
            event.consume();
            loginHandler.showLoginDialog();
        });
        registerButton.setOnAction(event -> {
            event.consume();
            registerHandler.showRegisterDialog();
        });
        exitButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });
    }
}