package Entities;

import Status.BookFormat;
import Status.BookStatus;
import org.junit.jupiter.api.Test;
import weka.core.stopwords.Null;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BookItemTest {

    @Test
    void getBarcode() {
        BookItem tmp = new BookItem("iii","jjjiji","abc","aaa","aaa",5,
                "jhjh","aaa",7, BookFormat.Ebook, BookStatus.Loaned, LocalDate.of(2005, 02, 10));
        assertEquals("aaa",tmp.getBarcode());
    }

}