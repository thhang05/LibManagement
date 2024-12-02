package Entity;

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

    public boolean handleEditBook(BookItem newBook) {
        return database.updateBook(newBook);
    }

    public boolean handleDeleteBook(String barcode) {
        return database.deleteBook(barcode);
    }

    public boolean verifyUserAccount(String username) {
        return database.verifyUserAccount(username);
    }

    public boolean lockUserAccount(String username) {
        return database.lockUserAccount(username);
    }

    public boolean handleSearch(String keyword, String searchType, List<BookItem> bookList) {
        return database.searchBooks(keyword, searchType, bookList);
    }

    public boolean handleSearch(String barcode, BookItem bookItem) {
        return database.searchBook(barcode, bookItem);
    }

    public boolean handleSearchOnline(String keyword, String searchType, List<BookItem> bookList) {
        return database.searchBookOnline(keyword, searchType, bookList);
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
}