package chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.*;
import javafx.application.Platform;
import javafx.scene.control.ListView;

public class ClientController {
    @FXML
    private TextField usernameField;
    @FXML
    private Button connectButton;
    @FXML
    private TextArea userStatusArea; 
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    @FXML
    private Button leaveButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public void initialize() {
        connectButton.setOnAction(event -> connect());
        sendButton.setOnAction(event -> sendMessage());
        leaveButton.setOnAction(event -> leaveChat());
        inputField.setOnAction(event -> sendMessage());

        usernameField.setOnAction(event -> {
            username = usernameField.getText().trim(); 
            if (username.isEmpty()) {
                chatArea.appendText("Please enter a valid username.\n");
            } else {
                connect(); 
            }
        });
    }

    @FXML
    private void connect() {
        String usernameInput = usernameField.getText().trim();

        if (usernameInput.isEmpty()) {
            chatArea.appendText("Please enter a valid username.\n");
            return;
        }

        username = usernameInput; 
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(username);

            InetAddress serverAddress = socket.getInetAddress();
            String serverIpAddress = serverAddress.getHostAddress();
            out.println("/ip " + username + " connected with server IP: " + serverIpAddress);

            connectButton.setDisable(true);
            usernameField.setDisable(true);
            leaveButton.setDisable(false);
            sendButton.setDisable(false);
            inputField.setDisable(false);

            new Thread(this::listenForMessages).start(); 
        } catch (IOException e) {
            chatArea.appendText("Failed to connect to server: " + e.getMessage() + "\n");
        }
    }

    private void addIpToChat(String message) {
        Platform.runLater(() -> {
            chatArea.appendText(message + "\n");
        });
    }

    @FXML
    private void sendMessage() {
        String message = inputField.getText().trim();

        if (!message.isEmpty()) {
            out.println(message); 
            chatArea.appendText("You: " + message + "\n"); 
        }

        inputField.clear(); 
    }

    @FXML
    private void leaveChat() {
        out.println("/quit");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userStatusArea.clear(); 
        resetUI();
        Platform.exit(); 
    }

    private void resetUI() {
        connectButton.setDisable(false);
        usernameField.setDisable(false);
        leaveButton.setDisable(true);
        sendButton.setDisable(true);
        inputField.setDisable(true);
        chatArea.clear();
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            chatArea.appendText("Connection lost: " + e.getMessage() + "\n");
        }
    }

    private void processMessage(String message) {
        if (message.startsWith("/ip")) {
            addIpToChat(message.substring(4)); 
        } else if (message.startsWith("/pending")) {
            chatArea.appendText("Your request is pending approval.\n");
            sendButton.setDisable(true); 
        } else if (message.startsWith("/users")) {
            String[] users = message.split(" ", 2);
            if (users.length > 1) {
                String[] userListArray = users[1].split(",");
                userStatusArea.clear();
                for (String user : userListArray) {
                    userStatusArea.appendText(user + "\n"); 
                }
            } else {
                userStatusArea.clear(); 
            }
        } else if (message.startsWith("/message")) {
            chatArea.appendText(message.substring(9) + "\n"); 
        } else {
            chatArea.appendText(message + "\n"); 
        }
    }
}
