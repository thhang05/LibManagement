package Entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void updateInfo() {
        Person temp = new Person("hangmam", "abc@gmail.com"
                ,"037237521577",
                new Address("hjhjhj","jjjj","klaklak", "00","klkl"));
        Account tmp = new Librarian("mam", "27102005");

        assertEquals(false, tmp.updateInfo(temp));
    }

    @Test
    void resetPassword() {
        Account tmp = new Librarian("mam", "27102005");

        assertEquals(true, tmp.resetPassword("02092005"));
    }
}