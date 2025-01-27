package chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.*;
import java.net.*;

public class ChatClient extends Application {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void connectToServer(String serverAddress, int port, String username) throws IOException {
        this.username = username;
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        out.println(username);
    }

    public void sendMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            out.println(message);
        }
    }

    public void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Connection lost: " + e.getMessage());
        }
    }

    public void closeConnection() throws IOException {
        out.println("/quit");
        socket.close();
    }
}