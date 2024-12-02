package UI;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import Entities.Book;

public class BookInfoController {
    @FXML
    AnchorPane bookInfoPane;

    @FXML
    Label titleLabel;
    @FXML
    Label ISBNLabel;
    @FXML
    Label authorNameLabel;
    @FXML
    Label languageLabel;
    @FXML
    Label publisherLabel;
    @FXML
    Label numberOfPagesLabel;
    @FXML
    Label subjectLabel;

    @FXML
    ImageView qrImageView;

    private boolean showQR;

    public void showBookInfo(boolean showQR) {
        titleLabel.setText(BookInfo.getCurrentBook().getTitle());
        ISBNLabel.setText("ISBN: " + BookInfo.getCurrentBook().getISBN());
        authorNameLabel.setText("Author: " + BookInfo.getCurrentBook().getAuthorName());
        languageLabel.setText("Language: " + BookInfo.getCurrentBook().getLanguage());
        publisherLabel.setText("Publisher: " + BookInfo.getCurrentBook().getPublisher());
        numberOfPagesLabel.setText("Number Of Pages: " + BookInfo.getCurrentBook().getNumberOfPages());
        subjectLabel.setText("Subject: " + BookInfo.getCurrentBook().getSubject());

        if (showQR) {
            qrImageView.setImage(generateQRCodeImage(BookInfo.getCurrentBook()));
        } else {
            String imageUrl = BookInfo.getCurrentBook().getImageUrl();
            if (imageUrl == null) {
                imageUrl = Objects.requireNonNull(getClass().getResource("/image/librarian/default_book_cover.png")).toExternalForm();
            }
            try {
                Image image = new Image(imageUrl);
                qrImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
        this.showQR = showQR;
    }

    @FXML
    public void borrowBook() {
        SceneUtilsController.getInstance().borrowBook(BookInfo.getCurrentBook());
    }


    /**
     * The saveQR method allows the user to save the QR code image
     * as a PNG file to a specified location. If the QR code has not been generated yet,
     * it generates the QR code for the current book before proceeding.
     * <p>
     * Functionality:
     * 1. Checks if the QR code needs to be generated for the current book.
     * If not already generated, it invokes the `generateQRCodeImage` method.
     * 2. Opens a file chooser dialog for the user to specify the save location and file name.
     * 3. Reads the QR code image from the `qrImageView`, processes its pixel data,
     * and creates a `BufferedImage`.
     * 4. Writes the image data to the specified file in PNG format.
     * <p>
     * Exception Handling:
     * - Catches `IOException` if there is an error during file writing and prints the error message.
     * <p>
     * Preconditions:
     * - The `qrImageView` must contain a valid QR code image.
     * <p>
     * Dependencies:
     * - Relies on JavaFX classes (`Image`, `PixelReader`, etc.) for image manipulation.
     * - Uses `javax.imageio.ImageIO` to write the PNG file.
     */
    @FXML
    public void saveQR() {
        if (!showQR) {
            generateQRCodeImage(BookInfo.getCurrentBook());
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Qr at:");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG files",
                "*.png"));
        File file = fileChooser.showSaveDialog(BookInfo.getBookInfoStage());

        if (file != null) {
            try {
                Image qrImage = qrImageView.getImage();
                int qrImageWidth = (int) qrImage.getWidth();
                int qrImageHeight = (int) qrImage.getHeight();

                PixelReader pixelReader = qrImage.getPixelReader();
                WritableImage writableImage = new WritableImage(pixelReader, qrImageWidth, qrImageHeight);
                BufferedImage bufferedImage =
                        new BufferedImage(qrImageWidth, qrImageHeight, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < qrImageWidth; x++) {
                    for (int y = 0; y < qrImageHeight; y++) {
                        bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
                    }
                }
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException e) {
                System.err.println("The error when saving the image:" + e.getMessage());
            }
        }
    }

    public void exitBookInfoStage(ActionEvent event) {
        Stage bookInfoStage = (Stage) bookInfoPane.getScene().getWindow();
        bookInfoStage.close();
    }


    /**
     * Generates a QR code image representing the information of a given book.
     * <p>
     * Functionality:
     * 1. Retrieves the book information as a string using `book.getInfo()`.
     * 2. Encodes the string into a QR code using the ZXing library (`QRCodeWriter`).
     * 3. Converts the QR code data (`BitMatrix`)
     * into a `WritableImage` to be displayed in a JavaFX application.
     * <p>
     * Parameters:
     *
     * @param book the `Book` object containing the information to encode in the QR code.
     *             <p>
     *             Returns:
     * @return a `WritableImage` containing the QR code for the given book,
     * with dimensions 150x150 pixels.
     * <p>
     * Exception Handling:
     * - Catches `WriterException` if the QR code generation fails, and logs the error to the console.
     * <p>
     * Implementation Details:
     * - Iterates through the `BitMatrix` to determine pixel colors (black or white).
     * - Uses a `PixelWriter` to write the QR code data into a `WritableImage`.
     * <p>
     * Dependencies:
     * - ZXing library (`QRCodeWriter`, `BitMatrix`, `WriterException`) for QR code generation.
     * - JavaFX classes (`WritableImage`, `PixelWriter`, `Color`) for image creation and manipulation.
     * <p>
     * Preconditions:
     * - The `Book` object must contain valid information in its `getInfo()` method.
     * - The method assumes a fixed QR code size of 150x150 pixels.
     */
    public WritableImage generateQRCodeImage(Book book) {
        String bookInfo = book.getInfo();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(bookInfo, BarcodeFormat.QR_CODE, 150, 150);
        } catch (WriterException e) {
            System.err.println("Error generating QR Code: " + e.getMessage());
        }

        WritableImage image = new WritableImage(150, 150);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int x = 0; x < 150; x++) {
            for (int y = 0; y < 150; y++) {
                assert bitMatrix != null;
                boolean black = bitMatrix.get(x, y);
                pixelWriter.setColor(x, y, black ? Color.BLACK : Color.WHITE);
            }
        }
        return image;
    }

    public AnchorPane getBookInfoPane() {
        return bookInfoPane;
    }
}