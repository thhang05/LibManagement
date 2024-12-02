package Controller.Book;

import database.Database;
import Status.AccountStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Librarian;
import Entities.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageAccountController {
    private Stage stage;
    private Librarian librarian;
    private Label totalMembersLabel;
    private final int itemsPerPage = 7;
    private final ObservableList<Member> userList = FXCollections.observableArrayList();

    @FXML
    private TableView<Member> userTable;

    @FXML
    private TableColumn<Member, String> usernameColumn;

    @FXML
    private TableColumn<Member, String> accountStatusColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private Button cancelButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLibrarian(Librarian librarian) {this.librarian = librarian;}

    public void setTotalMembersLabel(Label totalMembersLabel) {
        this.totalMembersLabel = totalMembersLabel;
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });

        Label noDataLabel = new Label("");
        userTable.setPlaceholder(noDataLabel);

        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        accountStatusColumn.setCellValueFactory(cellData-> cellData.getValue().accountStatusProperty().asString());

        userList.clear();
        Database.getInstance().getAllAccountFromDatabase(userList);

        updatePagination();

        showUsersPage(0);

        pagination.setPageFactory(pageIndex -> {
            showUsersPage(pageIndex);
            return new AnchorPane();
        });

        userTable.setRowFactory(_ -> {
            TableRow<Member> row = new TableRow<>();
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
                    Member clickedUser = row.getItem();
                    openUserDetailsWindow(clickedUser);
                }
            });
            return row;
        });
    }

    private void updatePagination() {
        if (userList.isEmpty()) {
            pagination.setVisible(false);
        } else {
            pagination.setVisible(true);
            int totalPages = (int) Math.ceil((double) userList.size() / itemsPerPage);
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(0);
        }
    }

    private void showUsersPage(int pageIndex) {
        if (userList.isEmpty()) {
            userTable.setItems(FXCollections.observableArrayList(userList));
            Label noDataLabel = new Label("");
            userTable.setPlaceholder(noDataLabel);
            return;
        }

        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, userList.size());

        List<Member> pageUsers = new ArrayList<>(userList.subList(fromIndex, toIndex));
        userTable.setItems(FXCollections.observableArrayList(pageUsers));
    }

    private void openUserDetailsWindow(Member user) {
        Stage detailsStage = new Stage();

        Label titleLabel = new Label("User Details");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-padding: 10 0 10 0;");

        Label statusLabel = new Label("Account Status: " + Database.getInstance().getAccountStatus(user.getUsername()));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label userDetails = new Label(user.detail());
        userDetails.setFont(Font.font("System", FontWeight.BOLD, 14));

        StackPane notificationPane = new StackPane();
        notificationPane.setMinHeight(50);

        Label verifySuccessLabel = new Label("Verification successful!");
        verifySuccessLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        verifySuccessLabel.setVisible(false);

        Label verifyFailLabel = new Label("Verification failed!");
        verifyFailLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        verifyFailLabel.setVisible(false);

        Label lockSuccessLabel = new Label("Account locked successfully!");
        lockSuccessLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        lockSuccessLabel.setVisible(false);

        Label lockFailLabel = new Label("Failed to lock account!");
        lockFailLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        lockFailLabel.setVisible(false);

        notificationPane.getChildren().addAll(verifySuccessLabel, verifyFailLabel, lockSuccessLabel, lockFailLabel);

        Button verifyButton = new Button("Verify");
        verifyButton.getStyleClass().add("button-login-register");
        verifyButton.setOnAction(event-> {
            event.consume();
            verifyUser(user, user.getUsername(), statusLabel, verifySuccessLabel, verifyFailLabel,
                    lockSuccessLabel, lockFailLabel);
        });

        Button lockButton = new Button("Lock");
        lockButton.getStyleClass().add("button-cancel");
        lockButton.setOnAction(event -> {
            event.consume();
            lockUser(user, user.getUsername(), statusLabel, lockSuccessLabel, lockFailLabel,
                    verifySuccessLabel, verifyFailLabel);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button-cancel");
        cancelButton.setOnAction(event -> {
            event.consume();
            detailsStage.close();
        });

        HBox buttonContainer = new HBox(verifyButton, lockButton, cancelButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(20);

        VBox contentContainer = new VBox(titleLabel, statusLabel, userDetails, notificationPane, buttonContainer);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setSpacing(10);

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

    private void verifyUser(Member user, String username, Label statusLabel, Label verifySuccessLabel, Label verifyFailLabel,
                            Label lockSuccessLabel, Label lockFailLabel) {
        boolean success = librarian.verifyUserAccount(username);

        if (success) {
            verifySuccessLabel.setVisible(true);
            verifyFailLabel.setVisible(false);
            lockSuccessLabel.setVisible(false);
            lockFailLabel.setVisible(false);

            user.setStatus(AccountStatus.Active);

            statusLabel.setText("Account Status: Active");

            userTable.refresh();
            totalMembersLabel.setText(librarian.getTotalMembers());
        } else {
            verifyFailLabel.setVisible(true);
            verifySuccessLabel.setVisible(false);
            lockSuccessLabel.setVisible(false);
            lockFailLabel.setVisible(false);
        }
    }

    private void lockUser(Member user, String username, Label statusLabel, Label lockSuccessLabel, Label lockFailLabel,
                          Label verifySuccessLabel, Label verifyFailLabel) {
        boolean success = librarian.lockUserAccount(username);

        if (success) {
            lockSuccessLabel.setVisible(true);
            lockFailLabel.setVisible(false);
            verifySuccessLabel.setVisible(false);
            verifyFailLabel.setVisible(false);

            user.setStatus(AccountStatus.Locked);

            statusLabel.setText("Account Status: Locked");

            userTable.refresh();
            totalMembersLabel.setText(librarian.getTotalMembers());
        } else {
            lockFailLabel.setVisible(true);
            lockSuccessLabel.setVisible(false);
            verifySuccessLabel.setVisible(false);
            verifyFailLabel.setVisible(false);
        }
    }
}