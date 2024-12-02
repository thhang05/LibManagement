package Controller.Book;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.BookItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewBookController {
    private Stage stage;
    private final int itemsPerPage = 6;
    private final ObservableList<BookItem> bookList = FXCollections.observableArrayList();

    @FXML
    private TableView<BookItem> bookTable;

    @FXML
    private TableColumn<BookItem, String> titleColumn;

    @FXML
    private TableColumn<BookItem, String> authorColumn;

    @FXML
    private TableColumn<BookItem, String> subjectColumn;

    @FXML
    private TableColumn<BookItem, String> publisherColumn;

    @FXML
    private TableColumn<BookItem, String> languageColumn;

    @FXML
    private TableColumn<BookItem, Double> priceColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private Button cancelButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });

        Label noDataLabel = new Label("");
        bookTable.setPlaceholder(noDataLabel);

        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorNameProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisherProperty());
        languageColumn.setCellValueFactory(cellData -> cellData.getValue().languageProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        bookList.clear();
        Database.getInstance().getAllBookFromDatabase(bookList);

        updatePagination();

        showBooksPage(0);

        pagination.setPageFactory(pageIndex -> {
            showBooksPage(pageIndex);
            return new AnchorPane();
        });

        bookTable.setRowFactory(_ -> {
            TableRow<BookItem> row = new TableRow<>();
            row.setPrefHeight(30);

            row.setOnMouseEntered(event -> {
                event.consume();
                row.setStyle("-fx-cursor: hand;");
            });

            row.setOnMouseExited(event -> {
                event.consume();
                row.setStyle("-fx-cursor: default;");
            });

            row.setOnMouseClicked(event -> {
                event.consume();
                if (!row.isEmpty()) {
                    BookItem clickedBook = row.getItem();
                    openBookDetailsWindow(clickedBook);
                }
            });
            return row;
        });
    }

    private void updatePagination() {
        if (bookList.isEmpty()) {
            pagination.setVisible(false);
        } else {
            pagination.setVisible(true);
            int totalPages = (int) Math.ceil((double) bookList.size() / itemsPerPage);
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(0);
        }
    }

    private void showBooksPage(int pageIndex) {
        if (bookList.isEmpty()) {
            bookTable.setItems(FXCollections.observableArrayList(bookList));
            Label noDataLabel = new Label("");
            bookTable.setPlaceholder(noDataLabel);
            return;
        }

        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, bookList.size());

        List<BookItem> pageBooks = new ArrayList<>(bookList.subList(fromIndex, toIndex));
        bookTable.setItems(FXCollections.observableArrayList(pageBooks));
    }

    private void openBookDetailsWindow(BookItem book) {
        Stage detailsStage = new Stage();

        Label titleLabel = new Label("Book Details");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-padding: 10 0 10 0;");

        Label bookDetails = new Label(book.detail());
        bookDetails.setFont(Font.font("System", FontWeight.BOLD, 14));

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button-cancel");
        cancelButton.setOnAction(event -> {
            event.consume();
            detailsStage.close();
        });

        HBox buttonContainer = new HBox(cancelButton);
        buttonContainer.setAlignment(Pos.CENTER);

        VBox contentContainer = new VBox(titleLabel, bookDetails, buttonContainer);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setSpacing(20);

        AnchorPane pane = new AnchorPane(contentContainer);
        AnchorPane.setTopAnchor(contentContainer, 10.0);
        AnchorPane.setLeftAnchor(contentContainer, 10.0);
        AnchorPane.setRightAnchor(contentContainer, 10.0);
        AnchorPane.setBottomAnchor(contentContainer, 10.0);

        Scene scene = new Scene(pane, 600, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        detailsStage.setScene(scene);
        detailsStage.initStyle(StageStyle.UNDECORATED);
        detailsStage.initModality(Modality.WINDOW_MODAL);
        detailsStage.initOwner(stage);

        stage.getScene().getRoot().setEffect(new GaussianBlur(10));

        detailsStage.setOnHidden(event -> {
            event.consume();
            stage.getScene().getRoot().setEffect(null);
        });

        detailsStage.showAndWait();
    }
}