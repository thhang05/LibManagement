package UI;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MenuController {
    @FXML
    private VBox menuBox;

    @FXML
    public void removeMenuBox() {
        if (menuBox.getParent() instanceof Pane parentPane) {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(1), parentPane);
            transition.setFromX(parentPane.getTranslateX());
            transition.setToX(parentPane.getTranslateX() - menuBox.getPrefWidth());

            AnchorPane overlayPane = (AnchorPane) parentPane.lookup("#overlayPane");
            transition.setOnFinished(event -> {
                SceneUtilsController.getInstance().toggleOverlay(overlayPane, false);
                menuBox.lookup("#removeMenuButton").setVisible(false);
            });
            transition.play();
        }
    }

    @FXML
    public void showHomeScene() {
        SceneUtilsController.getInstance().switchScene("home");
    }

    @FXML
    public void showOnLoanBooksScene() {
        SceneUtilsController.getInstance().switchScene("onLoanBooks");
    }

    public void showReturnedBooksScene() {
        SceneUtilsController.getInstance().switchScene("returnedBooks");
    }

}