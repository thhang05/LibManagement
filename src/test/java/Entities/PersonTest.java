package Entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void getName() {
        Person tmp = new Person("abc","abc@gmail.com","jjj",new Address("ddd","sss","aa","11","vn"));
        assertEquals("abc",tmp.getName());
    }

    @Test
    void getEmail() {
        Person tmp = new Person("abc","abc@gmail.com","jjj",new Address("ddd","sss","aa","11","vn"));
        assertEquals("abc@gmail.com",tmp.getEmail());
    }

    @Test
    void getPhoneNumber() {
        Person tmp = new Person("abc","abc@gmail.com","jjj",new Address("ddd","sss","aa","11","vn"));
        assertEquals("jjj",tmp.getPhoneNumber());
    }

    @Test
    void getAddress() {
        Person tmp = new Person("abc","abc@gmail.com","jjj",new Address("ddd","sss","aa","11","vn"));
        assertEquals(new Address("ddd","sss","aa","11","vn").getCity(),tmp.getAddress().getCity());
    }

}