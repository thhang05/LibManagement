package Handler;

import Controller.Account.RegisterController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class RegisterHandler {
    private final Stage ownerStage;

    public RegisterHandler(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void showRegisterDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/register.fxml"));
            AnchorPane registerPane = loader.load();

            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.initModality(Modality.WINDOW_MODAL);
            registerStage.initOwner(ownerStage);

            RegisterController registerController = loader.getController();
            registerController.setStage(registerStage);

            Scene scene = new Scene(registerPane);
            registerStage.setScene(scene);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            registerStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            registerPane.requestFocus();

            registerStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Failed to load the register dialog: " + e.getMessage());
        }
    }
}
