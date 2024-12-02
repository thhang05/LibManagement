package UI;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import database.Database;
import Status.TypeOfSearch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Popup;
import javafx.util.Duration;

import javax.imageio.ImageIO;

import Entities.Book;
import Entities.BookLending;
import Action.Account.Info;
import Action.Account.ResetPassword;

public class HomeController {

    @FXML
    private AnchorPane overlayPane;

    @FXML
    private TextField searchTextField;

    @FXML
    private MenuButton searchMenuButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button accountButton;

    @FXML
    private ContextMenu accountContextMenu;

    @FXML
    private Button notificationButton;
    private Popup notificationPopup;
    private ScrollPane scrollPane;
    private VBox notificationVbox;

    @FXML
    private HBox popularBookHBox;

    @FXML
    private HBox recommendBookHBox;

    @FXML
    public void showMenu() {
        SceneUtilsController.getInstance().showMenu(overlayPane);
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
        SceneUtilsController.getInstance().searchBook(searchInput, String.valueOf(TypeOfSearch.Subject));
    }


    /**
     * Displays a notification popup near the notification button when called.
     * The popup will be shown if it is not already visible, and will be hidden if it is currently displayed.
     * The popup is displayed based on the position of the notification button on the screen.
     * <p>
     * Functionality:
     * 1. Checks if the `notificationPopup` is null or empty. If so, initializes the popup by calling `initNotificationPopup()`.
     * 2. If the popup is currently visible, it will be hidden.
     * 3. If the popup is not visible, it will be displayed near the `notificationButton`, considering its current position in the window.
     * 4. Adds an event filter for mouse clicks on the scene. If a mouse click happens outside the popup and the button, the popup will be hidden.
     * 5. Ensures that clicking outside the popup or the button closes the popup.
     * <p>
     * The position of the popup is determined by the location of the notification button in the window. The popup will appear slightly below the button.
     * <p>
     * Preconditions:
     * - The `notificationButton` must be a valid and visible button in the scene.
     * - The `notificationPopup` must be properly initialized or will be initialized on the first show call.
     * <p>
     * PostConditions:
     * - The notification popup is shown or hidden depending on its current visibility state.
     * - If the popup is shown, clicking outside the popup or button will hide it.
     */
    public void showNotificationPopup() {
        if (notificationPopup == null || notificationPopup.getContent().isEmpty()) {
            notificationPopup = new Popup();
            initNotificationPopup();
        }

        if (notificationPopup.isShowing()) {
            notificationPopup.hide();
        } else {
            notificationPopup.show(notificationButton,
                    notificationButton.getScene().getWindow().getX()
                            + notificationButton.getLayoutX(),
                    notificationButton.getScene().getWindow().getY()
                            + notificationButton.getLayoutY() + notificationButton.getHeight());

            Scene scene = notificationButton.getScene();
            scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    double mouseX = event.getScreenX();
                    double mouseY = event.getScreenY();

                    boolean isInsidePopup = mouseX >= notificationPopup.getX()
                            && mouseX <= notificationPopup.getX() + notificationPopup.getWidth()
                            && mouseY >= notificationPopup.getY()
                            && mouseY <= notificationPopup.getY() + notificationPopup.getHeight();

                    boolean isInsideButton = mouseX >= notificationButton.localToScreen(notificationButton.getBoundsInLocal()).getMinX()
                            && mouseX <= notificationButton.localToScreen(notificationButton.getBoundsInLocal()).getMaxX()
                            && mouseY >= notificationButton.localToScreen(notificationButton.getBoundsInLocal()).getMinY()
                            && mouseY <= notificationButton.localToScreen(notificationButton.getBoundsInLocal()).getMaxY();

                    if (!isInsidePopup && !isInsideButton) {
                        notificationPopup.hide();
                        scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
                    }
                }
            });
        }
    }


    /**
     * Initializes and configures the notification popup that will be displayed when showing notifications.
     * The method sets up the layout and content of the popup, including the notifications and scrollable area.
     * <p>
     * Functionality:
     * 1. Initializes a new `Popup` object for displaying the notifications.
     * 2. Calls `initNotificationList()` to retrieve a list of notifications that will be shown in the popup.
     * 3. Creates a `VBox` layout to hold the notifications, with a 10px spacing between each notification.
     * 4. Iterates over the notifications, creating a `Text` object for each one, and adds it to the `VBox` in a `TextFlow` container.
     * 5. Sets the wrapping width of the text to ensure proper display in the available space.
     * 6. Creates a `ScrollPane` to contain the `VBox` and allows for scrolling of notifications if necessary.
     * 7. Configures the `ScrollPane` with a fixed width, dynamic height, and scrollbars that appear as needed.
     * 8. Adds the `ScrollPane` to the `Popup` content, ensuring that notifications are properly displayed and scrollable if there are many notifications.
     * <p>
     * Preconditions:
     * - `initNotificationList()` must return a list of notification strings that will be shown in the popup.
     * - `notificationPopup` must be properly initialized and ready to display content.
     * <p>
     * PostConditions:
     * - The notification popup is fully initialized with the notifications and layout, ready to be displayed.
     */
    public void initNotificationPopup() {
        notificationPopup = new Popup();
        ObservableList<String> notificationsObservableList = initNotificationList();
        notificationVbox = new VBox(10);
        for (String notification : notificationsObservableList) {
            Text text = new Text(notification);
            text.wrappingWidthProperty().set(200);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setMaxWidth(200);
            notificationVbox.getChildren().add(textFlow);
        }

        scrollPane = new ScrollPane(notificationVbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setPrefWidth(200);
        scrollPane.setMaxHeight(100);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        notificationPopup.getContent().add(scrollPane);

    }


    /**
     * Initializes the list of notifications for the current member based on their borrowed books and due dates.
     * It generates notifications for overdue books or books that are due soon, and adds them to an observable list
     * for display in the notification popup.
     * <p>
     * Functionality:
     * 1. Retrieves the list of currently borrowed books for the current member from the database.
     * 2. Creates a map to store notifications, where the key is the notification message and the value is the number of remaining days.
     * 3. Iterates through the borrowed books and calculates the remaining days until each book's due date.
     * 4. If the book is overdue, it generates a notification indicating how many days overdue the book is.
     * 5. If the book is due within the next 2 days, it generates a notification reminding the member to return the book soon.
     * 6. Sorts the notifications by the remaining days (in ascending order) so that the most urgent notifications come first.
     * 7. Adds a default welcome message ("Welcome to the library!") at the beginning of the notification list.
     * 8. Returns an observable list of notifications to be displayed in the UI.
     * <p>
     * Preconditions:
     * - The `Database.getInstance().getCurrentlyBorrowedBookOfMember()` method must return a list of books currently borrowed by the member.
     * - Each book lending must have a due date set for accurate calculation of overdue and soon-to-be-due books.
     * <p>
     * PostConditions:
     * - An observable list of notification messages is returned, sorted by urgency (overdue and soon-to-be-due books).
     * - The list is ready to be displayed in the notification popup.
     *
     * @return An observable list of notifications for the current member, including overdue book notifications and due date reminders.
     */
    public ObservableList<String> initNotificationList() {
        List<BookLending> bookLendingList = Database.getInstance().getCurrentlyBorrowedBookOfMember(SceneUtils.currentMember);
        Map<String, Integer> notificationsMap = new HashMap<>();

        for (BookLending bookLending : bookLendingList) {
            long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), bookLending.getDueDate());
            Book book = Database.getInstance().getBookByBarcode(bookLending.getItemId());
            String notification;

            if (remainingDays < 0) {
                notification = "The book '" + book.getTitle() + "' you borrowed is " + (remainingDays)
                        + " days overdue. Please return it!";
                notificationsMap.put(notification, (int) remainingDays);
            } else if (remainingDays <= 2) {
                notification = "The book '" + book.getTitle() + "' is due in " + remainingDays
                        + " days. Please return it by the due date.";
                notificationsMap.put(notification, (int) remainingDays);
            }
        }

        List<Map.Entry<String, Integer>> notificationEntries = new ArrayList<>(notificationsMap.entrySet());
        notificationEntries.sort(Comparator.comparingInt(Map.Entry::getValue));

        ObservableList<String> notifications = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : notificationEntries) {
            notifications.add(entry.getKey());
        }
        notifications.addFirst("Welcome to the library!");
        return notifications;
    }

    @FXML
    public void showAccountContextMenu() {
        accountContextMenu.show(accountButton, Side.BOTTOM, 0, 0);
    }

    @FXML
    public void logOut() {
        if (SceneUtils.ownerStage != null) {
            SceneUtils.closeMemberStage();
            SceneUtils.ownerStage.show();
        }
        if (SceneUtils.ownerStage == null) {
            SceneUtils.closeMemberStage();
            SceneUtils.ownerStage.close();
        }
        this.popularBookHBox.getChildren().clear();
        this.recommendBookHBox.getChildren().clear();
        this.notificationVbox.getChildren().clear();
        this.notificationPopup.getContent().clear();

        OnLoanBooksController onLoanBooksController = (OnLoanBooksController)
                SceneUtilsController.getInstance().getController("onLoanBooks");
        if (onLoanBooksController != null) {
            onLoanBooksController.getOnLoanBookList().clear();
            onLoanBooksController.getBookCache().clear();
        }
        ReturnedBooksController returnedBooksController = (ReturnedBooksController)
                SceneUtilsController.getInstance().getController("returnedBooks");
        if (returnedBooksController != null) {
            returnedBooksController.getReturnedBookList().clear();
            returnedBooksController.getBookCache().clear();
        }
    }

    @FXML
    public void showAccountInfo() {
        Info info = new Info(SceneUtils.memberStage, SceneUtils.currentMember, usernameLabel);
        info.showInfo();
        usernameLabel.setText(SceneUtils.currentMember.getUsername());
    }

    @FXML
    public void resetPassword() {
        ResetPassword resetPassword = new ResetPassword(SceneUtils.memberStage, SceneUtils.currentMember);
        resetPassword.showResetPasswordForm();
    }

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public void setupPopularBookHBox() {
        List<Book> bookList = Database.getInstance().getTop5BorrowedBooks();
        setupBoxForHome(bookList, popularBookHBox);
    }

    public void setupRecommendHBox() {
        List<Book> bookList = Database.getInstance().getDataBorrowedForRecommended(SceneUtils.currentMember.getUsername());
        setupBoxForHome(bookList, recommendBookHBox);
    }


    /**
     * Sets up an HBox with buttons representing books, where each button includes the book's image and title.
     * Loads images for the books asynchronously using a fixed thread pool to avoid UI blocking.
     *
     * @param bookList The list of books to display in the HBox.
     * @param hBox     The HBox container to add the buttons to.
     */
    public void setupBoxForHome(List<Book> bookList, HBox hBox) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (Book book : bookList) {
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
                Platform.runLater(() -> hBox.getChildren().add(bookButton));
            });

            loadImageTask.setOnFailed(event -> {
                System.out.println("Error loading images for book " + book.getTitle());
            });
            executorService.submit(loadImageTask);
        }
    }


    /**
     * Creates and configures a button to display information about a book.
     * The button will display the book's title and an image representing the book.
     *
     * @param book  The book whose information will be displayed.
     * @param image The image representing the book.
     * @return A Button that displays the book's title and image.
     */
    public Button settingBookButton(Book book, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPickOnBounds(true);
        Button bookButton = new Button(book.getTitle(), imageView);
        bookButton.setContentDisplay(ContentDisplay.TOP);
        bookButton.setOnAction(event -> {
            BookInfo.getInstance(SceneUtils.memberStage).showBookInfoStage(book, true);
        });

        return bookButton;
    }

    /**
     * The scannerQR method allows the user to select an image file containing a QR code
     * and decodes its content. If the decoding is successful and the content includes an
     * ISBN, the method searches for the book by ISBN and displays its information.
     * <p>
     * - If decoding fails or the content is invalid, an error message is displayed.
     * - Uses the ZXing library to process and decode QR codes.
     * <p>
     * Functionality:
     * 1. Opens a file chooser for the user to select a PNG image file.
     * 2. Reads the selected file and processes it to extract QR code content.
     * 3. If the content includes ISBN: , it extracts the ISBN and searches the database.
     * 4. Displays book information if the book is found.
     * 5. Shows an alert dialog for errors such as decoding failures or invalid QR content.
     * <p>
     * Exceptions:
     * - Displays an alert for IO errors or if the QR code cannot be decoded.
     */
    @FXML
    public void scannerQR() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG files",
                "*.png"));
        File file = fileChooser.showOpenDialog(SceneUtils.memberStage);

        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
                Result result = new MultiFormatReader().decode(bitmap);
                String content = result.getText();
                if (content == null) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Error: Cannot decode qr!");
                    alert.show();
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> alert.close());
                    pause.play();
                } else {
                    String[] lines = content.split("\n");
                    String ISBN;
                    for (String line : lines) {
                        if (line.contains("ISBN: ")) {
                            ISBN = line.split("ISBN: ")[1].trim();
                            List<Book> book = Database.getInstance().searchBookMember(ISBN, TypeOfSearch.ISBN);
                            BookInfo.getInstance(SceneUtils.memberStage).showBookInfoStage(book.getFirst(),
                                    false);
                        }
                    }
                }
            } catch (NotFoundException | IOException e) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Error: Failed to read image or Cannot find QR!");
                alert.show();
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(event -> alert.close());
                pause.play();
            }
        }
    }

    public void addNotification(String notification) {
        Label newLabel = new Label(notification);
        newLabel.setWrapText(true);
        newLabel.setMaxWidth(200);

        notificationVbox.getChildren().addFirst(newLabel);
    }

    public void initHomeScene() {
        searchTextField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                Node focusOwner = searchTextField.getScene().getFocusOwner();
                if (focusOwner != searchMenuButton) {
                    searchTextField.clear();
                }
            }
        });
    }

    public HBox getRecommendBookHBox() {
        return recommendBookHBox;
    }
}