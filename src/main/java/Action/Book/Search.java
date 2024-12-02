package Action.Book;

import Controller.Book.SearchController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Librarian;

import java.io.IOException;

public class Search {
    private final Stage ownerStage;
    private final Librarian librarian;
    private final Label totalBooksLabel;

    public Search(Stage ownerStage, Librarian librarian, Label totalBooksLabel) {
        this.ownerStage = ownerStage;
        this.librarian = librarian;
        this.totalBooksLabel = totalBooksLabel;
    }

    public void showSearchForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/search.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage searchStage = new Stage();
            searchStage.setScene(scene);
            searchStage.initStyle(StageStyle.UNDECORATED);
            searchStage.initModality(Modality.WINDOW_MODAL);
            searchStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            searchStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            SearchController searchController = loader.getController();
            searchController.setStage(searchStage);
            searchController.setLibrarian(librarian);
            searchController.setTotalBooksLabel(totalBooksLabel);

            rootPane.requestFocus();

            searchStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the search.fxml file: " + e.getMessage());
        }
    }
}
