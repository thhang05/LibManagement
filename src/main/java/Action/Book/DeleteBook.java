package Action.Book;

import Controller.Book.DeleteBookController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Librarian;

import java.io.IOException;

public class DeleteBook {
    private final Stage ownerStage;
    private final Librarian librarian;
    private final Label totalBooksLabel;

    public DeleteBook(Stage ownerStage, Librarian librarian, Label totalBooksLabel) {
        this.ownerStage = ownerStage;
        this.librarian = librarian;
        this.totalBooksLabel = totalBooksLabel;
    }

    public void showDeleteBookForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/delete.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage deleteBookStage = new Stage();
            deleteBookStage.setScene(scene);
            deleteBookStage.initStyle(StageStyle.UNDECORATED);
            deleteBookStage.initModality(Modality.WINDOW_MODAL);
            deleteBookStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            deleteBookStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            DeleteBookController deleteBookController = loader.getController();
            deleteBookController.setStage(deleteBookStage);
            deleteBookController.setLibrarian(librarian);
            deleteBookController.setTotalBooksLabel(totalBooksLabel);

            rootPane.requestFocus();

            deleteBookStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the delete.fxml file: " + e.getMessage());
        }
    }
}
