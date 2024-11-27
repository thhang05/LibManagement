package entity;

import status.AccountStatus;

import java.util.InputMismatchException;
import java.util.Scanner;

public class User extends Account{
    public User(String username, String password) {
        super(username, password);
    }

    public User(String username, String password, AccountStatus status, Person person) {
        super(username, password, status, person);
    }

    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Member Menu ---");
            System.out.println("[1] View available books");
            System.out.println("[2] Search book");
            System.out.println("[3] Borrow a book");
            System.out.println("[4] Return a book");
            System.out.println("[5] View account details");
            System.out.println("[6] Logout");
            System.out.print("Choose an option: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character

                switch (option) {
                    case 1:
                        viewAvailableBooks();
                        break;
                    case 2:
                        searchBook();
                        break;
                    case 3:
                        borrowBook();
                        break;
                    case 4:
                        returnBook();
                        break;
                    case 5:
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        return; // Exit the menu loop
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();  // Clear the invalid input
            }
        }
    }

    public void viewAvailableBooks() {
        System.out.println("Viewing available books...");
        // Implementation for viewing available books
    }

    public void searchBook() {

    }

    public void borrowBook() {
        System.out.println("Borrowing a book...");

    }

    public void returnBook() {
        System.out.println("Returning a book...");

    }

    @Override
    public boolean updateInfo(Person newPerson) {
        return false;
    }

    @Override
    public boolean resetPassword(String newPassword) {
        return false;
    }

    public String detail() {
        return "Username: " + username + "\n" +
                "Full Name: " + person.getName() + "\n" +
                "Phone Number: " + person.getPhoneNumber() + "\n" +
                "Email: " + person.getEmail() + "\n" +
                "Street Address: " + person.getAddress().getStreetAddress() + "\n" +
                "City: " + person.getAddress().getCity() + "\n" +
                "Country: " + person.getAddress().getCountry();
    }




}
