package Action.Book;

import Controller.Book.ManageAccountController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Librarian;

import java.io.IOException;

public class ManageAccount {
    private final Stage ownerStage;
    private final Librarian librarian;
    private final Label totalMembersLabel;

    public ManageAccount(Stage ownerStage, Librarian librarian, Label totalMembersLabel) {
        this.ownerStage = ownerStage;
        this.librarian = librarian;
        this.totalMembersLabel = totalMembersLabel;
    }

    public void showManageAccountForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/manage.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage manageAccountStage = new Stage();
            manageAccountStage.setScene(scene);
            manageAccountStage.initStyle(StageStyle.UNDECORATED);
            manageAccountStage.initModality(Modality.WINDOW_MODAL);
            manageAccountStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            manageAccountStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            ManageAccountController manageAccountController = loader.getController();
            manageAccountController.setStage(manageAccountStage);
            manageAccountController.setLibrarian(librarian);
            manageAccountController.setTotalMembersLabel(totalMembersLabel);

            rootPane.requestFocus();

            manageAccountStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the manage.fxml file: " + e.getMessage());
        }
    }
}
