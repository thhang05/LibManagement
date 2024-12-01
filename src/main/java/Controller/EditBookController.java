package Controller;

import Entity.BookFormat;
import Entity.BookStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.BookItem;
import models.Librarian;

import java.time.LocalDate;

public class EditBookController {
    private Stage stage;
    private Librarian librarian;

    @FXML
    private TextField searchField;

    @FXML
    private Label searchErrorLabel;

    @FXML
    private Label notification1;

    @FXML
    private Label isbnLabel;

    @FXML
    private TextField isbnField;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleField;

    @FXML
    private Label subjectLabel;

    @FXML
    private TextField subjectField;

    @FXML
    private Label publisherLabel;

    @FXML
    private TextField publisherField;

    @FXML
    private Label languageLabel;

    @FXML
    private TextField languageField;

    @FXML
    private Label numberOfPagesLabel;

    @FXML
    private TextField numberOfPagesField;

    @FXML
    private Label authorNameLabel;

    @FXML
    private TextField authorNameField;

    @FXML
    private Label barcodeLabel;

    @FXML
    private TextField barcodeField;

    @FXML
    private Label priceLabel;

    @FXML
    private TextField priceField;

    @FXML
    private Label bookFormatLabel;

    @FXML
    private TextField bookFormatField;

    @FXML
    private Label bookStatusLabel;

    @FXML
    private TextField bookStatusField;

    @FXML
    private Label publicationDateLabel;

    @FXML
    private TextField publicationDateField;

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
        this.librarian = librarian;}

    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> {
            event.consume();
            stage.close();
        });

        notification1.setText("* No Books Found");
        notification2.setText("Update Book SuccessFully");
        notification3.setText("Fail To Update Book");
        searchErrorLabel.setText("* Please enter a search term before searching");

        notification1.setVisible(false);
        notification2.setVisible(false);
        notification3.setVisible(false);
        searchErrorLabel.setVisible(false);

        searchField.setOnMousePressed(event -> {
            event.consume();
            isbnLabel.setVisible(false);
            isbnField.setVisible(false);
            titleLabel.setVisible(false);
            titleField.setVisible(false);
            subjectLabel.setVisible(false);
            subjectField.setVisible(false);
            publisherLabel.setVisible(false);
            publisherField.setVisible(false);
            languageLabel.setVisible(false);
            languageField.setVisible(false);
            numberOfPagesLabel.setVisible(false);
            numberOfPagesField.setVisible(false);
            authorNameLabel.setVisible(false);
            authorNameField.setVisible(false);
            barcodeLabel.setVisible(false);
            barcodeField.setVisible(false);
            priceLabel.setVisible(false);
            priceField.setVisible(false);
            bookFormatLabel.setVisible(false);
            bookFormatField.setVisible(false);
            bookStatusLabel.setVisible(false);
            bookStatusField.setVisible(false);
            publicationDateLabel.setVisible(false);
            publicationDateField.setVisible(false);
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        isbnField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        titleField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        subjectField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        publisherField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        languageField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        numberOfPagesField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        authorNameField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        barcodeField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        priceField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        bookFormatField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        bookStatusField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        publicationDateField.setOnMousePressed(event -> {
            event.consume();
            notification1.setVisible(false);
            notification2.setVisible(false);
            notification3.setVisible(false);
            searchErrorLabel.setVisible(false);
        });

        isbnLabel.setVisible(false);
        isbnField.setVisible(false);
        titleLabel.setVisible(false);
        titleField.setVisible(false);
        subjectLabel.setVisible(false);
        subjectField.setVisible(false);
        publisherLabel.setVisible(false);
        publisherField.setVisible(false);
        languageLabel.setVisible(false);
        languageField.setVisible(false);
        numberOfPagesLabel.setVisible(false);
        numberOfPagesField.setVisible(false);
        authorNameLabel.setVisible(false);
        authorNameField.setVisible(false);
        barcodeLabel.setVisible(false);
        barcodeField.setVisible(false);
        priceLabel.setVisible(false);
        priceField.setVisible(false);
        bookFormatLabel.setVisible(false);
        bookFormatField.setVisible(false);
        bookStatusLabel.setVisible(false);
        bookStatusField.setVisible(false);
        publicationDateLabel.setVisible(false);
        publicationDateField.setVisible(false);
    }

    @FXML
    public void handleUpdateBook() {
        if (!isbnField.isVisible()) {
            return;
        }

        BookItem bookItem = new BookItem();


        bookItem.setISBN(isbnField.getText());
        bookItem.setTitle(titleField.getText());
        bookItem.setSubject(subjectField.getText());
        bookItem.setPublisher(publisherField.getText());
        bookItem.setLanguage(languageField.getText());
        bookItem.setNumberOfPages(Integer.parseInt(numberOfPagesField.getText()));
        bookItem.setAuthorName(authorNameField.getText());
        bookItem.setBarcode(barcodeField.getText());
        bookItem.setPrice(Double.parseDouble(priceField.getText()));
        bookItem.setFormat(BookFormat.valueOf(bookFormatField.getText()));
        bookItem.setStatus(BookStatus.valueOf(bookStatusField.getText()));
        bookItem.setPublicationDate(LocalDate.parse(publicationDateField.getText()));

        boolean update = librarian.handleEditBook(bookItem);

        notification1.setVisible(false);
        searchErrorLabel.setVisible(false);
        if (!update) {
            notification2.setVisible(false);
            notification3.setVisible(true);
        } else {
            notification2.setVisible(true);
            notification3.setVisible(false);
        }
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

        isbnField.setText(bookItem.getISBN());
        titleField.setText(bookItem.getTitle());
        subjectField.setText(bookItem.getSubject());
        publisherField.setText(bookItem.getPublisher());
        languageField.setText(bookItem.getLanguage());
        numberOfPagesField.setText(String.valueOf(bookItem.getNumberOfPages()));
        authorNameField.setText(bookItem.getAuthorName());
        barcodeField.setText(bookItem.getBarcode());
        priceField.setText(String.valueOf(bookItem.getPrice()));
        bookFormatField.setText(String.valueOf(bookItem.getFormat()));
        bookStatusField.setText(String.valueOf(bookItem.getStatus()));
        publicationDateField.setText(String.valueOf(bookItem.getPublicationDate()));

        isbnLabel.setVisible(true);
        isbnField.setVisible(true);
        titleLabel.setVisible(true);
        titleField.setVisible(true);
        subjectLabel.setVisible(true);
        subjectField.setVisible(true);
        publisherLabel.setVisible(true);
        publisherField.setVisible(true);
        languageLabel.setVisible(true);
        languageField.setVisible(true);
        numberOfPagesLabel.setVisible(true);
        numberOfPagesField.setVisible(true);
        authorNameLabel.setVisible(true);
        authorNameField.setVisible(true);
        barcodeLabel.setVisible(true);
        barcodeField.setVisible(true);
        priceLabel.setVisible(true);
        priceField.setVisible(true);
        bookFormatLabel.setVisible(true);
        bookFormatField.setVisible(true);
        bookStatusLabel.setVisible(true);
        bookStatusField.setVisible(true);
        publicationDateLabel.setVisible(true);
        publicationDateField.setVisible(true);
    }
}