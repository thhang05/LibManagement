package Entity;

import Status.AccountStatus;
import java.util.Objects;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Member extends Account {
    public static final int MAX_BORROWABLE_BOOK = 10;
    public static final int MAX_OVERDUE_BOOKS_ALLOWED = 3;
    private int overdueBooks;

    public Member(String username, String password) {
        super(username, password);
        this.overdueBooks = 0;
    }

    public Member(String username, int overdueBooks) {
        this.username = username;
        this.overdueBooks = overdueBooks;
    }

    public Member(String username, String password, AccountStatus status, Person person) {
        super(username, password, status, person);
    }

    public IntegerProperty overdueBooksProperty() {
        return new SimpleIntegerProperty(overdueBooks);
    }

    public String detail() {
        return "Username: " + username + "\n" +
                "Full Name: " + person.getName() + "\n" +
                "Phone Number: " + person.getPhoneNumber() + "\n" +
                "Email: " + person.getEmail() + "\n" +
                "Street Address: " + person.getAddress().getStreetAddress() + "\n" +
                "City: " + person.getAddress().getCity() + "\n" +
                "State: " + person.getAddress().getState() + "\n" +
                "Zipcode: " + person.getAddress().getZipcode() + "\n" +
                "Country: " + person.getAddress().getCountry();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return Objects.equals(username, member.getUsername());
    }
}