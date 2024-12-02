package database;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoogleBooksAPITest {

    @Test
    void searchBooks() {
        JSONArray tmp = GoogleBooksAPI.getInstance().searchBooks("Hang", "title");
        boolean flag = tmp.isEmpty();
        assertEquals(false, flag);

    }
}