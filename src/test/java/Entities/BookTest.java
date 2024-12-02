package Entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void getISBN() {
        Book temp = new Book("12192", "sdjksdjka"
                , "dskjdkasj", "sdaskdjks", "dsllas", 78, "dksdj");
        assertEquals("12192", temp.getISBN());

    }

    @Test
    void getTitle() {
        Book temp = new Book("12192", "sdjksdjka"
                , "dskjdkasj", "sdaskdjks", "dsllas", 78, "dksdj");
        assertEquals("sdjksdjka", temp.getTitle());
    }

    @Test
    void getSubject() {
        Book temp = new Book("12192", "sdjksdjka"
                , "dskjdkasj", "sdaskdjks", "dsllas", 78, "dksdj");
        assertEquals("dskjdkasj", temp.getSubject());
    }

    @Test
    void getLanguage() {
        Book temp = new Book("12192", "sdjksdjka"
                , "dskjdkasj", "sdaskdjks", "dsllas", 78, "dksdj");
        assertEquals("dsllas", temp.getLanguage());
    }
}