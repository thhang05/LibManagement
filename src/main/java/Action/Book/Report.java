package Action.Book;

import Controller.Book.ReportController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Report {
    private final Stage ownerStage;

    public Report(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    public void showReportForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/librarian/report.fxml"));
            AnchorPane rootPane = loader.load();

            Scene scene = new Scene(rootPane);
            Stage reportStage = new Stage();
            reportStage.setScene(scene);
            reportStage.initStyle(StageStyle.UNDECORATED);
            reportStage.initModality(Modality.WINDOW_MODAL);
            reportStage.initOwner(ownerStage);

            ownerStage.getScene().getRoot().setEffect(new GaussianBlur(10));

            reportStage.setOnHidden(event -> {
                event.consume();
                ownerStage.getScene().getRoot().setEffect(null);
            });

            ReportController reportController = loader.getController();
            reportController.setStage(reportStage);

            rootPane.requestFocus();

            reportStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error loading the report.fxml file: " + e.getMessage());
        }
    }
}
