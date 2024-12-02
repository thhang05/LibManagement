package Action.Account;

import Controller.Account.InfoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Account;

import java.io.IOException;

public class Info {
    private final Stage ownerStage;
    private final Account account;
    private final Label usernameLabel;

    public Info(Stage ownerStage, Account account, Label usernameLabel) {
        this.ownerStage = ownerStage;
        this.account = account;
        this.usernameLabel = usernameLabel;
    }

    public void showInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/info.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage infoStage = new Stage();
            infoStage.setScene(scene);
            infoStage.initStyle(StageStyle.UNDECORATED);
            infoStage.initModality(Modality.WINDOW_MODAL);
            infoStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            infoStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            InfoController infoController = loader.getController();
            infoController.setStage(infoStage);
            infoController.setAccount(account);
            infoController.setUsernameLabel(usernameLabel);
            infoController.updateInfo();

            rootPane.requestFocus();

            infoStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the info.fxml file: " + e.getMessage());
        }
    }
}