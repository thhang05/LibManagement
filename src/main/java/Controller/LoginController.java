package Controller;

import Handler.LoginHandler;
import database.Database;
import Entity.AccountStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private Stage stage;
    private LoginHandler loginHandler;
    private final Database database = Database.getInstance();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField txtHidePassword;

    @FXML
    private TextField txtShowPassword;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label notification1;

    @FXML
    private Label notification2;

    @FXML
    private Label notification3;

    @FXML
    private Label notification4;

    @FXML
    private Button cancelButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    @FXML
    public void initialize() {
        txtShowPassword.setVisible(false);

        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });


        usernameField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
            notification3.setText("");
            notification4.setText("");
        });

        txtHidePassword.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
            notification3.setText("");
            notification4.setText("");
        });

        txtShowPassword.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
            notification3.setText("");
            notification4.setText("");
        });
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = showPasswordCheckBox.isSelected() ? txtShowPassword.getText().trim() : txtHidePassword.getText().trim();

        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");

        notification1.setText("");
        notification2.setText("");
        notification3.setText("");
        notification4.setText("");

        if (username.length() < 3) {
            usernameErrorLabel.setText("* Username must be at least 3 characters long");
            return;
        }
        if (password.length() < 6) {
            passwordErrorLabel.setText("* Password must be at least 6 characters long");
            return;
        }
        if (database.isUsernameExists(username) == null) {
            notification2.setText("* Username not found");
            return;
        }
        if (!database.isUsernameExists(username).equals(password)) {
            notification3.setText("* Incorrect password");
            return;
        }
        if (database.getAccountStatus(username).equals(AccountStatus.Pending)) {
            notification1.setText("* Your account is pending approval");
            return;
        }

        if (database.getAccountStatus(username).equals(AccountStatus.Locked)) {
            notification4.setText("* Your account has been locked");
            return;
        }

        loginHandler.handleLogin(username, password);
    }

    @FXML
    public void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            txtShowPassword.setText(txtHidePassword.getText());
            txtShowPassword.setVisible(true);
            txtHidePassword.setVisible(false);
        } else {
            txtHidePassword.setText(txtShowPassword.getText());
            txtHidePassword.setVisible(true);
            txtShowPassword.setVisible(false);
        }
    }
}