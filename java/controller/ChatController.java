package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import service.ChatbotService;
import model.Buku;

public class ChatController {

    @FXML
    private VBox chatBox;

    @FXML
    private TextField inputField;

    @FXML
    private ScrollPane scrollPane;

    private ChatbotService chatbot = new ChatbotService();

    @FXML
    public void initialize() {
        // Welcome message
        addBotMessage("Selamat datang di Perpustakaan Digital! 👋\n\nSaya adalah asisten virtual perpustakaan yang siap membantu Anda menemukan buku.");

        // Auto scroll setup
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });
    }

    @FXML
    private void handleSend() {
        String userInput = inputField.getText().trim();

        if (userInput.isEmpty()) return;

        // Tampilkan pesan user
        addUserMessage(userInput);

        // Proses chatbot
        String response = chatbot.processInput(userInput);

        // Tampilkan respon bot dengan delay untuk efek typing
        new Thread(() -> {
            try {
                Thread.sleep(500); // Delay 0.5 detik
                javafx.application.Platform.runLater(() -> {
                    addBotMessage(response);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        inputField.clear();
    }

    private void addUserMessage(String message) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPadding(new Insets(5, 0, 5, 50));

        VBox bubble = createMessageBubble(message, "#3498DB", "#FFFFFF", true);

        container.getChildren().add(bubble);
        chatBox.getChildren().add(container);
    }

    private void addBotMessage(String message) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 50, 5, 0));

        // Bot avatar
        Region avatar = new Region();
        avatar.setStyle("-fx-background-color: #ECF0F1; -fx-background-radius: 20; -fx-pref-width: 40; -fx-pref-height: 40;");
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);

        VBox bubble = createMessageBubble(message, "#FFFFFF", "#2C3E50", false);

        container.getChildren().addAll(avatar, bubble);
        HBox.setMargin(avatar, new Insets(0, 10, 0, 0));

        chatBox.getChildren().add(container);
    }

    private VBox createMessageBubble(String message, String bgColor, String textColor, boolean isUser) {
        VBox bubble = new VBox();
        bubble.setMaxWidth(300);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
                "-fx-text-fill: " + textColor + "; " +
                        "-fx-font-size: 14px; " +
                        "-fx-line-spacing: 2px;"
        );

        bubble.getChildren().add(messageLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));

        String borderRadius = isUser ?
                "-fx-background-radius: 18 18 4 18;" :
                "-fx-background-radius: 18 18 18 4;";

        bubble.setStyle(
                "-fx-background-color: " + bgColor + "; " +
                        borderRadius +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        // Timestamp
        Label timeLabel = new Label(java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        ));
        timeLabel.setStyle("-fx-text-fill: " + (isUser ? "#FFFFFF" : "#95A5A6") + "; -fx-font-size: 10px;");
        timeLabel.setPadding(new Insets(4, 0, 0, 0));
        bubble.getChildren().add(timeLabel);

        return bubble;
    }

    // Method untuk menampilkan card buku (seperti di PDF)
    private void addBookCard(Buku buku) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 50, 5, 0));

        // Bot avatar
        Region avatar = new Region();
        avatar.setStyle("-fx-background-color: #ECF0F1; -fx-background-radius: 20; -fx-pref-width: 40; -fx-pref-height: 40;");
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);

        VBox card = new VBox(10);
        card.setMaxWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        // Book icon dan judul
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Region bookIcon = new Region();
        bookIcon.setStyle("-fx-background-color: #E8E3F3; -fx-background-radius: 8; -fx-pref-width: 50; -fx-pref-height: 50;");

        VBox titleBox = new VBox(3);
        Label title = new Label(buku.getJudul());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setWrapText(true);

        Label author = new Label("Oleh " + buku.getPenulis());
        author.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 12px;");

        titleBox.getChildren().addAll(title, author);
        header.getChildren().addAll(bookIcon, titleBox);

        // Status
        Label status = new Label(buku.isTersedia() ? "✓ Tersedia (5)" : "✗ Stok Habis");
        status.setStyle(
                "-fx-text-fill: " + (buku.isTersedia() ? "#27AE60" : "#E74C3C") + "; " +
                        "-fx-font-size: 12px; -fx-font-weight: bold;"
        );

        // Separator
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #ECF0F1;");

        // Info
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
                createInfoRow("Penulis:", buku.getPenulis()),
                createInfoRow("Penerbit:", buku.getPenerbit()),
                createInfoRow("ISBN:", buku.getIsbn()),
                createInfoRow("Kategori:", "Fiksi")
        );

        // Buttons
        HBox buttons = new HBox(10);
        Button pinjamBtn = new Button("Pinjam Buku");
        pinjamBtn.setStyle(
                "-fx-background-color: #3498DB; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand; -fx-font-weight: bold;"
        );

        Button reservasiBtn = new Button("Reservasi");
        reservasiBtn.setStyle(
                "-fx-background-color: #BDC3C7; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;"
        );

        buttons.getChildren().addAll(pinjamBtn, reservasiBtn);

        card.getChildren().addAll(header, status, separator, infoBox, buttons);

        container.getChildren().addAll(avatar, card);
        HBox.setMargin(avatar, new Insets(0, 10, 0, 0));

        chatBox.getChildren().add(container);
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox();
        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #95A5A6; -fx-font-size: 12px;");
        labelText.setMinWidth(80);

        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: #2C3E50; -fx-font-size: 12px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(labelText, spacer, valueText);
        return row;
    }
}