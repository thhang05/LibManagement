package Action.Book;

import Controller.Book.EditBookController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Librarian;

import java.io.IOException;

public class EditBook {
    private final Stage ownerStage;
    private final Librarian librarian;

    public EditBook(Stage ownerStage, Librarian librarian) {
        this.ownerStage = ownerStage;
        this.librarian = librarian;
    }

    public void showEditBookForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/edit.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage editBookStage = new Stage();
            editBookStage.setScene(scene);
            editBookStage.initStyle(StageStyle.UNDECORATED);
            editBookStage.initModality(Modality.WINDOW_MODAL);
            editBookStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            editBookStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            EditBookController editBookController = loader.getController();
            editBookController.setStage(editBookStage);
            editBookController.setLibrarian(librarian);

            rootPane.requestFocus();

            editBookStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the edit.fxml file: " + e.getMessage());
        }
    }
}