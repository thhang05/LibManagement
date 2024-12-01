package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Account;

public class ResetPasswordController {
    private Stage stage;
    private Account account;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private Label notification1;

    @FXML
    private Label notification2;

    @FXML
    private Button cancelButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });

        passwordField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        newPasswordField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });
    }

    @FXML
    public void handleReset() {
        String password = passwordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String currentPassword = account.getPassword().trim();

        notification1.setText("");
        notification2.setText("");

        if (!password.equals(currentPassword)) {
            notification2.setText("Incorrect current password");
            return;
        }

        if (newPassword.length() < 6) {
            notification2.setText("Invalid new password. At least 6 characters long");
            return;
        }

        if (account.resetPassword(newPassword)) {
            notification1.setText("Password reset successful");
            account.setPassword(newPassword);
        } else {
            notification2.setText("Password reset failed");
        }
    }
}