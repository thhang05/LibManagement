package Action.Account;

import Controller.Account.ResetPasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Account;

import java.io.IOException;

public class ResetPassword  {
    private final Stage ownerStage;
    private final Account account;

    public ResetPassword(Stage ownerStage, Account account) {
        this.ownerStage = ownerStage;
        this.account = account;
    }

    public void showResetPasswordForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/reset.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage resetPasswordStage = new Stage();
            resetPasswordStage.setScene(scene);
            resetPasswordStage.initStyle(StageStyle.UNDECORATED);
            resetPasswordStage.initModality(Modality.WINDOW_MODAL);
            resetPasswordStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            resetPasswordStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            ResetPasswordController resetPasswordController = loader.getController();
            resetPasswordController.setStage(resetPasswordStage);
            resetPasswordController.setAccount(account);

            rootPane.requestFocus();

            resetPasswordStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the reset.fxml file: " + e.getMessage());
        }
    }
}