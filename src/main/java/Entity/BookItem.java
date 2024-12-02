package Entity;

import Status.BookFormat;
import Status.BookStatus;
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

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String detail() {
        return "Title: " + title + "\n" +
                "ISBN: " + ISBN + "\n" +
                "Barcode: " + barcode + "\n" +
                "Author: " + authorName + "\n" +
                "Subject: " + subject + "\n" +
                "Publisher: " + publisher + "\n" +
                "Language: " + language + "\n" +
                "Price: $" + price + "\n" +
                "Number of Pages: " + numberOfPages + "\n" +
                "Format: " + format + "\n" +
                "Status: " + status + "\n" +
                "Publication Date: " + publicationDate;
    }
}