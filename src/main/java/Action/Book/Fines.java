package Action.Book;

import Controller.Book.FinesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Fines {
    private final Stage ownerStage;

    public Fines(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void showFinesForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/fines.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage finesStage = new Stage();
            finesStage.setScene(scene);
            finesStage.initStyle(StageStyle.UNDECORATED);
            finesStage.initModality(Modality.WINDOW_MODAL);
            finesStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            finesStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            FinesController finesController = loader.getController();
            finesController.setStage(finesStage);

            rootPane.requestFocus();

            finesStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the fines.fxml file: " + e.getMessage());
        }
    }
}
