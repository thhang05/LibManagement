package main;

import javafx.application.Application;
import javafx.stage.Stage;
import menu.MenuManager;

public class LibApplication extends Application {

  @Override
  public void start(Stage primaryStage) {
    MenuManager menuManager = new MenuManager(primaryStage);
    menuManager.showMenu(primaryStage);
  }
  public static void main(String[] args) {
    launch(args);
  }
}
