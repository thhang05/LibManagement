module org.example.btloop {
    requires javafx.controls;
    requires javafx.fxml;


    requires java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.json;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires mysql.connector.j;


    opens org.example.btloop to javafx.fxml;
    exports main;
}