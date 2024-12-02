package Action.Book;

import Controller.Book.AddBookController;
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

public class AddBook {
    private final Stage ownerStage;
    private final Librarian librarian;
    private final Label totalBooksLabel;

    public AddBook(Stage ownerStage, Librarian librarian, Label totalBooksLabel) {
        this.ownerStage = ownerStage;
        this.librarian = librarian;
        this.totalBooksLabel = totalBooksLabel;
    }

    public void showAddBookForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/add.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage addBookStage = new Stage();
            addBookStage.setScene(scene);
            addBookStage.initStyle(StageStyle.UNDECORATED);
            addBookStage.initModality(Modality.WINDOW_MODAL);
            addBookStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            addBookStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            AddBookController addBookController = loader.getController();
            addBookController.setStage(addBookStage);
            addBookController.setLibrarian(librarian);
            addBookController.setTotalBooksLabel(totalBooksLabel);

            rootPane.requestFocus();

            addBookStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the add.fxml file: " + e.getMessage());
        }
    }
}