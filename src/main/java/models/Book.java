package models;

import javafx.beans.property.*;

public abstract class Book {
    protected String ISBN;
    protected String title;
    protected String categories;
    protected String publisher;
    protected String language;
    protected int numberOfPages;
    protected String author;

    public Book() {
        this.ISBN = null;
        this.title = null;
        this.publisher = null;
        this.language = null;
        this.numberOfPages = 0;
        this.author = null;
    }

    public Book(String ISBN, String title, String categories, String publisher, String language, int numberOfPages, String authorName) {
        this.ISBN = ISBN;
        this.title = title;
        this.categories = categories;
        this.publisher = publisher;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.author = authorName;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public StringProperty isbnProperty() {
        return new SimpleStringProperty(ISBN);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public StringProperty categoriesProperty() {
        return new SimpleStringProperty(categories);
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public StringProperty publisherProperty() {
        return new SimpleStringProperty(publisher);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public StringProperty languageProperty() {
        return new SimpleStringProperty(language);
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public IntegerProperty numberOfPagesProperty() {
        return new SimpleIntegerProperty(numberOfPages);
    }

    public String getAuthorName() {
        return author;
    }

    public void setAuthorName(String authorName) {
        this.author = authorName;
    }

    public StringProperty authorNameProperty() {
        return new SimpleStringProperty(author);
    }
}