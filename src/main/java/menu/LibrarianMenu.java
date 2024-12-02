package menu;

import Controller.Account.LibrarianMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Librarian;

import java.io.IOException;
import java.util.Objects;

public class LibrarianMenu {
    private final Stage ownerStage;
    private final Librarian librarian;

    public LibrarianMenu(Stage ownerStage, Librarian librarian) {
        this.ownerStage = ownerStage;
        this.librarian = librarian;
    }

    public void displayMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/librarian.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

            Stage librarianmenuStage = new Stage();
            librarianmenuStage.initStyle(StageStyle.UNDECORATED);
            librarianmenuStage.setScene(scene);

            LibrarianMenuController librarianMenuController = loader.getController();
            librarianMenuController.setStage(librarianmenuStage);
            librarianMenuController.setOwnerStage(ownerStage);
            librarianMenuController.setLibrarian(librarian);
            librarianMenuController.updateStatistics();

            librarianmenuStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load the librarian menu: " + e.getMessage());
        }
    }
}