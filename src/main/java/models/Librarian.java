package models;

import database.Database;

import java.util.List;

public class Librarian extends Account {
    private final Database database = Database.getInstance();

    public Librarian(String username, String password) {
        super(username, password);
    }

    public boolean handleAddBook(BookItem bookItem) {
        return database.addBook(bookItem);
    }

    public boolean handleEditBook(String barcode, BookItem newBook) {
        return database.updateBook(barcode, newBook);
    }

    public void handleDeleteBook() {
        System.out.println("Delete Book...");
    }

    public boolean verifyUserAccount(String username) {
        return database.verifyUserAccount(username);
    }

    public boolean lockUserAccount(String username) {
        return database.lockUserAccount(username);
    }

    public boolean handleSearch(String title, List<BookItem> bookList) {
        return database.searchBook(title, bookList);
    }

    public boolean handleSearch(String barcode, BookItem bookItem) {
        return database.searchBook(barcode, bookItem);
    }

    public boolean handleSearchOnline(String title, List<BookItem> bookList) {
        return database.searchBookOnline(title, bookList);
    }

    public void handleReports() {
        System.out.println("Report...");
    }

    public void handleFines() {
        System.out.println("Fine...");
    }

    public String getTotalMembers() {
        return database.getTotalMembers();
    }

    public String getTotalBooks() {
        return database.getTotalBooks();
    }

    public String getBorrowedBooks() {
        return database.getBorrowedBooks();
    }

    public String getOverdueBooks() {
        return database.getOverdueBooks();
    }

    @Override
    public boolean updateInfo(Person newPerson) {
        return database.updateInfo(username, newPerson);
    }

    @Override
    public boolean resetPassword(String newPassword) {
        return database.resetPassword(username, newPassword);
    }
}