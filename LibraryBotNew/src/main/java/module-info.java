module org.example.librarybot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens controller to javafx.fxml;
    opens admin.controller to javafx.fxml;
    opens org.example to javafx.fxml;
    opens model to javafx.base;

    exports org.example;
    exports controller;
    exports model;
    exports service;
    exports database;
    exports admin.controller;
}
