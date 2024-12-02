package Entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibrarianTest {

    @Test
    void getTotalMembers() {
        Librarian tmp = new Librarian(""," ");
        assertEquals("6",tmp.getTotalMembers());
    }

    @Test
    void getTotalBooks() {
        Librarian tmp = new Librarian(""," ");
        assertEquals("33",tmp.getTotalBooks());

    }

    @Test
    void getBorrowedBooks() {
        Librarian tmp = new Librarian(""," ");
        assertEquals("9",tmp.getBorrowedBooks());
    }

    @Test
    void getOverdueBooks() {
        Librarian tmp = new Librarian(""," ");
        assertEquals("0",tmp.getOverdueBooks());
    }
}