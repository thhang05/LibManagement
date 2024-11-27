package entity;

import database.Database;

import java.util.List;

public class Admin extends Account {
    private final Database database = Database.getInstance();

    public Admin(String username, String password) {
        super(username, password);
    }

    public boolean handleAddBook(BookItem bookItem) {
        return Database.getInstance().addBook(bookItem);
    }

    public boolean handleEditBook(String barcode, BookItem newBook) {
        return Database.getInstance().updateBook(barcode, newBook);
    }

    public void handleDeleteBook() {
        System.out.println("Delete Book...");
    }

    public boolean verifyUserAccount(String username) {
        return Database.getInstance().verifyUserAccount(username);
    }

    public boolean lockUserAccount(String username) {
        return Database.getInstance().lockUserAccount(username);
    }

    public boolean handleSearch(String title, List<BookItem> bookList) {
        return Database.getInstance().searchBook(title, bookList);
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
