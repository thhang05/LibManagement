package UI;

import database.Database;
import Status.TypeOfSearch;

import java.io.IOException;
import java.util.List;

import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Book;
import Entities.Member;

public class Search {
    private static Search searchInstance;

    private final Stage mainStage;

    private SearchController searchController;
    private static Stage resultForSearchStage;

    private Search(Stage mainStage) {
        this.mainStage = mainStage;
        initResultForSearch();
    }

    public static Search getInstance(Stage mainStage, Member member) {
        if (searchInstance == null) {
            searchInstance = new Search(mainStage);
        }
        return searchInstance;
    }

    private void initResultForSearch() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/member/search.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        searchController = loader.getController();

        Scene scene = new Scene(searchController.getResultForSearchPane());
        resultForSearchStage = new Stage();
        resultForSearchStage.setScene(scene);
        resultForSearchStage.initStyle(StageStyle.UNDECORATED);
        resultForSearchStage.initModality(Modality.WINDOW_MODAL);
        resultForSearchStage.initOwner(mainStage);

        resultForSearchStage.setOnHidden(event -> {
            event.consume();
            mainStage.getScene().getRoot().setEffect(null);
            searchController.resetSearchTextField();
            ((TextField) mainStage.getScene().getRoot().lookup("#searchTextField")).clear();
        });
    }

    public void showResultForSearchStage(String searchInput, String filter) {
        mainStage.getScene().getRoot().setEffect(new GaussianBlur(5));
        searchController.setSearchResultLabel(searchInput);
        List<Book> bookList = Database.getInstance().searchBookMember(searchInput,
                TypeOfSearch.valueOf(filter));
        searchController.showBookList(bookList);
        if (!resultForSearchStage.isShowing()) {
            resultForSearchStage.showAndWait();
        }
    }

    public static Stage getResultForSearchStage() {
        return resultForSearchStage;
    }
}