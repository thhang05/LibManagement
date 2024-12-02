package Action.Book;

import Controller.Book.ViewBookController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ViewBook {
    private final Stage ownerStage;

    public ViewBook(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void showBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/view.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage viewBookStage = new Stage();
            viewBookStage.setScene(scene);
            viewBookStage.initStyle(StageStyle.UNDECORATED);
            viewBookStage.initModality(Modality.WINDOW_MODAL);
            viewBookStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            viewBookStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            ViewBookController viewBookController = loader.getController();
            viewBookController.setStage(viewBookStage);

            rootPane.requestFocus();

            viewBookStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the view.fxml file: " + e.getMessage());
        }
    }
}
