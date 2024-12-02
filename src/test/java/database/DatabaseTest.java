package database;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;

class DatabaseTest {
    @Test
    void searchBooksUtil() {
        GoogleBooksAPI googleBooksAPI = GoogleBooksAPI.getInstance();

        String searchQuery = "Java";
        JSONArray books = googleBooksAPI.searchBooks(searchQuery, "Title");

        assertNotNull(books);
        assertFalse(books.isEmpty(), "No books found for search query: " + searchQuery);
    }

    @Test
    void resetPassword() {
        Database mydatabase = Database.getInstance();
        boolean result = mydatabase.resetPassword("root", "12341234");
        assertTrue(result, "Password reset failed for user: root");
    }
}