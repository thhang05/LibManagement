package Controller;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.*;
import models.Member;

import java.util.ArrayList;
import java.util.List;

public class FinesController {
    private Stage stage;
    private final int itemsPerPage = 6;
    private final ObservableList<Member> members = FXCollections.observableArrayList();

    @FXML
    private TableView<Member> finesTable;

    @FXML
    private TableColumn<Member, String> usernameColumn;

    @FXML
    private TableColumn<Member, Integer> overdueBooksColumn;

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
        finesTable.setPlaceholder(noDataLabel);

        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        overdueBooksColumn.setCellValueFactory(cellData -> cellData.getValue().overdueBooksProperty().asObject());

        members.clear();
        Database.getInstance().getOverdueBookCountByAccount(members);

        updatePagination();

        showBooksPage(0);

        pagination.setPageFactory(pageIndex -> {
            showBooksPage(pageIndex);
            return new AnchorPane();
        });

        finesTable.setRowFactory(_ -> {
            TableRow<Member> row = new TableRow<>();
            row.setPrefHeight(30);
            return row;
        });
    }

    private void updatePagination() {
        if (members.isEmpty()) {
            pagination.setVisible(false);
        } else {
            pagination.setVisible(true);
            int totalPages = (int) Math.ceil((double) members.size() / itemsPerPage);
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(0);
        }
    }

    private void showBooksPage(int pageIndex) {
        if (members.isEmpty()) {
            finesTable.setItems(FXCollections.observableArrayList(members));
            Label noDataLabel = new Label("");
            finesTable.setPlaceholder(noDataLabel);
            return;
        }

        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, members.size());

        List<Member> pageBooks = new ArrayList<>(members.subList(fromIndex, toIndex));
        finesTable.setItems(FXCollections.observableArrayList(pageBooks));
    }
}