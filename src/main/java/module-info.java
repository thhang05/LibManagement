module main {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.json;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires mysql.connector.j;
    requires commons.logging;
    requires java.naming;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.apache.commons.codec;
    requires webcam.capture;
    requires weka.stable;

    opens main to javafx.fxml;
    opens menu to javafx.fxml;
    //opens Controller to javafx.fxml;
    //opens Action to javafx.fxml;
    opens UI to javafx.fxml, javafx.graphics, javafx.base;
    opens Entities to javafx.base, javafx.fxml, javafx.graphics;

    exports main;
    opens Handler to javafx.fxml;
    opens Action.Book to javafx.fxml;
    opens Action.Account to javafx.fxml;
    opens Controller.Account to javafx.fxml;
    opens Controller.Book to javafx.fxml;
}