package Controller;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import models.Librarian;
import service.*;

public class LibrarianMenuController {
    private Stage stage;
    private Stage ownerStage;
    private Librarian librarian;
    private ContextMenu avatarContextMenu;

    @FXML
    private Button avatarButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label totalMembersLabel;

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label borrowedBooksLabel;

    @FXML
    private Label overdueBooksLabel;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOwnerStage(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
        displayFirstName();
    }

    private void displayFirstName() {
        if (librarian != null) {
            String username = librarian.getPerson().getName();
            String[] nameParts = username.split(" ");
            String firstName = nameParts[nameParts.length - 1];
            usernameLabel.setText(firstName);
        }
    }

    @FXML
    public void initialize() {
        avatarContextMenu = new ContextMenu();

        MenuItem accountInfoItem = new MenuItem("   Profile ");
        accountInfoItem.setOnAction(event -> {
            event.consume();
            showAccountInfo();
        });

        MenuItem resetItem = new MenuItem("   Reset  ");
        resetItem.setOnAction(event -> {
            event.consume();
            resetPassword();
        });

        MenuItem logoutItem = new MenuItem("  Log out");
        logoutItem.setOnAction(event -> {
            event.consume();
            handleLogout();
        });

        accountInfoItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-family: Arial;");
        resetItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-family: Arial;");
        logoutItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-font-family: Arial;");

        avatarContextMenu.getItems().addAll(accountInfoItem, resetItem, logoutItem);

        avatarButton.setContextMenu(avatarContextMenu);
    }

    @FXML
    public void handleAvatarClick() {
        if (avatarContextMenu.isShowing()) {
            avatarContextMenu.hide();
        } else {
            avatarContextMenu.show(avatarButton, Side.BOTTOM, -16, 0);
        }
    }

    public void updateStatistics() {
        totalMembersLabel.setText(librarian.getTotalMembers());
        totalBooksLabel.setText(librarian.getTotalBooks());
        borrowedBooksLabel.setText(librarian.getBorrowedBooks());
        overdueBooksLabel.setText(librarian.getOverdueBooks());
    }

    private void showAccountInfo() {
        Info info = new Info(stage, librarian, usernameLabel);
        info.showInfo();
    }

    private void resetPassword() {
        ResetPassword resetPassword = new ResetPassword(stage, librarian);
        resetPassword.showResetPasswordForm();
    }

    private void handleLogout() {
        if (ownerStage != null) {
            ownerStage.show();
        }
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    public void handleAddBook() {
        AddBook addBook = new AddBook(stage, librarian, totalBooksLabel);
        addBook.showAddBookForm();
    }

    @FXML
    public void handleEditBook() {
        EditBook editBook = new EditBook(stage, librarian);
        editBook.showEditBookForm();
    }

    @FXML
    public void handleDeleteBook() {
        DeleteBook deleteBook = new DeleteBook(stage, librarian, totalBooksLabel);
        deleteBook.showDeleteBookForm();
    }

    @FXML
    public void handleViewBooks() {
        ViewBook viewBook = new ViewBook(stage);
        viewBook.showBooks();
    }

    @FXML
    public void handleManageAccounts() {
        ManageAccount manageAccount = new ManageAccount(stage, librarian, totalMembersLabel);
        manageAccount.showManageAccountForm();
    }

    @FXML
    public void handleSearch() {
        Search search = new Search(stage, librarian, totalBooksLabel);
        search.showSearchForm();
    }

    @FXML
    public void handleReports() {
        Report report = new Report(stage);
        report.showReportForm();
    }

    @FXML
    public void handleFines() {
        Fines fines = new Fines(stage);
        fines.showFinesForm();
    }
}