package UI;

import Status.TypeOfSearch;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import Entities.Book;

public class SearchController {
    @FXML
    private AnchorPane resultForSearchPane;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label searchResultLabel;

    @FXML
    private ScrollPane bookListPane;

    @FXML
    private FlowPane bookListFlowPane;

    @FXML
    private Label noBooksLabel;

    public void setSearchResultLabel(String searchInput) {
        searchResultLabel.setText("Results for: " + searchInput);
    }

    @FXML
    public void exitStage(ActionEvent event) {
        event.consume();
        Stage resultForSearchStage = (Stage) resultForSearchPane.getScene().getWindow();
        resultForSearchStage.close();
    }


    /**
     * Displays a list of books in the `bookListFlowPane`.
     * Each book is represented by a button with an image and title.
     * If the list of books is empty, a message is shown to indicate no books are available.
     * <p>
     * Functionality:
     * 1. Checks if the provided list of books is empty.
     * If so, hides the book list pane and shows a "no books" label.
     * 2. If the list is not empty, hides the "no books" label
     * and clears the previous content in the book list flow pane.
     * 3. Creates a fixed thread pool with 4 threads to asynchronously load images for each book.
     * 4. For each book:
     * - Retrieves the book's image URL. If the URL is null, a default image URL is used.
     * - A background task (`Task<Image>`) is created to load the image asynchronously.
     * - Once the image is successfully loaded, a button is created for the book
     * and added to the `bookListFlowPane` on the JavaFX application thread using `Platform.runLater`.
     * 5. If an error occurs while loading an image, an error message is printed to the console.
     * 6. Ensures that the thread pool is shut down when the search stage is closed
     * by using `Search.getResultForSearchStage().setOnCloseRequest`.
     * <p>
     * Asynchronous Execution:
     * - The image loading for each book is done asynchronously in background threads
     * to prevent blocking the main UI thread.
     * - Once the image is loaded, the corresponding button for the book is added
     * to the UI in the main thread.
     * <p>
     * Parameters:
     * - `books`: A list of `Book` objects to be displayed.
     * <p>
     * Preconditions:
     * - The `bookListFlowPane` and `bookListPane` must be initialized and present in the UI.
     * - The `books` list must be non-null.
     * - Each `Book` object must have a valid image URL or use a default image URL if it's null.
     * <p>
     * PostConditions:
     * - The books are displayed as buttons in the flow pane with their images and titles.
     * - If the list is empty, a message indicating no books are available is shown.
     * - The background threads are properly managed and shut down when the search stage is closed.
     * <p>
     * Exceptions:
     * - If the image URL for a book is invalid or the image cannot be loaded,
     * an error message is logged to the console.
     */
    public void showBookList(List<Book> books) {
        if (books.isEmpty()) {
            bookListPane.setVisible(false);
            noBooksLabel.setVisible(true);
        } else {
            noBooksLabel.setVisible(false);
            bookListFlowPane.getChildren().clear();
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            for (Book book : books) {
                String imageUrl = book.getImageUrl();
                if (imageUrl == null) {
                    imageUrl = Objects.requireNonNull(getClass().getResource("/image/librarian/default_book_cover.png")).toExternalForm();
                }
                String finalImageUrl = imageUrl;
                Task<Image> loadImageTask = new Task<Image>() {
                    @Override
                    protected Image call() throws Exception {
                        return new Image(finalImageUrl);
                    }
                };

                loadImageTask.setOnSucceeded(event -> {
                    Image image = loadImageTask.getValue();
                    Button bookButton = settingBookButton(book, image);
                    Platform.runLater(() -> bookListFlowPane.getChildren().add(bookButton));
                });

                loadImageTask.setOnFailed(event -> {
                    System.err.println("Error loading images for book " + book.getTitle());
                });

                executorService.submit(loadImageTask);
            }
            Search.getResultForSearchStage().setOnCloseRequest(event -> executorService.shutdown());
            bookListPane.setVisible(true);
        }
    }


    /**
     * Creates and configures a button for displaying a book, including its image and title.
     * The button is used to display information about the book when clicked.
     * <p>
     * Functionality:
     * 1. Creates an `ImageView` to display the book's image with a specified width and height.
     * 2. Creates a `Button` that displays the book's title and the `ImageView` above it.
     * 3. Sets the preferred width of the button to 125 pixels
     * and aligns the content (title and image) to be displayed on top of the button.
     * 4. Adds an action listener to the button: when clicked,
     * it opens a detailed view of the book using the `BookInfo` instance.
     * <p>
     * Parameters:
     * - `book`: The `Book` object representing the book that the button will display.
     * - `image`: The `Image` object representing the image of the book to be shown on the button.
     * <p>
     * Returns:
     * - A configured `Button` object that displays the book's title and image,
     * and opens a detailed book view on click.
     * <p>
     * Preconditions:
     * - The `book` parameter must be a valid `Book` object containing at least a title and an image.
     * - The `image` parameter must be a valid `Image` object representing the book's cover.
     * <p>
     * PostConditions:
     * - A button is created and configured with the book's title and image,
     * and an action is set to show the book's detailed information when clicked.
     */
    public Button settingBookButton(Book book, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(125);
        imageView.setFitHeight(150);
        imageView.setPickOnBounds(true);
        Button bookButton = new Button(book.getTitle(), imageView);
        bookButton.setPrefWidth(125);
        bookButton.setContentDisplay(ContentDisplay.TOP);

        bookButton.setOnAction(event -> {
            BookInfo.getInstance(Search.getResultForSearchStage()).showBookInfoStage(book, true);
        });

        return bookButton;
    }

    @FXML
    public void searchByTitle() {
        String searchInput = searchTextField.getText();
        SceneUtilsController.getInstance().searchBook(searchInput, String.valueOf(TypeOfSearch.Title));
    }

    @FXML
    public void searchByISBN() {
        String searchInput = searchTextField.getText();
        SceneUtilsController.getInstance().searchBook(searchInput, String.valueOf(TypeOfSearch.ISBN));
    }

    @FXML
    public void searchByAuthor() {
        String searchInput = searchTextField.getText();
        SceneUtilsController.getInstance().searchBook(searchInput, String.valueOf(TypeOfSearch.Author));
    }

    @FXML
    public void searchBySubject() {
        String searchInput = searchTextField.getText();
        SceneUtilsController.getInstance().searchBook(searchInput,
                String.valueOf(TypeOfSearch.Subject));
    }

    public AnchorPane getResultForSearchPane() {
        return resultForSearchPane;
    }

    public void resetSearchTextField() {
        searchTextField.clear();
    }
}