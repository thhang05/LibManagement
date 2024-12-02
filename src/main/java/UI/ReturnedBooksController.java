package UI;

import database.Database;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import Entities.Book;
import Entities.BookLending;

public class ReturnedBooksController {
    @FXML
    private AnchorPane overlayPane;

    @FXML
    private TableView<BookLending> returnedBooksTableView;

    @FXML
    private TableColumn<BookLending, Integer> columnNo;
    @FXML
    private TableColumn<BookLending, String> columnTitle;
    @FXML
    private TableColumn<BookLending, String> columnAuthorName;
    @FXML
    private TableColumn<BookLending, String> columnISBN;
    @FXML
    private TableColumn<BookLending, String> columnBorrowedDate;
    @FXML
    private TableColumn<BookLending, String> columnStatus;
    @FXML
    private ContextMenu bookActionContextMenu;

    @FXML
    private Label totalReturnedBooksLabel;

    private final ObservableList<BookLending> returnedBookList = FXCollections.observableArrayList();

    private final Map<String, Book> bookCache = new HashMap<>();


    @FXML
    public void showMenu() {
        SceneUtilsController.getInstance().showMenu(overlayPane);
    }

    public void initReturnedBooksTableView() {
        ObservableList<BookLending> list = FXCollections.observableArrayList
                (Database.getInstance().getReturnedBookOfMember(SceneUtils.currentMember));

        Platform.runLater(() -> {
            returnedBookList.setAll(list);
            updateTotalReturnedBooksLabel();
            setupReturnedBooksTableView();
        });

        returnedBooksTableView.setRowFactory(tv -> {
            TableRow<BookLending> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.isSecondaryButtonDown()) {
                    BookLending selectedBook = row.getItem();

                    if (selectedBook != null) {
                        double mouseX = e.getScreenX();
                        double mouseY = e.getScreenY();
                        bookActionContextMenu.show(returnedBooksTableView, mouseX, mouseY);
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
     * Configures the `returnedBooksTableView`
     * to display detailed information about books that have been returned.
     * <p>
     * Functionality:
     * 1. Sets the fixed cell size of the table view to ensure consistent row height.
     * 2. Configures the columns in the table view:
     * - **No**: Displays the row number, automatically calculated based on the index of the row.
     * - **Title**: Displays the title of the returned book, retrieved dynamically
     * using the book's barcode.
     * - **Author Name**: Displays the author's name of the returned book,
     * also fetched using the barcode.
     * - **ISBN**: Displays the ISBN of the returned book or "No ISBN" if unavailable.
     * - **Borrowed Date**: Displays the creation (borrowed) date
     * of the book using a `PropertyValueFactory`.
     * - **Status**: Displays the status of the return,
     * checking if the book was returned on time or if it is overdue.
     * 3. Loads the `returnedBookList` into the table view to populate the data.
     * <p>
     * Implementation Details:
     * - Custom cell factories are used for columns to dynamically retrieve
     * and display book information based on the barcode.
     * - The status column evaluates whether the book was returned on time
     * or overdue by comparing the return date with the due date.
     * <p>
     * Asynchronous Execution:
     * - The configuration runs synchronously on the JavaFX Application Thread
     * (as it's inside the main UI thread).
     * <p>
     * Parameters:
     * None.
     * <p>
     * Preconditions:
     * - The `returnedBookList` observable list must be initialized with data.
     * - The `getBookByBarcode` method must return valid `Book` objects for existing barcodes.
     * <p>
     * PostConditions:
     * - The `returnedBooksTableView` is fully configured
     * and displays the relevant information for returned books.
     */
    public void setupReturnedBooksTableView() {
        returnedBooksTableView.setFixedCellSize(40);
        columnNo.setCellFactory(column -> new TableCell<BookLending, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(String.valueOf(getIndex() + 1));
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
        columnBorrowedDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

        columnStatus.setCellFactory(column -> new TableCell<BookLending, String>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    BookLending bookLending = getTableView().getItems().get(getIndex());
                    if (bookLending.getReturnDate().isAfter(bookLending.getDueDate())) {
                        setText("Overdue");
                    } else {
                        setText("On time");
                    }
                }
            }
        });
        returnedBooksTableView.setItems(returnedBookList);
    }

    @FXML
    public void borrowBook() {
        BookLending selectedBook = returnedBooksTableView.getSelectionModel().getSelectedItem();
        SceneUtilsController.getInstance().borrowBook
                (Database.getInstance().getBookByBarcode(selectedBook.getItemId()));
    }

    public void updateTotalReturnedBooksLabel() {
        totalReturnedBooksLabel.setText("Total: " + returnedBookList.size());
    }

    public ObservableList<BookLending> getReturnedBookList() {
        return returnedBookList;
    }

    public TableView<BookLending> getReturnedBooksTableView() {
        return returnedBooksTableView;
    }

    public Map<String, Book> getBookCache() {
        return bookCache;
    }
}