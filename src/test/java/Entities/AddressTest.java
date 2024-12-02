package Entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void getState() {
        Address tmp = new Address("abc","Hanoi","jjjh000","jhjh","khjj");
        String a = tmp.getState();
        assertEquals("jjjh000",a);
    }

    @Test
    void getCountry(){
        Address tmp = new Address("abc","Hanoi","jjjh000","jhjh","khjj");
        assertEquals("khjj",tmp.getCountry());
    }
}