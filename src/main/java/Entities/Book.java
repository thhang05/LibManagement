package Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    protected String ISBN;
    protected String title;
    protected String subject;
    protected String publisher;
    protected String language;
    protected int numberOfPages;
    protected String authorName;
    protected String imageUrl;

    public Book() {
        this.ISBN = null;
        this.title = null;
        this.publisher = null;
        this.language = null;
        this.numberOfPages = 0;
        this.authorName = null;
    }

    public Book(String ISBN, String title, String subject, String publisher, String language,
                int numberOfPages, String authorName) {
        this.ISBN = ISBN;
        this.title = title;
        this.subject = subject;
        this.publisher = publisher;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.authorName = authorName;
    }

    public Book(String ISBN, String title, String subject, String publisher, String language,
                int numberOfPages, String authorName, String imageUrl) {
        this.ISBN = ISBN;
        this.title = title;
        this.subject = subject;
        this.publisher = publisher;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public StringProperty subjectProperty() {
        return new SimpleStringProperty(subject);
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public StringProperty authorNameProperty() {
        return new SimpleStringProperty(authorName);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInfo() {
        return "Title: " + title + "\n"
                + "Author: " + authorName + "\n"
                + "ISBN: " + ISBN;
    }
}