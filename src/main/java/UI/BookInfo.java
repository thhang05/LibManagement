package UI;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Book;

public class BookInfo {
    private static BookInfo bookInfoInstance;
    private static Book currentBook;

    private BookInfoController bookInfoController;
    private static Stage bookInfoStage;

    private BookInfo(Stage parentStage) {
        initBookInfoStage(parentStage);
    }

    public static BookInfo getInstance(Stage parentStage) {
        if (bookInfoInstance == null) {
            bookInfoInstance = new BookInfo(parentStage);
        }
        return bookInfoInstance;
    }

    private void initBookInfoStage(Stage parentStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/member/bookInfo.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bookInfoController = loader.getController();

        Scene scene = new Scene(bookInfoController.getBookInfoPane());
        bookInfoStage = new Stage();
        bookInfoStage.setScene(scene);
        bookInfoStage.initStyle(StageStyle.UNDECORATED);
        bookInfoStage.initModality(Modality.WINDOW_MODAL);
        bookInfoStage.initOwner(parentStage);

        bookInfoStage.setOnHidden(event -> {
            event.consume();
            parentStage.getScene().getRoot().setEffect(null);
        });
    }

    public void showBookInfoStage(Book book, boolean showQR) {
        currentBook = book;
        bookInfoController.showBookInfo(showQR);
        if (!bookInfoStage.isShowing()) {
            bookInfoStage.showAndWait();
        }
    }

    public static Book getCurrentBook() {
        return currentBook;
    }

    public static Stage getBookInfoStage() {
        return bookInfoStage;
    }
}