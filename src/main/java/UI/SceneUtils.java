package UI;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Entities.Member;

public class SceneUtils {
    static Stage ownerStage;
    static Stage memberStage;
    static Member currentMember;

    public static void setupStage(Stage stage, Member member) {
        currentMember = member;
        ownerStage = stage;
        Scene scene = SceneUtilsController.getInstance().getCurrentScene();
        HomeController homeController = (HomeController)
                SceneUtilsController.getInstance().getController("home");
        if (memberStage == null) {
            memberStage = new Stage(StageStyle.UNDECORATED);
        }
        memberStage.setScene(scene);

        homeController.setUsernameLabel(currentMember.getUsername());
        homeController.initNotificationPopup();
        homeController.setupPopularBookHBox();
        homeController.setupRecommendHBox();

        OnLoanBooksController onLoanBooksController = (OnLoanBooksController)
                SceneUtilsController.getInstance().getController("onLoanBooks");
        onLoanBooksController.initOnLoanBooksTableView();

        ReturnedBooksController returnedBooksController = (ReturnedBooksController)
                SceneUtilsController.getInstance().getController("returnedBooks");
        returnedBooksController.initReturnedBooksTableView();

        memberStage.show();
    }

    public static void closeMemberStage() {
        memberStage.close();
    }
}