module org.example.librarybot {
    requires javafx.controls;
    requires javafx.fxml;

    opens controller to javafx.fxml;
    opens org.example to javafx.fxml;
    exports org.example;
    exports controller;
    exports model;
    exports service;
}