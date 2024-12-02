package Controller.Book;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Entities.BookItem;
import Entities.Librarian;

public class DeleteBookController {
    private Stage stage;
    private Librarian librarian;
    private Label totalBooksLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Label searchErrorLabel;

    @FXML
    private Label notification1;

    @FXML
    private Label isbnLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label numberOfPagesLabel;

    @FXML
    private Label authorNameLabel;

    @FXML
    private Label barcodeLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label bookFormatLabel;

    @FXML
    private Label bookStatusLabel;

    @FXML
    private Label publicationDateLabel;

    @FXML
    private Label notification2;

    @FXML
    private Label notification3;

    @FXML
    private Button cancelButton;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public void setTotalBooksLabel(Label totalBooksLabel) {
        this.totalBooksLabel = totalBooksLabel;
    }

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });

        notification1.setText("* No Books Found");
        notification2.setText("Delete Book SuccessFully");
        notification3.setText("Fail To Delete Book");
        searchErrorLabel.setText("* Please enter a search term before searching");

        notification1.setVisible(false);
        notification2.setVisible(false);
        notification3.setVisible(false);
        searchErrorLabel.setVisible(false);

        isbnLabel.setText("");
        titleLabel.setText("");
        subjectLabel.setText("");
        publisherLabel.setText("");
        languageLabel.setText("");
        numberOfPagesLabel.setText("");
        authorNameLabel.setText("");
        barcodeLabel.setText("");
        priceLabel.setText("");
        bookFormatLabel.setText("");
        bookStatusLabel.setText("");
        publicationDateLabel.setText("");

        searchField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);

            isbnLabel.setText("");
            titleLabel.setText("");
            subjectLabel.setText("");
            publisherLabel.setText("");
            languageLabel.setText("");
            numberOfPagesLabel.setText("");
            authorNameLabel.setText("");
            barcodeLabel.setText("");
            priceLabel.setText("");
            bookFormatLabel.setText("");
            bookStatusLabel.setText("");
            publicationDateLabel.setText("");
        });
    }

    @FXML
    public void handleDeleteBook() {
        if (isbnLabel.getText().isEmpty()) {
            return;
        }

        String barcode = searchField.getText();

        boolean delete = librarian.handleDeleteBook(barcode);

        if (!delete) {
            searchErrorLabel.setVisible(false);
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(true);
            return;
        }

        searchErrorLabel.setVisible(false);
        notification1.setVisible(false);
        notification2.setVisible(true);
        notification3.setVisible(false);
        totalBooksLabel.setText(librarian.getTotalBooks());
    }

    @FXML
    public void handleSearchBook() {
        String barcode = searchField.getText().trim();

        if (barcode.isBlank()) {
            searchErrorLabel.setVisible(true);
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            return;
        }

        BookItem bookItem = new BookItem();

        boolean found = librarian.handleSearch(barcode, bookItem);

        if (!found) {
            searchErrorLabel.setVisible(false);
            notification1.setVisible(true);
            notification2.setVisible(false);
            notification3.setVisible(false);
            return;
        }

        isbnLabel.setText("ISBN: " + bookItem.getISBN());
        titleLabel.setText("Title: " + bookItem.getTitle());
        subjectLabel.setText("Subject: " + bookItem.getSubject());
        publisherLabel.setText("Publisher: " + bookItem.getPublisher());
        languageLabel.setText("Language: " + bookItem.getLanguage());
        numberOfPagesLabel.setText("Number Of Pages: " + bookItem.getNumberOfPages());
        authorNameLabel.setText("Author Name: " + bookItem.getAuthorName());
        barcodeLabel.setText("Barcode: " + bookItem.getBarcode());
        priceLabel.setText("Price: $" + bookItem.getPrice());
        bookFormatLabel.setText("Format: " + bookItem.getFormat());
        bookStatusLabel.setText("Status: " + bookItem.getStatus());
        publicationDateLabel.setText("Publication Date: " + bookItem.getPublicationDate());
    }
}