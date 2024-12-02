package UI;

import database.Database;
import Status.BookStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import Entities.Book;
import Entities.BookItem;
import Entities.Member;
import Entities.BookLending;

public class SceneUtilsController {
    private static SceneUtilsController SceneUtilsControllerInstances;
    private final Database database = Database.getInstance();
    private Scene currentScene;

    private final int SCENE_WIDTH = 800;
    private final int SCENE_HEIGHT = 600;


    private final HashMap<String, Scene> scenes = new HashMap<>();
    private final HashMap<String, Object> controllers = new HashMap<>();
    private VBox menuBox;


    private SceneUtilsController() {
        initMenuBox();
        currentScene = initScene("home");
        initScene("onLoanBooks");
        initScene("returnedBooks");

        ((Pane) currentScene.getRoot()).getChildren().add(menuBox);
        menuBox.setTranslateX(-menuBox.getPrefWidth());
        HomeController controller = (HomeController) this.getController("home");
        controller.initHomeScene();
    }

    public static SceneUtilsController getInstance() {
        if (SceneUtilsControllerInstances == null) {
            SceneUtilsControllerInstances = new SceneUtilsController();
        }
        return SceneUtilsControllerInstances;
    }

    public void switchScene(String nameScene) {
        if (currentScene == scenes.get(nameScene)) {
            return;
        }
        resetCurrentScene();
        Scene newScene = scenes.get(nameScene);

        ((Pane) currentScene.getRoot()).getChildren().remove(menuBox);

        Stage stage = (Stage) currentScene.getWindow();

        currentScene = newScene;

        Pane newRoot = (Pane) currentScene.getRoot();
        newRoot.getChildren().add(menuBox);

        menuBox.setTranslateX(-menuBox.getPrefWidth());

        menuBox.lookup("#removeMenuButton").setVisible(false);
        stage.setScene(newScene);
    }

    public void showMenu(AnchorPane overlayPane) {
        menuBox.setVisible(true);
        menuBox.setTranslateX(-menuBox.getPrefWidth());
        TranslateTransition transition =
                new TranslateTransition(Duration.seconds(1), currentScene.getRoot());
        transition.setFromX(0);
        transition.setToX(menuBox.getPrefWidth());

        toggleOverlay(overlayPane, true);
        menuBox.lookup("#removeMenuButton").setVisible(true);

        transition.play();
    }

    public Scene initScene(String nameScene) {
        Scene scene = null;
        try {
            FXMLLoader paneLoader =
                    new FXMLLoader(getClass().getResource("/fxml/member/" + nameScene + ".fxml"));
            Pane pane = paneLoader.load();
            scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);
            scene.getStylesheets().add("/userInterface.css");
            this.scenes.put(nameScene, scene);

            Object controller = paneLoader.getController();
            controllers.put(nameScene, controller);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scene;
    }

    public void resetCurrentScene() {
        Pane currentPane = (Pane) currentScene.getRoot();
        currentPane.setTranslateX(0);
        AnchorPane overlayPane = (AnchorPane) currentPane.lookup("#overlayPane");
        SceneUtilsController.getInstance().toggleOverlay(overlayPane, false);
        menuBox.lookup("#removeMenuButton").setVisible(false);
    }

    public void initMenuBox() {
        try {
            FXMLLoader menuLoader =
                    new FXMLLoader(getClass().getResource("/fxml/member/menu.fxml"));
            menuBox = menuLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void toggleOverlay(AnchorPane overlayPane, boolean showOverlayPane) {
        overlayPane.setDisable(showOverlayPane);

        Button button = (Button) overlayPane.lookup("#menuButton");
        if (button != null) {
            button.setVisible(!showOverlayPane);
        }
    }

    /**
     * Handles the process of borrowing a book and provides feedback to the user.
     * <p>
     * Functionality:
     * 1. Displays an alert if the current member is already borrowing the specified book.
     * 2. Checks whether the member has reached the maximum limit of borrowed books.
     * 3. Checks if the member has exceeded the maximum number of overdue books allowed.
     * 4. Verifies the availability of the book to be borrowed.
     * 5. Creates a new book lending record if conditions are met.
     * 6. Updates the on-loan book list and refreshes the view to reflect the newly borrowed book.
     * 7. Clears and reinitialized the recommended book section in the main screen.
     * 8. Shows a success message in an alert if the book is borrowed successfully.
     * <p>
     * Implementation Details:
     * - The `Database` class is used to query the current member's borrowed books, overdue books,
     * and the availability of books based on their ISBN.
     * - A new `BookLending` object is created to represent the borrowing record.
     * - An alert dialog is displayed to inform the user of the result of their action.
     * - The `PauseTransition` class is used to close the alert after a set duration.
     * <p>
     * Asynchronous Execution:
     * - The configuration of the alert and notification is executed on the JavaFX Application Thread.
     * <p>
     * Parameters:
     * - `Book`: The book to be borrowed, provided as an input parameter.
     * <p>
     * Preconditions:
     * - The `Book` object must contain valid information for the borrowing process.
     * <p>
     * PostConditions:
     * - If conditions are met, the `BookLending` record is created
     * and added to the `onLoanBooksController`.
     * - A notification is added to the `HomeController` indicating the successful borrowing.
     * - The alert is shown to the user with relevant information
     * about the success or failure of the action.
     */
    public void borrowBook(Book book) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        if (Database.getInstance().isBookBorrowedByMember(SceneUtils.currentMember, book)) {
            alert.setContentText("You are borrowing this book.");
        } else if (Database.getInstance().getTotalBorrowedBookOfMember(SceneUtils.currentMember)
                >= Member.MAX_BORROWABLE_BOOK) {
            alert.setContentText("You have borrowed more than "
                    + Member.MAX_BORROWABLE_BOOK + " books. Please return some books!");
        } else if (Database.getInstance().getTotalOverdueBookOfMember(SceneUtils.currentMember)
                >= Member.MAX_OVERDUE_BOOKS_ALLOWED) {
            alert.setContentText("You are only allowed to have 3 overdue books. " +
                    "Please return some books!");
        } else {
            String barcode = Database.getInstance().findingCanBorrowBook(book.getISBN());
            if (barcode.equals("No book available for borrowing.")) {
                alert.setContentText("No book available for borrowing.");
            } else {
                BookLending bookLending = new BookLending(barcode, LocalDate.now(),
                        LocalDate.now().plusDays(BookItem.DEFAULT_BORROW_DAYS),
                        null, SceneUtils.currentMember.getUsername());
                Database.getInstance().createBookLendingRecord(bookLending);
                Database.getInstance().setBookItemStatus(bookLending.getItemId(), BookStatus.Loaned);
                OnLoanBooksController onLoanBooksController =
                        ((OnLoanBooksController) this.getController("onLoanBooks"));
                onLoanBooksController.getOnLoanBookList().add(bookLending);
                onLoanBooksController.updateTotalOnLoanBooksLabel();
                onLoanBooksController.getOnLoanBooksTableView().refresh();

                HomeController homeController = (HomeController) this.getController("home");
                homeController.getRecommendBookHBox().getChildren().clear();
                homeController.setupRecommendHBox();
                alert.setContentText("You have successfully borrowed the book: " + book.getTitle());
                HomeController controller = (HomeController) this.getController("home");
                controller.addNotification("You have successfully borrowed the book: " + book.getTitle());
            }
        }
        Platform.runLater(() -> {
            alert.show();
            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(event -> alert.close());
            pause.play();
        });
    }

    /**
     * Searches for books based on the given search input and filter criteria.
     * <p>
     * Functionality:
     * 1. Checks if the search input is empty or only contains whitespace.
     * If so, displays an alert informing the user that the search field cannot be empty.
     * 2. If the search input is valid, initializes a `Search` instance
     * and displays the search results using the specified filter.
     * 3. The filter can be one of the following: **author**, **title**, **ISBN**, or **subject**.
     * <p>
     * Implementation Details:
     * - If the search input is empty, an `Alert` dialog is shown to the user
     * with a message indicating that the search field is required.
     * - A `PauseTransition` is used to automatically close the alert after 1.5 seconds.
     * - If the search input is valid, a `Search` object is created
     * to handle the search logic and display the results.
     * <p>
     * Asynchronous Execution:
     * - The display of the alert and the interaction with the `Search` instance are executed
     * on the JavaFX Application Thread.
     * <p>
     * Parameters:
     * - `searchInput`: A `String` representing the user's input for the book search.
     * - `filter`: A `String` representing the filter criteria,
     * which can be **author**, **title**, **ISBN**, or **subject**.
     * <p>
     * Preconditions:
     * - The method is called within the JavaFX application, with access to the current scene
     * and `SceneUtilsController`.
     * - The `Search` class should be properly implemented and capable of handling the search
     * and displaying the results.
     * <p>
     * PostConditions:
     * - If the search input is empty, an alert is displayed to inform the user
     * and closes after 1.5 seconds.
     * - If the search input is valid, the search results are displayed based on the given filter.
     */
    public void searchBook(String searchInput, String filter) {
        if (searchInput.trim().isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("The search field cannot be empty!");
            alert.show();

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> alert.close());
            pause.play();
        } else {
            Search results = Search.getInstance(
                    (Stage) SceneUtilsController.getInstance().getCurrentScene().getWindow(),
                    SceneUtils.currentMember);
            results.showResultForSearchStage(searchInput, filter);
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public Object getController(String nameScene) {
        return controllers.get(nameScene);
    }
}