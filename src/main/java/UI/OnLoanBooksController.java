package UI;

import database.Database;
import Status.BookStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import Entities.Book;
import Entities.BookItem;
import Entities.BookLending;


public class OnLoanBooksController {
    @FXML
    private AnchorPane overlayPane;

    @FXML
    private TableView<BookLending> onLoanBooksTableView;
    @FXML
    private TableColumn<BookLending, Integer> columnNo;
    @FXML
    private TableColumn<BookLending, String> columnTitle;
    @FXML
    private TableColumn<BookLending, String> columnAuthorName;
    @FXML
    private TableColumn<BookLending, String> columnISBN;
    @FXML
    private TableColumn<BookLending, String> columnDateBorrowed;
    @FXML
    private TableColumn<BookLending, String> columnRemainingTime;
    @FXML
    private ContextMenu bookActionContextMenu;

    @FXML
    private Label totalOnLoanBooksLabel;

    private final ObservableList<BookLending> onLoanBookList = FXCollections.observableArrayList();
    private final Map<String, Book> bookCache = new HashMap<>();


    @FXML
    public void showMenu() {
        SceneUtilsController.getInstance().showMenu(overlayPane);
    }


    /**
     * Configures the `onLoanBooksTableView`
     * to display detailed information about books currently on loan.
     * <p>
     * Functionality:
     * 1. Sets the fixed cell size of the table view to ensure consistent row height.
     * 2. Configures the columns in the table view:
     * - **No**: Displays the row number, aligned to the center.
     * - **Title**: Fetches and displays the title of the book based on its barcode.
     * - **Author Name**: Fetches and displays the author's name of the book based on its barcode.
     * - **ISBN**: Fetches and displays the ISBN of the book or "No ISBN" if unavailable.
     * - **Date Borrowed**: Displays the borrowing date using a `PropertyValueFactory`.
     * - **Remaining Time**: Calculates and displays the remaining days until the due date,
     * or "Overdue" if past the due date.
     * 3. Loads the `onLoanBookList` into the table view to populate the data.
     * <p>
     * Implementation Details:
     * - Uses custom cell factories for columns to customize the display of book-related information.
     * - Fetches book details (`title`, `authorName`, `ISBN`) dynamically
     * using the barcode from the `BookLending` object.
     * - Calculates the remaining time to the due date using `ChronoUnit.DAYS`
     * and compares it with the current date.
     * <p>
     * Asynchronous Execution:
     * - The configuration is wrapped in a `Platform.runLater` block
     * to ensure execution occurs on the JavaFX Application Thread.
     * <p>
     * Dependencies:
     * - JavaFX classes: `TableCell`, `PropertyValueFactory`, `Platform`, etc.
     * - Java time classes: `LocalDate`, `ChronoUnit`.
     * - The `getBookByBarcode` method is used to fetch book details from a barcode.
     * <p>
     * Parameters:
     * None.
     * <p>
     * Preconditions:
     * - The `onLoanBookList` observable list must be initialized with data.
     * - The `getBookByBarcode` method must return valid `Book` objects for existing barcodes.
     * <p>>
     * PostConditions:
     * - The `onLoanBooksTableView` is fully configured and displays loan information for books.
     */
    public void initOnLoanBooksTableView() {
        ObservableList<BookLending> list = FXCollections.observableArrayList
                (Database.getInstance().getCurrentlyBorrowedBookOfMember(SceneUtils.currentMember));

        onLoanBookList.setAll(list);
        updateTotalOnLoanBooksLabel();
        setupOnLoanBooksTableView();

        onLoanBooksTableView.setRowFactory(tv -> {
            TableRow<BookLending> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.isSecondaryButtonDown()) {
                    BookLending selectedBook = row.getItem();

                    if (selectedBook != null) {
                        double mouseX = e.getScreenX();
                        double mouseY = e.getScreenY();
                        bookActionContextMenu.show(onLoanBooksTableView, mouseX, mouseY);
                    }
                }
            });
            return row;
        });
    }

    private Book getBookByBarcode(String barcode) {
        if (!bookCache.containsKey(barcode)) {
            Book book = Database.getInstance().getBookByBarcode(barcode);
            if (book != null) {
                bookCache.put(barcode, book);
            }
        }
        return bookCache.get(barcode);
    }


    /**
     * Sets up the TableView for displaying the list of books that are currently on loan.
     * This method configures the columns of the table and defines how the data
     * for each column should be displayed.
     * It also sets the cell factory for each column to customize the way data is rendered,
     * including the display of
     * book information (e.g., title, author, ISBN) and remaining time for returning the book.
     * <p>
     * Functionality:
     * 1. The table is configured with fixed cell sizes of 40 pixels for each row.
     * 2. The "No" column is populated with the index of the row,
     * starting from 1, and centers the text.
     * 3. The "Title" column is populated with the title of the book,
     * fetched using the book's barcode.
     * 4. The "Author Name" column is populated with the author's name,
     * fetched using the book's barcode.
     * 5. The "ISBN" column is populated with the book's ISBN, fetched using the book's barcode.
     * 6. The "Date Borrowed" column is populated with the date when the book was borrowed.
     * 7. The "Remaining Time" column calculates
     * and displays the remaining days until the book is due.
     * - If the book is overdue, the text will display "Overdue".
     * - If the book is not overdue, the remaining number of days
     * until the due date will be displayed.
     * 8. The method ensures that the table is populated with data from the `onLoanBookList`.
     * <p>
     * Preconditions:
     * - `onLoanBookList` must be initialized and contain a list of currently borrowed books.
     * - `getBookByBarcode()` must be able to return valid book details (title, author, ISBN)
     * based on the barcode.
     * <p>
     * PostConditions:
     * - The TableView is fully configured with the appropriate data for each column.
     * - The table view is populated with the list of books currently on loan.
     *
     * @see BookLending
     * @see Book
     */
    public void setupOnLoanBooksTableView() {
        onLoanBooksTableView.setFixedCellSize(40);
        Platform.runLater(() -> {
            columnNo.setCellFactory(column -> new TableCell<BookLending, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(String.valueOf(getIndex() + 1));
                        setStyle("-fx-alignment: CENTER;");
                    } else {
                        setText(null);
                    }
                }
            });

            columnTitle.setCellFactory(column -> new TableCell<BookLending, String>() {
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        BookLending bookLending = getTableView().getItems().get(getIndex());
                        String barcode = bookLending.getItemId();
                        Book book = getBookByBarcode(barcode);
                        if (book != null) {
                            setText(book.getTitle());
                        } else {
                            setText("null");
                        }
                    }
                }
            });
            columnAuthorName.setCellFactory(column -> new TableCell<BookLending, String>() {
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        BookLending bookLending = getTableView().getItems().get(getIndex());
                        String barcode = bookLending.getItemId();
                        Book book = getBookByBarcode(barcode);
                        if (book != null) {
                            setText(book.getAuthorName());
                        } else {
                            setText("null");
                        }
                    }
                }
            });
            columnISBN.setCellFactory(column -> new TableCell<BookLending, String>() {
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        BookLending bookLending = getTableView().getItems().get(getIndex());
                        String barcode = bookLending.getItemId();
                        Book book = getBookByBarcode(barcode);
                        if (book != null) {
                            setText(book.getISBN());
                        } else {
                            setText("No ISBN");
                        }
                    }
                }
            });
            columnDateBorrowed.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

            columnRemainingTime.setCellFactory(column -> new TableCell<BookLending, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        BookLending bookLending = getTableView().getItems().get(getIndex());
                        LocalDate dueDate = bookLending.getDueDate();
                        int remainingDays = (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
                        setText(remainingDays >= 0 ? String.valueOf(remainingDays) : "Overdue");
                    } else {
                        setText(null);
                    }
                }
            });
        });

        onLoanBooksTableView.setItems(onLoanBookList);
    }

    public void updateTotalOnLoanBooksLabel() {
        totalOnLoanBooksLabel.setText("Total: " + onLoanBookList.size());
    }


    /**
     * Handles the process of returning a book, updating its status,
     * and reflecting the changes in the system.
     * <p>
     * Functionality:
     * 1. Retrieves the selected `BookLending` object from the `onLoanBooksTableView`.
     * 2. Sets the return date of the selected book to the current date.
     * 3. Updates the book's status in the database to `Available`,
     * indicating it is no longer on loan.
     * 4. Removes the returned book from the list of books currently on loan (`onLoanBookList`).
     * 5. Updates the label showing the total number of books currently on loan.
     * 6. Refreshes the table view to reflect the updated list.
     * 7. Displays an alert notifying the user that the book has been successfully returned.
     * 8. Sends a notification to the home screen indicating which book was returned.
     * 9. Adds the returned book to the `ReturnedBooksController`
     * for further processing, including updating
     * the list of returned books and the total count.
     * 10. Refreshes the returned books table view in the `ReturnedBooksController`.
     * 11. Updates the book lending record in the database to reflect the return of the book.
     * <p>
     * Asynchronous Execution:
     * - Displays the success alert using `Platform.runLater`
     * to ensure it runs on the JavaFX Application Thread.
     * - The alert is shown for 3 seconds before being automatically closed.
     * <p>
     * Parameters:
     * None.
     * <p>
     * Preconditions:
     * - A book must be selected in the `onLoanBooksTableView`.
     * - The `Database.getInstance()` must be properly initialized and connected.
     * <p>
     * PostConditions:
     * - The selected book is marked as returned, its status is updated,
     * and the system reflects these changes.
     * - Notifications and updates are sent to relevant controllers (home and returned books).
     * - The tables are refreshed to show the most up-to-date data.
     */
    @FXML
    public void returnBook() {
        BookLending selectedBook = onLoanBooksTableView.getSelectionModel().getSelectedItem();
        selectedBook.setReturnDate(LocalDate.now());
        String barcode = selectedBook.getItemId();
        Database.getInstance().setBookItemStatus(barcode, BookStatus.Available);
        onLoanBookList.remove(selectedBook);
        updateTotalOnLoanBooksLabel();
        onLoanBooksTableView.refresh();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("You have successfully returned the book.");
        HomeController homeController = ((HomeController)
                SceneUtilsController.getInstance().getController("home"));
        homeController.addNotification("You have successfully returned the book: "
                + Database.getInstance().getBookByBarcode(barcode).getTitle());
        ReturnedBooksController returnedBooksController = (ReturnedBooksController)
                SceneUtilsController.getInstance().getController("returnedBooks");
        returnedBooksController.getReturnedBookList().add(selectedBook);
        returnedBooksController.updateTotalReturnedBooksLabel();
        returnedBooksController.getReturnedBooksTableView().refresh();
        Platform.runLater(() -> {
            alert.show();
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> alert.close());
            pause.play();
        });
        Database.getInstance().updateBookLending(selectedBook);
    }


    /**
     * Handles the process of renewing a book loan, updating the due date
     * and reflecting the changes in the system.
     * <p>
     * Functionality:
     * 1. Retrieves the selected `BookLending` object from the `onLoanBooksTableView`.
     * 2. Checks if the book is overdue by comparing its current due date with the current date.
     * 3. If the book is overdue, an alert is displayed informing the user that
     * the book cannot be renewed and must be returned.
     * 4. If the book is not overdue, the method adds a default number of days
     * to the current due date, renewing the loan.
     * 5. Updates the due date of the `BookLending` object
     * and saves the updated object to the database.
     * 6. Updates the `onLoanBookList` with the renewed book lending
     * and refreshes the table view to reflect the changes.
     * 7. Displays an alert notifying the user of the successful renewal, showing the new due date.
     * <p>
     * Asynchronous Execution:
     * - The success or failure alert is displayed using `Platform.runLater`
     * to ensure it runs on the JavaFX Application Thread.
     * - The alert is shown for 3 seconds before being automatically closed.
     * <p>
     * Parameters:
     * None.
     * <p>
     * Preconditions:
     * - A book must be selected in the `onLoanBooksTableView`.
     * - The `Database.getInstance()` must be properly initialized and connected.
     * - The selected `BookLending` object must have a valid due date.
     * <p>
     * PostConditions:
     * - If the book is not overdue, its due date is extended by a default number of days.
     * - The book's lending record is updated in the database and in the table view.
     * - An alert is shown to the user,
     * confirming the renewal or informing them of the overdue status.
     */
    @FXML
    public void renewBook() {
        BookLending selectedBookLending = onLoanBooksTableView.getSelectionModel().getSelectedItem();
        LocalDate dueDate = selectedBookLending.getDueDate();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        if (!dueDate.isAfter(LocalDate.now())) {
            alert.setContentText("The book is overdue and cannot be renewed. Please return book!");
        } else {
            LocalDate newDueDate = dueDate.plusDays(BookItem.DEFAULT_BORROW_DAYS);
            selectedBookLending.setDueDate(newDueDate);
            Database.getInstance().updateBookLending(selectedBookLending);
            int index = onLoanBookList.indexOf(selectedBookLending);
            if (index != -1) {
                onLoanBookList.set(index, selectedBookLending);
                updateTotalOnLoanBooksLabel();
                onLoanBooksTableView.refresh();
                alert.setContentText("You have successfully renewed the book. " +
                        "You can borrow the book until " + selectedBookLending.getDueDate());
            }
        }
        Platform.runLater(() -> {
            alert.show();
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> alert.close());
            pause.play();
        });
    }

    public ObservableList<BookLending> getOnLoanBookList() {
        return onLoanBookList;
    }

    public TableView<BookLending> getOnLoanBooksTableView() {
        return onLoanBooksTableView;
    }

    public Map<String, Book> getBookCache() {
        return bookCache;
    }
}