package database;

import java.io.InputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import entity.Person;
import entity.Address;
import entity.BookItem;
import entity.User;


import org.json.JSONArray;
import org.json.JSONObject;
import status.AccountStatus;

public class Database {
    private static Database myDatabase;
    private Connection connection = null;

    private Database() {
        try {
            Properties props = new Properties();


            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find config.properties");
                    return;
                }
                // Tải các thuộc tính từ file config.properties
                props.load(input);
            } catch (IOException e) {
                System.out.println("Error reading config.properties: " + e.getMessage());
                return;
            }


            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");


            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error during database connection: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

    }
    public void saveBooks(JSONArray books) {
        String insertSQL = "INSERT INTO Books (authors, title, publisher, ISBN, NumberOfPages, Language, categories) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQL)) {
            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);

                // Get title from API
                String title = book.getJSONObject("volumeInfo").getString("title");

                // Get author from API
                JSONArray authorsArray = book.getJSONObject("volumeInfo").optJSONArray("authors");
                String authors = authorsArray != null ? authorsArray.join(", ") : "Unknown";

                // Get the publisher
                String publisher = book.getJSONObject("volumeInfo").optString("publisher", "Unknown");

                // Get the ISBN
                JSONArray industryIdentifiers = book.getJSONObject("volumeInfo").optJSONArray("industryIdentifiers");
                String ISBN = "Unknown";
                if (industryIdentifiers != null) {
                    for (int j = 0; j < industryIdentifiers.length(); j++) {
                        JSONObject identifierObject = industryIdentifiers.getJSONObject(j);
                        String type = identifierObject.getString("type");
                        if (type.equals("ISBN_10") || type.equals("ISBN_13")) {
                            ISBN = identifierObject.getString("identifier");
                            break;
                        }
                    }
                }

                // Get the Number of pages
                int numberOfPage = book.getJSONObject("volumeInfo").optInt("pageCount", 0);

                // Get the language
                String language = book.getJSONObject("volumeInfo").getString("language");

                // Get the categories
                JSONArray categoriesArray = book.getJSONObject("volumeInfo").optJSONArray("categories");
                String categories = categoriesArray != null ? categoriesArray.join(", ") : "Unknown";

                preparedStatement.setString(1, authors);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, publisher);
                preparedStatement.setString(4, ISBN);
                preparedStatement.setInt(5, numberOfPage);
                preparedStatement.setString(6, language);
                preparedStatement.setString(7, categories);

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public String isUsernameExists(String username) {
        String password = null;
        String query = "SELECT password FROM Account WHERE BINARY username = ?";
        try (PreparedStatement findingStatement = this.connection.prepareStatement(query)) {
            findingStatement.setString(1, username);
            ResultSet resultSet = findingStatement.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }

        return password;
    }

    public AccountStatus getAccountStatus(String username) {
        String query = "SELECT accountStatus FROM Account WHERE BINARY username = ?";
        AccountStatus accountStatus = AccountStatus.Pending;
        try (PreparedStatement findingStatement = this.connection.prepareStatement(query)) {
            findingStatement.setString(1, username);
            ResultSet resultSet = findingStatement.executeQuery();
            if (resultSet.next()) {
                accountStatus = AccountStatus.valueOf(resultSet.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return accountStatus;
    }

    public boolean registerUser(String username, String password, Person person, AccountStatus accountStatus) {
        String register = "INSERT INTO Account (username, password, accountStatus, " +
                "fullName, email, phoneNumber, streetAddress, city, state, zipCode, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?)";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(register)) {
            String fullName = person.getName();
            String email = person.getEmail();
            String phoneNumber = person.getPhoneNumber();
            String streetAddress = person.getAddress().getStreetAddress();
            String city = person.getAddress().getCity();
            String country = person.getAddress().getCountry();

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, accountStatus.name());
            preparedStatement.setString(4, fullName);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, phoneNumber);
            preparedStatement.setString(7, streetAddress);
            preparedStatement.setString(8, city);
            preparedStatement.setString(9, country);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateInfo(String username, Person newPerson) {
        return false;
    }
    public boolean resetPassword(String username, String newPassword) {
        String query = "UPDATE Account " + "SET password = '" + newPassword + "' WHERE username = '" + username + "'";
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean searchBook(String title, List<BookItem> bookList) {
        return false;
    }
    public boolean searchBook(String barcode, BookItem bookItem) {
        return false;
    }
    public boolean searchBookOnline(String title, List<BookItem> bookList) {
        return false;
    }
    public void getAllAccountFromDatabase(List<User> userList) {

    }

    public void getAllBookFromDatabase(List<BookItem> bookList) {

    }

    public boolean verifyUserAccount(String username) {
        return true;
    }

    public boolean lockUserAccount(String username) {
        return false;
    }

    public boolean updateBook(String barcode, BookItem newBook) {
        return false;
    }

    public String getTotalMembers() {
        return "100";
    }

    public String getTotalBooks() {
        return "500";
    }

    public String getBorrowedBooks() {
        return "200";
    }

    public String getOverdueBooks() {
        return "20";
    }

    public boolean addBook(BookItem bookItem) {
        return false;
    }

    public void closeDatabase() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (Exception e) {
            System.out.println("Error closing the database: " + e.getMessage());
        }
    }

    public Person getPerson(String username) {
        String query = "SELECT * FROM Account WHERE BINARY username = ?";
        try (PreparedStatement findingStatement = this.connection.prepareStatement(query)) {
            findingStatement.setString(1, username); // set the parameter for statement
            ResultSet resultSet = findingStatement.executeQuery();
            if (resultSet.next()) {
                String fullName = resultSet.getString(4);
                String email = resultSet.getString(5);
                String phoneNumber = resultSet.getString(6);
                String streetAddress = resultSet.getString(7);
                String city = resultSet.getString(8);
                String state = resultSet.getString(9);
                String zipCode = resultSet.getString(10);
                String country = resultSet.getString(11);
                Address address = new Address(streetAddress, city, country);
                return new Person(fullName, email, phoneNumber, address);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
    }

    public static Database getInstance() {
        if (myDatabase == null) {
            myDatabase = new Database();
        }
        return myDatabase;
    }




}
