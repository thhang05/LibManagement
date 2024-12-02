package Controller;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import Entity.BookLending;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportController {
    private Stage stage;
    private final int itemsPerPage = 6;
    private final ObservableList<BookLending> bookLendings = FXCollections.observableArrayList();

    @FXML
    private TableView<BookLending> bookLendingTable;

    @FXML
    private TableColumn<BookLending, String> usernameColumn;

    @FXML
    private TableColumn<BookLending, String> barcodeColumn;

    @FXML
    private TableColumn<BookLending, LocalDate> borrowDateColumn;

    @FXML
    private TableColumn<BookLending, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<BookLending, LocalDate> returnedDateColumn;

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
        bookLendingTable.setPlaceholder(noDataLabel);

        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().MemberIdProperty());
        barcodeColumn.setCellValueFactory(cellData -> cellData.getValue().ItemIdProperty());
        borrowDateColumn.setCellValueFactory(cellData -> cellData.getValue().creationDateProperty());
        dueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());
        returnedDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateProperty());

        bookLendings.clear();
        Database.getInstance().getAllBookLendingFromDatabase(bookLendings);

        updatePagination();

        showBooksPage(0);

        pagination.setPageFactory(pageIndex -> {
            showBooksPage(pageIndex);
            return new AnchorPane();
        });

        bookLendingTable.setRowFactory(_ -> {
            TableRow<BookLending> row = new TableRow<>();
            row.setPrefHeight(30);
            return row;
        });
    }

    private void updatePagination() {
        if (bookLendings.isEmpty()) {
            pagination.setVisible(false);
        } else {
            pagination.setVisible(true);
            int totalPages = (int) Math.ceil((double) bookLendings.size() / itemsPerPage);
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(0);
        }
    }

    private void showBooksPage(int pageIndex) {
        if (bookLendings.isEmpty()) {
            bookLendingTable.setItems(FXCollections.observableArrayList(bookLendings));
            Label noDataLabel = new Label("");
            bookLendingTable.setPlaceholder(noDataLabel);
            return;
        }

        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, bookLendings.size());

        List<BookLending> pageBooks = new ArrayList<>(bookLendings.subList(fromIndex, toIndex));
        bookLendingTable.setItems(FXCollections.observableArrayList(pageBooks));
    }
}
