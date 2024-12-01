package database;

import Handler.AuthorHandler;
import Status.BookFormat;
import Status.BookStatus;
import Status.TypeOfSearch;
import Status.AccountStatus;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import javafx.util.Pair;
import Entity.Address;
import Entity.Book;
import Entity.BookItem;
import Entity.Member;
import Entity.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Entity.BookLending;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

import static database.BookRecommendation.*;

public class Database {
    private static Database myDatabase;
    private Connection connection = null;

    private Database() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find config.properties");
                    return;
                }
                props.load(input);
            }

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("Error during database connection: " + e.getMessage());
        }
    }

    public void searchBooksUtil(JSONArray books, List<BookItem> bookItemList) {
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
            if (ISBN.equals("Unknown")) {
                continue;
            }

            // Get the Number of pages
            int numberOfPage = book.getJSONObject("volumeInfo").optInt("pageCount", 0);

            // Get the language
            String language = book.getJSONObject("volumeInfo").getString("language");

            // Get the categories
            JSONArray categoriesArray = book.getJSONObject("volumeInfo").optJSONArray("categories");
            String categories = categoriesArray != null ? categoriesArray.join(", ") : "Unknown";

            // Get the price
            double price = 50.0;
            if (book.getJSONObject("saleInfo").has("listPrice")) {
                price = book.getJSONObject("saleInfo").getJSONObject("listPrice").getDouble("amount");
            }

            // Get Publication Date
            String tmp = book.getJSONObject("volumeInfo").optString("publishedDate", null);
            LocalDate pubDate;
            if (tmp != null) {
                try {
                    pubDate = getLocalDate(tmp);
                } catch (Exception e) {
                    pubDate = LocalDate.of(2005, 4, 16);
                }
            } else {
                pubDate = LocalDate.of(2005, 4, 16);
            }

            //get the imageUrl
            String imageUrl = null;

            try {
                if (book.has("volumeInfo")) {
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    if (volumeInfo.has("imageLinks")) {
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        if (imageLinks.has("thumbnail")) {
                            imageUrl = imageLinks.getString("thumbnail");
                        }
                    }
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }


            BookItem temp = new BookItem();
            temp.setAuthorName(authors);
            temp.setTitle(title);
            temp.setPublisher(publisher);
            temp.setISBN(ISBN);
            temp.setNumberOfPages(numberOfPage);
            temp.setLanguage(language);
            temp.setSubject(categories);
            temp.setBarcode("Unknown");
            temp.setDueDate(null);
            temp.setBorrowedDate(null);
            temp.setPrice(price);
            temp.setFormat(BookFormat.valueOf("Paperback"));
            temp.setStatus(BookStatus.valueOf("Available"));
            temp.setPublicationDate(pubDate);
            if (imageUrl != null) {
                temp.setImageUrl(imageUrl);
            }
            bookItemList.add(temp);
        }
    }

    private static LocalDate getLocalDate(String tmp) {
        Function<String, LocalDate> changeDate = (_) -> {
            try {
                return LocalDate.parse(tmp, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                try {
                    return LocalDate.parse(tmp + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    try {
                        return LocalDate.parse(tmp + "-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException exc) {
                        return LocalDate.parse("2000-01-01");
                    }
                }
            }
        };
        return changeDate.apply(tmp);
    }

    public String isUsernameExists(String username) {
        String password = null;
        String query = "SELECT password FROM Account WHERE BINARY username = ?";

        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }

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

    public Person getPerson(String username) {
        String query = "SELECT * FROM Account WHERE BINARY username = ?";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
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
                Address address = new Address(streetAddress, city, state,
                        zipCode, country);
                return new Person(fullName, email, phoneNumber, address);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
    }

    public AccountStatus getAccountStatus(String username) {
        String query = "SELECT accountStatus FROM Account WHERE BINARY username = ?";
        AccountStatus accountStatus = AccountStatus.Pending;
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
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
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(register)) {
            String fullName = person.getName();
            String email = person.getEmail();
            String phoneNumber = person.getPhoneNumber();
            String streetAddress = person.getAddress().getStreetAddress();
            String city = person.getAddress().getCity();
            String state = person.getAddress().getState();
            String zipCode = person.getAddress().getZipcode();
            String country = person.getAddress().getCountry();

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, accountStatus.name());
            preparedStatement.setString(4, fullName);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, phoneNumber);
            preparedStatement.setString(7, streetAddress);
            preparedStatement.setString(8, city);
            preparedStatement.setString(9, state);
            preparedStatement.setString(10, zipCode);
            preparedStatement.setString(11, country);
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
        String query = "UPDATE Account SET fullName = ?, email = ?, phoneNumber = ?, " +
                "streetAddress = ?, city = ?, state = ?, zipCode = ?, country = ? " +
                "WHERE username = ?";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newPerson.getName());
            preparedStatement.setString(2, newPerson.getEmail());
            preparedStatement.setString(3, newPerson.getPhoneNumber());
            preparedStatement.setString(4, newPerson.getAddress().getStreetAddress());
            preparedStatement.setString(5, newPerson.getAddress().getCity());
            preparedStatement.setString(6, newPerson.getAddress().getState());
            preparedStatement.setString(7, newPerson.getAddress().getZipcode());
            preparedStatement.setString(8, newPerson.getAddress().getCountry());
            preparedStatement.setString(9, username);

            preparedStatement.addBatch();
            preparedStatement.executeBatch();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean resetPassword(String username, String newPassword) {
        String query = "UPDATE Account " + "SET password = '" + newPassword + "' WHERE username = '" + username + "'";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean searchBooks(String keyword, String searchType, List<BookItem> bookList) {
        String column;
        switch (searchType) {
            case "Title":
                column = "title";
                break;
            case "Author":
                column = "authors";
                break;
            case "Subject":
                column = "categories";
                break;
            default:
                return false;
        }

        String query = "SELECT BookItems.*, Books.authors, Books.title, Books.publisher, " +
                "Books.NumberOfPages, Books.Language, Books.categories FROM BookItems " +
                "JOIN Books ON Books.ISBN = BookItems.ISBN " +
                "WHERE Books." + column + " LIKE '%" + keyword + "%'";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }

        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String ISBN = resultSet.getString(1);
                String barcode = resultSet.getString(2);
                LocalDate borrowDate = resultSet.getDate(3) != null ? resultSet.getDate(3).toLocalDate() : null;
                LocalDate dueDate = resultSet.getDate(4) != null ? resultSet.getDate(4).toLocalDate() : null;
                double price = resultSet.getDouble(5);
                BookFormat bookFormat = BookFormat.valueOf(resultSet.getString(6));
                BookStatus bookStatus = BookStatus.valueOf(resultSet.getString(7));
                LocalDate publicationDate = resultSet.getDate(8).toLocalDate();
                String author = resultSet.getString(9);
                String title = resultSet.getString(10);
                String publisher = resultSet.getString(11);
                int numberOfPage = resultSet.getInt(12);
                String language = resultSet.getString(13);
                String categories = resultSet.getString(14);

                BookItem tmp = new BookItem(ISBN, title, categories, publisher, language, numberOfPage,
                        author, barcode, price, bookFormat, bookStatus, publicationDate);
                tmp.setBorrowedDate(borrowDate);
                tmp.setDueDate(dueDate);
                bookList.add(tmp);
            }
            return !bookList.isEmpty();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    public boolean searchBook(String findingBarcode, BookItem bookItem) {
        String query = "SELECT BookItems.*, Books.authors, Books.title, Books.publisher," +
                "Books.NumberOfPages, Books.Language, Books.categories FROM BookItems " +
                "JOIN Books ON Books.ISBN = BookItems.ISBN WHERE BookItems.barcode = '" + findingBarcode + "'";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String ISBN = resultSet.getString(1);
                String barcode = resultSet.getString(2);
                LocalDate borrowDate = resultSet.getDate(3) != null ? resultSet.getDate(3).toLocalDate() : null;
                LocalDate dueDate = resultSet.getDate(4) != null ? resultSet.getDate(4).toLocalDate() : null;
                double price = resultSet.getDouble(5);
                BookFormat bookFormat = BookFormat.valueOf(resultSet.getString(6));
                BookStatus bookStatus = BookStatus.valueOf(resultSet.getString(7));
                LocalDate publicationDate = resultSet.getDate(8).toLocalDate();
                String author = resultSet.getString(9);
                String title = resultSet.getString(10);
                String publisher = resultSet.getString(11);
                int numberOfPage = resultSet.getInt(12);
                String language = resultSet.getString(13);
                String categories = resultSet.getString(14);

                bookItem.setAuthorName(author);
                bookItem.setTitle(title);
                bookItem.setPublisher(publisher);
                bookItem.setISBN(ISBN);
                bookItem.setNumberOfPages(numberOfPage);
                bookItem.setLanguage(language);
                bookItem.setSubject(categories);
                bookItem.setBarcode(barcode);
                bookItem.setDueDate(dueDate);
                bookItem.setBorrowedDate(borrowDate);
                bookItem.setPrice(price);
                bookItem.setFormat(bookFormat);
                bookItem.setStatus(bookStatus);
                bookItem.setPublicationDate(publicationDate);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    public boolean searchBookOnline(String keyword, String searchType, List<BookItem> bookList) {
        JSONArray books = GoogleBooksAPI.getInstance().searchBooks(keyword, searchType);
        if (books == null || books.isEmpty()) {
            return false;
        }
        searchBooksUtil(books, bookList);
        return true;
    }

    public void getAllAccountFromDatabase(List<Member> userList) {
        String query = "SELECT * FROM Account";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement findingStatement = this.connection.prepareStatement(query)) {
            ResultSet resultSet = findingStatement.executeQuery();
            while (resultSet.next()) {
                String userName = resultSet.getString(1);
                String password = resultSet.getString(2);
                AccountStatus accountStatus = AccountStatus.valueOf(resultSet.getString(3));
                String fullName = resultSet.getString(4);
                String email = resultSet.getString(5);
                String phoneNumber = resultSet.getString(6);
                String streetAddress = resultSet.getString(7);
                String city = resultSet.getString(8);
                String state = resultSet.getString(9);
                String zipCode = resultSet.getString(10);
                String country = resultSet.getString(11);
                userList.add(new Member(userName, password, accountStatus,
                        new Person(fullName, email, phoneNumber, new Address(streetAddress, city, state, zipCode, country))));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }

    public void getAllBookFromDatabase(List<BookItem> bookList) {
        String query = "SELECT BookItems.*, Books.authors, Books.title, Books.publisher, " +
                "Books.NumberOfPages, Books.Language, Books.categories FROM BookItems " +
                "JOIN Books ON Books.ISBN = BookItems.ISBN";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String ISBN = resultSet.getString(1);
                String barcode = resultSet.getString(2);
                LocalDate borrowDate = resultSet.getDate(3) != null ? resultSet.getDate(3).toLocalDate() : null;
                LocalDate dueDate = resultSet.getDate(4) != null ? resultSet.getDate(4).toLocalDate() : null;
                double price = resultSet.getDouble(5);
                BookFormat bookFormat = BookFormat.valueOf(resultSet.getString(6));
                BookStatus bookStatus = BookStatus.valueOf(resultSet.getString(7));
                LocalDate publicationDate = resultSet.getDate(8).toLocalDate();
                String author = resultSet.getString(9);
                String title = resultSet.getString(10);
                String publisher = resultSet.getString(11);
                int numberOfPage = resultSet.getInt(12);
                String language = resultSet.getString(13);
                String categories = resultSet.getString(14);
                BookItem tmp = new BookItem(ISBN, title, categories, publisher, language, numberOfPage,
                        author, barcode, price, bookFormat, bookStatus, publicationDate);
                tmp.setBorrowedDate(borrowDate);
                tmp.setDueDate(dueDate);
                bookList.add(tmp);
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void getAllBookLendingFromDatabase(List<BookLending> bookLendingList) {
        String query = "SELECT * FROM BookLending";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String itemId = resultSet.getString(1);
                LocalDate creationDate = resultSet.getDate(2).toLocalDate();
                LocalDate dueDate = resultSet.getDate(3) != null ? resultSet.getDate(3).toLocalDate() : null;
                LocalDate returnDate = resultSet.getDate(4) != null ? resultSet.getDate(4).toLocalDate() : null;
                String memId = resultSet.getString(5);
                int lendingID = resultSet.getInt(6);
                BookLending temp = new BookLending(itemId, creationDate, dueDate, returnDate, memId, lendingID);
                bookLendingList.add(temp);
            }
        } catch (SQLException e) {
            System.out.println("SQl error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getOverdueBookCountByAccount(List<Member> members) {
        String query = """
                SELECT\s
                    Account.username AS memberID,\s
                    COALESCE(COUNT(CASE WHEN BL.returnDate > BL.dueDate THEN 1 END), 0) AS totalOverdue
                FROM\s
                    Account
                LEFT JOIN\s
                    BookLending BL
                ON\s
                    Account.username = BL.memberID
                GROUP BY\s
                    Account.username;
                """;
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String userName = resultSet.getString(1);
                int overdueBooks = resultSet.getInt(2);
                Member member = new Member(userName, overdueBooks);
                members.add(member);
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    public boolean verifyUserAccount(String username) {
        String query = "UPDATE Account SET accountStatus = 'Active' WHERE username = ?";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            return false;
        }
    }

    public boolean lockUserAccount(String username) {
        String query = "UPDATE Account SET accountStatus = 'Locked' WHERE username = ?";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
            return false;
        }
    }

    public boolean addBook(BookItem bookItem) {
        String insertBook = "INSERT INTO Books (authors, title, publisher, ISBN, " +
                "NumberOfPages, Language, categories, imageLink) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE imageLink = VALUES(imageLink)";

        String insertBookItems = "INSERT INTO BookItems (ISBN, barcode, borrowedDate, dueDate, " +
                "price,format, bookStatus, publicationDate) VALUES (?, ?, ?, ?, ?, ? ,? ,?)";
        List<BookItem> bookItemList = new ArrayList<>();
        searchBookOnline(bookItem.getTitle(), "Title", bookItemList);
        for (BookItem it : bookItemList) {
            if (it.getISBN().equals(bookItem.getISBN()) && it.getImageUrl() != null) {
                bookItem.setImageUrl(it.getImageUrl());
                break;
            }
        }
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement preparedStatement1 = this.connection.prepareStatement(insertBook);
             PreparedStatement preparedStatement2 = this.connection.prepareStatement(insertBookItems)) {

            preparedStatement1.setString(1, bookItem.getAuthorName());
            preparedStatement1.setString(2, bookItem.getTitle());
            preparedStatement1.setString(3, bookItem.getPublisher());
            preparedStatement1.setString(4, bookItem.getISBN());
            preparedStatement1.setInt(5, bookItem.getNumberOfPages());
            preparedStatement1.setString(6, bookItem.getLanguage());
            preparedStatement1.setString(7, bookItem.getSubject());
            preparedStatement1.setString(8, bookItem.getImageUrl());

            preparedStatement2.setString(1, bookItem.getISBN());
            preparedStatement2.setString(2, bookItem.getBarcode());
            preparedStatement2.setDate(3, null);
            preparedStatement2.setDate(4, null);
            preparedStatement2.setDouble(5, bookItem.getPrice());
            preparedStatement2.setString(6, bookItem.getFormat().toString());
            preparedStatement2.setString(7, bookItem.getStatus().toString());
            preparedStatement2.setDate(8, Date.valueOf(bookItem.getPublicationDate()));

            preparedStatement1.addBatch();
            preparedStatement2.addBatch();
            preparedStatement1.executeBatch();
            preparedStatement2.executeBatch();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean updateBook(BookItem newBook) {
        String query = """
                UPDATE BookItems\s
                JOIN Books ON Books.ISBN = BookItems.ISBN
                SET BookItems.borrowedDate = ?,
                    BookItems.dueDate = ?,
                    BookItems.price = ?,
                    BookItems.format = ?,
                    BookItems.bookStatus = ?,
                    BookItems.publicationDate = ?,
                    BookItems.ISBN = ?,
                    Books.authors = ?,
                    Books.title = ?,
                    Books.publisher = ?,
                    Books.NumberOfPages = ?,
                    Books.Language = ?,
                    Books.categories = ?
                WHERE BookItems.barcode = ?;
                """;

        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, newBook.getBorrowedDate() != null ? java.sql.Date.valueOf(newBook.getBorrowedDate()) : null);
            stmt.setDate(2, newBook.getDueDate() != null ? java.sql.Date.valueOf(newBook.getDueDate()) : null);
            stmt.setDouble(3, newBook.getPrice());
            stmt.setString(4, newBook.getFormat().toString());
            stmt.setString(5, newBook.getStatus().toString());
            stmt.setDate(6, newBook.getPublicationDate() != null ? java.sql.Date.valueOf(newBook.getPublicationDate()) : null);
            stmt.setString(7, newBook.getISBN());
            stmt.setString(8, newBook.getAuthorName());
            stmt.setString(9, newBook.getTitle());
            stmt.setString(10, newBook.getPublisher());
            stmt.setInt(11, newBook.getNumberOfPages());
            stmt.setString(12, newBook.getLanguage());
            stmt.setString(13, newBook.getSubject());
            stmt.setString(14, newBook.getBarcode());
            if (stmt.executeUpdate() == 0) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean deleteBook(String barcode) {
        String query = "DELETE FROM BookItems WHERE barcode = '" + barcode + "'";
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public String getTotalMembers() {
        String query = "SELECT * FROM Account WHERE accountStatus IN('Active', 'Admin')";
        int numMem = 0;
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try (PreparedStatement findingStatement = this.connection.prepareStatement(query)) {
            ResultSet resultSet = findingStatement.executeQuery();
            while (resultSet.next()) {
                numMem++;
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return Integer.toString(numMem);
    }

    public String getTotalBooks() {
        String query = "SELECT COUNT(barcode) FROM BookItems";
        int i = 0;
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                i = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Integer.toString(i);
    }

    public String getBorrowedBooks() {
        String query = "SELECT COUNT(barcode) FROM BookItems WHERE bookStatus = 'Loaned'";
        int i = 0;
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                i = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Integer.toString(i);
    }

    public String getOverdueBooks() {
        String query = "SELECT COUNT(barcode) FROM BookItems WHERE dueDate < CURDATE()";
        int i = 0;
        if (connection == null) {
            initializeConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    initializeConnection();
                }
            } catch (SQLException e) {
                System.out.println("Error checking if connection is closed: " + e.getMessage());
                initializeConnection();
            }
        }
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                i = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Integer.toString(i);
    }

    public List<Book> searchBookMember(String inputSearch, TypeOfSearch typeOfSearch) {
        List<Book> resList = new ArrayList<>();
        String filter = switch (typeOfSearch) {
            case Title -> "title";
            case Author -> "authors";
            case ISBN -> "ISBN";
            case Subject -> "categories";
        };
        String query = "SELECT * FROM Books WHERE " + filter + " LIKE '%" + inputSearch + "%'";
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String authors = resultSet.getString(1);
                String title = resultSet.getString(2);
                String publisher = resultSet.getString(3);
                String ISBN = resultSet.getString(4);
                int numberOfPages = resultSet.getInt(5);
                String language = resultSet.getString(6);
                String categories = resultSet.getString(7);
                String imageLink = resultSet.getString(8);
                Book temp = new Book(ISBN, title, categories, publisher, language, numberOfPages, authors, imageLink);
                resList.add(temp);
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return resList;
    }

    // tra ve barcode
    // neu khong co tra ve chuoi "No book available for borrowing."
    public String findingCanBorrowBook(String ISBN) {
        String barcodeResult = "No book available for borrowing.";
        String findingQuery = "SELECT * FROM BookItems WHERE ISBN = '" + ISBN + "' AND bookStatus = 'Available'";
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(findingQuery);
            while (resultSet.next()) {
                if (resultSet.getString(7).equals("Available")) {
                    barcodeResult = resultSet.getString(2);
                    break;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return barcodeResult;
    }

    public void createBookLendingRecord(BookLending bookLending) {
        String query = "INSERT INTO BookLending(itemID, creationDate, dueDate, returnDate, memberID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, bookLending.getItemId());
            preparedStatement.setDate(2, Date.valueOf(bookLending.getCreationDate()));
            preparedStatement.setDate(3, Date.valueOf(bookLending.getDueDate()));
            if (bookLending.getReturnDate() != null) {
                preparedStatement.setDate(4, Date.valueOf(bookLending.getReturnDate()));
            } else {
                preparedStatement.setNull(4, Types.DATE);
            }
            preparedStatement.setString(5, bookLending.getMemberId());

            preparedStatement.addBatch();
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }

    }

    public boolean isBookBorrowedByMember(Member member, Book book) {
        String query = "SELECT * " +
                "FROM BookLending " +
                "JOIN Account ON BookLending.memberID = Account.username " +
                "JOIN BookItems ON BookLending.itemID = BookItems.barcode " +
                "WHERE returnDate IS NULL AND Account.username = ? AND BookItems.ISBN = ?";

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, member.getUsername());
            stmt.setString(2, book.getISBN());
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false; // Mặc định trả về false nếu có lỗi
    }

    public int getTotalBorrowedBookOfMember(Member member) {
        String query = "SELECT COUNT(*)" +
                "FROM BookLending " +
                "WHERE returnDate IS NULL AND memberID = ?";

        int res = 0;
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, member.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public int getTotalOverdueBookOfMember(Member member) {
        String query = "SELECT COUNT(*)" +
                "FROM BookLending " +
                "WHERE (returnDate > dueDate OR (returnDate IS NULL AND dueDate < CURDATE())) " +
                "AND memberID = ?";
        int res = 0;
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, member.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                res = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public List<BookLending> getCurrentlyBorrowedBookOfMember(Member member) {
        String query = "SELECT *" +
                "FROM BookLending " +
                "WHERE returnDate IS NULL AND memberID = ?";
        List<BookLending> bookList = new ArrayList<>();

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, member.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String memberID = rs.getString("memberID");
                String itemID = rs.getString("itemID");
                LocalDate creationDate = rs.getDate("creationDate") != null ? rs.getDate("creationDate").toLocalDate() : null;
                LocalDate dueDate = rs.getDate("dueDate") != null ? rs.getDate("dueDate").toLocalDate() : null;
                LocalDate returnDate = rs.getDate("returnDate") != null ? rs.getDate("returnDate").toLocalDate() : null;
                int lendingId = rs.getInt("lendingID");
                BookLending temp = new BookLending(itemID, creationDate, dueDate, returnDate, memberID, lendingId);
                bookList.add(temp);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return bookList;
    }

    // lay ra nhung ban ghi da tra cua member
    public List<BookLending> getReturnedBookOfMember(Member member) {
        String query = "SELECT *" +
                "FROM BookLending " +
                "WHERE returnDate IS NOT NULL AND memberID = ?";
        List<BookLending> bookList = new ArrayList<>();

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, member.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String memberID = rs.getString("memberID");
                String itemID = rs.getString("itemID");
                LocalDate creationDate = rs.getDate("creationDate").toLocalDate();
                LocalDate dueDate = rs.getDate("dueDate").toLocalDate();
                LocalDate returnDate = rs.getDate("returnDate").toLocalDate();
                int lendingId = rs.getInt("lendingID");
                BookLending temp = new BookLending(itemID, creationDate, dueDate, returnDate, memberID, lendingId);
                bookList.add(temp);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return bookList;
    }

    public List<BookLending> getOverdueBookOfMember(Member member) {
        String query = "SELECT *" +
                "FROM BookLending " +
                "WHERE (returnDate > dueDate OR (returnDate IS NULL AND dueDate < CURDATE())) " +
                "AND memberID = ?";
        List<BookLending> bookList = new ArrayList<>();

        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, member.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String memberID = rs.getString("memberID");
                String itemID = rs.getString("itemID");
                LocalDate creationDate = rs.getDate("creationDate") != null ? rs.getDate("creationDate").toLocalDate() : null;
                LocalDate dueDate = rs.getDate("dueDate") != null ? rs.getDate("dueDate").toLocalDate() : null;
                LocalDate returnDate = rs.getDate("returnDate") != null ? rs.getDate("returnDate").toLocalDate() : null;
                int lendingId = rs.getInt("lendingID");
                BookLending temp = new BookLending(itemID, creationDate, dueDate, returnDate, memberID, lendingId);
                bookList.add(temp);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return bookList;
    }

    public List<Book> getTop5BorrowedBooks() {
        String query = """
                SELECT COUNT(BookLending.itemID) as Total, Books.* FROM BookLending\s
                JOIN BookItems ON BookLending.itemID = BookItems.barcode\s
                JOIN Books ON Books.ISBN = BookItems.ISBN
                GROUP BY ISBN
                ORDER BY Total DESC LIMIT 5\s""";
        List<Book> bookList = new ArrayList<>();
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String authors = resultSet.getString("authors");
                String title = resultSet.getString("title");
                String publisher = resultSet.getString("publisher");
                String ISBN = resultSet.getString("ISBN");
                int numberOfPages = resultSet.getInt("NumberOfPages");
                String language = resultSet.getString("Language");
                String categories = resultSet.getString("categories");
                String imageLink = resultSet.getString("imageLink");
                Book temp = new Book(ISBN, title, categories, publisher, language, numberOfPages, authors, imageLink);
                bookList.add(temp);
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return bookList;

    }

    public void setBookItemStatus(String barcode, BookStatus bookStatus) {
        String query = "UPDATE BookItems SET bookStatus = '" + bookStatus.toString() + "' WHERE barcode = '" + barcode + "'";
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    public Book getBookByBarcode(String barcode) {
        String query = "SELECT Books.*, BookItems.barcode FROM BookItems " +
                "JOIN Books ON Books.ISBN = BookItems.ISBN WHERE barcode = '" + barcode + "'";
        Book temp = new Book();
        try {
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                temp.setISBN(resultSet.getString("ISBN"));
                temp.setLanguage(resultSet.getString("language"));
                temp.setSubject(resultSet.getString("categories"));
                temp.setPublisher(resultSet.getString("publisher"));
                temp.setTitle(resultSet.getString("title"));
                temp.setAuthorName(resultSet.getString("authors"));
                temp.setImageUrl(resultSet.getString("imageLink"));
                temp.setNumberOfPages(resultSet.getInt("NumberOfPages"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return temp;
    }

    public void updateBookLending(BookLending bookLending) {
        String query = "UPDATE BookLending SET dueDate = ? , returnDate = ? WHERE itemId = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setDate(1, Date.valueOf(bookLending.getDueDate()));
            if (bookLending.getReturnDate() != null) {
                preparedStatement.setDate(2, Date.valueOf(bookLending.getReturnDate()));
            } else {
                preparedStatement.setNull(2, java.sql.Types.DATE);
            }
            preparedStatement.setString(3, bookLending.getItemId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    public List<Book> getDataBorrowedForRecommended(String memberID) {
        String query = """
                SELECT DISTINCT
                    Books.authors,
                    Books.title,
                    Books.publisher,
                    Books.NumberOfPages,
                    Books.Language,
                    Books.categories,
                    Books.ISBN,
                    Books.imageLink,
                    MIN(BookItems.format) AS format,
                    CASE
                        WHEN COUNT(BookLending.memberID) > 0 THEN 1
                        ELSE 0
                    END AS borrowed
                FROM
                    Books
                JOIN
                    BookItems
                ON
                    BookItems.ISBN = Books.ISBN
                LEFT JOIN
                    BookLending
                ON
                    BookItems.barcode = BookLending.itemID AND BookLending.memberID = ?
                GROUP BY
                    Books.authors,
                    Books.title,
                    Books.publisher,
                    Books.NumberOfPages,
                    Books.Language,
                    Books.categories
                """;

        List<Book> bookList = new ArrayList<>();
        List<BookRecommendation> recommendations = new ArrayList<>();
        List<Pair<Book, Double>> recommendedBooksWithProbability = new ArrayList<>();

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, memberID);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Đọc dữ liệu từ kết quả truy vấn và tạo Book và BookRecommendation
            while (resultSet.next()) {
                String authors = resultSet.getString("authors");
                String title = resultSet.getString("title");
                String publisher = resultSet.getString("publisher");
                int numberOfPages = resultSet.getInt("NumberOfPages");
                String language = resultSet.getString("Language");
                String categories = resultSet.getString("categories");
                String format = resultSet.getString("format");
                Boolean borrowed = resultSet.getBoolean("borrowed");

                // Tạo đối tượng Book
                Book temp = new Book(resultSet.getString("ISBN"), title, categories, publisher, language,
                        numberOfPages, authors, resultSet.getString("imageLink"));
                bookList.add(temp);

                // Tạo đối tượng BookRecommendation
                BookRecommendation recommendation = new BookRecommendation(
                        authors, title, publisher, numberOfPages, language, categories, format, borrowed
                );
                recommendations.add(recommendation);
            }

            // Xử lý dữ liệu và huấn luyện mô hình
            AuthorHandler authorHandler = new AuthorHandler(recommendations);
            Instances dataSet = prepareData(recommendations, authorHandler);
            Logistic logisticModel = trainAndEvaluateLogisticRegression(dataSet);

            // Dự đoán xác suất cho các sách chưa được mượn và thêm vào danh sách kết hợp
            for (int i = 0; i < recommendations.size(); i++) {
                BookRecommendation book = recommendations.get(i);
                if (!book.getBorrowed()) {
                    double curProbability = predictBorrowedProbability(book, authorHandler, logisticModel);
                    recommendedBooksWithProbability.add(new Pair<>(bookList.get(i), curProbability));
                }
            }

            // Sắp xếp sách theo xác suất giảm dần
            recommendedBooksWithProbability.sort((p1, p2) -> Double.compare(p2.getValue(), p1.getValue()));

            // Trích xuất danh sách sách đã sắp xếp
            List<Book> result = new ArrayList<>();
            for (Pair<Book, Double> pair : recommendedBooksWithProbability) {
                result.add(pair.getKey());
            }

            return result.isEmpty() ? bookList : result;
        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            return bookList;
        }
    }

    public static Database getInstance() {
        if (myDatabase == null) {
            myDatabase = new Database();
        }
        return myDatabase;
    }
}