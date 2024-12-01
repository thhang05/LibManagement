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

public class AddBookController {
    private Stage stage;
    private Librarian librarian;
    private Label totalBooksLabel;

    @FXML
    private TextField isbnField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextField publisherField;

    @FXML
    private TextField languageField;

    @FXML
    private TextField numberOfPagesField;

    @FXML
    private TextField authorNameField;

    @FXML
    private TextField barcodeField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField bookFormatField;

    @FXML
    private TextField bookStatusField;

    @FXML
    private TextField publicationDateField;

    @FXML
    private Label notification1;

    @FXML
    private Label notification2;

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

        isbnField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        titleField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        subjectField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        publisherField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        languageField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        numberOfPagesField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        authorNameField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        barcodeField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        priceField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        bookFormatField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        bookStatusField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });

        publicationDateField.setOnMousePressed(event -> {
            event.consume();
            notification1.setText("");
            notification2.setText("");
        });
    }

    @FXML
    public void handleAddBook() {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String subject = subjectField.getText().trim();
        String publisher = publisherField.getText().trim();
        String language = languageField.getText().trim();
        String numberOfPages = numberOfPagesField.getText().trim();
        String authorName = authorNameField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String price = priceField.getText().trim();
        String bookFormat = bookFormatField.getText().trim();
        String bookStatus = bookStatusField.getText().trim();
        String publicationDate = publicationDateField.getText().trim();

        notification1.setText("");
        notification2.setText("");

        BookItem bookItem = new BookItem(isbn, title, subject, publisher, language, Integer.parseInt(numberOfPages), authorName, barcode,
                Double.parseDouble(price), BookFormat.valueOf(bookFormat), BookStatus.valueOf(bookStatus), LocalDate.parse(publicationDate));
        if (librarian.handleAddBook(bookItem)) {
            notification1.setText("Book added successfully");
            totalBooksLabel.setText(librarian.getTotalBooks());
        } else {
            notification2.setText("Failed to Add Book");
        }
    }
}