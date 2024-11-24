package models;

import enums.BookFormat;
import enums.BookStatus;
import javafx.beans.property.*;

import java.time.LocalDate;
public class BookItem extends Book {
    private String barcode;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private double price;
    private BookFormat format;
    private BookStatus status;
    private LocalDate publicationDate;
    public static final int DEFAULT_BORROW_DAYS = 14;

    public BookItem() {
        this.barcode = null;
        this.borrowedDate = null;
        this.dueDate = null;
        this.price = 0;
        this.format = null;
        this.status = null;
        this.publicationDate = null;
    }

    public BookItem(String ISBN, String title, String subject, String publisher, String language,
                    int numberOfPages, String authorName, String barcode, double price, BookFormat format,
                    BookStatus status, LocalDate publicationDate) {

        super(ISBN, title, subject, publisher, language, numberOfPages, authorName);

        this.barcode = barcode;
        this.price = price;
        this.format = format;
        this.status = status;
        this.publicationDate = publicationDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public StringProperty barcodeProperty() {
        return new SimpleStringProperty(barcode);
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public ObjectProperty<LocalDate> borrowedDateProperty() {
        return new SimpleObjectProperty<>(borrowedDate);
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ObjectProperty<LocalDate> dueDateProperty() {
        return new SimpleObjectProperty<>(dueDate);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public DoubleProperty priceProperty() {
        return new SimpleDoubleProperty(price);
    }

    public BookFormat getFormat() {
        return format;
    }

    public void setFormat(BookFormat format) {
        this.format = format;
    }

    public ObjectProperty<BookFormat> formatProperty() {
        return new SimpleObjectProperty<>(format);
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public ObjectProperty<BookStatus> statusProperty() {
        return new SimpleObjectProperty<>(status);
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ObjectProperty<LocalDate> publicationDateProperty() {
        return new SimpleObjectProperty<>(publicationDate);
    }

    public void checkout() {
        if (this.getStatus() == BookStatus.Loaned) {
            System.out.println("The book has already been borrowed by someone else.");
            return;
        }

        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(DEFAULT_BORROW_DAYS);
        this.setStatus(BookStatus.Loaned);
        System.out.println("You have successfully borrowed the book. The return date is: " + dueDate);
    }

    public String detail() {
        return "Title: " + title + "\n" +
                "ISBN: " + ISBN + "\n" +
                "Barcode: " + barcode + "\n" +
                "Author: " + author + "\n" +
                "Subject: " + categories + "\n" +
                "Publisher: " + publisher + "\n" +
                "Language: " + language + "\n" +
                "Price: $" + price + "\n" +
                "Number of Pages: " + numberOfPages + "\n" +
                "Format: " + format + "\n" +
                "Status: " + status + "\n" +
                "Publication Date: " + publicationDate;
    }
}